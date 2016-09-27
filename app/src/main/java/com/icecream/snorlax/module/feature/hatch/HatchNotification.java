
package com.icecream.snorlax.module.feature.hatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v7.app.NotificationCompat;
import android.view.View;

import com.icecream.snorlax.R;
import com.icecream.snorlax.module.context.pokemongo.PokemonGo;
import com.icecream.snorlax.module.context.snorlax.Snorlax;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import POGOProtos.Data.PokemonDataOuterClass;

import static com.icecream.snorlax.common.Helper.calcLevel;
import static com.icecream.snorlax.common.Helper.calcPotential;
import static com.icecream.snorlax.common.Helper.getPokemonName;

@Singleton
final class HatchNotification {

    private final Context mContext;
    private final Resources mResources;
    private final NotificationManager mNotificationManager;

    @Inject
    HatchNotification(@Snorlax Context context, @Snorlax Resources resources, @PokemonGo NotificationManager notificationManager) {
        mContext = context;
        mResources = resources;
        mNotificationManager = notificationManager;
    }

    @SuppressWarnings("deprecation")
    void show(List<EggHatchRewards> hatchedEggsAwardedList, List<PokemonDataOuterClass.PokemonData> hatchedEggsAwardedListPokemonData) {
        new Handler(Looper.getMainLooper()).post(() -> {

            if (hatchedEggsAwardedList == null || hatchedEggsAwardedListPokemonData == null
                    || hatchedEggsAwardedList.size() != hatchedEggsAwardedListPokemonData.size())
                return;

            String title = "";
            String summary = "";
            final StringBuilder longText = new StringBuilder(512);
            int eggsHatched = 0;
            int experienceAwardedTotal = 0;
            int stardustAwardedTotal = 0;
            String lastHatchedPokemonName = "";

            for (int i = 0; i < hatchedEggsAwardedList.size(); i++) {
                int experienceAwarded = hatchedEggsAwardedList.get(i).getExperienceAwarded();
                int candyAwarded = hatchedEggsAwardedList.get(i).getCandyAwarded();
                int stardustAwarded = hatchedEggsAwardedList.get(i).getStardustAwarded();

                PokemonDataOuterClass.PokemonData pokemonData = hatchedEggsAwardedListPokemonData.get(i);

                eggsHatched++;
                experienceAwardedTotal += experienceAwarded;
                stardustAwardedTotal += stardustAwarded;
                lastHatchedPokemonName = getPokemonName(pokemonData.getPokemonIdValue(), mResources);

                if (longText.length() > 0)
                    longText.append('\n');
                longText.append(lastHatchedPokemonName).append(" \uD83D\uDC23 ");
                longText.append(experienceAwarded).append(" XP | ");
                longText.append(candyAwarded).append(" Cd. | ");
                longText.append(stardustAwarded).append(" SD.\n");
                longText.append("L. ").append(calcLevel(pokemonData));
                longText.append(" | IVs: ").append(calcPotential(pokemonData));
                longText.append("% | ").append(pokemonData.getIndividualAttack());
                longText.append("/").append(pokemonData.getIndividualDefense());
                longText.append("/").append(pokemonData.getIndividualStamina());
            }

            if (eggsHatched > 0) {
                if (eggsHatched > 1) {
                    title = "Eggs hatched: " + eggsHatched;
                } else {
                    title = "Egg hatched: " + lastHatchedPokemonName;
                }
                summary = eggsHatched + " egg" + (eggsHatched > 1 ? "s" : "") + " hatched, got " +
                        experienceAwardedTotal + " XP, " + stardustAwardedTotal + " Stardust";
            }


            Notification notification = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.ic_pokeball)
                    .setLargeIcon(Bitmap.createScaledBitmap(
                            BitmapFactory.decodeResource(
                                    mResources,
                                    getEggResourceId()
                            ),
                            getLargeIconWidth(),
                            getLargeIconHeight(),
                            false
                    ))
                    .setContentTitle(title)
                    .setContentText(summary)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 60, 300, 60})
                    .setPriority(Notification.PRIORITY_MAX)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .build();

            hideIcon(notification);

            mNotificationManager.notify(1000, notification);
        });
    }

    @DrawableRes
    private int getEggResourceId() {
        return mResources.getIdentifier("egg", "drawable", mContext.getPackageName());
    }

    private int getLargeIconWidth() {
        return mResources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
    }

    private int getLargeIconHeight() {
        return mResources.getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
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
