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

package com.icecream.snorlax.module.feature.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.ContextWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.icecream.snorlax.R;
import com.icecream.snorlax.module.context.snorlax.Snorlax;
import com.icecream.snorlax.module.feature.Feature;
import com.icecream.snorlax.module.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@Singleton
final class Ui implements Feature {

	private final ClassLoader mClassLoader;
	private final LayoutInflater mLayoutInflater;

	private XC_MethodHook.Unhook mUnhookUnity;

	@Inject
	Ui(ClassLoader classLoader, @Snorlax LayoutInflater layoutInflater) {
		mClassLoader = classLoader;
		mLayoutInflater = layoutInflater;
	}

	@Override
	public void subscribe() throws Exception {
		final Class<?> unity = XposedHelpers.findClass("com.unity3d.player.UnityPlayer", mClassLoader);
		if (unity == null) {
			Log.e("Cannot find UnityPlayer class");
			return;
		}

		mUnhookUnity = XposedHelpers.findAndHookConstructor(unity, ContextWrapper.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				final FrameLayout frameLayout = (FrameLayout) param.thisObject;

				FrameLayout ui = (FrameLayout) mLayoutInflater.inflate(R.layout.ui_feature, frameLayout, false);

				Log.d("Class %s", ui.findViewById(R.id.fab).getClass().getName());

				View fab = ui.findViewById(R.id.fab);
				if (fab != null) {
					fab.setOnClickListener(v -> Toast.makeText(v.getContext(), "By igoticecream", Toast.LENGTH_LONG).show());
					fab.setOnLongClickListener(v -> {
						frameLayout.removeView(ui);
						return true;
					});
				}

				frameLayout.addView(ui);
				/*
				TextView textView = new TextView(frameLayout.getContext());
				textView.setText("HOLAAAA!!!!");
				textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0f);

				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
				);
				params.gravity = Gravity.CENTER;

				frameLayout.addView(textView, params);
				*/
			}
		});
	}

	@Override
	public void unsubscribe() throws Exception {
		if (mUnhookUnity != null) {
			mUnhookUnity.unhook();
		}
	}
}
