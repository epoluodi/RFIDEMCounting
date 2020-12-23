package com.honeywell.android.rfidemcounting.bean;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RFIDList extends RealmObject {
    @PrimaryKey
    private String id;
    private String epcid;
    private String name;
    private String state;
    private EmBean emlist;
    private String emtime;
    private String reason;
    private String emname;
    private boolean isem=false;

    public RFIDList() {
        this.id = UUID.randomUUID().toString();;
    }


    public EmBean getEmlist() {
        return emlist;
    }

    public String getEmtime() {
        return emtime;
    }

    public boolean isIsem() {
        return isem;
    }

    public void setIsem(boolean isem) {
        this.isem = isem;
    }

    public String getEmname() {
        return emname;
    }

    public void setEmname(String emname) {
        this.emname = emname;
    }

    public void setEmtime(String emtime) {
        this.emtime = emtime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setEmlist(EmBean emlist) {
        this.emlist = emlist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEpcid() {
        return epcid;
    }

    public void setEpcid(String epcid) {
        this.epcid = epcid;
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
}
