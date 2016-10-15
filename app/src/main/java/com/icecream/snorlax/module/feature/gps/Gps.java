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

import android.content.ContextWrapper;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.icecream.snorlax.R;
import com.icecream.snorlax.module.context.snorlax.Snorlax;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Singleton
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class Gps implements Feature {

	private final ClassLoader mClassLoader;
	private final LayoutInflater mLayoutInflater;

	private XC_MethodHook.Unhook mUnhook;
	private XC_MethodHook.Unhook mUnhookUnity;

	private double mLatitude;
	private double mLongitude;

	@Inject
	Gps(ClassLoader classLoader, @Snorlax LayoutInflater layoutInflater) {
		mClassLoader = classLoader;
		mLayoutInflater = layoutInflater;

		mLatitude = 10.465231614627145;
		mLongitude = -66.97446120465979;
	}

	@Override
	public void subscribe() throws Exception {
		unsubscribe();

		final Class<?> location = XposedHelpers.findClass("com.nianticlabs.nia.location.NianticLocationManager", mClassLoader);
		if (location == null) {
			Log.e("Cannot find NianticLocationManager class");
			return;
		}

		final Class<?> unity = XposedHelpers.findClass("com.unity3d.player.UnityPlayer", mClassLoader);
		if (unity == null) {
			Log.e("Cannot find UnityPlayer class");
			return;
		}

		mUnhook = XposedHelpers.findAndHookMethod(location, "locationUpdate", Location.class, int[].class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				Log.d("locationUpdate");

				Location location = (Location) param.args[0];
				location.setLatitude(mLatitude);
				location.setLongitude(mLongitude);

				Log.d(location.toString());

				param.args[0] = location;
			}
		});

		mUnhookUnity = XposedHelpers.findAndHookConstructor(unity, ContextWrapper.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				final FrameLayout frameLayout = (FrameLayout) param.thisObject;

				FrameLayout ui = (FrameLayout) mLayoutInflater.inflate(R.layout.ui_feature, frameLayout, false);

				View fab = ui.findViewById(R.id.fab);
				if (fab != null) {
					fab.setOnClickListener(v -> {
						mLatitude = 10.46471;
						mLongitude = -66.97505;
					});
					fab.setOnLongClickListener(v -> {
						frameLayout.removeView(ui);
						return true;
					});
				}
				frameLayout.addView(ui);
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
