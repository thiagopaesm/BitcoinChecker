package com.ls.bitcoinapp.model;

public class CoinModel {
    private String id;
    private String symbol;
    private String price;
    private String priceShow;
    private TypeCoin typeCoin;
    String percent;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public TypeCoin getTypeCoin() {
        return typeCoin;
    }

    public void setTypeCoin(TypeCoin typeCoin) {
        this.typeCoin = typeCoin;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public CoinModel(String id, String symbol, String price, String priceShow, TypeCoin typeCode, String percent, boolean isAlarm) {
        this.id = id;
        this.percent = percent;
        this.symbol = symbol;
        this.price = price;
        this.priceShow = priceShow;
        this.typeCoin = typeCode;
        this.isAlarm = isAlarm;
    }
}
