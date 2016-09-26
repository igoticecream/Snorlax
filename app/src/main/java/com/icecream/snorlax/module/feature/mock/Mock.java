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

package com.icecream.snorlax.module.feature.mock;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.ContentResolver;
import android.provider.Settings;

import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

@Singleton
public final class Mock implements Feature {

	private final ClassLoader mClassLoader;
	private final MockPreferences mPreferences;

	private XC_MethodHook.Unhook mUnhookGetString;
	private XC_MethodHook.Unhook mUnhookGetStringForUser;
	private XC_MethodHook.Unhook mUnhookMockProvider;

	@Inject
	Mock(ClassLoader classLoader, MockPreferences preferences) {
		mClassLoader = classLoader;
		mPreferences = preferences;
	}

	@Override
	public void subscribe() throws Exception {
		final Class<?> secure = XposedHelpers.findClass("android.provider.Settings.Secure", mClassLoader);
		if (secure == null) {
			Log.e("Cannot find Secure class");
			return;
		}

		final Class<?> location = XposedHelpers.findClass("android.location.Location", mClassLoader);
		if (location == null) {
			Log.e("Cannot find Location class");
			return;
		}

		mUnhookGetString = findAndHookMethod(secure, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
			@Override
			@SuppressWarnings("deprecation")
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!mPreferences.isEnabled()) {
					return;
				}

				String requested = (String) param.args[1];
				if (requested.equals(Settings.Secure.ALLOW_MOCK_LOCATION)) {
					param.setResult("0");
				}
			}
		});

		mUnhookGetStringForUser = XposedHelpers.findAndHookMethod(secure, "getStringForUser", ContentResolver.class, String.class, Integer.TYPE, new XC_MethodHook() {
			@Override
			@SuppressWarnings("deprecation")
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (!mPreferences.isEnabled()) {
					return;
				}

				String requested = (String) param.args[1];
				if (requested.equals(Settings.Secure.ALLOW_MOCK_LOCATION)) {
					param.setResult("0");
				}
			}
		});

		mUnhookMockProvider = findAndHookMethod(location, "isFromMockProvider",
			new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					if (!mPreferences.isEnabled()) {
						return;
					}
					param.setResult(false);
				}
			}
		);
	}

	@Override
	public void unsubscribe() throws Exception {
		if (mUnhookGetString != null) {
			mUnhookGetString.unhook();
		}

		if (mUnhookGetStringForUser != null) {
			mUnhookGetStringForUser.unhook();
		}

		if (mUnhookMockProvider != null) {
			mUnhookMockProvider.unhook();
		}
	}
}
