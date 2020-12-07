package com.honeywell.android.data.generate;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.honeywell.android.data.model.InventoryTask;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "INVENTORY_TASK".
*/
public class InventoryTaskDao extends AbstractDao<InventoryTask, Long> {

    public static final String TABLENAME = "INVENTORY_TASK";

    /**
     * Properties of entity InventoryTask.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property TaskId = new Property(0, Long.class, "taskId", true, "_id");
        public final static Property UserName = new Property(1, String.class, "userName", false, "USER_NAME");
        public final static Property TaskName = new Property(2, String.class, "taskName", false, "TASK_NAME");
        public final static Property InventoryState = new Property(3, int.class, "inventoryState", false, "INVENTORY_STATE");
    }

    private DaoSession daoSession;


    public InventoryTaskDao(DaoConfig config) {
        super(config);
    }
    
    public InventoryTaskDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"INVENTORY_TASK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: taskId
                "\"USER_NAME\" TEXT NOT NULL ," + // 1: userName
                "\"TASK_NAME\" TEXT NOT NULL ," + // 2: taskName
                "\"INVENTORY_STATE\" INTEGER NOT NULL );"); // 3: inventoryState
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"INVENTORY_TASK\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, InventoryTask entity) {
        stmt.clearBindings();
 
        Long taskId = entity.getTaskId();
        if (taskId != null) {
            stmt.bindLong(1, taskId);
        }
        stmt.bindString(2, entity.getUserName());
        stmt.bindString(3, entity.getTaskName());
        stmt.bindLong(4, entity.getInventoryState());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, InventoryTask entity) {
        stmt.clearBindings();
 
        Long taskId = entity.getTaskId();
        if (taskId != null) {
            stmt.bindLong(1, taskId);
        }
        stmt.bindString(2, entity.getUserName());
        stmt.bindString(3, entity.getTaskName());
        stmt.bindLong(4, entity.getInventoryState());
    }

    @Override
    protected final void attachEntity(InventoryTask entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public InventoryTask readEntity(Cursor cursor, int offset) {
        InventoryTask entity = new InventoryTask( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // taskId
            cursor.getString(offset + 1), // userName
            cursor.getString(offset + 2), // taskName
            cursor.getInt(offset + 3) // inventoryState
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, InventoryTask entity, int offset) {
        entity.setTaskId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserName(cursor.getString(offset + 1));
        entity.setTaskName(cursor.getString(offset + 2));
        entity.setInventoryState(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(InventoryTask entity, long rowId) {
        entity.setTaskId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(InventoryTask entity) {
        if(entity != null) {
            return entity.getTaskId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(InventoryTask entity) {
        return entity.getTaskId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
