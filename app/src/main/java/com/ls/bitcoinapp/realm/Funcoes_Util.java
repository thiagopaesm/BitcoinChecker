package com.ls.bitcoinapp.realm;

public class Funcoes_Util {

    public static double MathRound2decimal(double valor){
        valor = Math.round(valor * 100.0)/100.0;
        return valor;
    }
    public static float MathRound2decimal(float valor){
        double aux = valor;
        aux = Math.round(aux * 100.0)/100.0;
        return (float) aux;
    }
}
