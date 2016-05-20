package com.douncoding.readingsalon.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.douncoding.readingsalon.dao.Member;

public class Owner {
    public static final String TAG = Owner.class.getSimpleName();

    public static final String PREFERENCE_NAME = "owner";

    SharedPreferences mPreferences;

    public Owner(Context context) {
        mPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public int getId() {
        return load().getId().intValue();
    }

    public boolean isLogin() {
        return load().getId().intValue() > 0;
    }

    public boolean isWriter() {
        return load().getWriter() > 0;
    }

    public void store(Member member) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong("id", member.getId());
        editor.putString("name", member.getName());
        editor.putString("email", member.getEmail());
        editor.putInt("writer", member.getWriter());
        editor.apply();
    }

    public Member load() {
        Member member = new Member();

        member.setId(mPreferences.getLong("id", 0));
        member.setName(mPreferences.getString("name", null));
        member.setEmail(mPreferences.getString("email", null));
        member.setWriter(mPreferences.getInt("writer", 0));
        return member;
    }

    public void delete() {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("id", 0);
        editor.putString("name", null);
        editor.putString("email", null);
        editor.putInt("writer", 0);
        editor.apply();
    }
}
