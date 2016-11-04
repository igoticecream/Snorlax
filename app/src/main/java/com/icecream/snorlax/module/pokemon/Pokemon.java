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

package com.icecream.snorlax.module.pokemon;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Pokemon {

	private final String[] mNames;
	private final PokemonData mPokemonData;

	Pokemon(PokemonData pokemonData, String[] names) {
		mPokemonData = pokemonData;
		mNames = names;
	}

	public float getLevel() {
		if (mPokemonData.getIsEgg()) {
			return 0;
		}

		float level = 1;
		float cpMultiplier = mPokemonData.getCpMultiplier() + mPokemonData.getAdditionalCpMultiplier();

		for (double currentCpM : PokemonCp.CpM) {
			if (Math.abs(cpMultiplier - currentCpM) < 0.0001) {
				return level;
			}
			level += 0.5;
		}
		return level;
	}

	public double getIv() {
		return ((double) (getAttack() + getDefense() + getStamina())) / 45.0;
	}

	public int getAttack() {
		return mPokemonData.getIndividualAttack();
	}

	public int getDefense() {
		return mPokemonData.getIndividualDefense();
	}

	public int getStamina() {
		return mPokemonData.getIndividualStamina();
	}

	public int getCp() {
		return mPokemonData.getCp();
	}

	public int getHp() {
		return mPokemonData.getStaminaMax();
	}

	public String getName() {
		return mNames[getNumber() - 1];
	}

	public int getNumber() {
		return PokemonMetaRegistry.getMeta(mPokemonData.getPokemonId()).getNumber();
	}

	public PokemonMoveMeta getMoveFast() {
		return PokemonMoveMetaRegistry.getMeta(mPokemonData.getMove1());
	}

	public PokemonMoveMeta getMoveCharge() {
		return PokemonMoveMetaRegistry.getMeta(mPokemonData.getMove2());
	}

	public PokemonType getType1() {
		return PokemonMetaRegistry.getMeta(mPokemonData.getPokemonId()).getType1();
	}

	public PokemonType getType2() {
		return PokemonMetaRegistry.getMeta(mPokemonData.getPokemonId()).getType2();
	}

	public PokemonClass getPokemonClass() {
		return PokemonMetaRegistry.getMeta(mPokemonData.getPokemonId()).getPokemonClass();
	}

	public double getPokemonBaseWeight() {
		return PokemonMetaRegistry.getMeta(mPokemonData.getPokemonId()).getPokedexWeightKg();
	}

	public double getPokemonWeight() {
		return mPokemonData.getWeightKg();
	}

	public double getPokemonBaseHeight() {
		return PokemonMetaRegistry.getMeta(mPokemonData.getPokemonId()).getPokedexHeightM();
	}

	public double getPokemonHeight() {
		return mPokemonData.getHeightM();
	}
}
