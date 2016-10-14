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
public abstract class BroadcastPokemonMap {

	static BroadcastPokemonMap create(@Nullable Long encounterId, @Nullable Integer pokedex, @Nullable Long expirationTimestampMs, @Nullable Double latitude, @Nullable Double longitude, @Nullable String spawnpointId, @Nullable Long s2CellId) {
		return new AutoValue_BroadcastPokemonMap(encounterId, pokedex, expirationTimestampMs, latitude, longitude, spawnpointId, s2CellId);
	}

	public static TypeAdapter<BroadcastPokemonMap> typeAdapter(Gson gson) {
		return new AutoValue_BroadcastPokemonMap.GsonTypeAdapter(gson);
	}

	@SerializedName("encounter_id")
	@Nullable
	abstract Long getEncounterId();

	@SerializedName("pokedex")
	@Nullable
	abstract Integer getPokedex();

	@SerializedName("expiration_timestamp_ms")
	@Nullable
	abstract Long getExpirationTimestampMs();

	@SerializedName("latitude")
	@Nullable
	abstract Double getLatitude();

	@SerializedName("longitude")
	@Nullable
	abstract Double getLongitude();

	@SerializedName("spawnpoint_id")
	@Nullable
	abstract String getSpawnpointId();

	@SerializedName("s2_cell_id")
	@Nullable
	abstract Long getS2CellId();
}