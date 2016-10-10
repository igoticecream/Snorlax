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

package com.icecream.snorlax.module.feature.encounter;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.res.Resources;

import com.icecream.snorlax.R;
import com.icecream.snorlax.module.context.snorlax.Snorlax;

import de.robv.android.xposed.XSharedPreferences;
import rx.Observable;

@Singleton
final class EncounterPreferences {

	private final Resources mResources;
	private final XSharedPreferences mPreferences;

	@Inject
	EncounterPreferences(@Snorlax Resources resources, XSharedPreferences preferences) {
		mResources = resources;
		mPreferences = preferences;
	}

	<T> Observable.Transformer<T, T> isDismissEnabled() {
		return observable -> observable
			.doOnNext(t -> mPreferences.reload())
			.filter(t -> {
				final boolean expected = mResources.getBoolean(R.bool.preference_encounter_dismiss_notification_enable);
				return expected == mPreferences.getBoolean(mResources.getString(R.string.preference_encounter_dismiss_notification_enable_key), expected);
			});
	}

	<T> Observable.Transformer<T, T> isEnabled() {
		return observable -> observable
			.doOnNext(t -> mPreferences.reload())
			.filter(t -> {
				final boolean expected = getPreferenceDefaultValue();
				return expected == getPreference(expected);
			});
	}

	private boolean getPreferenceDefaultValue() {
		return mResources.getBoolean(R.bool.preference_encounter_notification_enable);
	}

	private boolean getPreference(boolean defaultValue) {
		return mPreferences.getBoolean(mResources.getString(R.string.preference_encounter_notification_enable_key), defaultValue);
	}
}
