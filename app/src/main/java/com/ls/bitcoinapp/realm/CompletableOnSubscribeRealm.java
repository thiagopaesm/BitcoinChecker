package com.ls.bitcoinapp.realm;

import android.util.Log;

import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.realm.Realm;
import io.realm.exceptions.RealmException;

public abstract class CompletableOnSubscribeRealm implements CompletableOnSubscribe {
    @Override
    public void subscribe(CompletableEmitter emitter) throws Exception {
        Realm realm;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            emitter.onError(e);
            return;
        }
        if (realm == null) {
            emitter.onError(new NullPointerException("Realm Object is null"));
            return;
        }
        realm.beginTransaction();
        try {
            run(realm);
            realm.commitTransaction();
            emitter.onComplete();
            return;
        } catch (RuntimeException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage(), e);
            emitter.onError(new RealmException("Error during transaction.", e));
            realm.cancelTransaction();
            return;
        } catch (Error e) {
            emitter.onError(e);
            try {
                realm.cancelTransaction();
            } catch (IllegalStateException ex) {
                emitter.onError(ex);
            }
            return;
        } finally {
            realm.close();
        }
    }

    abstract void run(Realm realm);
}
