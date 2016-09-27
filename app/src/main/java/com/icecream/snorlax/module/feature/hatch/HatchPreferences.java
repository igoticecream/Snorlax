package com.icecream.snorlax.module.feature.hatch;

import android.content.res.Resources;

import com.icecream.snorlax.R;
import com.icecream.snorlax.module.context.snorlax.Snorlax;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.robv.android.xposed.XSharedPreferences;
import rx.Observable;

@Singleton
final class HatchPreferences {

    private final Resources mResources;
    private final XSharedPreferences mPreferences;

    @Inject
    HatchPreferences(@Snorlax Resources resources, XSharedPreferences preferences) {
        mResources = resources;
        mPreferences = preferences;
    }

    <T> Observable.Transformer<T, T> isEnabled() {
        return observable -> observable
                .doOnNext(t -> mPreferences.reload())
                .filter(t -> {
                    final boolean excepted = getPreferenceDefaultValue();
                    return excepted == getPreference(excepted);
                });
    }

    private boolean getPreferenceDefaultValue() {
        return mResources.getBoolean(R.bool.preference_hatch_notification_enable);
    }

    private boolean getPreference(boolean defaultValue) {
        return mPreferences.getBoolean(mResources.getString(R.string.preference_hatch_notification_enable_key), defaultValue);
    }
}
