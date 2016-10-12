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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import com.icecream.snorlax.R;

import timber.log.Timber;

import static com.icecream.snorlax.R.xml.preferences;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public class SettingsFragment extends PreferenceFragmentCompat {

	private SettingsReadable mSettingsReadable;

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(preferences);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setDivider(new ColorDrawable(Color.TRANSPARENT));
		setDividerHeight(0);

		getListView().setPadding(0, 0, 0, getActivity().getResources().getDimensionPixelSize(R.dimen.padding_list_bottom));
	}

	@Override
	public void onPause() {
		super.onPause();
		SettingsReadable settingsReadable = new SettingsReadable(
			getActivity().getApplicationInfo(),
			getPreferenceManager()
		);

		settingsReadable
			.setReadable()
			.subscribe(Timber::d, throwable -> {
				Timber.e(throwable);
				showReadableError();
			});
	}

	private void showReadableError() {
		if (getActivity() != null) {
			Toast.makeText(getActivity(), R.string.error_readable, Toast.LENGTH_LONG).show();
		}
	}
}