package com.halfplatepoha.frnds.home.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.models.Chat;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;
import com.halfplatepoha.frnds.home.IFrndsConstants;
import com.halfplatepoha.frnds.home.fragment.FriendDetailDialogFragment;
import com.halfplatepoha.frnds.home.activity.HomeActivity;
import com.halfplatepoha.frnds.home.model.ChatListingModel;
import com.halfplatepoha.frnds.models.User;
import com.halfplatepoha.frnds.models.fb.InstalledFrnds;
import com.halfplatepoha.frnds.ui.OpenSansTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

import static com.halfplatepoha.frnds.R.drawable.chat;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.FriendViewHolder>{

    private Context mContext;
    private ArrayList<ChatListingModel> mFriends;
    private FragmentManager mFragmentManager;

    private OnRowClickListener listener;

    public FriendsListAdapter(Context context) {
        mContext = context;

        mFriends = new ArrayList<>();

        mFragmentManager = ((HomeActivity)mContext).getSupportFragmentManager();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View friendRowView = LayoutInflater.from(mContext).inflate(R.layout.row_frnds_list, parent, false);
        return new FriendViewHolder(friendRowView);
    }

    public void setListener(OnRowClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        if(mFriends.get(position) != null){
            Glide.with(mContext)
                    .load(mFriends.get(position).getFrndImageUrl())
                    .error(R.drawable.pappi)
                    .into(holder.ivFrndAvatar);

            holder.tvFrndName.setText(mFriends.get(position).getFrndName());

            holder.tvFrndStatus.setText(mFriends.get(position).getLastMessage());
            holder.tvFrndStatus.setTextColor(ContextCompat.getColor(mContext, mFriends.get(position).isMessageRead() ? R.color.textRead : R.color.textUnread));

            holder.ivIndicator.setVisibility(mFriends.get(position).isMessageRead() ? View.GONE: View.VISIBLE);
        }
    }

    public void addChat(Chat chat) {
        ChatListingModel chatModel = new ChatListingModel(chat);
        mFriends.add(chat.getFrndPosition(), chatModel);
        notifyItemInserted(mFriends.size() - 1);
    }

    @Override
    public int getItemCount() {
        if(mFriends == null)
            return 0;
        return mFriends.size();
    }

    public void refreshChat(boolean fromNotification, String frndId, String lastMessage, long timestamp) {
        int position = getPositionFromId(frndId);
        if(position != -1) {
            ChatListingModel chat = mFriends.get(position);
            if (timestamp != chat.getLastTimestamp()) {
                chat.setFrndPosition(0);
                chat.setLastMessage(lastMessage);
                chat.setLastTimestamp(timestamp);
                chat.setMessageRead(!fromNotification);

                mFriends.remove(position);
                mFriends.add(0, chat);
                notifyItemMoved(position, 0);
                notifyItemChanged(0);
            } else {
                chat.setMessageRead(true);
                notifyItemChanged(position);
            }
        }
    }

    public int getPositionFromId(String frndId) {
        for(int i=0; i<mFriends.size(); i++) {
            if(mFriends.get(i).getFrndId().equalsIgnoreCase(frndId))
                return i;
        }
        return -1;
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.ivFrndAvatar) CircleImageView ivFrndAvatar;
        @Bind(R.id.tvFrndName) OpenSansTextView tvFrndName;
        @Bind(R.id.tvFrndStatus) OpenSansTextView tvFrndStatus;
        @Bind(R.id.ivIndicator) ImageView ivIndicator;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rowFriend)
        public void openSongDetails() {
            ChatListingModel frnd = mFriends.get(getAdapterPosition());
            Pair<View, String> avatarTransition = Pair.create((View)ivFrndAvatar, mContext.getString(R.string.frnd_avatar_transition));
            Pair<View, String> nameTransition = Pair.create((View)tvFrndName, mContext.getString(R.string.frnd_name_transition));

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation((Activity) mContext, avatarTransition, nameTransition);

            listener.onRowClick(options, frnd.getFrndId());
        }

        @OnClick(R.id.ivFrndAvatar)
        public void openDetailDialog() {
            ChatListingModel frnd = mFriends.get(getAdapterPosition());
            FriendDetailDialogFragment dlgDetail = new FriendDetailDialogFragment();
            Bundle dlgBundle = new Bundle();
            dlgBundle.putString(IFrndsConstants.FRIEND_NAME, frnd.getFrndName());
            dlgBundle.putString(IFrndsConstants.FRIEND_IMAGE_URL, frnd.getFrndImageUrl());
            dlgDetail.setArguments(dlgBundle);
            dlgDetail.show(mFragmentManager, IFrndsConstants.DETAIL_DIALOG_TAG);
        }

    }

    public interface OnRowClickListener {
        void onRowClick(ActivityOptionsCompat options, String frndId);
    }

}
