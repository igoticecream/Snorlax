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

import java.util.concurrent.TimeUnit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.icecream.snorlax.BuildConfig;
import com.icecream.snorlax.R;
import com.icecream.snorlax.app.SnorlaxApp;
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
	@BindView(R.id.tab)
	TabLayout mTabLayout;
	@BindView(R.id.pager)
	ViewPager mViewPager;

	private Unbinder mUnbinder;
	private AlertDialog mAboutDialog;
	private AlertDialog mFormatInfoDialog;
	private AlertDialog mDonationDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mUnbinder = ButterKnife.bind(this);

		setupToolbar();
		setupPreferences();
		setupViewPager();

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
	}

	private void setupViewPager() {
		PagerAdapter adapter = new HomeViewPagerAdapter(getSupportFragmentManager());

		mViewPager.setAdapter(adapter);
		mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

		mTabLayout.setupWithViewPager(mViewPager);
		mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				if (mViewPager != null) {
					mViewPager.setCurrentItem(tab.getPosition());
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {

			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	private void checkIfFirstTime() {
		final String version = "version";
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if (preferences.getInt(version, -1) < BuildConfig.VERSION_CODE) {
			mDonationDialog = HomeDialog.showDonation(this);
			preferences.edit().putInt(version, BuildConfig.VERSION_CODE).apply();
		}
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
				mAboutDialog = HomeDialog.showAbout(this);
				return true;
			case R.id.format_info:
				mFormatInfoDialog = HomeDialog.showFormatInfo(this);
				return true;
			case R.id.donation:
				mDonationDialog = HomeDialog.showDonation(this);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		HomeDialog.dismiss(mFormatInfoDialog, mAboutDialog, mDonationDialog);
	}
}
