/*
 * Copyright (c) 2016. Pedro Diaz <igoticecream@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icecream.snorlax.module.feature.broadcast;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.common.rx.RxFuncitons;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.feature.mitm.MitmEnvelope;
import com.icecream.snorlax.module.feature.mitm.MitmRelay;
import com.icecream.snorlax.module.util.Log;

import POGOProtos.Enums.PokemonIdOuterClass;
import rx.Observable;
import rx.Subscription;

import static POGOProtos.Map.Fort.FortDataOuterClass.FortData;
import static POGOProtos.Map.MapCellOuterClass.MapCell;
import static POGOProtos.Networking.Requests.RequestOuterClass.Request;
import static POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import static POGOProtos.Networking.Responses.GetMapObjectsResponseOuterClass.GetMapObjectsResponse;

@Singleton
public final class Broadcast implements Feature {

	private final Gson mGson;
	private final MitmRelay mMitmRelay;
	private final BroadcastPreferences mPreferences;
	private final BroadcastNotification mBroadcastNotification;

	private Subscription mSubscription;

	@Inject
	Broadcast(Gson gson, MitmRelay mitmRelay, BroadcastPreferences preferences, BroadcastNotification notification) {
		mGson = gson;
		mMitmRelay = mitmRelay;
		mPreferences = preferences;
		mBroadcastNotification = notification;
	}

	@Override
	public void subscribe() {
		unsubscribe();

		mSubscription = mMitmRelay
			.getObservable()
			.compose(mPreferences.isEnabled())
			.compose(getMapObjectResponse())
			.compose(onMapObjectResponse())
			.subscribe(mBroadcastNotification::send, Log::e);
	}

	private Observable.Transformer<MitmEnvelope, GetMapObjectsResponse> getMapObjectResponse() {
		return observable -> observable
			.flatMap(envelope -> {
				List<Request> requests = envelope.getRequest().getRequestsList();

				for (int i = 0; i < requests.size(); i++) {
					if (requests.get(i).getRequestType() == RequestType.GET_MAP_OBJECTS) {
						return Observable.just(envelope.getResponse().getReturns(i));
					}
				}
				return Observable.empty();
			})
			.flatMap(bytes -> Observable.fromCallable(() -> getMapObjectResponse(bytes)))
			.doOnError(Log::e)
			.onErrorResumeNext(throwable -> Observable.empty());
	}

	private Observable.Transformer<GetMapObjectsResponse, String> onMapObjectResponse() {
		return observable -> observable
			.flatMapIterable(GetMapObjectsResponse::getMapCellsList)
			.flatMap(mapCell -> Observable
				.zip(
					getMapPokemons(mapCell),
					getWildPokemons(mapCell),
					getNearbyPokemons(mapCell),
					getDiskPokemons(mapCell),
					BroadcastPokemon::create
				)
				.filter(BroadcastPokemon::hasData)
				.map(mGson::toJson)
			);
	}

	private GetMapObjectsResponse getMapObjectResponse(ByteString bytes) throws InvalidProtocolBufferException {
		return GetMapObjectsResponse.parseFrom(bytes);
	}

	private Observable<List<BroadcastPokemonMap>> getMapPokemons(MapCell mapCell) {
		return Observable
			.from(mapCell.getCatchablePokemonsList())
			.map(pokemon -> BroadcastPokemonMap.create(
				pokemon.getEncounterId(),
				pokemon.getPokemonId().getNumber(),
				pokemon.getExpirationTimestampMs(),
				pokemon.getLatitude(),
				pokemon.getLongitude(),
				pokemon.getSpawnPointId(),
				mapCell.getS2CellId()
			))
			.toList()
			.switchIfEmpty(Observable.just(null));
	}

	private Observable<List<BroadcastPokemonWild>> getWildPokemons(MapCell mapCell) {
		return Observable
			.from(mapCell.getWildPokemonsList())
			.map(pokemon -> BroadcastPokemonWild.create(
				pokemon.getEncounterId(),
				pokemon.hasPokemonData() ? pokemon.getPokemonData().getPokemonId().getNumber() : PokemonIdOuterClass.PokemonId.MISSINGNO_VALUE,
				pokemon.getTimeTillHiddenMs(),
				pokemon.getLastModifiedTimestampMs(),
				pokemon.getLatitude(),
				pokemon.getLongitude(),
				pokemon.getSpawnPointId(),
				mapCell.getS2CellId()
			))
			.toList()
			.switchIfEmpty(Observable.just(null));
	}

	private Observable<List<BroadcastPokemonNearby>> getNearbyPokemons(MapCell mapCell) {
		return Observable
			.from(mapCell.getNearbyPokemonsList())
			.map(pokemon -> BroadcastPokemonNearby.create(
				pokemon.getEncounterId(),
				pokemon.getPokemonId().getNumber(),
				pokemon.getDistanceInMeters(),
				mapCell.getS2CellId()
			))
			.toList()
			.switchIfEmpty(Observable.just(null));
	}

	private Observable<List<BroadcastPokemonDisk>> getDiskPokemons(MapCell mapCell) {
		return Observable
			.from(mapCell.getFortsList())
			.filter(FortData::hasLureInfo)
			.map(fortData -> BroadcastPokemonDisk.create(
				fortData.getLureInfo().getEncounterId(),
				fortData.getLureInfo().getActivePokemonId().getNumber(),
				fortData.getLureInfo().getLureExpiresTimestampMs(),
				fortData.getLastModifiedTimestampMs(),
				fortData.getLatitude(),
				fortData.getLongitude(),
				fortData.getLureInfo().getFortId(),
				mapCell.getS2CellId()
			))
			.toList()
			.switchIfEmpty(Observable.just(null));
	}

	@Override
	public void unsubscribe() {
		RxFuncitons.unsubscribe(mSubscription);
	}
}
