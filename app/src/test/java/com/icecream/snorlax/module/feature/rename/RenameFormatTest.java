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
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class RenameFormatTest {

	private static final String POKEMON_NAME = "Snorlax";
	private static final float POKEMON_LEVEL = 22.5f;

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

	@Test
	public void testNicknameCompleteFormat() throws Exception {
		mExpected = POKEMON_NAME;
		setRenameFormat("%NICK%");
	}

	@Test
	public void testNicknameIncompleteFormat() throws Exception {
		mExpected = "%NICK";
		setRenameFormat("%NICK");
	}

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

	@Test
	public void testNicknameFormatWithPlainText() throws Exception {
		setRenameFormat("Plain Text %NICK%");
		mExpected = "Plain Text " + POKEMON_NAME;
	}

	@Test
	public void testNicknameFormatWithSpaces() throws Exception {
		setRenameFormat("%NICK %NICK%");
		mExpected = "%NICK " + POKEMON_NAME;
	}
}