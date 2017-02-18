package com.halfplatepoha.frnds.network;

import com.halfplatepoha.frnds.IConstants;

import rx.Observable;
import rx.Subscriber;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * Created by surajkumarsau on 15/09/16.
 */
public class PaginationSubscriber<Res> extends Subscriber<Res> {

    private final Subject<Void, Void> nextPageTrigger = PublishSubject.create();
    private final Subscriber<Res> mDelegate;
    private int count = 0;

    public PaginationSubscriber(BaseSubscriber<Res> delegate) {
        mDelegate = delegate;
    }

    public Observable<Void> getNextPageTrigger() {
        return nextPageTrigger;
    }

    @Override
    public void onCompleted() {
        mDelegate.onCompleted();
    }

    @Override
    public void onError(Throwable e) {
        mDelegate.onError(e);
    }

    @Override
    public void onNext(Res res) {
        count += 1;
        if(count == IConstants.PAGE_SIZE){
            nextPageTrigger.onNext(null);
            count = 0;
        }
        mDelegate.onNext(res);
    }
}
