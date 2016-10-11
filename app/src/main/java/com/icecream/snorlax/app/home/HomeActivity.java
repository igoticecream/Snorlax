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

package com.icecream.snorlax.app.home;

import java.io.File;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.icecream.snorlax.BuildConfig;
import com.icecream.snorlax.R;
import com.icecream.snorlax.app.SnorlaxApp;
import com.icecream.snorlax.common.Files;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;

import static com.icecream.snorlax.R.xml.preferences;

public class HomeActivity extends AppCompatActivity {

	@BindView(R.id.coordinator)
	CoordinatorLayout mCoordinatorLayout;
	@BindView(R.id.toolbar)
	Toolbar mToolbar;
	@BindView(R.id.fab)
	FloatingActionButton mFab;

	private Unbinder mUnbinder;
	private AlertDialog mAboutDialog;
	private AlertDialog mFormatInfoDialog;
	private AlertDialog mDonationDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		mUnbinder = ButterKnife.bind(this);

		setupToolbar();
		setupPreferences();

		if (savedInstanceState == null) {
			Observable
				.just(SnorlaxApp.isEnabled())
				.filter(enabled -> !enabled)
				.map(enabled -> getPackageManager().getLaunchIntentForPackage(BuildConfig.XPOSED_ID))
				.map(intent -> {
					if (intent != null) {
						return Snackbar.make(mFab, R.string.error_xposed_disabled, Snackbar.LENGTH_LONG).setAction(R.string.enable, v -> startActivity(intent));
					}
					else {
						return Snackbar.make(mFab, R.string.error_xposed_missing, Snackbar.LENGTH_LONG);
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
					Snackbar.make(mFab, R.string.error_pokemon_missing, Snackbar.LENGTH_LONG).show();
				}
			})
			.filter(intent -> intent != null)
			.subscribe(this::startActivity);

		checkIfFirstTime();
	}

	private void setupToolbar() {
		setSupportActionBar(mToolbar);
	}

	private void setupPreferences() {
		PreferenceManager.setDefaultValues(this, preferences, false);

		getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content, new SettingsFragment())
			.commit();
	}

	private void checkIfFirstTime() {
		final String version = "version";
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getInt(version, -1) < BuildConfig.VERSION_CODE) {
			showDonation();
			preferences.edit().putInt(version, BuildConfig.VERSION_CODE).apply();
		}
	}

	@SuppressLint("InflateParams")
	private void showDonation() {
		mDonationDialog = new AlertDialog.Builder(this, R.style.Snorlax_Dialog)
			.setTitle(R.string.donation)
			.setView(getLayoutInflater().inflate(R.layout.donation_dialog, null, false))
			.setPositiveButton("Paypal", (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A9PPGNDJEC33E"))))
			.setCancelable(true)
			.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUnbinder.unbind();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.about:
				showAbout();
				return true;
			case R.id.format_info:
				showFormatInfo();
				return true;
			case R.id.donation:
				showDonation();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@SuppressLint("InflateParams")
	private void showAbout() {
		View view = getLayoutInflater().inflate(R.layout.about_dialog, null, false);

		TextView version = (TextView) view.findViewById(R.id.version);
		version.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));

		TextView author = (TextView) view.findViewById(R.id.author);
		author.setText(getString(R.string.about_author));

		TextView github = (TextView) view.findViewById(R.id.github);
		github.setText(getString(R.string.about_github));

		TextView thanks = (TextView) view.findViewById(R.id.thanks);
		thanks.setText(getString(R.string.about_thanks));

		mAboutDialog = new AlertDialog.Builder(this, R.style.Snorlax_Dialog)
			.setTitle(R.string.app_name)
			.setView(view)
			.setPositiveButton("Github", (dialog, which) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_repository)))))
			.setCancelable(true)
			.show();
	}

	@SuppressLint("InflateParams")
	private void showFormatInfo() {
		mFormatInfoDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.format_info)
			.setView(getLayoutInflater().inflate(R.layout.format_dialog, null, false))
			.setPositiveButton(android.R.string.ok, null)
			.setCancelable(true)
			.show();
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mFormatInfoDialog != null && mFormatInfoDialog.isShowing()) {
			mFormatInfoDialog.dismiss();
		}
		if (mAboutDialog != null && mAboutDialog.isShowing()) {
			mAboutDialog.dismiss();
		}
		if (mDonationDialog != null && mDonationDialog.isShowing()) {
			mDonationDialog.dismiss();
		}
	}

	public static class SettingsFragment extends PreferenceFragmentCompat {

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
		@SuppressLint("SetWorldReadable")
		public void onPause() {
			super.onPause();

			// Workaround since Google enforce security on Nougat on getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
			File sharedPrefsDir = new File(getActivity().getApplicationInfo().dataDir, "shared_prefs");
			File sharedPrefsFile = new File(sharedPrefsDir, getPreferenceManager().getSharedPreferencesName() + ".xml");

			final boolean isDirReadable = Files.setReadable(sharedPrefsDir);
			final boolean isPrefReadable = Files.setReadable(sharedPrefsFile);

			if (!isDirReadable || !isPrefReadable) {
				showReadableError();
			}
		}

		private void showReadableError() {
			if (getActivity() != null) {
				Toast.makeText(getActivity(), R.string.error_readable, Toast.LENGTH_LONG).show();
			}
		}
	}
}
