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

package com.icecream.snorlax.app.ui;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.icecream.snorlax.BuildConfig;
import com.icecream.snorlax.R;
import com.icecream.snorlax.app.SnorlaxApp;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

public class HomeActivity extends AppCompatActivity {

	@BindView(R.id.coordinator)
	CoordinatorLayout mCoordinatorLayout;
	@BindView(R.id.toolbar)
	Toolbar mToolbar;
	@BindView(R.id.fab)
	FloatingActionButton mFab;

	private Unbinder mUnbinder;

	@Override
	protected void onCreate(Bundle savedStateInstanceState) {
		super.onCreate(savedStateInstanceState);
		setContentView(R.layout.home_activity);
		mUnbinder = ButterKnife.bind(this);

		setupToolbar();
		setupPreferences();

		if (savedStateInstanceState == null) {
			Observable
				.just(SnorlaxApp.isEnabled())
				.filter(enabled -> !enabled)
				.map(enabled -> getPackageManager().getLaunchIntentForPackage(BuildConfig.XPOSED_ID))
				.map(intent -> {
					if (intent != null) {
						return Snackbar.make(mFab, R.string.xposed_disabled, Snackbar.LENGTH_LONG).setAction(R.string.enable, v -> startActivity(intent));
					}
					else {
						return Snackbar.make(mFab, R.string.xposed_missing, Snackbar.LENGTH_LONG);
					}
				})
				.subscribe(Snackbar::show);
		}

		RxView
			.clicks(mFab)
			.throttleFirst(3, TimeUnit.SECONDS)
			.map(click -> getPackageManager().getLaunchIntentForPackage(BuildConfig.POKEMON_GO_ID))
			.doOnNext(intent -> {
				if (intent == null) {
					Snackbar.make(mFab, R.string.pokemon_missing, Snackbar.LENGTH_LONG).show();
				}
			})
			.filter(intent -> intent != null)
			.subscribe(this::startActivity);
	}

	private void setupToolbar() {
		setSupportActionBar(mToolbar);
	}

	private void setupPreferences() {
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content, new SettingsFragment())
			.commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUnbinder.unbind();
	}

	public static class SettingsFragment extends PreferenceFragmentCompat {

		@Override
		@SuppressWarnings("deprecation")
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			//Note: this will throw security exception on Nougat
			getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
			addPreferencesFromResource(R.xml.preferences);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			// Remove dividers
			setDivider(new ColorDrawable(Color.TRANSPARENT));
			setDividerHeight(0);
		}

		@Override
		@SuppressLint("SetWorldReadable")
		@SuppressWarnings("ResultOfMethodCallIgnored")
		public void onPause() {
			super.onPause();

			File sharedPrefsDir = new File(getActivity().getApplicationInfo().dataDir, "shared_prefs");
			File sharedPrefsFile = new File(sharedPrefsDir, getPreferenceManager().getSharedPreferencesName() + ".xml");
			if (sharedPrefsFile.exists()) {
				sharedPrefsFile.setReadable(true, false);
			}
		}
	}
}
