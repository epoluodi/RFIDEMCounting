package com.honeywell.android.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.NotNull;
import com.honeywell.android.data.generate.DaoSession;
import com.honeywell.android.data.generate.InventoryItemDao;
import com.honeywell.android.data.generate.UserDao;
import com.honeywell.android.data.generate.InventoryTaskDao;


@Entity
public class InventoryTask implements Serializable {
    @Id(autoincrement = true)
    private Long taskId;
    @NotNull
    private String userName;
    @NotNull
    private String taskName;
    @NotNull
    private int inventoryState;
    @ToMany(referencedJoinProperty = "inventoryTaskId")
    public List<InventoryItem> inventoryList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 196911266)
    private transient InventoryTaskDao myDao;
    @Generated(hash = 1349380220)
    public InventoryTask(Long taskId, @NotNull String userName, @NotNull String taskName,
            int inventoryState) {
        this.taskId = taskId;
        this.userName = userName;
        this.taskName = taskName;
        this.inventoryState = inventoryState;
    }
    @Generated(hash = 564335267)
    public InventoryTask() {
    }
    public Long getTaskId() {
        return this.taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getTaskName() {
        return this.taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public int getInventoryState() {
        return this.inventoryState;
    }
    public void setInventoryState(int inventoryState) {
        this.inventoryState = inventoryState;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 810134195)
    public List<InventoryItem> getInventoryList() {
        if (inventoryList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            InventoryItemDao targetDao = daoSession.getInventoryItemDao();
            List<InventoryItem> inventoryListNew = targetDao
                    ._queryInventoryTask_InventoryList(taskId);
            synchronized (this) {
                if (inventoryList == null) {
                    inventoryList = inventoryListNew;
                }
            }
        }
        return inventoryList;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 751628847)
    public synchronized void resetInventoryList() {
        inventoryList = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 151353910)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getInventoryTaskDao() : null;
    }

    @Override
    public String toString() {
        return "InventoryTask{" +
                "taskId=" + taskId +
                ", userName='" + userName + '\'' +
                ", taskName='" + taskName + '\'' +
                ", inventoryState=" + inventoryState +
                ", inventoryList=" + getInventoryList() +
                '}';
    }
}
