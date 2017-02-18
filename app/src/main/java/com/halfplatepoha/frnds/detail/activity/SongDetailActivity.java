package com.halfplatepoha.frnds.detail.activity;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.adapter.AlbumListAdapter;
import com.halfplatepoha.frnds.detail.adapter.ChatAdapter;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.adapter.ChatAdapter.OnPlayClickListener;
import com.halfplatepoha.frnds.detail.fragment.ShareSongFragment;
import com.halfplatepoha.frnds.detail.model.MessageModel;
import com.halfplatepoha.frnds.detail.model.SongModel;
import com.halfplatepoha.frnds.fcm.IFCMConstants;
import com.halfplatepoha.frnds.mediaplayer.PlayerService;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.models.request.SendMessageRequest;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.FrndsClient;
import com.halfplatepoha.frnds.models.request.UpdateTrackRequest;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;
import com.halfplatepoha.frnds.search.activity.SearchScreenActivity;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansEditText;
import com.halfplatepoha.frnds.ui.OpenSansTextView;
import com.halfplatepoha.frnds.utils.AppUtil;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.codetail.animation.ViewAnimationUtils;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SongDetailActivity extends AppCompatActivity implements
        View.OnFocusChangeListener, OnTouchListener, OnPlayClickListener,
        AlbumListAdapter.OnAlbumClickListener{

    private static final String TAG = SongDetailActivity.class.getSimpleName();

    private FrndsClient mFrndsClient;
    private SoundCloudClient mSoundCloudClient;

    private ChatDAO helper;

    private int deltaX;

    @Bind(R.id.etMessage) OpenSansEditText etMessage;
    @Bind(R.id.rlAlbums) RecyclerView rlAlbums;
    @Bind(R.id.rlChat) RecyclerView rlChat;
    @Bind(R.id.ivAlbumBg) GlideImageView ivAlbumBg;
    @Bind(R.id.playlist) View playlist;
    @Bind(R.id.ivFrndAvatar) CircleImageView ivFrndAvatar;
    @Bind(R.id.tvTitle) OpenSansTextView tvTitle;
    @Bind(R.id.pendingChat) View pendingChat;
    @Bind(R.id.btnPlaylist) ImageButton btnPlaylist;
    @Bind(R.id.ivSongPlayingIndicator) View songPlayedIndicator;
    @Bind(R.id.messageContainer) FrameLayout messageContainer;
    @Bind(R.id.btnSend) View btnSend;
    @Bind(R.id.btnMessageBoxMusic) View btnMessageboxMusic;
    @Bind(R.id.tvTrackTitle) OpenSansTextView tvTrackTitle;
    @Bind(R.id.tvTrackArtist) OpenSansTextView tvTrackArtist;

    @Bind(R.id.playlist_zero_state) View playlistZero;

    @Bind(R.id.tvAlbumHelpText) OpenSansTextView tvAlbumHelpText;

    private int[] btnPlaylistCoordinates;

    private String mFrndId;
    private String mFbId;
    private String mFrndImageUrl;
    private String mFrndName;

    private long latestTimestamp;
    private String latestMessage;

    private String mSource;

    private AlbumListAdapter mAlbumListAdapter;
    private ChatAdapter mChatAdapter;

    private LinearLayoutManager mChatLayoutManager;

    private Chat mChat;

    private Animation leftSlide, rightSlide;

    private RotateAnimation rotateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);
        ButterKnife.bind(this);

        helper = new ChatDAO(Realm.getDefaultInstance());

        buildApiClients();

        setupRecyclerViews();

        getDataFromBundle();

        setupToolbar();

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mNotificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
        registerReceiver(songStatusReceiver, new IntentFilter(IConstants.SONG_STATUS_BROADCAST));
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_CHAT);

        addDataToAdapters();

        if(btnPlaylistCoordinates == null) {
            btnPlaylistCoordinates = new int[2];
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            btnPlaylistCoordinates[0] = metrics.widthPixels;
            btnPlaylistCoordinates[1] = 0;
        }

        etMessage.setOnFocusChangeListener(this);

        if(messageContainer != null) {
            deltaX = ((FrameLayout.LayoutParams)(messageContainer).getLayoutParams()).getMarginStart();
        }

        rlChat.setOnTouchListener(this);

        buildAnimations();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_NONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mNotificationReceiver);
            unregisterReceiver(songStatusReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buildAnimations() {
        rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        leftSlide = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) messageContainer.getLayoutParams();
                param.leftMargin = deltaX - (int)(deltaX * interpolatedTime);
                param.rightMargin = (int)(deltaX * interpolatedTime);
                messageContainer.setLayoutParams(param);
                btnMessageboxMusic.setAlpha(interpolatedTime);
            }
        };
        leftSlide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btnMessageboxMusic.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        leftSlide.setInterpolator(new AccelerateDecelerateInterpolator());
        leftSlide.setDuration(300);

        rightSlide = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) messageContainer.getLayoutParams();
                param.rightMargin = deltaX - (int)(deltaX * interpolatedTime);
                param.leftMargin = (int)(deltaX * interpolatedTime);
                messageContainer.setLayoutParams(param);
                btnMessageboxMusic.setAlpha(1 - interpolatedTime);
            }
        };
        rightSlide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                btnMessageboxMusic.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        rightSlide.setInterpolator(new AccelerateDecelerateInterpolator());
        rightSlide.setDuration(300);
    }

    private void addDataToAdapters() {
        //--adding songs to adapter
        mAlbumListAdapter.refresh();
        mChatAdapter.refresh();

        if(mChat != null) {
            RealmResults<Song> songs = mChat.getFrndSongs().sort(IDbConstants.SONG_TIME_STAMP_KEY, Sort.ASCENDING);

            if (songs != null) {
                for (int i = 0; i < songs.size(); i++) {
                    SongModel model = new SongModel();
                    model.setTrackName(songs.get(i).getSongTitle());
                    model.setTrackTimeStamp(songs.get(i).getSongTimestamp());
                    model.setTrackImageUrl(songs.get(i).getSongImgUrl());
                    model.setTrackUser(songs.get(i).getSongArtist());
                    model.setTrackUrl(songs.get(i).getSongUrl());
                    model.setTrackShareUrl(songs.get(i).getSongShareUrl());

                    mAlbumListAdapter.addSong(model);
                }

                if (songs.size() > 0) {
                    playlistZero.setVisibility(View.GONE);
                    tvTrackArtist.setVisibility(View.VISIBLE);
                    tvTrackTitle.setVisibility(View.VISIBLE);
                    tvTrackTitle.setText(songs.last().getSongTitle());
                    tvTrackArtist.setText(songs.last().getSongArtist());
                    ivAlbumBg.setImageUrl(this, songs.last().getSongImgUrl());
                } else {
                    playlistZero.setVisibility(View.VISIBLE);
                    tvAlbumHelpText.setText(String.format("Add Songs to start Syncing with %s", mFrndName));
                    tvTrackArtist.setVisibility(View.GONE);
                    tvTrackTitle.setVisibility(View.GONE);
                }
            }

            //--adding messages to chat
            RealmResults<Message> messages = mChat.getFrndMessages().sort(IDbConstants.MSG_TIME_STAMP_KEY, Sort.ASCENDING);

            if (messages != null) {
                for (int i = 0; i < messages.size(); i++) {
                    MessageModel model = new MessageModel();
                    model.setMessageType(messages.get(i).getMsgType());
                    model.setMessage(messages.get(i).getMsgBody());
                    model.setUserType(messages.get(i).getUserType());
                    model.setMessageType(messages.get(i).getMsgType());
                    model.setMessageTrackUrl(messages.get(i).getMsgTrackUrl());
                    model.setMessageTimeStamp(messages.get(i).getMsgTimestamp());
                    mChatAdapter.addMessage(model);
                }



                if (mChatAdapter.getItemCount() != 0)
                    rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
            }
        }
    }

    private void getChatObjectFromDb() {
        mChat = helper.getFrndWithFrndId(mFrndId);

        mFrndImageUrl = mChat.getFrndImageUrl();
        mFrndName = mChat.getFrndName();

        if(mChat.getFrndLastMessage() != null) {
            latestMessage = mChat.getFrndLastMessage().getMsgBody();
            latestTimestamp = mChat.getFrndLastMessage().getMsgTimestamp();
        }
    }

    private void startSearchActivity() {
        Intent searchIntent = new Intent(this, SearchScreenActivity.class);
        searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(searchIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    private void setupRecyclerViews() {
        mAlbumListAdapter = new AlbumListAdapter(this);
        mChatAdapter = new ChatAdapter(this);

        mAlbumListAdapter.setListener(this);
        mChatAdapter.setOnPlayClickListener(this);

        mChatLayoutManager = new LinearLayoutManager(this);
        mChatLayoutManager.setStackFromEnd(true);
        mChatLayoutManager.setReverseLayout(false);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rlAlbums);

        rlChat.setAdapter(mChatAdapter);
        rlChat.setLayoutManager(mChatLayoutManager);
        rlAlbums.setAdapter(mAlbumListAdapter);
        rlAlbums.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupToolbar() {
        Glide.with(this)
                .load(mFrndImageUrl)
                .into(ivFrndAvatar);

        tvTitle.setText(mFrndName);
    }

    private void callUpdateTracksApi(String trackId) {
        UpdateTrackRequest req = new UpdateTrackRequest();
        req.setFbId(mFbId);
        req.setTo(mFrndId);
        req.setTrackId(trackId);
        mFrndsClient.updateTrack(req)
            .subscribeOn(Schedulers.newThread())
            .observeOn(Schedulers.newThread())
            .subscribe(new BaseSubscriber<Void>() {
                @Override
                public void onObjectReceived(Void aVoid) {

                }
            });
    }

    private void callSendMessageApi(final Message message) {
        SendMessageRequest req  = new SendMessageRequest();
        req.setFbId(mFbId);
        req.setMessage(message.getMsgBody());
        req.setTo(mFrndId);

        mFrndsClient.sendMessage(req)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Void>() {
                    @Override
                    public void onObjectReceived(Void aVoid) {
                        helper.insertMessageToChat(mFrndId, message);
                        latestMessage = message.getMsgBody();
                        latestTimestamp = message.getMsgTimestamp();
                    }
                });
    }

    private void buildApiClients() {
        mFrndsClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.FRNDS_BASE_URL)
                .setClientClass(FrndsClient.class)
                .setConnectTimeout(5, TimeUnit.MINUTES)
                .setReadTimeout(5, TimeUnit.MINUTES)
                .addRetryInterceptor()
                .setHeader(IConstants.CONTENT_TYPE, IConstants.APPLICATION_JSON)
                .buildClient();

        mSoundCloudClient = new ClientGenerator.Builder()
                .setLoggingInterceptor()
                .setBaseUrl(IConstants.SOUNDCLOUD_BASE_URL)
                .setApiKeyInterceptor(IConstants.API_KEY_PARAM, IConstants.API_KEY_VALUE)
                .setClientClass(SoundCloudClient.class)
                .buildClient();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(intent != null) {
            mFrndId = intent.getStringExtra(IDetailsConstants.FRND_ID);

            getChatObjectFromDb();
        }
    }

    private void getDataFromBundle() {
        if(getIntent() != null) {
            mSource = getIntent().getStringExtra(IDetailsConstants.SOURCE_TYPE);
            mFrndId = getIntent().getStringExtra(IDetailsConstants.FRND_ID);

            if(IDetailsConstants.SOURCE_NOTIFICATION.equalsIgnoreCase(mSource)) {
                mFrndName = getIntent().getStringExtra(IFCMConstants.NOTIF_FRIEND_NAME);
                String trackTitle = getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_NAME);
                String trackUrl = getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_URL);
                String trackImageUrl = getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_IMAGE_URL);
                String tracKShareUrl = getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_SHARE_URL);
                String trackArtist = getIntent().getStringExtra(IFCMConstants.NOTIF_TRACK_ARTIST);
                String message = getIntent().getStringExtra(IFCMConstants.NOTIF_MESSAGE);
                String type = getIntent().getStringExtra(IFCMConstants.NOTIF_TYPE);
                long timestamp = !TextUtils.isEmpty(getIntent().getStringExtra(IFCMConstants.NOTIF_TIMESTAMP)) ?
                        Long.valueOf(getIntent().getStringExtra(IFCMConstants.NOTIF_TIMESTAMP)) : 0L;

                if ("SONG".equalsIgnoreCase(type)) {
                    addSongToPlaylist(trackUrl, trackImageUrl, tracKShareUrl,
                            trackTitle, trackArtist, timestamp,
                            IDetailsConstants.TYPE_FRND);
                } else {
                    Message msg = new Message.Builder()
                            .setMsgBody(message)
                            .setMsgTimestamp(timestamp)
                            .setMsgType(IDbConstants.TYPE_MESSAGE)
                            .setUserType(IDetailsConstants.TYPE_FRND)
                            .setFrndId(mFrndId)
                            .build();

                    helper.insertMessageToChat(mFrndId, msg);
                }
            }

            getChatObjectFromDb();
        }

        mFbId = FrndsPreference.getFromPref(IPrefConstants.FB_USER_ID, "");
    }

    @OnClick({R.id.btnMusic, R.id.btnMessageBoxMusic, R.id.playlist_zero_state})
    public void addMusic() {
        Intent searchScreenIntent = new Intent(this, SearchScreenActivity.class);
        startActivityForResult(searchScreenIntent, IDetailsConstants.SONG_DETAILS_REQUEST);
    }

    @OnClick(R.id.back)
    public void back() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(IDetailsConstants.LATEST_MESSAGE, latestMessage);
        resultIntent.putExtra(IDetailsConstants.LATEST_MESSAGE_TIMESTAMP, latestTimestamp);
        resultIntent.putExtra(IDetailsConstants.FRND_ID, mFrndId);
        setResult(RESULT_OK, resultIntent);
        onBackPressed();
    }

    @OnClick(R.id.btnSend)
    public void addMessage() {
        long timestamp = System.currentTimeMillis();
        if(!TextUtils.isEmpty(etMessage.getText())) {
            Message.Builder msgBuilder = new Message.Builder()
                    .setMsgBody(etMessage.getText().toString())
                    .setMsgTimestamp(timestamp)
                    .setFrndId(mFrndId)
                    .setMsgType(IDbConstants.TYPE_MESSAGE)
                    .setUserType(IDetailsConstants.TYPE_ME);

            MessageModel messageModel = new MessageModel();
            messageModel.setMessageTimeStamp(timestamp);
            messageModel.setMessage(etMessage.getText().toString());
            messageModel.setMessageType(IDbConstants.TYPE_MESSAGE);
            messageModel.setUserType(IDetailsConstants.TYPE_ME);

            mChatAdapter.addMessage(messageModel);

            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

            callSendMessageApi(msgBuilder.build());

            etMessage.setText("");
        }
    }

    @OnClick(R.id.btnPlaylist)
    public void togglePlaylist() {
        if(playlist.getVisibility() == View.VISIBLE) {
            circularCollapse();
        } else {
            circularReveal();
        }
    }

    private void circularCollapse() {
        Animator anim = ViewAnimationUtils.createCircularReveal(playlist,
                btnPlaylistCoordinates[0],
                btnPlaylistCoordinates[1],
                getfFinalRadius(playlist, btnPlaylistCoordinates[0], btnPlaylistCoordinates[1]),
                0,
                View.LAYER_TYPE_HARDWARE);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                playlist.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        anim.setDuration(300);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.start();
    }

    private void circularReveal() {
        playlist.setVisibility(View.VISIBLE);
        Animator anim = ViewAnimationUtils.createCircularReveal(playlist,
                btnPlaylistCoordinates[0],
                btnPlaylistCoordinates[1],
                0,
                getfFinalRadius(playlist, btnPlaylistCoordinates[0], btnPlaylistCoordinates[1]),
                View.LAYER_TYPE_HARDWARE);
        anim.setDuration(300);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.start();
    }

    private float getfFinalRadius(View view, int centerX, int centerY) {
        return (float) Math.hypot(view.getWidth() / 2f, view.getHeight() / 2f)
                + hypo(view, centerX, centerY);
    }

    private float hypo(View view, float x, float y) {
        Point p1 = new Point((int)x, (int)y);
        Point p2 = new Point(view.getWidth() / 2, view.getHeight() / 2);

        return (float) Math.sqrt(Math.pow(p1.y - p2.y, 2) + Math.pow(p1.x - p2.x, 2));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case IDetailsConstants.SONG_DETAILS_REQUEST:{
                if(resultCode == RESULT_OK){
                    String trackId = String.valueOf(data.getExtras().getLong(IDetailsConstants.TRACK_ID));
                    String trackUrl = data.getExtras().getString(IDetailsConstants.TRACK_URL);
                    String trackShareUrl = data.getExtras().getString(IDetailsConstants.TRACK_SHARE_URL);

                    String trackImageUrl = (data.getExtras().getString(IDetailsConstants.TRACK_IMAGE_URL));

                    FrndsLog.e(trackImageUrl);

                    String trackTitle = data.getExtras().getString(IDetailsConstants.TRACK_TITLE);
                    String trackArtist = data.getExtras().getString(IDetailsConstants.TRACK_ARTIST);

                    //--setting background image of activity
                    ivAlbumBg.setImageUrl(this, trackImageUrl);
                    tvTrackTitle.setText(trackTitle);
                    tvTrackArtist.setText(trackArtist);

                    if(playlistZero.getVisibility() == View.VISIBLE) {
                        playlistZero.setVisibility(View.GONE);
                        tvTrackTitle.setVisibility(View.VISIBLE);
                        tvTrackArtist.setVisibility(View.VISIBLE);
                    }

                    addSongToPlaylist(trackUrl, trackImageUrl, trackShareUrl, trackTitle, trackArtist,
                            System.currentTimeMillis(), IDetailsConstants.TYPE_ME);

                    startPlayingTrack(trackTitle, trackUrl);

                    callUpdateTracksApi(trackId);

                    FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_IMAGE_URL, trackImageUrl);
                    FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_NAME, trackTitle);
                    FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_URL, trackUrl);
                    FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_NAME, mFrndName);
                    FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_ID, mFrndId);
                }
            }
            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPlayingTrack(String trackTitle, String trackUrl) {
        Intent playerServiceIntent = new Intent(this, PlayerService.class);
        playerServiceIntent.setAction(PlayerService.ACTION_PLAY);
        playerServiceIntent.putExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE, trackTitle);
        playerServiceIntent.putExtra(IDetailsConstants.SERVICE_STREAM_URL, trackUrl);
        playerServiceIntent.putExtra(IDetailsConstants.FRND_ID, mFrndId);
        startService(playerServiceIntent);
    }

    private void addSongToPlaylist(String trackUrl, String trackImageUrl, String trackShareUrl,
                                   String trackTitle, String trackArtist,
                                   long timestamp, @IDetailsConstants.UserType int userType) {

        if(trackImageUrl != null)
            trackImageUrl = trackImageUrl.replace(IDetailsConstants.IMG_500_X_500_SUFFIX, IDetailsConstants.IMG_300_X_300_SUFFIX);

        Song song = new Song();
        song.setSongArtist(trackArtist);
        song.setSongImgUrl(trackImageUrl);
        song.setSongTimestamp(timestamp);
        song.setSongTitle(trackTitle);
        song.setSongShareUrl(trackShareUrl);
        song.setSongUrl(trackUrl);
        song.setFrndId(mFrndId);

        String user = (IDetailsConstants.TYPE_ME == userType) ? getString(R.string.current_user_placeholder) : mFrndName;

        Message message = new Message.Builder()
                .setMsgBody(String.format(getString(R.string.music_chat_message), user, trackTitle))
                .setMsgTimestamp(timestamp)
                .setMsgTrackUrl(trackUrl)
                .setMsgType(IDbConstants.TYPE_MUSIC)
                .setUserType(userType)
                .build();

        SongModel songModel = new SongModel();
        songModel.setTrackUrl(trackUrl);
        songModel.setTrackUser(trackArtist);
        songModel.setTrackImageUrl(trackImageUrl);
        songModel.setTrackTimeStamp(timestamp);
        songModel.setTrackName(trackTitle);
        songModel.setTrackShareUrl(trackShareUrl);

        MessageModel messageModel = new MessageModel();
        messageModel.setMessage(String.format(getString(R.string.music_chat_message), "You", trackTitle));
        messageModel.setUserType(userType);
        messageModel.setMessageType(IDbConstants.TYPE_MUSIC);
        messageModel.setMessageTrackUrl(trackUrl);

        latestTimestamp = timestamp;
        latestMessage = messageModel.getMessage();

        mAlbumListAdapter.addSong(songModel);
        mChatAdapter.addMessage(messageModel);

        rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

        helper.insertSongToChat(mFrndId, song);
        helper.insertMessageToChat(mFrndId, message);
    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                String trackId = intent.getStringExtra(IConstants.FRND_TRACK_ID);
                @IDbConstants.MessageType int messageType = intent.getIntExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE);
                String messageBody = intent.getStringExtra(IConstants.FRND_MESSAGE);
                String trackUrl = intent.getStringExtra(IConstants.FRND_TRACK_URL);
                String frndId = intent.getStringExtra(IConstants.FRND_ID);
                String trackTitle = intent.getStringExtra(IConstants.FRND_TRACK_TITLE);
                String timestamp = intent.getStringExtra(IConstants.FRND_TIME_STAMP);

                long ts = Long.parseLong(timestamp);

                Message message = new Message();
                message.setMsgBody(messageBody);
                message.setMsgTimestamp(ts);
                message.setUserType(IDetailsConstants.TYPE_FRND);
                message.setMsgType(messageType);
                message.setMsgTrackUrl(trackUrl);

                if(messageType == IDbConstants.TYPE_MUSIC) {
                    Song song = new Song();
                    song.setSongUrl(trackUrl);
                    song.setFrndId(frndId);
                    song.setSongTitle(trackTitle);
                    song.setSongTimestamp(ts);

                    helper.insertSongToChat(frndId, song);
                }

                helper.insertMessageToChat(frndId, message);

                refreshChatDetails(frndId, messageType, messageBody, trackId, trackTitle, trackUrl, ts);
            }
        }
    };

    private BroadcastReceiver songStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                @IDetailsConstants.CurrentSongStatusType int currentPlayingStatus =
                        intent.getIntExtra(IDetailsConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_PLAYING);
                FrndsPreference.setInPref(IPrefConstants.CURRENT_PLAYING_STATUS, currentPlayingStatus);

                songPlayedIndicator.setVisibility(currentPlayingStatus == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING ?
                                                                                View.VISIBLE : View.GONE);

                btnPlaylist.setAnimation(currentPlayingStatus == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING ?
                                                                                rotateAnimation :  null);
            }
        }
    };

    private void refreshChatDetails(String frndId, int messageType, String message, String trackId,
                                    final String trackTitle, final String trackUrl, final long timestamp) {
        if(mFrndId.equalsIgnoreCase(frndId)) {
            if(messageType == IDbConstants.TYPE_MUSIC) {

                //--getting song artist & image url
                mSoundCloudClient.getTrackDetails(trackId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new BaseSubscriber<TrackDetails>() {
                            @Override
                            public void onObjectReceived(TrackDetails trackDetails) {
                                SongModel songModel = new SongModel();
                                songModel.setTrackName(trackTitle);
                                songModel.setTrackImageUrl(trackDetails.getArtwork_url());
                                songModel.setTrackTimeStamp(timestamp);
                                songModel.setTrackUser(trackDetails.getUser().getUsername());
                                songModel.setTrackUrl(trackUrl);
                                songModel.setTrackShareUrl(trackDetails.getPermalink_url());

                                Song song = new Song();
                                song.setSongImgUrl(trackDetails.getArtwork_url());
                                song.setSongTimestamp(timestamp);
                                song.setFrndId(mFrndId);
                                song.setSongShareUrl(trackDetails.getPermalink_url());
                                if(trackDetails.getUser() != null) {
                                    song.setSongArtist(trackDetails.getUser().getUsername());
                                }
                                helper.updateSong(song);

                                mAlbumListAdapter.addSong(songModel);
                            }
                        });
            }

            Message msg = new Message();
            msg.setMsgTimestamp(timestamp);
            msg.setMsgType(messageType);
            msg.setUserType(IDetailsConstants.TYPE_FRND);
            msg.setMsgBody(message);

            MessageModel messageModel = new MessageModel();
            messageModel.setMessageType(messageType);
            messageModel.setMessage(message);
            messageModel.setMessageTrackUrl(trackUrl);
            messageModel.setUserType(IDetailsConstants.TYPE_FRND);
            messageModel.setMessageType(messageType);

            latestMessage = messageModel.getMessage();
            latestTimestamp = messageModel.getMessageTimeStamp();

            mChatAdapter.addMessage(messageModel);

            rlChat.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);

        } else {
            pendingChat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if(isFocus) {
            messageContainer.startAnimation(leftSlide);
            circularCollapse();
        } else {
            if(TextUtils.isEmpty(((EditText)view).getText()))
                messageContainer.startAnimation(rightSlide);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.rlChat:{
                if(TextUtils.isEmpty(etMessage.getText())) {
                    messageContainer.startAnimation(rightSlide);
                }
                AppUtil.hideSoftKeyboard(this);
                etMessage.clearFocus();
            }
            break;
        }
        return false;
    }

    @Override
    public void onPlayClick(String trackUrl, String message, int position) {
        String trackName = message.split(" played ")[1];
        startPlayingTrack(trackName, trackUrl);
        rlAlbums.smoothScrollToPosition(position);
    }

    @Override
    public void onAlbumClick(SongModel song) {
        ShareSongFragment shareSong = new ShareSongFragment();
        shareSong.setFrndId(mFrndId);
        shareSong.setFrndName(mFrndName);
        shareSong.setSongModel(song);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home, shareSong)
                .addToBackStack(IDetailsConstants.SONG_SHARE_TAG)
                .commit();
    }
}
