package com.example.deadzonemobiledetection.app;

/**
 * Created by informix on 20/6/2014.
 */
public class DrawerModel {
    private int icon;
    private String title;
    private String counter;

    private boolean isGroupHeader = false;

    public DrawerModel(String title) {
        this(-1,title,null);
        isGroupHeader = true;
    }
    public DrawerModel(int icon, String title, String counter) {
        super();
        this.icon = icon;
        this.title = title;
        this.counter = counter;
    }

    public boolean isGroupHeader() {
        return isGroupHeader;
    }

    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
