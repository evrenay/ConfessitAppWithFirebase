package com.confessit;

/**
 * Created by EVREN on 17.4.2018.
 */

public class Posts {

    private String userComment;
    private String imageUrl;
    private String userEmail;

    public  Posts ( String userComment, String imageUrl, String userEmail){
        this.userComment = userComment;
        this.imageUrl = imageUrl;
        this.userEmail = userEmail;
    }


    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
