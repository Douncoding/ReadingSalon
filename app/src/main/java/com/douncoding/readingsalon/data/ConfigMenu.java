package com.douncoding.readingsalon.data;

public class ConfigMenu {
    public static final int TYPE_BASIC = 1;
    public static final int TYPE_TOGGLE = 2;
    int type = TYPE_BASIC;

    int iconRes;
    String title;
    String expend;
    boolean push;

    public ConfigMenu(int iconRes, String title) {
        this.iconRes = iconRes;
        this.title = title;
        this.type = TYPE_BASIC;
    }

    public ConfigMenu(int iconRes, String title, int type) {
        this.iconRes = iconRes;
        this.title = title;
        this.type = type;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getExpend() {
        return expend;
    }

    public void setExpend(String expend) {
        this.expend = expend;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }


}
