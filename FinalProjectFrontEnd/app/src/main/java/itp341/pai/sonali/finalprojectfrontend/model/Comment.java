package itp341.pai.sonali.finalprojectfrontend.model;

import java.util.Date;

/**
 * Created by Sonali Pai on 11/10/2017.
 */

public class Comment {

    //data members
    private Integer id;
    private String title;
    private String commentText;
    private Integer userId;
    private long bathroomId;

    public Comment(String title, String commentText, Integer userId, long bathroomId) {
        this.title = title;
        this.commentText = commentText;
        this.userId = userId;
        this.bathroomId = bathroomId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public long getBathroomId() {
        return bathroomId;
    }

    public void setBathroomId(long bathroomId) {
        this.bathroomId = bathroomId;
    }
}
