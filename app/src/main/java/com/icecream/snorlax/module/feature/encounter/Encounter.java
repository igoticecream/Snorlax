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
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.support.v4.util.Pair;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.feature.mitm.MitmRelay;
import com.icecream.snorlax.module.util.Log;
import com.icecream.snorlax.module.util.Pokemons;
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
	private final EncounterPreferences mPreferences;
	private final EncounterNotification mEncounterNotification;
	private Subscription mSubscription;

	@Inject
	Encounter(MitmRelay mitmRelay, EncounterPreferences preferences, EncounterNotification encounterNotification) {
		mMitmRelay = mitmRelay;
		mPreferences = preferences;
		mEncounterNotification = encounterNotification;
	}

	private String formatMove(String move) {
		StringBuilder builder = new StringBuilder();
		for (String part : move.split("_")) {
			builder
				.append(part.charAt(0))
				.append(part.substring(1).toLowerCase(Locale.US))
				.append(" ");
		}
		return builder.toString().trim();
	}

	private void onEncounter(PokemonData data, CaptureProbability probability) {
		// TODO factory
		Pokemons pokemons = new Pokemons(data);
		// TODO factory
		EncounterProbability encounterProbability = new EncounterProbability(probability);

		mEncounterNotification.show(
			pokemons.getNumber(),
			pokemons.getName(),
			pokemons.getIvPercentage(),
			pokemons.getIndividualAttack(),
			pokemons.getIndividualDefense(),
			pokemons.getIndividualStamina(),
			pokemons.getCp(),
			pokemons.getLevel(),
			pokemons.getStamina(),
			formatMove(pokemons.getMove1().name()),
			formatMove(pokemons.getMove2().name()),
			encounterProbability.getWithPokeball(),
			encounterProbability.getWithPokeballAndBerry(),
			encounterProbability.getWithGreatball(),
			encounterProbability.getWithGreatballAndBerry(),
			encounterProbability.getWithUltraball(),
			encounterProbability.getWithUltraballAndBerry()
		);
	}

	private void onWildEncounter(ByteString bytes) {
		try {
			EncounterResponse response = EncounterResponse.parseFrom(bytes);
			onEncounter(response.getWildPokemon().getPokemonData(), response.getCaptureProbability());
		}
		catch (InvalidProtocolBufferException | NullPointerException e) {
			Log.d("EncounterResponse failed: %s" + e.getMessage());
			Log.e(e);
		}
	}

	private void onDiskEncounter(ByteString bytes) {
		try {
			DiskEncounterResponse response = DiskEncounterResponse.parseFrom(bytes);
			onEncounter(response.getPokemonData(), response.getCaptureProbability());
		}
		catch (InvalidProtocolBufferException | NullPointerException e) {
			Log.d("DiskEncounterResponse failed: %s" + e.getMessage());
			Log.e(e);
		}
	}

	private void onIncenseEncounter(ByteString bytes) {
		try {
			IncenseEncounterResponse response = IncenseEncounterResponse.parseFrom(bytes);
			onEncounter(response.getPokemonData(), response.getCaptureProbability());
		}
		catch (InvalidProtocolBufferException | NullPointerException e) {
			Log.d("IncenseEncounterResponse failed: %s" + e.getMessage());
			Log.e(e);
		}
	}

	@Override
	public void subscribe() {
		unsubscribe();

		mSubscription = mMitmRelay
			.getObservable()
			.compose(mPreferences.isEnabled())
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
	}

	@Override
	public void unsubscribe() {
		RxFuncitons.unsubscribe(mSubscription);
	}
}
