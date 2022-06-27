package com.ls.bitcoinapp.realm;

import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.realm.Realm;
import io.realm.exceptions.RealmException;

public abstract class SingleOnSubscribeRealm<T> implements SingleOnSubscribe<T> {
    @Override
    public void subscribe(SingleEmitter<T> emitter) throws Exception {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
        } catch (Exception e) {
            emitter.onError(e);
        }
        if (realm == null) {
//            emitter.onError(NullPointerException("Realm Object is null"))
            return;
        }
        realm.beginTransaction();
        try {
            T obj = get(realm);
            realm.commitTransaction();
            if (obj != null) {
                emitter.onSuccess(obj);
            }
            return;
        } catch (RuntimeException runE) {
            emitter.onError(new RealmException("Error during transaction.", runE));
            realm.cancelTransaction();
            return;
        } catch (Error e) {
            emitter.onError(e);
            try {
                realm.cancelTransaction();
            } catch (IllegalStateException ie) {
                emitter.onError(ie);
            }
            return;
        } finally {
            realm.close();
        }
    }

    abstract T get(Realm realm);
}
