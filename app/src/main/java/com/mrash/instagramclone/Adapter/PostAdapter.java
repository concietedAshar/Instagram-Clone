package com.mrash.instagramclone.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hendraanggrian.appcompat.widget.SocialTextView;
import com.mrash.instagramclone.R;

public class PostAdapter {

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public ImageView imgProfile;
        public  ImageView postImage;
        public  ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;
        public TextView username;
        public TextView noOfLikes;
        public TextView auther;
        public TextView noOfComments;
        SocialTextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.image_profile);
            postImage = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);
            username = itemView.findViewById(R.id.username);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            auther = itemView.findViewById(R.id.author);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            description = itemView.findViewById(R.id.description);


        }
    }
}
