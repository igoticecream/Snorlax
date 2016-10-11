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

import static POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class PokemonMoveMeta {

	private PokemonMove mMove;
	private PokemonType mType;
	private int mPower;
	private int mAccuracy;
	private int mTime;
	private int mEnergy;
	private double mCriticalChance;

	PokemonMoveMeta() {
	}

	public PokemonType getType() {
		return mType;
	}

	public void setType(PokemonType type) {
		mType = type;
	}

	public int getPower() {
		return mPower;
	}

	public void setPower(int power) {
		mPower = power;
	}

	public int getAccuracy() {
		return mAccuracy;
	}

	public void setAccuracy(int accuracy) {
		mAccuracy = accuracy;
	}

	public int getTime() {
		return mTime;
	}

	public void setTime(int time) {
		mTime = time;
	}

	public int getEnergy() {
		return mEnergy;
	}

	public void setEnergy(int energy) {
		mEnergy = energy;
	}

	public double getCriticalChance() {
		return mCriticalChance;
	}

	public void setCriticalChance(double criticalChance) {
		mCriticalChance = criticalChance;
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

	public PokemonMove getMove() {
		return mMove;
	}

	public void setMove(PokemonMove move) {
		mMove = move;
	}
}
