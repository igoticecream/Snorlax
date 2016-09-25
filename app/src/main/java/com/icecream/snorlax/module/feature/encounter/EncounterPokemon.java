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

import java.util.Locale;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

@SuppressWarnings({"unused", "WeakerAccess"})
final class EncounterPokemon {

	private PokemonData mPokemon;

	EncounterPokemon(PokemonData pokemon) {
		mPokemon = pokemon;
	}

	int getNumber() {
		return mPokemon.getPokemonId().getNumber();
	}

	String getName() {
		StringBuilder builder = new StringBuilder();
		for (String part : mPokemon.getPokemonId().name().split("_")) {
			builder
				.append(part.charAt(0))
				.append(part.substring(1).toLowerCase(Locale.US))
				.append(" ");
		}
		return builder.toString().trim();
	}

	float getLevel() {
		float level;
		float combinedCpMultiplier = getCombinedCpMultiplier();

		if (combinedCpMultiplier < 0.734f) {
			level = 58.35178527f * combinedCpMultiplier * combinedCpMultiplier - 2.838007664f * combinedCpMultiplier + 0.8539209906f;
		}
		else {
			level = 171.0112688f * combinedCpMultiplier - 95.20425243f;
		}
		return Math.round((level) * 2) / 2.0f;
	}

	float getCombinedCpMultiplier() {
		return getCpMultiplier() + getAdditionalCpMultiplier();
	}

	float getCpMultiplier() {
		return mPokemon.getCpMultiplier();
	}

	float getAdditionalCpMultiplier() {
		return mPokemon.getAdditionalCpMultiplier();
	}

	int getCp() {
		return mPokemon.getCp();
	}

	int getStamina() {
		return mPokemon.getStaminaMax();
	}

	PokemonMove getMove1() {
		return mPokemon.getMove1();
	}

	PokemonMove getMove2() {
		return mPokemon.getMove2();
	}

	double getIvPercentage() {
		return ((Math.floor((getIvRatio() * 100) * 100)) / 100);
	}

	double getIvRatio() {
		return ((double) (getIndividualAttack() + getIndividualDefense() + getIndividualStamina())) / 45.0;
	}

	int getIndividualAttack() {
		return mPokemon.getIndividualAttack();
	}

	int getIndividualDefense() {
		return mPokemon.getIndividualDefense();
	}

	int getIndividualStamina() {
		return mPokemon.getIndividualStamina();
	}
}
