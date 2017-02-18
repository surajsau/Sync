package com.halfplatepoha.frnds.home.activity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.fragment.FriendsListFragment;
import com.halfplatepoha.frnds.mediaplayer.PlayerService;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

import static com.halfplatepoha.frnds.R.id.btnPlaylist;

public class HomeActivity extends AppCompatActivity {

//    @Bind(R.id.pager) ViewPager pager;
//    @Bind(R.id.tabs) TabLayout tabs;

    @Bind(R.id.player)
    View player;

    @Bind(R.id.tvPlayerTrackFrnd)
    OpenSansTextView tvPlayerTrackFrnd;

    @Bind(R.id.tvPlayerTrackTitle)
    OpenSansTextView tvPlayerTrackTitle;

    @Bind(R.id.ivPlayerAlbum)
    GlideImageView ivPlayerAlbum;

    @Bind(R.id.btnPlayNext)
    ImageButton btnPlayNext;

    private ChatDAO helper;

//    private TabPagerAdapter mTabAdapter;

//    private BottomSheetBehavior bottomSheetBehavior;

    private FriendsListFragment mFriendsListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        ButterKnife.bind(this);

        helper = new ChatDAO(Realm.getDefaultInstance());

//        bottomSheetBehavior = BottomSheetBehavior.from(player);

        setupListFragment();

        if(helper.getSongsCount() == 0)
            player.setVisibility(View.GONE);

        registerReceiver(notificationReceiver, new IntentFilter(IConstants.CHAT_BROADCAST));
        registerReceiver(songStatusReceiver, new IntentFilter(IConstants.SONG_STATUS_BROADCAST));
    }

    private void setupListFragment() {
        mFriendsListFragment = new FriendsListFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentHome, mFriendsListFragment, IFrndsConstants.HOME_FRAGMENT_TAG)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        try {
            unregisterReceiver(notificationReceiver);
            unregisterReceiver(songStatusReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    private void setupPager() {
//        mTabAdapter = new TabPagerAdapter(this, getSupportFragmentManager());
//        pager.setAdapter(mTabAdapter);
//
//        tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));
//        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
//        tabs.setupWithViewPager(pager);
//
//        for(int i=0; i<IFrndsConstants.tabDrawables.length; i++) {
//            tabs.getTabAt(i).setIcon(ContextCompat.getDrawable(this, IFrndsConstants.tabDrawables[i]));
//        }
//
//        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager){
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                tab.setIcon(IFrndsConstants.tabSelectedDrawables[tab.getPosition()]);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.setIcon(IFrndsConstants.tabDrawables[tab.getPosition()]);
//            }
//        });
//
//        tabs.getTabAt(0).select();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayer();
    }

    private void updatePlayer() {
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_LISTING);
        String albumUrl = FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_IMAGE_URL, "");
        String track = FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_NAME, "");
        String frndName = FrndsPreference.getFromPref(IPrefConstants.LATEST_FRND_NAME, "");
        @IDetailsConstants.CurrentSongStatusType int status = FrndsPreference.getIntFromPref(IPrefConstants.CURRENT_PLAYING_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);

//        bottomSheetBehavior.setState(status == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING ?
//                BottomSheetBehavior.STATE_EXPANDED : BottomSheetBehavior.STATE_COLLAPSED);
        if(!TextUtils.isEmpty(albumUrl)) {
            player.setVisibility(View.VISIBLE);
            btnPlayNext.setImageDrawable(ContextCompat.getDrawable(this,
                    status == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING ?
                            R.drawable.pause : R.drawable.play));
            ivPlayerAlbum.setImageUrl(this, albumUrl);
            tvPlayerTrackTitle.setText(track);
            tvPlayerTrackFrnd.setText(String.format("with %s", frndName));
        }
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FrndsPreference.setInPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_NONE);
    }

    private BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null) {
                String trackId = intent.getStringExtra(IConstants.FRND_TRACK_ID);
                @IDbConstants.MessageType int messageType = intent.getIntExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE);
                String messageBody = intent.getStringExtra(IConstants.FRND_MESSAGE);
                String trackUrl = intent.getStringExtra(IConstants.FRND_TRACK_URL);
                String trackImageUrl = intent.getStringExtra(IConstants.FRND_TRACK_IMAGE_URL);
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
                    song.setSongImgUrl(trackImageUrl);

                    helper.insertSongToChat(frndId, song);
                }

                helper.insertMessageToChat(frndId, message);

                mFriendsListFragment.refreshChatDetails(true, frndId, messageBody, ts);
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
                updatePlayer();
            }
        }
    };

    @OnClick(R.id.btnProfile)
    public void openProfilesAndSettings() {
        Intent profileIntent = new Intent(this, ProfileAndSettingsActivity.class);
        startActivity(profileIntent);
    }

    @OnClick(R.id.btnPlayNext)
    public void onPlayNext() {
        @IDetailsConstants.CurrentSongStatusType int status = FrndsPreference.getIntFromPref(IPrefConstants.CURRENT_PLAYING_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);
        if(status == IDetailsConstants.CURRENT_SONG_STATUS_PLAYING) {
            FrndsPreference.setInPref(IPrefConstants.CURRENT_PLAYING_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);
            Intent stopServiceIntent = new Intent(this, PlayerService.class);
            stopServiceIntent.setAction(PlayerService.ACTION_STOP);
            startService(stopServiceIntent);
        } else {
            String latestSongUrl = FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_URL, "");
            String latestTrackTitle = FrndsPreference.getFromPref(IPrefConstants.LATEST_SONG_NAME, "");
            String latestFrndId = FrndsPreference.getFromPref(IPrefConstants.LATEST_FRND_ID, "");

            if(!TextUtils.isEmpty(latestSongUrl)) {
                FrndsPreference.setInPref(IPrefConstants.CURRENT_PLAYING_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_PLAYING);
                Intent startServiceIntent = new Intent(this, PlayerService.class);
                startServiceIntent.setAction(PlayerService.ACTION_PLAY);
                startServiceIntent.putExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE, latestTrackTitle);
                startServiceIntent.putExtra(IDetailsConstants.SERVICE_STREAM_URL, latestSongUrl);
                startServiceIntent.putExtra(IDetailsConstants.FRND_ID, latestFrndId);
                startService(startServiceIntent);
            }

        }

        updatePlayer();
    }

}
