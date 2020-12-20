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
    private EmList emlist;

    public RFIDList() {
        this.id = UUID.randomUUID().toString();;
    }


    public EmList getEmlist() {
        return emlist;
    }

    public void setEmlist(EmList emlist) {
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
