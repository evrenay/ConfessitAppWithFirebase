package com.confessit;

/**
 * Created by EVREN on 17.4.2018.
 */

public class Posts {
    public static final int TYPE_MESSAGE_PHOTO = 1;
    public static final int TYPE_MESSAGE_COMMENT = 2;
    private String userComment;
    private String imageUrl;
    private String userEmail;
    private int mType;

    private Posts(){}

    public int getType() {
        return mType;
    };
    public String getUserComment(){ return userComment;}
    public String getUserEmail(){ return userEmail;}
    public String getImageUrl(){ return imageUrl;}
    public static class Builder {
        private int mType;
        private String userComment;
        private String imageUrl;
        private String userEmail;
        public Builder(int type) {
            mType = type;
        }
        public Builder userComment(String mUserComment) {
           userComment=mUserComment;
            return this;
        }
        public Builder imageUrl(String mImageUrl) {
            imageUrl = mImageUrl;
            return this;
        }
        public Builder userEmail(String mUserEmail) {
            userEmail = mUserEmail;
            return this;
        }
        public Posts build() {
            Posts posts = new Posts();
            posts.mType = mType;
            posts.userEmail=userEmail;
            posts.userComment = userComment;
            posts.imageUrl = imageUrl;
            return posts;
        }


    }
}
