package com.ls.bitcoinapp;

import static com.ls.bitcoinapp.Constant.ID_ALARM_BIT_COIN;
import static com.ls.bitcoinapp.Constant.ID_ALARM_XMR;
import static com.ls.bitcoinapp.Constant.MINUTE_TEST;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ls.bitcoinapp.adapter.AdapterCoin;
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

public class MainActivity extends AppCompatActivity implements AdapterCoin.OnSwitchAlarmListener {
    private RecyclerView recyclerView;
    private AdapterCoin adapterCoin;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcv);
        adapterCoin = new AdapterCoin(this);
        recyclerView.setAdapter(adapterCoin);
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(runnable, MINUTE_TEST);
        Constant.startAlarm(ID_ALARM_BIT_COIN, this);
        updateData();
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateData();
            handler.postDelayed(this, MINUTE_TEST);
        }
    };

    private void updateData() {
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
                                float percent = (float) (((priceNew - priceOld) / priceOld) * 100);
                                c.setPercent(String.valueOf(percent));
                            } else if (c.getTypeCoin() == coinModel2.getTypeCoin()) {
                                double priceNew = Double.parseDouble(coinModel.getPrice());
                                double priceOld = Double.parseDouble(c.getPrice());
                                float percent = (float) (((priceNew - priceOld) / priceOld) * 100);
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
//                                                        createNotifyTempSave(MainActivity.this);
                                                        adapterCoin.setList(coinModels);
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

    private void createNotifyTempSave(Context context) {
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
                            .setContentText("BTC")
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

    @Override
    public void onSwitchAlarmListener(CoinModel coinModel) {
        boolean isAlarm = coinModel.isAlarm();
        coinModel.setAlarm(!isAlarm);

        RealmCompletableObservable.create(realm -> realm.insertOrUpdate(new CoinForRealm(coinModel.getId(),
                coinModel.getPrice(),
                coinModel.getSymbol(),
                coinModel.getTypeCoin().toString(),
                coinModel.getPercent(),
                coinModel.isAlarm()))).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                int id = coinModel.getTypeCoin() == TypeCoin.BTCUSDT ? ID_ALARM_BIT_COIN : ID_ALARM_XMR;
                if (coinModel.isAlarm()) {
                    Constant.startAlarm(id, MainActivity.this);
                } else {
                    Constant.cancelAlarm(id, MainActivity.this);
                }
                adapterCoin.notifyDataSetChanged();
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });

    }
}