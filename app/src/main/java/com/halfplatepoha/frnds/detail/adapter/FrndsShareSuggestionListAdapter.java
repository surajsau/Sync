package com.halfplatepoha.frnds.detail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.models.User;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by surajkumarsau on 14/09/16.
 */
public class FrndsShareSuggestionListAdapter extends RecyclerView.Adapter<FrndsShareSuggestionListAdapter.FrndsShareSuggestionViewHolder>{

    private Context mContext;
    private ArrayList<User> mFriends;

    public FrndsShareSuggestionListAdapter(Context context) {
        mContext = context;
        mFriends = new ArrayList<>();
    }

    @Override
    public FrndsShareSuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View frndRowView = LayoutInflater.from(mContext).inflate(R.layout.row_frnds_share_suggestion_list, parent, false);
        return new FrndsShareSuggestionViewHolder(frndRowView);
    }

    @Override
    public void onBindViewHolder(FrndsShareSuggestionViewHolder holder, int position) {
    }

    public void addFriend(User user) {
        mFriends.add(user);
        notifyItemInserted(mFriends.size() - 1);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class FrndsShareSuggestionViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.ivFrndAvatar) CircleImageView ivFrndAvatar;
        @Bind(R.id.tvFrndName) OpenSansTextView tvFrndName;

        public FrndsShareSuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rowFrndShareSuggestion)
        public void playWithAnotherFriend() {

        }
    }
}
