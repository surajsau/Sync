package com.halfplatepoha.frnds.network;

import com.halfplatepoha.frnds.FrndsLog;

import rx.Subscriber;

/**
 * Created by surajkumarsau on 25/08/16.
 */
public abstract class BaseSubscriber<Res> extends Subscriber<Res> {
    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        FrndsLog.d(e.getMessage());
    }

    @Override
    public void onNext(Res res) {
        onObjectReceived(res);
    }

    public abstract void onObjectReceived(Res res);
}
