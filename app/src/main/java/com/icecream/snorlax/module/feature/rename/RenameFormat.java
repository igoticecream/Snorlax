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

package com.icecream.snorlax.module.feature.rename;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.support.annotation.Nullable;

import com.icecream.snorlax.common.Decimals;
import com.icecream.snorlax.common.Strings;
import com.icecream.snorlax.module.Pokemons;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import static java.lang.Integer.parseInt;

@Singleton
final class RenameFormat {

	private static final String BASE_NICK = "NICK";
	private static final String BASE_LVL = "LVL";
	private static final String BASE_IV = "IV";
	private static final String BASE_ATT = "ATT";
	private static final String BASE_DEF = "DEF";
	private static final String BASE_STA = "STA";

	private final Pokemons mPokemons;
	private final RenamePreferences mRenamePreferences;

	@Inject
	RenameFormat(Pokemons pokemons, RenamePreferences renamePreferences) {
		mPokemons = pokemons;
		mRenamePreferences = renamePreferences;
	}

	@Nullable
	private String processNick(String target, String nick) {
		final int length = target.length();
		final int dot = target.indexOf('.') + 1;

		if (length == BASE_NICK.length()) {
			return nick;
		}
		else if (dot > 0 && length > dot) {
			try {
				return Strings.truncateAt(nick, parseInt(target.substring(dot)));
			}
			catch (NumberFormatException ignored) {
			}
		}
		return null;
	}

	@Nullable
	private String processLevel(String target, float level) {

		if (target.equals(BASE_LVL)) {
			return Decimals.format(level, 1, 2, 1, 1);
		}
		if (target.equals(BASE_LVL.concat("P"))) {
			return Decimals.format(level, 2, 2, 1, 1);
		}
		if (target.startsWith(BASE_LVL.concat("."))) {
			try {
				final int decimals = Integer.parseInt(target.substring(target.indexOf('.') + 1));
				return Decimals.format(level, 1, 2, decimals, decimals);
			}
			catch (NumberFormatException | IndexOutOfBoundsException ignored) {
			}
		}
		if (target.startsWith(BASE_LVL.concat("P."))) {
			try {
				final int decimals = Integer.parseInt(target.substring(target.indexOf('.') + 1));
				return Decimals.format(level, 2, 2, decimals, decimals);
			}
			catch (NumberFormatException | IndexOutOfBoundsException ignored) {
			}
		}
		return null;
	}

	@Nullable
	private String processIv(String target, double iv) {
		if (target.equals(BASE_IV)) {
			return Decimals.format(iv, 1, 3, 1, 1);
		}
		if (target.equals(BASE_IV.concat("P"))) {
			return Decimals.format(iv, 3, 3, 1, 1);
		}
		if (target.startsWith(BASE_IV.concat("."))) {
			try {
				final int decimals = Integer.parseInt(target.substring(target.indexOf('.') + 1));
				return Decimals.format(iv, 1, 3, decimals, decimals);
			}
			catch (NumberFormatException | IndexOutOfBoundsException ignored) {
			}
		}
		if (target.startsWith(BASE_IV.concat("P."))) {
			try {
				final int decimals = Integer.parseInt(target.substring(target.indexOf('.') + 1));
				return Decimals.format(iv, 3, 3, decimals, decimals);
			}
			catch (NumberFormatException | IndexOutOfBoundsException ignored) {
			}
		}
		return null;
	}

	private String processAttack(String target, int attack) {
		if (target.equals(BASE_ATT)) {
			return Decimals.format(attack, 1, 2, 0, 0);
		}
		if (target.equals(BASE_ATT.concat("P"))) {
			return Decimals.format(attack, 2, 2, 0, 0);
		}
		if (target.equals(BASE_ATT.concat("H"))) {
			return Integer.toHexString(attack).toUpperCase();
		}
		return null;
	}

	private String processDefense(String target, int defense) {
		if (target.equals(BASE_DEF)) {
			return Decimals.format(defense, 1, 2, 0, 0);
		}
		if (target.equals(BASE_DEF.concat("P"))) {
			return Decimals.format(defense, 2, 2, 0, 0);
		}
		if (target.equals(BASE_DEF.concat("H"))) {
			return Integer.toHexString(defense).toUpperCase();
		}
		return null;
	}

	private String processStamina(String target, int stamina) {
		if (target.equals(BASE_STA)) {
			return Decimals.format(stamina, 1, 2, 0, 0);
		}
		if (target.equals(BASE_STA.concat("P"))) {
			return Decimals.format(stamina, 2, 2, 0, 0);
		}
		if (target.equals(BASE_STA.concat("H"))) {
			return Integer.toHexString(stamina).toUpperCase();
		}
		return null;
	}

	private String processFormat(Pokemons.Data pokemonsData, String command) throws NullPointerException {
		final String target = command.toUpperCase();

		String processed = null;

		if (target.startsWith(BASE_NICK)) {
			processed = processNick(target, pokemonsData.getName());
		}
		else if (target.startsWith(BASE_LVL)) {
			processed = processLevel(target, pokemonsData.getLevel());
		}
		else if (target.startsWith(BASE_IV)) {
			processed = processIv(target, pokemonsData.getIvRatio() * 100);
		}
		else if (target.startsWith(BASE_ATT)) {
			processed = processAttack(target, pokemonsData.getAttack());
		}
		else if (target.startsWith(BASE_DEF)) {
			processed = processDefense(target, pokemonsData.getDefense());
		}
		else if (target.startsWith(BASE_STA)) {
			processed = processStamina(target, pokemonsData.getStamina());
		}

		return Strings.isNullOrEmpty(processed) ? "%" + command + "%" : processed;
	}

	String format(PokemonData pokemonData) throws NullPointerException, IllegalArgumentException {
		final Pokemons.Data data = mPokemons.with(pokemonData);
		final String format = mRenamePreferences.getFormat();

		StringBuilder builder = new StringBuilder();

		for (int i = 0, len = format.length(); i < len; ) {
			int nextPercent = format.indexOf('%', i + 1);
			if (format.charAt(i) != '%') {
				final int end = (nextPercent == -1) ? len : nextPercent;

				builder.append(format.substring(i, end));
				i = end;
			}
			else if (nextPercent == -1) {
				builder.append(format.substring(i));
				i = len;
			}
			else if (format.substring(i + 1, nextPercent).contains(" ")) {
				builder.append(format.substring(i, nextPercent));
				i = nextPercent;
			}
			else {
				builder.append(processFormat(data, format.substring(i + 1, nextPercent)));
				i = nextPercent + 1;
			}
		}

		return builder.toString();
	}
}
