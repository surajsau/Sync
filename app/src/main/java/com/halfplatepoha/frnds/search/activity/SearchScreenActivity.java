package com.halfplatepoha.frnds.search.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.models.response.SearchResponse;
import com.halfplatepoha.frnds.network.Pagination;
import com.halfplatepoha.frnds.network.PaginationListener;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.search.adapter.SearchResultAdapter;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchScreenActivity extends AppCompatActivity implements ValueEventListener,
        View.OnClickListener {

    private static final String TAG = SearchScreenActivity.class.getSimpleName();

    private SoundCloudClient mClient;

    private SearchResultAdapter mAdapter;

    private Subscription mPaginationSubscription;

    private SearchResultPagination pagination;

    @Bind(R.id.searchBar) ViewGroup searchBar;
    @Bind(R.id.etSearch) EditText etSearch;
    @Bind(R.id.rlSearchResult) RecyclerView rlSearchResult;
    @Bind(R.id.back) ImageButton back;
    @Bind(R.id.llPoweredBySoundcloud) View llPoweredBySoundCloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        ButterKnife.bind(this);

        mClient = new ClientGenerator.Builder()
                        .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                        .setLoggingInterceptor()
                        .setApiKeyInterceptor(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE)
                        .setClientClass(SoundCloudClient.class)
                        .buildClient();

        setupRecyclerView();

        setupSearchEditText();

        back.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        mAdapter = new SearchResultAdapter(this);
        rlSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rlSearchResult.setAdapter(mAdapter);
    }

    private void setupSearchEditText() {
        etSearch.clearFocus();

        RxTextView.textChanges(etSearch)
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return (charSequence != null && !TextUtils.isEmpty(charSequence.toString()));
                    }
                })
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(textWatcher);
    }

    private void callSearchApi(String s) {
        if(pagination == null)
            pagination = new SearchResultPagination();

        pagination.setSearchString(s);
        mPaginationSubscription = Pagination.paging(rlSearchResult, pagination)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new BaseSubscriber<List<TrackDetails>>() {
                    @Override
                    public void onObjectReceived(List<TrackDetails> trackDetails) {
                        if(trackDetails != null && !trackDetails.isEmpty()) {
                            rlSearchResult.setVisibility(View.VISIBLE);
                            llPoweredBySoundCloud.setVisibility(View.GONE);
                        }
                        Observable.just(trackDetails)
                                .flatMap(new Func1<List<TrackDetails>, Observable<TrackDetails>>() {
                                    @Override
                                    public Observable<TrackDetails> call(List<TrackDetails> trackDetails) {
                                        return Observable.from(trackDetails);
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseSubscriber<TrackDetails>() {
                                    @Override
                                    public void onObjectReceived(TrackDetails details) {
                                        mAdapter.addItemToList(details);
                                    }
                                });
                    }
                });
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        SearchResponse value = dataSnapshot.getValue(SearchResponse.class);
        etSearch.setText(value.getSearch());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {}

    private BaseSubscriber<CharSequence> textWatcher = new BaseSubscriber<CharSequence>() {
        @Override
        public void onObjectReceived(CharSequence s) {
            FrndsLog.e("Search term: " + s);
            callSearchApi(s.toString());
            mClient.getSearchResult(s.toString(), 10, 0)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubscriber<List<TrackDetails>>() {
                        @Override
                        public void onObjectReceived(List<TrackDetails> trackDetails) {
                            if(trackDetails != null && !trackDetails.isEmpty()) {
                                FrndsLog.e("count: " + trackDetails.size());
                                rlSearchResult.setVisibility(View.VISIBLE);
                                llPoweredBySoundCloud.setVisibility(View.GONE);
                                mAdapter.refreshList(trackDetails);
                            }
                        }
                    });
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:{
                onBackPressed();
            }
            break;
        }
    }

    private class SearchResultPagination implements PaginationListener<TrackDetails> {

        private String mSearchString;

        public void setSearchString(String searchString) {
            mSearchString = searchString;
        }

        @Override
        public Observable<List<TrackDetails>> onNextPage(int offset) {
            return mClient.getSearchResult(mSearchString, IConstants.PAGE_SIZE, offset + 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.cross)
    public void clearText() {
        etSearch.setText("");
        rlSearchResult.setVisibility(View.GONE);
        llPoweredBySoundCloud.setVisibility(View.VISIBLE);
    }
}
