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

package com.icecream.snorlax.module.util;

import java.util.Locale;

import de.robv.android.xposed.XposedBridge;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Log {

	public static void d(String format, Object... args) {
		XposedBridge.log(String.format(Locale.US, format, args));
	}

	public static void e(Throwable throwable) {
		XposedBridge.log(throwable);
	}

	public static void e(Throwable throwable, String format, Object... args) {
		XposedBridge.log(new Exception(String.format(Locale.US, format, args), throwable));
	}

	public static void e(String format, Object... args) {
		XposedBridge.log(new Exception(String.format(Locale.US, format, args)));
	}

	private Log() {
		throw new AssertionError("No instances");
	}
}
