package com.ls.bitcoinapp.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class CoinForRealm extends RealmObject {
    @Required
    @PrimaryKey
    private String id;
    private String price;
    private String priceShow;
    private String type;
    private String symbol;
    private String percent;
    private boolean isAlarm;


    public String getPriceShow() {
        return priceShow;
    }

    public void setPriceShow(String priceShow) {
        this.priceShow = priceShow;
    }

    public boolean isAlarm() {
        return isAlarm;
    }

    public void setAlarm(boolean alarm) {
        isAlarm = alarm;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CoinForRealm(String id, String price, String priceShow, String type, String symbol, String percnet, boolean isAlarm) {
        this.id = id;
        this.percent = percnet;
        this.price = price;
        this.priceShow = priceShow;
        this.type = type;
        this.symbol = symbol;
        this.isAlarm = isAlarm;
    }
    public CoinForRealm() {
    }
}
