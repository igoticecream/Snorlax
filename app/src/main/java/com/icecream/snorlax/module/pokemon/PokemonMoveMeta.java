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

import java.util.ArrayList;
import java.util.List;

import com.icecream.snorlax.common.Strings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

@Accessors(prefix = "m")
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class PokemonMoveMeta {

	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonMove mMove;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonType mType;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mPower;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mAccuracy;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mCriticalChance;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mTime;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mEnergy;

	PokemonMoveMeta() {
	}

	@Override
	public String toString() {
		List<String> move = new ArrayList<>();

		for (String string : getMove().name().split("_")) {
			if (!string.equalsIgnoreCase("FAST")) {
				move.add(string);
			}
		}
		return Strings.capitalize(move.toArray(new String[0]));
	}
}
