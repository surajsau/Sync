package com.halfplatepoha.frnds.search.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.search.activity.SearchScreenActivity;
import com.halfplatepoha.frnds.ui.AutoLoadingRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by surajkumarsau on 25/08/16.
 */
public class SearchResultAdapter extends AutoLoadingRecyclerAdapter<TrackDetails, SearchResultAdapter.SearchResultViewHolder> {

    private Context mContext;

    private ArrayList<TrackDetails> mList;

    public SearchResultAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(mContext).inflate(R.layout.row_search_result, parent, false);
        return new SearchResultViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        if(mList.get(position) != null) {
            holder.tvTitle.setText(mList.get(position).getTitle());
            Glide.with(mContext)
                    .load(mList.get(position).getArtwork_url())
                    .into(holder.ivResultIcon);
            holder.row.setTag(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<TrackDetails> tracks) {
        if(!mList.isEmpty()) {
            int size = mList.size();

            mList.clear();
            notifyItemRangeRemoved(0, size);
        }

        mList.addAll(tracks);
        notifyItemRangeInserted(0, tracks.size());
    }

    public void addItemToList(TrackDetails details) {
        mList.add(details);
        notifyItemInserted(mList.size() - 1);
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.ivResultIcon) ImageView ivResultIcon;
        @Bind(R.id.tvTitle) TextView tvTitle;
        @Bind(R.id.row) View row;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            row.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.row:
                    TrackDetails trackDetails = (TrackDetails) view.getTag();
                    openSongDetailsActivity(trackDetails);
                    break;
            }
        }
    }

    private void openSongDetailsActivity(TrackDetails trackDetails) {
        Intent songDetail = new Intent();
        songDetail.putExtra(IDetailsConstants.TRACK_ID, trackDetails.getId());
        songDetail.putExtra(IDetailsConstants.TRACK_ARTIST, trackDetails.getUser().getUsername());
        songDetail.putExtra(IDetailsConstants.TRACK_URL, trackDetails.getStream_url());
        songDetail.putExtra(IDetailsConstants.TRACK_IMAGE_URL, trackDetails.getArtwork_url());
        songDetail.putExtra(IDetailsConstants.TRACK_SHARE_URL, trackDetails.getPermalink_url());
        songDetail.putExtra(IDetailsConstants.TRACK_TITLE, trackDetails.getTitle());
        ((SearchScreenActivity)mContext).setResult(Activity.RESULT_OK, songDetail);
        ((SearchScreenActivity)mContext).onBackPressed();
    }
}
