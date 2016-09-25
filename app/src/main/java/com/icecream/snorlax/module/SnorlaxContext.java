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

package com.icecream.snorlax.module;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;

import com.icecream.snorlax.BuildConfig;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
final class SnorlaxContext extends ContextWrapper {

	static SnorlaxContext create(Context from) throws PackageManager.NameNotFoundException {
		return new SnorlaxContext(from.createPackageContext(BuildConfig.SNORLAX_ID, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE), from);
	}

	private final Context mPokemonGoContext;

	private SnorlaxContext(Context snorlax, Context pokemonGo) {
		super(snorlax);
		mPokemonGoContext = pokemonGo;
	}

	@Override
	public Context getApplicationContext() {
		return this;
	}
}
