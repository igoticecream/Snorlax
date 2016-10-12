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

package com.icecream.snorlax.app.home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.icecream.snorlax.BuildConfig;
import com.icecream.snorlax.R;

import rx.Observable;

@SuppressWarnings({"unused", "FieldCanBeLocal", "WeakerAccess"})
final class HomeDialog {

	@SuppressLint("InflateParams")
	static AlertDialog showDonation(Context context) {
		return new AlertDialog.Builder(context, R.style.Snorlax_Dialog)
			.setTitle(R.string.donation)
			.setView(LayoutInflater.from(context).inflate(R.layout.donation_dialog, null, false))
			.setPositiveButton("Paypal", (dialog, which) -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=A9PPGNDJEC33E"))))
			.setCancelable(true)
			.show();
	}

	@SuppressLint("InflateParams")
	static AlertDialog showAbout(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.about_dialog, null, false);

		TextView version = (TextView) view.findViewById(R.id.version);
		version.setText(context.getString(R.string.about_version, BuildConfig.VERSION_NAME));

		TextView author = (TextView) view.findViewById(R.id.author);
		author.setText(context.getString(R.string.about_author));

		TextView github = (TextView) view.findViewById(R.id.github);
		github.setText(context.getString(R.string.about_github));

		TextView thanks = (TextView) view.findViewById(R.id.thanks);
		thanks.setText(context.getString(R.string.about_thanks));

		return new AlertDialog.Builder(context, R.style.Snorlax_Dialog)
			.setTitle(R.string.app_name)
			.setView(view)
			.setPositiveButton("Github", (dialog, which) -> context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.about_repository)))))
			.setCancelable(true)
			.show();
	}

	@SuppressLint("InflateParams")
	static AlertDialog showFormatInfo(Context context) {
		return new AlertDialog.Builder(context)
			.setTitle(R.string.format_info)
			.setView(LayoutInflater.from(context).inflate(R.layout.format_dialog, null, false))
			.setPositiveButton(android.R.string.ok, null)
			.setCancelable(true)
			.show();
	}

	static void dismiss(AlertDialog... dialogs) {
		Observable
			.from(dialogs)
			.filter(dialog -> dialog != null)
			.filter(Dialog::isShowing)
			.forEach(Dialog::dismiss);
	}

	HomeDialog() {
		throw new AssertionError("No instances");
	}
}
