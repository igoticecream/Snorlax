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

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.icecream.snorlax.app.home.settings.SettingsFragment;
import com.icecream.snorlax.common.Strings;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

	private final List<String> mItems;

	HomeViewPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		mItems = new ArrayList<>();
		mItems.add("Setting");
		mItems.add("Advanced");
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return new SettingsFragment();
			case 1:
				return new SettingsFragment();
			default:
				throw new RuntimeException("Unknown fragment at position " + String.valueOf(position));
		}
	}

	@Override
	public int getCount() {
		return mItems != null ? mItems.size() : 0;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mItems != null ? mItems.get(position) : Strings.EMPTY;
	}
}
