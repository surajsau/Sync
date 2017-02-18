package com.halfplatepoha.frnds.network;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.IConstants;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by surajkumarsau on 15/09/16.
 */
public class Pagination {

    private static final int EMPTY_LIST_ITEM_COUNT = 0;
    private static final int DEFAULT_REQUEST_LIMIT = 10;
    private static final int MAX_ATTEMPTS_TO_RETRY_LOADING = 5;

    public static <T> Observable<List<T>> paging(RecyclerView rv, PaginationListener<T> paginationListener) {
        return paging(rv, paginationListener, DEFAULT_REQUEST_LIMIT, EMPTY_LIST_ITEM_COUNT, MAX_ATTEMPTS_TO_RETRY_LOADING);
    }

    public static <T> Observable<List<T>> paging(RecyclerView rv, final PaginationListener<T> listener, int limit, int emptyListCount, final int retryCount) {
        final int startNumberForRetryAttempt = 0;
        return getScrollObservable(rv, limit, emptyListCount)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .distinctUntilChanged()
                .switchMap(new Func1<Integer, Observable<? extends List<T>>>() {
                    @Override
                    public Observable<? extends List<T>> call(Integer offset) {
                        return getPaginationObservable(listener, listener.onNextPage(offset), startNumberForRetryAttempt, offset, retryCount);
                    }
                });
    }

    private static Observable<Integer> getScrollObservable(final RecyclerView rv, final int limit, final int emptyListCount) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(final Subscriber<? super Integer> subscriber) {
                final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if(!subscriber.isUnsubscribed()) {
                            int position = getLastVisibleItemPosition(recyclerView) + 1;
                            int updatePosition = recyclerView.getAdapter().getItemCount();
                            FrndsLog.e("position: " + position + ", updatePosition: " + updatePosition);
                            if(position >= updatePosition)
                                subscriber.onNext(recyclerView.getAdapter().getItemCount());
                        }
                    }
                };
                rv.addOnScrollListener(scrollListener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override
                    public void call() {
                        rv.removeOnScrollListener(scrollListener);
                    }
                }));

                if(rv.getAdapter().getItemCount() == emptyListCount)
                    subscriber.onNext(rv.getAdapter().getItemCount());
            }
        });
    }

    private static int getLastVisibleItemPosition(RecyclerView rv) {
        LinearLayoutManager layoutManager = (LinearLayoutManager)rv.getLayoutManager();
        return layoutManager.findLastVisibleItemPosition();
    }

    private static <T> Observable<List<T>> getPaginationObservable(final PaginationListener<T> listener, Observable<List<T>> observable, final int numberOfAttemptToRetry,
                                                                   final int offset, final int retryCount) {
        return observable.onErrorResumeNext(new Func1<Throwable, Observable<? extends List<T>>>() {
            @Override
            public Observable<? extends List<T>> call(Throwable throwable) {
                if(numberOfAttemptToRetry < retryCount) {
                    int attempToRetry = numberOfAttemptToRetry + 1;
                    return getPaginationObservable(listener, listener.onNextPage(offset), attempToRetry, offset, retryCount);
                }
                return Observable.empty();
            }
        });
    }
}
