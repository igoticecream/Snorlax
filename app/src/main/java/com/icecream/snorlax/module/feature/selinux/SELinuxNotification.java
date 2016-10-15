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

import android.content.Context;
import android.content.res.Resources;

import com.icecream.snorlax.module.context.pokemongo.PokemonGo;
import com.icecream.snorlax.module.context.snorlax.Snorlax;

@Singleton
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
final class SELinuxNotification {

	private final Context mContext;
	private final Resources mResources;

	@Inject
	SELinuxNotification(@PokemonGo Context context, @Snorlax Resources resources) {
		mContext = context;
		mResources = resources;
	}

	void show(boolean result) {
		//show(mResources.getString(result ? R.string.attest_ok : R.string.attest_error));
	}

	private void show(final String message) {
		//new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show());
	}
}
