package com.halfplatepoha.frnds;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class FrndsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(this);
        FrndsPreference.init(this, IConstants.PREFERNCE_FILE);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .name("sync.realm")
                .build();
        Realm.setDefaultConfiguration(config);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
