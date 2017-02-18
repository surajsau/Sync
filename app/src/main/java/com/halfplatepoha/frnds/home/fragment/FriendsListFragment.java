package com.halfplatepoha.frnds.home.fragment;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.TokenTracker;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.activity.HomeActivity;
import com.halfplatepoha.frnds.home.adapter.FriendsListAdapter;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.halfplatepoha.frnds.network.BaseSubscriber;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Func1;

import static com.halfplatepoha.frnds.R.drawable.play;

public class FriendsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ChatDAO.OnTransactionCompletedListener, FriendsListAdapter.OnRowClickListener{

    @Bind(R.id.rlFrnds) RecyclerView rlFrnds;
    @Bind(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;

    @Bind(R.id.zero_state)
    View zeroState;

    private FriendsListAdapter mAdapter;

    private Realm mRealm;

    private ChatDAO helper;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
        helper = new ChatDAO(mRealm);
        helper.setOnTransactionCompletedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        setupRecyclerView();

        getListFromDb();
    }

    private void getListFromDb() {
        RealmResults<Chat> chats = mRealm.where(Chat.class).findAll();
        if(chats == null || chats.size() == 0) {
            zeroState.setVisibility(View.VISIBLE);
        } else {
            zeroState.setVisibility(View.GONE);
            for (int i = 0; i < chats.size(); i++) {
                try {
                    if (chats.get(i).getFrndPosition() == null) {
                        mRealm.beginTransaction();
                        chats.get(i).setFrndPosition(i);
                        mRealm.commitTransaction();
                    }
                } catch (Exception e) {
                    mRealm.cancelTransaction();
                }
                mAdapter.addChat(chats.get(i));
            }
        }
    }

    private void setupRecyclerView() {
        mAdapter = new FriendsListAdapter(getActivity());
        mAdapter.setListener(this);
        rlFrnds.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlFrnds.setAdapter(mAdapter);

        refreshLayout.setEnabled(true);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorAccent)
                , ContextCompat.getColor(getActivity(), R.color.soundCloud));
        refreshLayout.setOnRefreshListener(this);
    }

    private BaseSubscriber<Chat> chatSubscriber = new BaseSubscriber<Chat>() {
        @Override
        public void onObjectReceived(Chat chat) {
            mAdapter.addChat(chat);
        }
    };

    @Override
    public void onRefresh() {
        updateFrndsList();
    }

    private void updateFrndsList() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if(token == null)
            token = TokenTracker.getInstance().getCurrentAccessToken();

        Bundle params = new Bundle();
        params.putString("fields", "installed, id, name, picture.type(large)");

        new GraphRequest(token, "/me/friends", params, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if(response.getError() == null) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        FrndsLog.e(response.getRawResponse());
                        InstalledFrnds frnds = mapper.readValue(response.getRawResponse(), InstalledFrnds.class);
                        helper.updateChatList(frnds, IDbConstants.UPDATE_FRND_LIST_TRANSACTION_ID);
                    }  catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //TODO: error case
                }
            }
        }).executeAsync();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mRealm.close();
    }

    @Override
    public void onTransactionComplete(int transcationId) {
        switch (transcationId) {
            case IDbConstants.UPDATE_FRND_LIST_TRANSACTION_ID:
                refreshLayout.setRefreshing(false);
        }
    }

    public void refreshChatDetails(boolean isFromNotification, String frndId, String message, long timestamp) {
        mAdapter.refreshChat(isFromNotification, frndId, message, timestamp);
    }

    @Override
    public void onRowClick(ActivityOptionsCompat options, String frndId) {
        Intent songDetailsIntent = new Intent(getActivity(), SongDetailActivity.class);
        songDetailsIntent.putExtra(IDetailsConstants.FRND_ID, frndId);
        startActivityForResult(songDetailsIntent, IFrndsConstants.FRIEND_LIST_REQUEST, options.toBundle());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IFrndsConstants.FRIEND_LIST_REQUEST:{
                if(resultCode == Activity.RESULT_OK && data != null) {
                    long latestTimestamp = data.getLongExtra(IDetailsConstants.LATEST_MESSAGE_TIMESTAMP, 0L);
                    String latestMessage = data.getStringExtra(IDetailsConstants.LATEST_MESSAGE);
                    String latestFrndId = data.getStringExtra(IDetailsConstants.FRND_ID);

                    refreshChatDetails(false, latestFrndId, latestMessage, latestTimestamp);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btnShareSync)
    public void shareSync() {
        String message = "Hi! Let\'s Sync what we listen to daily. Let\'s spread love. Download Sync from Playstore" + IConstants.APP_URL;
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            startActivity(whatsappIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "WhatsApp isn\'t installed", Toast.LENGTH_SHORT).show();
        }
    }
}
