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

package com.icecream.snorlax.module.feature.rename;

import com.icecream.snorlax.module.Pokemons;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static POGOProtos.Data.PokemonDataOuterClass.PokemonData;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
	Pokemons.class,
	PokemonData.class,
	RenamePreferences.class
})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RenameFormatTest {

	private static final String POKEMON_NAME = "Snorlax";
	private static final float POKEMON_LEVEL = 8.5f;
	private static final int POKEMON_ATTACK = 1;
	private static final int POKEMON_DEFENSE = 1;
	private static final int POKEMON_STAMINA = 1;
	// A/D/S -> 0.0666666666666667

	@Mock
	private Pokemons mPokemons;
	@Mock
	private Pokemons.Data mPokemonsData;
	@Mock
	private RenamePreferences mRenamePreferences;
	@Mock
	private PokemonData mProto;

	@InjectMocks
	private RenameFormat mSut;

	private String mExpected;

	private void setRenameFormat(String format) {
		Mockito
			.doReturn(format)
			.when(mRenamePreferences)
			.getFormat();
	}

	@Before
	public void setUp() throws Exception {
		// Given
		Mockito.doReturn(mPokemonsData).when(mPokemons).with(mProto);
		Mockito.doReturn(POKEMON_NAME).when(mPokemonsData).getName();
		Mockito.doReturn(POKEMON_LEVEL).when(mPokemonsData).getLevel();
		Mockito.doReturn(POKEMON_ATTACK).when(mPokemonsData).getAttack();
		Mockito.doReturn(POKEMON_DEFENSE).when(mPokemonsData).getDefense();
		Mockito.doReturn(POKEMON_STAMINA).when(mPokemonsData).getStamina();
		Mockito.doCallRealMethod().when(mPokemonsData).getIvRatio();
	}

	@After
	public void tearDown() throws Exception {
		// When
		final String formatted = mSut.format(mProto);

		// Then
		Mockito.verify(mPokemons).with(mProto);
		Mockito.verify(mRenamePreferences).getFormat();
		Mockito.verifyNoMoreInteractions(mPokemons, mRenamePreferences);

		MatcherAssert.assertThat(formatted, Matchers.is(mExpected));
	}

	//region Command processing
	@Test
	public void testCommandCompleteFormat() throws Exception {
		mExpected = POKEMON_NAME;
		setRenameFormat("%NICK%");
	}

	@Test
	public void testCommandIncompleteFormat() throws Exception {
		mExpected = "%NICK";
		setRenameFormat("%NICK");
	}

	@Test
	public void testCommandFormatWithPlainText() throws Exception {
		setRenameFormat("Plain Text %NICK%");
		mExpected = "Plain Text " + POKEMON_NAME;
	}

	@Test
	public void testCommandFormatWithSpaces() throws Exception {
		setRenameFormat("%NICK %NICK%");
		mExpected = "%NICK " + POKEMON_NAME;
	}

	@Test
	public void testNotCommand() throws Exception {
		mExpected = "%SNORLAX%";
		setRenameFormat("%SNORLAX%");
	}
	//endregion

	//region Nick
	@Test
	public void testNicknameTruncateBelowLength() throws Exception {
		mExpected = POKEMON_NAME.substring(0, 3);
		setRenameFormat("%NICK.3%");
	}

	@Test
	public void testNicknameTruncateExactLength() throws Exception {
		mExpected = POKEMON_NAME;
		setRenameFormat("%NICK.7%");
	}

	@Test
	public void testNicknameTruncateAboveLength() throws Exception {
		mExpected= POKEMON_NAME;
		setRenameFormat("%NICK.30%");
	}

	@Test
	public void testNicknameTruncateIncompleteFormat() throws Exception {
		mExpected = "%NICK.%";
		setRenameFormat("%NICK.%");
	}

	@Test
	public void testNicknameTruncateWrongFormat() throws Exception {
		mExpected = "%NICK.1a%";
		setRenameFormat("%NICK.1a%");
	}
	//endregion

	//region Level
	@Test
	public void testLevel() throws Exception {
		mExpected = "8.5";
		setRenameFormat("%LVL%");
	}

	@Test
	public void testLevelNoDecimal() throws Exception {
		mExpected = "9";
		setRenameFormat("%LVL.0%");
	}

	@Test
	public void testLevelOneDecimal() throws Exception {
		mExpected = "8.5";
		setRenameFormat("%LVL.1%");
	}

	@Test
	public void testLevelMoreDecimal() throws Exception {
		mExpected = "8.500";
		setRenameFormat("%LVL.3%");
	}

	@Test
	public void testLevelWrongDecimal() throws Exception {
		mExpected = "%LVL.A%";
		setRenameFormat("%LVL.A%");
	}

	@Test
	public void testLevelWithPadding() throws Exception {
		mExpected = "08.5";
		setRenameFormat("%LVLP%");
	}

	@Test
	public void testLevelWithPaddingNoDecimal() throws Exception {
		mExpected = "09";
		setRenameFormat("%LVLP.0%");
	}

	@Test
	public void testLevelWithPaddingOneDecimal() throws Exception {
		mExpected = "08.5";
		setRenameFormat("%LVLP.1%");
	}

	@Test
	public void testLevelWithPaddingMoreDecimal() throws Exception {
		mExpected = "08.500";
		setRenameFormat("%LVLP.3%");
	}

	@Test
	public void testLevelWithPaddingWrongDecimal() throws Exception {
		mExpected = "%LVLP.A%";
		setRenameFormat("%LVLP.A%");
	}
	//endregion

	//region Iv
	@Test
	public void testIv() throws Exception {
		mExpected = "6.7";
		setRenameFormat("%IV%");
	}

	@Test
	public void testIvNoDecimal() throws Exception {
		mExpected = "7";
		setRenameFormat("%IV.0%");
	}

	@Test
	public void testIvOneDecimal() throws Exception {
		mExpected = "6.7";
		setRenameFormat("%IV.1%");
	}

	@Test
	public void testIvMoreDecimal() throws Exception {
		mExpected = "6.667";
		setRenameFormat("%IV.3%");
	}

	@Test
	public void testIvWrongDecimal() throws Exception {
		mExpected = "%IV.A%";
		setRenameFormat("%IV.A%");
	}

	@Test
	public void testIvWithPadding() throws Exception {
		mExpected = "006.7";
		setRenameFormat("%IVP%");
	}

	@Test
	public void testIvWithPaddingNoDecimal() throws Exception {
		mExpected = "007";
		setRenameFormat("%IVP.0%");
	}

	@Test
	public void testIvWithPaddingOneDecimal() throws Exception {
		mExpected = "006.7";
		setRenameFormat("%IVP.1%");
	}

	@Test
	public void testIvWithPaddingMoreDecimal() throws Exception {
		mExpected = "006.667";
		setRenameFormat("%IVP.3%");
	}

	@Test
	public void testIvWithPaddingWrongDecimal() throws Exception {
		mExpected = "%IVP.A%";
		setRenameFormat("%IVP.A%");
	}
	//endregion

	//region Attack
	@Test
	public void testAttack() throws Exception {
		mExpected = "1";
		setRenameFormat("%ATT%");
	}

	@Test
	public void testAttackTwoDigits() throws Exception {
		Mockito.doReturn(10).when(mPokemonsData).getAttack();

		mExpected = "10";
		setRenameFormat("%ATT%");
	}

	@Test
	public void testAttackWithPadding() throws Exception {
		mExpected = "01";
		setRenameFormat("%ATTP%");
	}

	@Test
	public void testAttackHex() throws Exception {
		Mockito.doReturn(15).when(mPokemonsData).getAttack();

		mExpected = "F";
		setRenameFormat("%ATTH%");
	}
	//endregion

	//region Defense
	@Test
	public void testDefense() throws Exception {
		mExpected = "1";
		setRenameFormat("%DEF%");
	}

	@Test
	public void testDefenseTwoDigits() throws Exception {
		Mockito.doReturn(10).when(mPokemonsData).getDefense();

		mExpected = "10";
		setRenameFormat("%DEF%");
	}

	@Test
	public void testDefenseWithPadding() throws Exception {
		mExpected = "01";
		setRenameFormat("%DEFP%");
	}

	@Test
	public void testDefenseHex() throws Exception {
		Mockito.doReturn(15).when(mPokemonsData).getDefense();

		mExpected = "F";
		setRenameFormat("%DEFH%");
	}
	//endregion

	//region Stamina
	@Test
	public void testStamina() throws Exception {
		mExpected = "1";
		setRenameFormat("%STA%");
	}

	@Test
	public void testStaminaTwoDigits() throws Exception {
		Mockito.doReturn(10).when(mPokemonsData).getStamina();

		mExpected = "10";
		setRenameFormat("%STA%");
	}

	@Test
	public void testStaminaWithPadding() throws Exception {
		mExpected = "01";
		setRenameFormat("%STAP%");
	}

	@Test
	public void testStaminaHex() throws Exception {
		Mockito.doReturn(15).when(mPokemonsData).getStamina();

		mExpected = "F";
		setRenameFormat("%STAH%");
	}
	//endregion
}
