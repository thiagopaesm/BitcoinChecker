package com.ls.bitcoinapp.network;

import com.ls.bitcoinapp.model.CoinModel;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CoinApi {
    @GET("price")
    Single<CoinModel> getCoinPriceDetail(@Query("symbol") String nameCoin);
}
