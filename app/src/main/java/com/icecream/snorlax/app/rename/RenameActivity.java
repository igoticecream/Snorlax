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

package com.icecream.snorlax.app.rename;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;

import com.icecream.snorlax.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class RenameActivity extends AppCompatActivity {

	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	private Unbinder mUnbinder;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rename_activity);
		mUnbinder = ButterKnife.bind(this);

		setupToolbar();
		setupOptions();
	}

	private void setupToolbar() {
		setSupportActionBar(mToolbar);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	private void setupOptions() {
		RenameOptions[] options = new RenameOptions[]{
			RenameOptions.create("IV"),
			RenameOptions.create("ATT"),
			RenameOptions.create("DEF"),
			RenameOptions.create("STA")
		};

		ArrayAdapter<RenameOptions> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
	}
}
