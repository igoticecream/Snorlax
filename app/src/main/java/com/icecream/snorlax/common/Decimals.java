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

package com.icecream.snorlax.common;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Decimals {

	private static DecimalFormat getDecimalFormat(int integerDigits, int fractionDigits) {
		DecimalFormat format = new DecimalFormat();
		format.setRoundingMode(RoundingMode.UP);
		format.setMinimumFractionDigits(fractionDigits);
		format.setMaximumFractionDigits(fractionDigits);
		format.setMinimumIntegerDigits(integerDigits);
		format.setMaximumIntegerDigits(integerDigits);
		return format;
	}

	public static String format(float value, int integerDigits, int fractionDigits) {
		return getDecimalFormat(integerDigits, fractionDigits).format(value);
	}

	public static String format(double value, int integerDigits, int fractionDigits) {
		return getDecimalFormat(integerDigits, fractionDigits).format(value);
	}

	public static String format(int value, int integerDigits, int fractionDigits) {
		return getDecimalFormat(integerDigits, fractionDigits).format(value);
	}

	private Decimals() {
		throw new AssertionError("No instances");
	}
}
