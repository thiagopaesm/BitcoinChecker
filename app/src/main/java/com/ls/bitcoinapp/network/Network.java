package com.ls.bitcoinapp.network;

import com.ls.bitcoinapp.model.CoinModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 60;
    private static OkHttpClient okHttpClient;
    private static CoinApi coinApi;
    public static Network network;

    private Network() {
        getClient();
        createApiClient();
    }

    public static void initNetwork() {
        if (network == null) {
            network = new Network();
        }
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.binance.com/api/v3/ticker/")
                    .client(httpClient.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private void createApiClient() {
        if (coinApi == null) {
            coinApi = retrofit.create(CoinApi.class);
        }
    }

    public Single<CoinModel> getCoin(String coin) {
        return coinApi.getCoinPriceDetail(coin);
    }


}
