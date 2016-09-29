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

package com.icecream.snorlax.module;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.res.Resources;

import com.icecream.snorlax.R;
import com.icecream.snorlax.module.context.snorlax.Snorlax;

import static POGOProtos.Data.Capture.CaptureProbabilityOuterClass.CaptureProbability;
import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

@Singleton
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Pokemons {

	private static final double[] CpM = {
		0.0939999967813492,
		0.135137432089339,
		0.166397869586945,
		0.192650913155325,
		0.215732470154762,
		0.236572651424822,
		0.255720049142838,
		0.273530372106572,
		0.290249884128571,
		0.306057381389863,
		0.321087598800659,
		0.335445031996451,
		0.349212676286697,
		0.362457736609939,
		0.375235587358475,
		0.387592407713878,
		0.399567276239395,
		0.4111935532161,
		0.422500014305115,
		0.432926420512509,
		0.443107545375824,
		0.453059948165049,
		0.46279838681221,
		0.472336085311278,
		0.481684952974319,
		0.490855807179549,
		0.499858438968658,
		0.5087017489616,
		0.517393946647644,
		0.525942516110322,
		0.534354329109192,
		0.542635753803599,
		0.550792694091797,
		0.558830584490385,
		0.566754519939423,
		0.57456912814537,
		0.582278907299042,
		0.589887907888945,
		0.597400009632111,
		0.604823648665171,
		0.61215728521347,
		0.619404107958234,
		0.626567125320435,
		0.633649178748576,
		0.6406529545784,
		0.647580971386554,
		0.654435634613037,
		0.661219265805859,
		0.667934000492096,
		0.674581885647492,
		0.681164920330048,
		0.687684901255373,
		0.694143652915955,
		0.700542901033063,
		0.706884205341339,
		0.713169074873823,
		0.719399094581604,
		0.725575586915154,
		0.731700003147125,
		0.734741038550429,
		0.737769484519958,
		0.740785579737136,
		0.743789434432983,
		0.746781197247765,
		0.749761044979095,
		0.752729099732281,
		0.75568550825119,
		0.758630370209851,
		0.761563837528229,
		0.76448604959218,
		0.767397165298462,
		0.770297293677362,
		0.773186504840851,
		0.776064947064992,
		0.778932750225067,
		0.781790050767666,
		0.784636974334717,
		0.787473608513275,
		0.790300011634827
	};

	private final String[] mNames;

	@Inject
	Pokemons(@Snorlax Resources resources) {
		mNames = resources.getStringArray(R.array.pokemon);
	}

	public Pokemons.Data with(PokemonData pokemonData) {
		return new Data(pokemonData);
	}

	public Probability with(CaptureProbability captureProbability) {
		return new Probability(captureProbability);
	}

	public final class Probability {

		private final CaptureProbability mCaptureProbability;

		Probability(CaptureProbability captureProbability) {
			mCaptureProbability = captureProbability;
		}

		public double getWithPokeball() {
			return getRate(0, 1);
		}

		private double getRate(int index, double multiplier) {
			return Math.round(Math.min(100.0d, mCaptureProbability.getCaptureProbability(index) * 100d * multiplier) * 100.0d) / 100.0d;
		}

		public double getWithPokeballAndBerry() {
			return getRate(0, 1.5);
		}

		public double getWithGreatball() {
			return getRate(1, 1);
		}

		public double getWithGreatballAndBerry() {
			return getRate(1, 1.5);
		}

		public double getWithUltraball() {
			return getRate(2, 1);
		}

		public double getWithUltraballAndBerry() {
			return getRate(2, 1.5);
		}
	}

	public final class Data {

		private final PokemonData mPokemonData;

		Data(PokemonData pokemonData) {
			mPokemonData = pokemonData;
		}

		public float getLevel() {
			if (mPokemonData.getIsEgg()) {
				return 0;
			}

			float level = 1;
			float cpMultiplier = mPokemonData.getCpMultiplier() + mPokemonData.getAdditionalCpMultiplier();

			for (double currentCpM : CpM) {
				if (Math.abs(cpMultiplier - currentCpM) < 0.0001) {
					return level;
				}
				level += 0.5;
			}
			return level;
		}

		public double getIvPercentage() {
			return ((Math.floor((getIvRatio() * 100) * 100)) / 100);
		}

		public double getIvRatio() {
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

		public String getMove1WithFormat() {
			String move = getMove1().name();

			StringBuilder builder = new StringBuilder();
			for (String part : move.split("_")) {
				if (!part.equalsIgnoreCase("fast")) {
					builder
						.append(part.charAt(0))
						.append(part.substring(1).toLowerCase(Locale.US))
						.append(" ");
				}
			}
			return builder.toString().trim();
		}

		public PokemonMove getMove1() {
			return mPokemonData.getMove1();
		}

		public String getMove2WithFormat() {
			String move = getMove2().name();

			StringBuilder builder = new StringBuilder();
			for (String part : move.split("_")) {
				if (!part.equalsIgnoreCase("fast")) {
					builder
						.append(part.charAt(0))
						.append(part.substring(1).toLowerCase(Locale.US))
						.append(" ");
				}
			}
			return builder.toString().trim();
		}

		public PokemonMove getMove2() {
			return mPokemonData.getMove2();
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
			return mPokemonData.getPokemonId().getNumber();
		}
	}
}
