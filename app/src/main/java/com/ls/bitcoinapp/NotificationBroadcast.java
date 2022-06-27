package com.ls.bitcoinapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ls.bitcoinapp.model.CoinForRealm;
import com.ls.bitcoinapp.model.CoinModel;
import com.ls.bitcoinapp.model.TypeCoin;
import com.ls.bitcoinapp.network.Network;
import com.ls.bitcoinapp.realm.RealmCompletableObservable;
import com.ls.bitcoinapp.realm.RealmSingleObservable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        updateData(context);
    }

    private void updateData(Context context) {
        Single.zip(
                Network.network.getCoin("BTCUSDT"),
                Network.network.getCoin("XMRBTC"),
                getListCoinRealm(),
                (coinModel, coinModel2, coinModels) -> {
                    coinModel.setTypeCoin(TypeCoin.BTCUSDT);
                    coinModel2.setTypeCoin(TypeCoin.XMRBTC);
                    ArrayList<CoinModel> list = new ArrayList<>();
                    if (coinModels.isEmpty()) {
                        coinModel.setId(UUID.randomUUID().toString());
                        coinModel2.setId(UUID.randomUUID().toString());
                        coinModel.setAlarm(false);
                        coinModel2.setAlarm(false);
                        list.add(coinModel);
                        list.add(coinModel2);
                    } else {
                        for (CoinModel c : coinModels) {
                            if (c.getTypeCoin() == coinModel.getTypeCoin()) {
                                double priceNew = Double.parseDouble(coinModel.getPrice());
                                double priceOld = Double.parseDouble(c.getPrice());
                                double percent = (priceNew - priceOld) / 100.0;
                                c.setPercent(String.valueOf(percent));
                            } else if (c.getTypeCoin() == coinModel2.getTypeCoin()) {
                                double priceNew = Double.parseDouble(coinModel.getPrice());
                                double priceOld = Double.parseDouble(c.getPrice());
                                double percent = (priceNew - priceOld) / 100.0;
                                c.setPercent(String.valueOf(percent));
                            }
                        }
                        list.addAll(coinModels);
                    }
                    return list;
                }
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ArrayList<CoinModel>>() {
                    @Override
                    public void onSuccess(@NonNull ArrayList<CoinModel> coinModels) {
                        RealmCompletableObservable.create(new RealmCompletableObservable.OnConsumerListener() {
                            @Override
                            public void onConsumer(Realm realm) {
                                ArrayList<CoinForRealm> list = new ArrayList<>();
                                for (CoinModel c : coinModels) {
                                    list.add(new CoinForRealm(c.getId(),
                                            c.getPrice(),
                                            c.getTypeCoin().toString(),
                                            c.getSymbol(),
                                            c.getPercent(),
                                            c.isAlarm()));
                                }
                                realm.copyToRealmOrUpdate(list);
                            }
                        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        getListCoinRealm().subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new DisposableSingleObserver<ArrayList<CoinModel>>() {
                                                    @Override
                                                    public void onSuccess(@NonNull ArrayList<CoinModel> coinModels) {
                                                        // Update UI
                                                        String price = "";
                                                        for (CoinModel c: coinModels) {
                                                            if (c.getTypeCoin() == TypeCoin.BTCUSDT) {
                                                                price = c.getPrice();
                                                                break;
                                                            }
                                                        }
                                                        createNotifyTempSave(context, price);
                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        Log.e("TAG", "get list error");
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.e("TAG", "insert error");
                                    }
                                });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("TAG", "Zip error");
                    }
                });
    }

    private Single<ArrayList<CoinModel>> getListCoinRealm() {
        return RealmSingleObservable.create(realm -> {
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
        });
    }

    private void createNotifyTempSave(Context context, String price) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String CHANNEL_ID = "Coin"; // The id of the channel.
            String name = "Notification";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            Notification notification =
                    new Notification.Builder(context)
                            .setContentIntent(contentIntent)
                            .setContentText("BTC : " + price)
                            .setChannelId(CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setAutoCancel(true)
                            .build();
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(1, notification);
        } else {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
            notification
                    .setContentIntent(contentIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentText("BTC")
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification.build());
        }
    }
}