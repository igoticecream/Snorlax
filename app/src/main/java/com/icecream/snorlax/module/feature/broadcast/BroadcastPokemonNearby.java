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

package com.icecream.snorlax.module.feature.broadcast;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public abstract class BroadcastPokemonNearby {

	static BroadcastPokemonNearby create(@Nullable Long encounterId, @Nullable Integer pokedex, @Nullable Float distanceInMeters, @Nullable Long s2CellId) {
		return new AutoValue_BroadcastPokemonNearby(encounterId, pokedex, distanceInMeters, s2CellId);
	}

	public static TypeAdapter<BroadcastPokemonNearby> typeAdapter(Gson gson) {
		return new AutoValue_BroadcastPokemonNearby.GsonTypeAdapter(gson);
	}

	@SerializedName("encounter_id")
	@Nullable
	abstract Long getEncounterId();

	@SerializedName("pokedex")
	@Nullable
	abstract Integer getPokedex();

	@SerializedName("distance_in_meters")
	@Nullable
	abstract Float getDistanceInMeters();

	@SerializedName("s2_cell_id")
	@Nullable
	abstract Long getS2CellId();
}
