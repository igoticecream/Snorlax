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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.tokenautocomplete.TokenCompleteTextView;

@SuppressWarnings({"unused", "WeakerAccess", "FieldCanBeLocal"})
public class RenameCompletionView extends TokenCompleteTextView<RenameOptions> {

	private final LayoutInflater mLayoutInflater;

	public RenameCompletionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	protected View getViewForObject(RenameOptions person) {
		/*
		TextView view = (TextView) mLayoutInflater.inflate(R.layout.contact_token, (ViewGroup) getParent(), false);
		view.setText(person.getOption());
		return view;
		*/
		return null;
	}

	@Override
	protected RenameOptions defaultObject(String completionText) {
		//Stupid simple example of guessing if we have an email or not
		/*
		int index = completionText.indexOf('@');

		if (index == -1) {
			return new Person(completionText, completionText.replace(" ", "") + "@example.com");
		}
		else {
			return new Person(completionText.substring(0, index), completionText);
		}
		*/
		return RenameOptions.create("Dummy");
	}
}
