package com.halfplatepoha.frnds.detail.fragment;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.adapter.FrndsShareSuggestionListAdapter;
import com.halfplatepoha.frnds.detail.model.SongModel;
import com.halfplatepoha.frnds.mediaplayer.PlayerService;
import com.halfplatepoha.frnds.ui.GlideImageView;
import com.halfplatepoha.frnds.ui.OpenSansTextView;
import com.halfplatepoha.frnds.utils.AppUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.gms.common.api.Status.st;
import static com.halfplatepoha.frnds.R.id.ivAlbum;
import static com.halfplatepoha.frnds.R.id.tvAlbumArtist;
import static com.halfplatepoha.frnds.R.id.tvAlbumTitle;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareSongFragment extends Fragment {

    @Bind(R.id.ivAlbum)
    GlideImageView ivAlbum;

    @Bind(R.id.tvAlbumArtist)
    OpenSansTextView tvAlbumArtist;

    @Bind(R.id.tvAlbumTitle)
    OpenSansTextView tvAlbumTitle;

    @Bind(R.id.btnPlaySong)
    ImageButton btnPlaySong;

    private String frndId;
    private String frndName;

    private FrndsShareSuggestionListAdapter mAdapter;

    private @IDetailsConstants.CurrentSongStatusType int status;

    private StringBuilder shareMsg;

    private SongModel songModel;

    public ShareSongFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_song, container, false);
    }

    public void setFrndId(String frndId) {
        this.frndId = frndId;
    }

    public void setFrndName(String frndName) {
        this.frndName = frndName;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        status = IDetailsConstants.CURRENT_SONG_STATUS_STOP;

        ivAlbum.setImageUrl(getActivity(), songModel.getTrackImageUrl());
        tvAlbumArtist.setText(songModel.getTrackUser());
        tvAlbumTitle.setText(songModel.getTrackName());
    }

    @OnClick(R.id.back)
    public void close() {
        AppUtil.hideSoftKeyboard(getActivity());
        getActivity().onBackPressed();
    }

    @OnClick(R.id.btnShare)
    public void share() {
        FrndsLog.e(shareMsg.toString());
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareMsg.toString() );
        try {
            startActivity(whatsappIntent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "WhatsApp isn\'t installed", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSongModel(SongModel songModel) {
        this.songModel = songModel;
        shareMsg = new StringBuilder("Check out ");
        shareMsg.append(songModel.getTrackName())
                .append(" on SoundCloud ")
                .append(songModel.getTrackShareUrl())
                .append("Sync with your friends, download from ")
                .append(IConstants.APP_URL);
    }

    @OnClick(R.id.btnPlaySong)
    public void playSong() {
        if(status == IDetailsConstants.CURRENT_SONG_STATUS_STOP) {
            Intent playerServiceIntent = new Intent(getActivity(), PlayerService.class);
            playerServiceIntent.setAction(PlayerService.ACTION_PLAY);
            playerServiceIntent.putExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE, songModel.getTrackName());
            playerServiceIntent.putExtra(IDetailsConstants.SERVICE_STREAM_URL, songModel.getTrackUrl());
            playerServiceIntent.putExtra(IDetailsConstants.FRND_ID, frndId);
            getActivity().startService(playerServiceIntent);

            btnPlaySong.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.pause_big));

            status = IDetailsConstants.CURRENT_SONG_STATUS_PLAYING;

            FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_IMAGE_URL, songModel.getTrackImageUrl());
            FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_NAME, songModel.getTrackName());
            FrndsPreference.setInPref(IPrefConstants.LATEST_SONG_URL, songModel.getTrackUrl());
            FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_NAME, frndName);
            FrndsPreference.setInPref(IPrefConstants.LATEST_FRND_ID, frndId);
            FrndsPreference.setInPref(IPrefConstants.CURRENT_PLAYING_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_PLAYING);
        } else {
            Intent playerServiceIntent = new Intent(getActivity(), PlayerService.class);
            playerServiceIntent.setAction(PlayerService.ACTION_STOP);
            getActivity().startService(playerServiceIntent);

            status = IDetailsConstants.CURRENT_SONG_STATUS_STOP;

            btnPlaySong.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.play_big));
        }
    }
}
