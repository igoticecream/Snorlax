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

import static POGOProtos.Data.Capture.CaptureProbabilityOuterClass.CaptureProbability;

final class EncounterProbability {

	private CaptureProbability mProbability;

	EncounterProbability(CaptureProbability probability) {
		mProbability = probability;
	}

	double getWithPokeball() {
		return getRate(0, 1);
	}

	private double getRate(int index, double multiplier) {
		double rate = mProbability.getCaptureProbability(index) * 100d * multiplier;
		if (rate > 100.0d) {
			rate = 100.0d;
		}
		return Math.round(rate * 100.0d) / 100.0d;
	}

	double getWithPokeballAndBerry() {
		return getRate(0, 1.5);
	}

	double getWithGreatball() {
		return getRate(1, 1);
	}

	double getWithGreatballAndBerry() {
		return getRate(1, 1.5);
	}

	double getWithUltraball() {
		return getRate(2, 1);
	}

	double getWithUltraballAndBerry() {
		return getRate(2, 1.5);
	}
}
