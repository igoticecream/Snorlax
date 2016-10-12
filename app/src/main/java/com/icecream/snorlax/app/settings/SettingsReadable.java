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

package com.icecream.snorlax.app.settings;

import java.io.File;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.support.v7.preference.PreferenceManager;

import eu.chainfire.libsuperuser.Shell;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
final class SettingsReadable {

	private final ApplicationInfo mApplicationInfo;
	private final PreferenceManager mPreferenceManager;

	SettingsReadable(ApplicationInfo applicationInfo, PreferenceManager preferenceManager) {
		mApplicationInfo = applicationInfo;
		mPreferenceManager = preferenceManager;
	}

	private List<String> execute() {
		File directory = new File(mApplicationInfo.dataDir, "shared_prefs");
		File file = new File(directory, mPreferenceManager.getSharedPreferencesName() + ".xml");

		// Using SH since we are the owner of the files
		return Shell.SH.run(new String[]{
			String.format("chmod 755 %s", directory.getAbsolutePath()),
			String.format("chmod 664 %s", file.getAbsolutePath())
		});
	}

	Observable<String> setReadable() {
		return Observable
			.fromCallable(this::execute)
			.flatMapIterable(strings -> strings)
			.map(string -> String.format("%s%c", string, (char) 10))
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread());
	}
}
