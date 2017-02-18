package com.halfplatepoha.frnds.detail.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.model.MessageModel;
import com.halfplatepoha.frnds.ui.OpenSansButton;
import com.halfplatepoha.frnds.ui.OpenSansTextView;
import com.halfplatepoha.frnds.utils.AppUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by surajkumarsau on 31/08/16.
 */
public class ChatAdapter extends RecyclerView.Adapter {

    private ArrayList<MessageModel> mMessages;
    private Context mContext;

    public ChatAdapter(Context context) {
        mContext = context;
        mMessages = new ArrayList<>();
    }

    private OnPlayClickListener listener;

    public void setOnPlayClickListener(OnPlayClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case IDetailsConstants.TYPE_ME:{
                View meChat = LayoutInflater.from(mContext).inflate(R.layout.row_chat_me, parent, false);
                return new MeChatViewHolder(meChat);
            }

            case IDetailsConstants.TYPE_FRND:{
                View frndChat = LayoutInflater.from(mContext).inflate(R.layout.row_chat_frnd, parent, false);
                return new FrndChatViewHolder(frndChat);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(mMessages.get(position) != null) {
            if(holder instanceof MeChatViewHolder) {
                ((MeChatViewHolder) holder).tvMeMsg.setText(mMessages.get(position).getMessage());
                ((MeChatViewHolder) holder).btnPlayPause
                        .setVisibility(mMessages.get(position).getMessageType() == IDbConstants.TYPE_MUSIC ?
                                            View.VISIBLE : View.GONE);
            } else if(holder instanceof FrndChatViewHolder) {
                ((FrndChatViewHolder) holder).tvFrndMessage.setText(mMessages.get(position).getMessage());
                ((FrndChatViewHolder) holder).btnPlayPause
                        .setVisibility(mMessages.get(position).getMessageType() == IDbConstants.TYPE_MUSIC ?
                                View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mMessages == null)
            return 0;
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mMessages.get(position).getUserType();
    }

    public class MeChatViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvMeMsg) OpenSansTextView tvMeMsg;
        @Bind(R.id.btnPlayPause) ImageButton btnPlayPause;
        @Bind(R.id.btnRetry) OpenSansButton btnRetry;

        public MeChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btnPlayPause)
        public void onPlayPauseClicked() {
            int position = getAdapterPosition();
            listener.onPlayClick(mMessages.get(position).getMessageTrackUrl(),
                    mMessages.get(position).getMessage(),
                    position);
        }

        @OnClick(R.id.btnRetry)
        public void retrySend() {

        }
    }

    public void addMessage(MessageModel msg) {
        if(mMessages == null)
            mMessages = new ArrayList<>();
        mMessages.add(msg);
        notifyItemInserted(mMessages.size() - 1);
    }

    public void refresh() {
        if(mMessages != null) {
            mMessages.clear();
        }
        notifyDataSetChanged();
    }

    public class FrndChatViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tvFrndMsg) OpenSansTextView tvFrndMessage;
        @Bind(R.id.btnPlayPause) ImageButton btnPlayPause;

        public FrndChatViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btnPlayPause)
        public void onPlayPauseClicked() {
            int position = getAdapterPosition();
            listener.onPlayClick(mMessages.get(position).getMessageTrackUrl(),
                    mMessages.get(position).getMessage(),getAdapterPosition());
        }

    }

    public interface OnPlayClickListener {
        void onPlayClick(String trackUrl, String message, int position);
    }

}
