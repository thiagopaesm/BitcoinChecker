package com.ls.bitcoinapp.realm;

import io.reactivex.Completable;
import io.realm.Realm;

public class RealmCompletableObservable {
    public interface OnConsumerListener {
        void onConsumer(Realm realm);
    }

    public static Completable create(OnConsumerListener listener) {
        return Completable.create(new CompletableOnSubscribeRealm() {
            @Override
            void run(Realm realm) {
                listener.onConsumer(realm);
            }
        });
    }
}
