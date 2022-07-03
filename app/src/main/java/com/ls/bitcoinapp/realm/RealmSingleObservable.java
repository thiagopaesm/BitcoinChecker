package com.ls.bitcoinapp.realm;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.realm.Realm;

public class RealmSingleObservable {
    public interface OnConsumerListener<T> {
        T onConsumer(Realm realm);
    }

    public static <T> Single<T> create(OnConsumerListener<T> listener) {
        return Single.create(new SingleOnSubscribeRealm<T>() {
            @Override
            T get(Realm realm) {
                return listener.onConsumer(realm);
            }

            @Override
            public void subscribe(SingleEmitter<T> emitter) throws Exception {
                super.subscribe(emitter);
            }
        });
    }
}
