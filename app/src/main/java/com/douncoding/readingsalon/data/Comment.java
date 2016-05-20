package com.douncoding.readingsalon.data;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Comment {
    private int id;         // 댓글 번호
    private int cid;        // 포스트 번호
    private String updatedAt; // 작성일자
    private String content; // 내용

    private int mid;        // 회원 번호
    private String name;    // 회원 이름

    public Comment() {}

    public int getId() {
        return this.id;
    }

    public int getMemberId() {
        return mid;
    }

    public int getContentsId() {
        return cid;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        Date date;

        SimpleDateFormat informat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.KOREA);

        SimpleDateFormat outformat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.KOREAN);

        try {
            date = informat.parse(updatedAt);
            return outformat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public void setName(String name) {
        this.name = name;
    }
}