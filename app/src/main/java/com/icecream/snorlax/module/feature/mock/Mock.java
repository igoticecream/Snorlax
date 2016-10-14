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
import android.os.Bundle;
import android.provider.Settings;

import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Singleton
public final class Mock implements Feature {

	private final ClassLoader mClassLoader;
	private final MockPreferences mPreferences;

	private XC_MethodHook.Unhook mUnhookGetInt1;
	private XC_MethodHook.Unhook mUnhookGetInt2;
	private XC_MethodHook.Unhook mUnhookGetFloat1;
	private XC_MethodHook.Unhook mUnhookGetFloat2;
	private XC_MethodHook.Unhook mUnhookGetLong1;
	private XC_MethodHook.Unhook mUnhookGetLong2;
	private XC_MethodHook.Unhook mUnhookGetString;

	private XC_MethodHook.Unhook mUnhookGms;
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

		final XC_MethodHook hook = new XC_MethodHook() {
			@Override
			@SuppressWarnings("deprecation")
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (mPreferences.isEnabled()) {
					String methodName = param.method.getName();
					String setting = (String) param.args[1];
					if (setting.equals(Settings.Secure.ALLOW_MOCK_LOCATION)) {
						switch (methodName) {
							case "getInt":
								param.setResult(0);
								break;
							case "getString":
								param.setResult("0");
								break;
							case "getFloat":
								param.setResult(0.0f);
								break;
							case "getLong":
								param.setResult(0L);
								break;
							default:
								break;
						}
					}
				}
			}
		};

		mUnhookGetInt1 = XposedHelpers.findAndHookMethod(secure, "getInt", ContentResolver.class, String.class, hook);
		mUnhookGetInt2 = XposedHelpers.findAndHookMethod(secure, "getInt", ContentResolver.class, String.class, int.class, hook);

		mUnhookGetFloat1 = XposedHelpers.findAndHookMethod(secure, "getFloat", ContentResolver.class, String.class, hook);
		mUnhookGetFloat2 = XposedHelpers.findAndHookMethod(secure, "getFloat", ContentResolver.class, String.class, float.class, hook);

		mUnhookGetLong1 = XposedHelpers.findAndHookMethod(secure, "getLong", ContentResolver.class, String.class, hook);
		mUnhookGetLong2 = XposedHelpers.findAndHookMethod(secure, "getLong", ContentResolver.class, String.class, long.class, hook);

		mUnhookGetString = XposedHelpers.findAndHookMethod(secure, "getString", ContentResolver.class, String.class, hook);

		mUnhookGms = XposedHelpers.findAndHookMethod(location, "getExtras", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				final String GMS_MOCK_KEY = "mockLocation";
				if (mPreferences.isEnabled()) {
					Bundle extras = (Bundle) param.getResult();
					if (extras != null && extras.getBoolean(GMS_MOCK_KEY)) {
						extras.putBoolean(GMS_MOCK_KEY, false);
					}
					param.setResult(extras);
				}
			}
		});

		mUnhookMockProvider = XposedHelpers.findAndHookMethod(location, "isFromMockProvider", new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				if (mPreferences.isEnabled()) {
					param.setResult(false);
				}
			}
		});
	}

	@Override
	public void unsubscribe() throws Exception {
		if (mUnhookGetInt1 != null) {
			mUnhookGetInt1.unhook();
		}

		if (mUnhookGetInt2 != null) {
			mUnhookGetInt2.unhook();
		}

		if (mUnhookGetFloat1 != null) {
			mUnhookGetFloat1.unhook();
		}

		if (mUnhookGetFloat2 != null) {
			mUnhookGetFloat2.unhook();
		}

		if (mUnhookGetLong1 != null) {
			mUnhookGetLong1.unhook();
		}

		if (mUnhookGetLong2 != null) {
			mUnhookGetLong2.unhook();
		}

		if (mUnhookGetString != null) {
			mUnhookGetString.unhook();
		}

		if (mUnhookGms != null) {
			mUnhookGms.unhook();
		}

		if (mUnhookMockProvider != null) {
			mUnhookMockProvider.unhook();
		}
	}
}
