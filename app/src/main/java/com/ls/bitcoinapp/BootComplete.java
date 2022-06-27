package com.ls.bitcoinapp;

import static com.ls.bitcoinapp.Constant.ID_ALARM_BIT_COIN;
import static com.ls.bitcoinapp.Constant.ID_ALARM_XMR;
import static com.ls.bitcoinapp.Constant.MINUTE_TEST;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ls.bitcoinapp.model.CoinForRealm;
import com.ls.bitcoinapp.model.CoinModel;
import com.ls.bitcoinapp.model.TypeCoin;
import com.ls.bitcoinapp.realm.RealmSingleObservable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RealmSingleObservable.create(realm -> {
            List<CoinForRealm> list = realm.where(CoinForRealm.class).findAll();
            ArrayList<CoinModel> lis = new ArrayList<>();
            for (CoinForRealm c : list) {
                lis.add(new CoinModel(c.getId(),
                        c.getSymbol(),
                        c.getPrice(),
                        TypeCoin.valueOf(c.getType()),
                        c.getPercent(),
                        c.isAlarm()));
            }
            return lis;
        }).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new DisposableSingleObserver<ArrayList<CoinModel>>() {
            @Override
            public void onSuccess(@NonNull ArrayList<CoinModel> coinModels) {
                for (CoinModel c : coinModels) {
                    if (c.isAlarm()) {
                        int id = c.getTypeCoin() == TypeCoin.BTCUSDT ? ID_ALARM_BIT_COIN : ID_ALARM_XMR;
                        Constant.startAlarm(id, context);
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
        Log.e("TAG", "Boot complete");
    }
}
