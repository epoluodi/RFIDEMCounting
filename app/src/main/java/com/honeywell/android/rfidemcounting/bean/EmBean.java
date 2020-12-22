package com.honeywell.android.rfidemcounting.bean;

import java.sql.Time;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EmBean extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String state;
    private String username;
    private RealmList<RFIDList> rfidList;
    private String time;
    private boolean selected=false;
    public EmBean() {

            this.id = UUID.randomUUID().toString();;

    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public RealmList<RFIDList> getRfidList() {
        return rfidList;
    }

    public void setRfidList(RealmList<RFIDList> rfidList) {
        this.rfidList = rfidList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
