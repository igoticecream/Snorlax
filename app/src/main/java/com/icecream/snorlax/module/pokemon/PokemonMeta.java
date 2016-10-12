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

package com.icecream.snorlax.module.pokemon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static POGOProtos.Enums.PokemonFamilyIdOuterClass.PokemonFamilyId;
import static POGOProtos.Enums.PokemonIdOuterClass.PokemonId;
import static POGOProtos.Enums.PokemonMoveOuterClass.PokemonMove;

@Accessors(prefix = "m")
@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
public final class PokemonMeta {

	@Getter
	@Setter(AccessLevel.PACKAGE)
	private String mTemplateId;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonFamilyId mFamily;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonClass mPokemonClass;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonType mType2;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mPokedexHeightM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mHeightStdDev;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mBaseStamina;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mCylRadiusM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mBaseFleeRate;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mBaseAttack;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mDiskRadiusM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mCollisionRadiusM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mPokedexWeightKg;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private MovementType mMovementType;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonType mType1;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mCollisionHeadRadiusM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mMovementTimerS;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mJumpTimeS;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mModelScale;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private String mUniqueId;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mBaseDefense;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mAttackTimerS;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mWeightStdDev;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mCylHeightM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mCandyToEvolve;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mCollisionHeightM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mShoulderModeScale;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mBaseCaptureRate;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonId mParentId;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private double mCylGroundM;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonMove[] mQuickMoves;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private PokemonMove[] mCinematicMoves;
	@Getter
	@Setter(AccessLevel.PACKAGE)
	private int mNumber;

	PokemonMeta() {
	}
}
