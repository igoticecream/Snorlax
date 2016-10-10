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

package com.icecream.snorlax.module.feature.encounter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.support.v4.util.Pair;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.module.Pokemons;
import com.icecream.snorlax.module.event.DismissNotification;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.feature.mitm.MitmRelay;
import com.icecream.snorlax.module.util.Log;
import com.icecream.snorlax.module.util.RxBus;
import com.icecream.snorlax.module.util.RxFuncitons;

import rx.Observable;
import rx.Subscription;

import static POGOProtos.Data.Capture.CaptureProbabilityOuterClass.CaptureProbability;
import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static POGOProtos.Networking.Requests.RequestOuterClass.Request;
import static POGOProtos.Networking.Requests.RequestTypeOuterClass.RequestType;
import static POGOProtos.Networking.Responses.DiskEncounterResponseOuterClass.DiskEncounterResponse;
import static POGOProtos.Networking.Responses.EncounterResponseOuterClass.EncounterResponse;
import static POGOProtos.Networking.Responses.IncenseEncounterResponseOuterClass.IncenseEncounterResponse;

@Singleton
public final class Encounter implements Feature {

	private final MitmRelay mMitmRelay;
	private final Pokemons mPokemons;
	private final EncounterPreferences mEncounterPreferences;
	private final EncounterNotification mEncounterNotification;
	private final RxBus mRxBus;

	private Subscription mSubscription;
	private Subscription mRxBusSubscription;

	@Inject
	Encounter(MitmRelay mitmRelay, Pokemons pokemons, EncounterPreferences encounterPreferences, EncounterNotification encounterNotification, RxBus rxBus) {
		mMitmRelay = mitmRelay;
		mPokemons = pokemons;
		mEncounterPreferences = encounterPreferences;
		mEncounterNotification = encounterNotification;
		mRxBus = rxBus;
	}

	@Override
	public void subscribe() {
		unsubscribe();

		mSubscription = mMitmRelay
			.getObservable()
			.compose(mEncounterPreferences.isEnabled())
			.flatMap(envelope -> {
				List<Request> requests = envelope.getRequest().getRequestsList();

				for (int i = 0; i < requests.size(); i++) {
					RequestType type = requests.get(i).getRequestType();

					switch (type) {
						case ENCOUNTER:
						case DISK_ENCOUNTER:
						case INCENSE_ENCOUNTER:
							return Observable.just(new Pair<>(type, envelope.getResponse().getReturns(i)));
						default:
							break;
					}
				}
				return Observable.empty();
			})
			.subscribe(pair -> {
				switch (pair.first) {
					case ENCOUNTER:
						onWildEncounter(pair.second);
						break;
					case DISK_ENCOUNTER:
						onDiskEncounter(pair.second);
						break;
					case INCENSE_ENCOUNTER:
						onIncenseEncounter(pair.second);
						break;
					default:
						break;
				}
			}, Log::e);

		mRxBusSubscription = mRxBus
			.receive(DismissNotification.class)
			.compose(mEncounterPreferences.isDismissEnabled())
			.subscribe(dismiss -> mEncounterNotification.cancel());
	}

	private void onWildEncounter(ByteString bytes) {
		try {
			EncounterResponse response = EncounterResponse.parseFrom(bytes);
			onEncounter(response.getWildPokemon().getPokemonData(), response.getCaptureProbability());
		}
		catch (InvalidProtocolBufferException | NullPointerException e) {
			Log.d("EncounterResponse failed: %s", e.getMessage());
			Log.e(e);
		}
		catch (IllegalArgumentException e) {
			Log.d("Cannot process IncenseEncounterResponse: %s", e.getMessage());
			Log.e(e);
		}
	}

	private void onDiskEncounter(ByteString bytes) {
		try {
			DiskEncounterResponse response = DiskEncounterResponse.parseFrom(bytes);
			onEncounter(response.getPokemonData(), response.getCaptureProbability());
		}
		catch (InvalidProtocolBufferException | NullPointerException e) {
			Log.d("DiskEncounterResponse failed: %s", e.getMessage());
			Log.e(e);
		}
		catch (IllegalArgumentException e) {
			Log.d("Cannot process IncenseEncounterResponse: %s", e.getMessage());
			Log.e(e);
		}
	}

	private void onIncenseEncounter(ByteString bytes) {
		try {
			IncenseEncounterResponse response = IncenseEncounterResponse.parseFrom(bytes);
			onEncounter(response.getPokemonData(), response.getCaptureProbability());
		}
		catch (InvalidProtocolBufferException | NullPointerException e) {
			Log.d("IncenseEncounterResponse failed: %s", e.getMessage());
			Log.e(e);
		}
		catch (IllegalArgumentException e) {
			Log.d("Cannot process IncenseEncounterResponse: %s", e.getMessage());
			Log.e(e);
		}
	}

	private void onEncounter(PokemonData pokemonData, CaptureProbability captureProbability) throws NullPointerException, IllegalArgumentException {
		Pokemons.Data data = mPokemons.with(pokemonData);
		Pokemons.Probability probability = mPokemons.with(captureProbability);

		mEncounterNotification.show(
			data.getNumber(),
			data.getName(),
			data.getIvPercentage(),
			data.getAttack(),
			data.getDefense(),
			data.getStamina(),
			data.getCp(),
			data.getLevel(),
			data.getHp(),
			data.getMove1WithFormat(),
			data.getMove2WithFormat(),
			probability.getWithPokeball(),
			probability.getWithGreatball(),
			probability.getWithUltraball()
		);
	}

	@Override
	public void unsubscribe() {
		RxFuncitons.unsubscribe(mSubscription);
		RxFuncitons.unsubscribe(mRxBusSubscription);
	}
}
