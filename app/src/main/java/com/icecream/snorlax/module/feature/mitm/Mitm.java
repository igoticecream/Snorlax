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

package com.icecream.snorlax.module.feature.mitm;

import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.os.Build;

import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Singleton
public final class Mitm implements Feature {

	private final ClassLoader mClassLoader;

	private XC_MethodHook.Unhook mUnhookInputStream;
	private XC_MethodHook.Unhook mUnhookOutputStream;

	@Inject
	Mitm(ClassLoader classLoader) {
		mClassLoader = classLoader;
	}

	@Override
	public void subscribe() throws Exception {
		final Class<?> http = XposedHelpers.findClass(getHttpUrlConnection(), mClassLoader);
		if (http == null) {
			Log.e("Cannot find HttpsURLConnection class");
			return;
		}

		mUnhookInputStream = XposedHelpers.findAndHookMethod(http, "getInputStream", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				param.setResult(new MitmInputStream((InputStream) param.getResult()));
			}
		});

		mUnhookOutputStream = XposedHelpers.findAndHookMethod(http, "getOutputStream", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				param.setResult(new MitmOutputStream((OutputStream) param.getResult()));
			}
		});
	}

	private String getHttpUrlConnection() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			return "com.android.okhttp.internal.huc.HttpURLConnectionImpl";
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return "com.android.okhttp.internal.http.HttpURLConnectionImpl";
		}
		else {
			return "libcore.net.http.HttpURLConnectionImpl";
		}
	}

	@Override
	public void unsubscribe() throws Exception {
		if (mUnhookInputStream != null)
			mUnhookInputStream.unhook();

		if (mUnhookOutputStream != null)
			mUnhookOutputStream.unhook();
	}
}
