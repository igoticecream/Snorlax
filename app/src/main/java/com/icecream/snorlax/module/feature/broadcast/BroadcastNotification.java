package com.icecream.snorlax.module.feature.broadcast;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.icecream.snorlax.module.context.pokemongo.PokemonGo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
final class BroadcastNotification {

    private final Context mContext;

    @Inject
    BroadcastNotification(@PokemonGo Context context) {
        mContext = context;
    }

    void show(final String message) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show());
    }

    void send(final Intent intent) {
        mContext.sendBroadcast(intent);
    }
}
