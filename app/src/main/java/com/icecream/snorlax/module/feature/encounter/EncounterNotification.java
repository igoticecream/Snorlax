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

package com.icecream.snorlax.module.feature.encounter;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;

import com.icecream.snorlax.R;
import com.icecream.snorlax.common.Helper;
import com.icecream.snorlax.common.Strings;
import com.icecream.snorlax.module.context.pokemongo.PokemonGo;
import com.icecream.snorlax.module.context.snorlax.Snorlax;

@Singleton
final class EncounterNotification {

	private final Context mContext;
	private final Resources mResources;
	private final NotificationManager mNotificationManager;

	@Inject
	EncounterNotification(@Snorlax Context context, @Snorlax Resources resources, @PokemonGo NotificationManager notificationManager) {
		mContext = context;
		mResources = resources;
		mNotificationManager = notificationManager;
	}

	@SuppressWarnings("deprecation")
	void show(int pokemonNumber, String pokemonName, double iv, int attack, int defense, int stamina, int cp, double level, int hp, String move1, String move2, double pokeRate, double pokeBerryRate, double greatRate, double greatBerryRate, double ultraRate, double ultraBerryRate) {
		new Handler(Looper.getMainLooper()).post(() -> {

			Notification notification = new NotificationCompat.Builder(mContext)
				.setSmallIcon(R.drawable.ic_pokeball)
				.setLargeIcon(Bitmap.createScaledBitmap(
					BitmapFactory.decodeResource(
						mResources,
						getPokemonResourceId(pokemonNumber)
					),
					getLargeIconWidth(),
					getLargeIconHeight(),
					false
				))
				.setContentTitle(mContext.getString(R.string.notification_title, Helper.getPokemonName(pokemonNumber, mResources), cp, hp, level))
				.setContentText(mContext.getString(R.string.notification_content, iv, attack, defense, stamina))
				.setStyle(new NotificationCompat.InboxStyle()
					.addLine(mContext.getString(R.string.notification_categoty_stats_content, iv, attack, defense, stamina))
					.addLine(getBoldSpannable(mContext.getString(R.string.notification_categoty_moves_title)))
					.addLine(mContext.getString(R.string.notification_categoty_moves_content, move1, move2))
					.addLine(getBoldSpannable(mContext.getString(R.string.notification_categoty_catch_title)))
					.addLine(mContext.getString(R.string.notification_categoty_catch_content_pokeball, pokeRate, pokeBerryRate))
					.addLine(mContext.getString(R.string.notification_categoty_catch_content_greatball, greatRate, greatBerryRate))
					.addLine(mContext.getString(R.string.notification_categoty_catch_content_ultraball, ultraRate, ultraBerryRate))
				)
				.setColor(ContextCompat.getColor(mContext, R.color.red_700))
				.setAutoCancel(true)
				//.setVibrate(new long[]{0, 60, 300, 60})
				.setVibrate(new long[]{0})
				.setPriority(Notification.PRIORITY_MAX)
				.setCategory(NotificationCompat.CATEGORY_ALARM)
				.build();

			hideIcon(notification);

			mNotificationManager.notify(1000, notification);
		});
	}

	@DrawableRes
	private int getPokemonResourceId(int pokemonNumber) {
		return mResources.getIdentifier("pokemon_" + Strings.padStart(String.valueOf(pokemonNumber), 3, '0'), "drawable", mContext.getPackageName());
	}

	private int getLargeIconWidth() {
		return mResources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
	}

	private int getLargeIconHeight() {
		return mResources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
	}

	private Spannable getBoldSpannable(String text) {
		Spannable spannable = new SpannableString(text);
		spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, spannable.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return spannable;
	}

	@SuppressWarnings("deprecation")
	private void hideIcon(Notification notification) {
		int iconId = mResources.getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
		if (iconId != 0) {
			if (notification.contentView != null) {
				notification.contentView.setViewVisibility(iconId, View.INVISIBLE);
			}
			if (notification.bigContentView != null) {
				notification.bigContentView.setViewVisibility(iconId, View.INVISIBLE);
			}
		}
	}
}
