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

package com.icecream.snorlax.module.feature.selinux;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Singleton
public final class SELinux implements Feature {

	private final ClassLoader mClassLoader;
	private final SELinuxPreferences mSELinuxPreferences;
	private final SELinuxNotification mSELinuxNotification;

	private boolean mIsPermissive;
	private XC_MethodHook.Unhook mUnhookAttest;
	private XC_MethodHook.Unhook mUnhookAttestResponse;

	@Inject
	SELinux(ClassLoader classLoader, SELinuxPreferences seLinuxPreferences, SELinuxNotification seLinuxNotification) {
		mClassLoader = classLoader;
		mSELinuxPreferences = seLinuxPreferences;
		mSELinuxNotification = seLinuxNotification;
	}

	@Override
	public void subscribe() throws Exception {
		unsubscribe();

		final Class<?> safetyNet = XposedHelpers.findClass("com.nianticlabs.nia.platform.SafetyNetService", mClassLoader);
		if (safetyNet == null) {
			Log.e("Cannot find SafetyNetService class");
			return;
		}

		mUnhookAttest = XposedHelpers.findAndHookMethod(safetyNet, "attest", byte[].class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				final Boolean selinux = SELinuxHelper.isPermissive();

				if (mSELinuxPreferences.isEnabled() && selinux != null && selinux) {
					mIsPermissive = true;

					SELinuxHelper.setEnforce(SELinuxHelper.ENFORCING);
					mSELinuxNotification.show(SELinuxHelper.getEnforce());
				}
			}
		});

		mUnhookAttestResponse = XposedHelpers.findAndHookMethod(safetyNet, "attestResponse", byte[].class, String.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				if (mIsPermissive) {
					mIsPermissive = false;

					SELinuxHelper.setEnforce(SELinuxHelper.PERMISSIVE);
					mSELinuxNotification.show(SELinuxHelper.getEnforce());
				}
			}
		});
	}

	@Override
	public void unsubscribe() throws Exception {
		if (mUnhookAttest != null) {
			mUnhookAttest.unhook();
		}
		if (mUnhookAttestResponse != null) {
			mUnhookAttestResponse.unhook();
		}
	}
}
