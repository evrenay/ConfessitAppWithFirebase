package com.confessit;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import confessit.evren.com.confessit.R;

/**
 * Created by EVREN on 17.4.2018.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userComment;
        public TextView userEmail;
        public ImageView postImage;
        public CardView card_view;


        public ViewHolder(View view) {
            super(view);

            card_view = (CardView)view.findViewById(R.id.post_cardview);
            userComment = (TextView)view.findViewById(R.id.post_comment);
            userEmail = (TextView)view.findViewById(R.id.post_nick);
            postImage = (ImageView)view.findViewById(R.id.post_photos);

        }
    }

    ArrayList<Posts> postsList;
    CustomItemClickListener listener;
    public PostsAdapter(ArrayList<Posts> postsList, CustomItemClickListener listener) {

        this.postsList = postsList;
        this.listener = listener;
    }
    @Override
    public int getItemViewType(int position) {
        Posts posts = postsList.get(position);
        return posts.getType();
    }
    @Override
    public PostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = -1;
        switch (viewType) {
            case Posts.TYPE_MESSAGE_COMMENT:
                layout = R.layout.custom_post_comment;
                break;
            case Posts.TYPE_MESSAGE_PHOTO:
                layout = R.layout.custom_post;
                break;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getLayoutPosition());
            }
        });

        return view_holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Posts posts = postsList.get(position);
        if(posts.getType()==1){
            holder.userComment.setText(posts.getUserComment());
            holder.userEmail.setText(posts.getUserEmail());
            Picasso.get().load(posts.getImageUrl()).into(holder.postImage);
        }//photo
        else if(posts.getType()==2){
            holder.userComment.setText(posts.getUserComment());
            holder.userEmail.setText(posts.getUserEmail());
        } //only comment

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
