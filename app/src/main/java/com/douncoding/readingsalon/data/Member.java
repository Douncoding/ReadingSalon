package com.douncoding.readingsalon.data;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Member {
    int id;
    String name;
    String email;
    String password;
    int writer;

    public Member() {
        this.id = 0;
    }

    public boolean isLogin() {
        if (id > 0)
            return true;
        else
            return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isWriter() {
        if (writer > 0)
            return true;
        else
            return false;
    }

    public int getWriter() {
        return writer;
    }

    public void setWriter(int writer) {
        this.writer = writer;
    }
    public static Member newInstance(String jsonString) {
        Gson gson = new Gson();
        Member member = gson.fromJson(jsonString, Member.class);
        return member;
    }
}
