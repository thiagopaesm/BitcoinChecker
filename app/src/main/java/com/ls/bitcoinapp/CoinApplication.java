package com.ls.bitcoinapp;

import android.app.Application;

import com.ls.bitcoinapp.network.Network;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CoinApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Network.initNetwork();
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("CoinDatabase")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
