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

package com.icecream.snorlax.module.util;

import java.util.Locale;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Pokemons {

	private PokemonData mPokemon;

	public Pokemons(PokemonData pokemon) {
		mPokemon = pokemon;
	}

	public int getNumber() {
		return mPokemon.getPokemonId().getNumber();
	}

	public String getName() {
		StringBuilder builder = new StringBuilder();
		for (String part : mPokemon.getPokemonId().name().split("_")) {
			builder
				.append(part.charAt(0))
				.append(part.substring(1).toLowerCase(Locale.US))
				.append(" ");
		}
		return builder.toString().trim();
	}

	public float getLevel() {
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

	public float getCombinedCpMultiplier() {
		return getCpMultiplier() + getAdditionalCpMultiplier();
	}

	public float getCpMultiplier() {
		return mPokemon.getCpMultiplier();
	}

	public float getAdditionalCpMultiplier() {
		return mPokemon.getAdditionalCpMultiplier();
	}

	public int getCp() {
		return mPokemon.getCp();
	}

	public int getStamina() {
		return mPokemon.getStaminaMax();
	}

	public PokemonMove getMove1() {
		return mPokemon.getMove1();
	}

	public PokemonMove getMove2() {
		return mPokemon.getMove2();
	}

	public double getIvPercentage() {
		return ((Math.floor((getIvRatio() * 100) * 100)) / 100);
	}

	public double getIvRatio() {
		return ((double) (getIndividualAttack() + getIndividualDefense() + getIndividualStamina())) / 45.0;
	}

	public int getIndividualAttack() {
		return mPokemon.getIndividualAttack();
	}

	public int getIndividualDefense() {
		return mPokemon.getIndividualDefense();
	}

	public int getIndividualStamina() {
		return mPokemon.getIndividualStamina();
	}
}
