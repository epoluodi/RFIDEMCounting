package com.honeywell.android.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;
import com.honeywell.android.data.generate.DaoSession;
import com.honeywell.android.data.generate.InventoryTaskDao;
import com.honeywell.android.data.generate.InventoryItemDao;

@Entity
public class InventoryItem {
    @Id(autoincrement = true) 
    private Long itemID;
    @NotNull
    private long inventoryTaskId;
    @NotNull
    private String epcID;
    @NotNull
    private boolean isCounted;
    @NotNull
    private long inventoryTime;
    @NotNull
    private String userName;
    private int failedReason;
    @Generated(hash = 1074823162)
    public InventoryItem(Long itemID, long inventoryTaskId, @NotNull String epcID,
            boolean isCounted, long inventoryTime, @NotNull String userName,
            int failedReason) {
        this.itemID = itemID;
        this.inventoryTaskId = inventoryTaskId;
        this.epcID = epcID;
        this.isCounted = isCounted;
        this.inventoryTime = inventoryTime;
        this.userName = userName;
        this.failedReason = failedReason;
    }

    @Generated(hash = 1953320296)
    public InventoryItem() {
    }

    public Long getItemID() {
        return this.itemID;
    }

    public void setItemID(Long itemID) {
        this.itemID = itemID;
    }

    public long getInventoryTaskId() {
        return this.inventoryTaskId;
    }

    public void setInventoryTaskId(long inventoryTaskId) {
        this.inventoryTaskId = inventoryTaskId;
    }

    public String getEpcID() {
        return this.epcID;
    }

    public void setEpcID(String epcID) {
        this.epcID = epcID;
    }

    public boolean getIsCounted() {
        return this.isCounted;
    }

    public void setIsCounted(boolean isCounted) {
        this.isCounted = isCounted;
    }

    public long getInventoryTime() {
        return this.inventoryTime;
    }

    public void setInventoryTime(long inventoryTime) {
        this.inventoryTime = inventoryTime;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getFailedReason() {
        return this.failedReason;
    }

    public void setFailedReason(int failedReason) {
        this.failedReason = failedReason;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "itemID=" + itemID +
                ", inventoryTaskId=" + inventoryTaskId +
                ", epcID='" + epcID + '\'' +
                ", isCounted=" + isCounted +
                ", inventoryTime=" + inventoryTime +
                ", userName='" + userName + '\'' +
                ", failedReason=" + failedReason +
                '}';
    }
}
