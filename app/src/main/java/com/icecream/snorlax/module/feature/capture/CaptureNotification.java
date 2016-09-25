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

package com.icecream.snorlax.module.feature.capture;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

@Singleton
final class CaptureNotification {

	private final Application mApplication;

	@Inject
	CaptureNotification(Application application) {
		mApplication = application;
	}

	void show(final String message) {
		new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mApplication, message, Toast.LENGTH_SHORT).show());
	}
}
