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

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.res.Resources;

import com.icecream.snorlax.R;
import com.icecream.snorlax.module.context.snorlax.Snorlax;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static POGOProtos.Enums.PokemonIdOuterClass.PokemonId;

@Singleton
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class PokemonFactory {

	private final String[] mNames;

	@Inject
	PokemonFactory(@Snorlax Resources resources) {
		mNames = resources.getStringArray(R.array.pokemon);
	}

	public Pokemon with(PokemonData pokemonData) throws NullPointerException, IllegalArgumentException {
		if (pokemonData == null) {
			throw new NullPointerException("PokemonData cannot be null");
		}
		if (pokemonData.getPokemonId().equals(PokemonId.MISSINGNO) || pokemonData.getPokemonId().equals(PokemonId.UNRECOGNIZED)) {
			throw new IllegalArgumentException("Unrecognized Pokemon Id");
		}
		return new Pokemon(pokemonData, mNames);
	}
}
