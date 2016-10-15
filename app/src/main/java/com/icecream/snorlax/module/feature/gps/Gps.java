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

package com.icecream.snorlax.module.feature.gps;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.location.Location;

import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Singleton
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Gps implements Feature {

	private final ClassLoader mClassLoader;

	private XC_MethodHook.Unhook mUnhook;

	@Inject
	Gps(ClassLoader classLoader) {
		mClassLoader = classLoader;
	}

	@Override
	public void subscribe() throws Exception {
		unsubscribe();

		final Class<?> location = XposedHelpers.findClass("com.nianticlabs.nia.location.NianticLocationManager", mClassLoader);
		if (location == null) {
			Log.e("Cannot find NianticLocationManager class");
			return;
		}

		mUnhook = XposedHelpers.findAndHookMethod(location, "locationUpdate", Location.class, int[].class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				Log.d("locationUpdate");

				Location location = (Location) param.args[0];
				location.setLatitude(10.465231614627145);
				location.setLongitude(-66.97446120465979);

				Log.d(location.toString());

				param.args[0] = location;
			}
		});
	}

	@Override
	public void unsubscribe() throws Exception {
		if (mUnhook != null) {
			mUnhook.unhook();
		}
	}
}
