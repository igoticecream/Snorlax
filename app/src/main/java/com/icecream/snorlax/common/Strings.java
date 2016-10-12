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

import java.util.Locale;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Strings {

	public static final String EMPTY = "";
	public static final String DOT = ".";
	public static final String COLON = ":";
	public static final String SPACE = " ";

	public static String nullToEmpty(String string) {
		return (string == null) ? EMPTY : string;
	}

	public static String emptyToNull(String string) {
		return isNullOrEmpty(string) ? null : string;
	}

	public static boolean isNullOrEmpty(String string) {
		return isNull(string) || isEmpty(string);
	}

	public static boolean isNull(String string) {
		return string == null;
	}

	public static boolean isEmpty(String string) {
		return string.trim().length() == 0;
	}

	public static String valueOrDefault(String string, String defaultString) {
		return isNullOrEmpty(string) ? defaultString : string;
	}

	public static String truncateAt(String string, int length) {
		return (string.length() > length) ? string.substring(0, length) : string;
	}

	public static String padEnd(String string, int minLength, char padChar) {
		if (string.length() >= minLength) {
			return string;
		}
		StringBuilder sb = new StringBuilder(minLength);
		sb.append(string);
		for (int i = string.length(); i < minLength; i++) {
			sb.append(padChar);
		}
		return sb.toString();
	}

	public static String padStart(String string, int minLength, char padChar) {
		if (string.length() >= minLength) {
			return string;
		}
		StringBuilder sb = new StringBuilder(minLength);
		for (int i = string.length(); i < minLength; i++) {
			sb.append(padChar);
		}
		sb.append(string);
		return sb.toString();
	}

	public static String capitalize(String[] string) {
		if (string == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (String sub : string) {
			builder
				.append(capitalize(sub))
				.append(" ");
		}
		return builder.toString().trim();
	}

	public static String capitalize(String string) {
		if (isNullOrEmpty(string) || string.trim().length() < 2) {
			return string;
		}
		return String.valueOf(string.charAt(0)).toUpperCase(Locale.US) + string.substring(1).toLowerCase(Locale.US);
	}

	private Strings() {
		throw new AssertionError("No instances");
	}
}
