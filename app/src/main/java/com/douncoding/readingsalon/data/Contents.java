package com.douncoding.readingsalon.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Contents {
    private int id;
    private int type;
    private String title;
    private String content;
    private String image;
    private String subject;
    private String overview;

    private String createdAt;
    private String updatedAt;

    private int commentCount;
    private int favoritesCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public String getCreatedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("KST"));

        try {
            Date date = sdf.parse(createdAt);
            return new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 hh:mm:ss", Locale.KOREA).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt;
        }
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA);
        sdf.setTimeZone(TimeZone.getTimeZone("KST"));

        try {
            Date date = sdf.parse(updatedAt);
            return new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 hh:mm:ss", Locale.KOREA).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return updatedAt;
        }
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
