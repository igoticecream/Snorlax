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

import static POGOProtos.Data.Capture.CaptureProbabilityOuterClass.CaptureProbability;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class PokemonProbability {

	private final CaptureProbability mCaptureProbability;

	PokemonProbability(CaptureProbability captureProbability) {
		mCaptureProbability = captureProbability;
	}

	public double getPokeball() {
		return getRate(0);
	}

	private double getRate(int index) {
		return Math.round(Math.min(100.0d, mCaptureProbability.getCaptureProbability(index) * 100d) * 100.0d) / 100.0d;
	}

	public double getGreatball() {
		return getRate(1);
	}

	public double getUltraball() {
		return getRate(2);
	}
}
