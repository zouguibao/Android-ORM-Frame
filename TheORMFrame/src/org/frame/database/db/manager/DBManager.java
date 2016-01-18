package org.frame.database.db.manager;

import java.util.List;

import org.frame.database.db.OnUpdateListener;
import org.frame.database.db.util.Config;
import org.frame.database.db.util.DBUtil;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DBManager {
  public static DBManager instance;
  private DBHelper dbHelper;

  private DBManager() {
    super();
  }

  public static DBManager getInstance() {
    if (instance == null) {
      instance = new DBManager();
    }
    return instance;
  }

  /**
   * 初始化数据库，建议在application中调用
   */
  public void init(Config config) {
    if (dbHelper == null) {
      if (config.listener != null) {
        dbHelper = new DBHelper(config.context, config.dbName, null, config.dbVersion, null,
            config.listener);
      } else {
        dbHelper = new DBHelper(config.context, config.dbName, null, config.dbVersion, null);
      }
    }
  }


  /**
   * 插入或更新某条记录
   * @param t
   */
  public <T> void saveOrUpdate(T t) {
    DBUtil.saveOrUpdateObject(dbHelper.getWritableDatabase(), t);
  }

  /**
   * 批量插入或删除记录
   * @param list
   */
  public <T> void batchSaveOrUpdate(List<T> list) {
    DBUtil.batchSaveOrUpdate(dbHelper.getWritableDatabase(), list);
  }


  /**
   * 根据id查询某条记录
   * 
   * @param idKey
   * @param id
   * @param clz
   * @return
   */
  public <T> T queryById(String idKey, String id, Class<T> clz) {
    T t = DBUtil.queryById(dbHelper.getReadableDatabase(), idKey, id, clz);
    return t;
  }

  /**
   * 根据查询添加获取某条记录
   * @param selection
   * @param selectionArgs
   * @param clz
   * @return
   */
  public <T> T queryObjBySelection(String selection, String[] selectionArgs,
      Class<T> clz){
    T t = DBUtil.queryObjBySelection(dbHelper.getReadableDatabase(), selection, selectionArgs, clz);
    return t;
  }

  /**
   * 查询所有
   * 
   * @param clz
   * @return
   */
  public <T> List<T> queryAll(Class<T> clz) {
    List<T> list = DBUtil.queryList(dbHelper.getReadableDatabase(), clz);
    return list;
  }

  /**
   * 传入查询条件查询结果
   * 
   * @param selection
   * @param selectionArgs
   * @param clz
   * @return
   */
  public <T> List<T> queryListBySelection(String selection, String[] selectionArgs, Class<T> clz) {
    List<T> list =
        DBUtil.queryListBySql(dbHelper.getReadableDatabase(), clz, selection, selectionArgs);
    return list;
  }
  
  /**
   * 清除某条记录
   * @param id
   * @param clz
   */
  public void deleteObjById(String id,Class<?> clz){
    DBUtil.deleteColumnById(dbHelper.getWritableDatabase(),  id,  clz);
  }

  /**
   * 删除某张表
   * @param clz
   */
  public void dropTable(Class<?> clz){
    DBUtil.dropTable(dbHelper.getWritableDatabase(), clz);
  }

  /**
   * 删除所有表
   * 
   * @param clz
   */
  public void dropAllTable() {
    DBUtil.dropDb(dbHelper.getWritableDatabase());
  }


  public class DBHelper extends SQLiteOpenHelper {

    private OnUpdateListener mListener;

    public DBHelper(Context context, String name, CursorFactory factory, int version,
        DatabaseErrorHandler errorHandler) {
      super(context, name, factory, version, errorHandler);
    }

    public DBHelper(Context context, String name, CursorFactory factory, int version,
        DatabaseErrorHandler errorHandler, OnUpdateListener mListener) {
      this(context, name, factory, version, errorHandler);
      this.mListener = mListener;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      if (mListener != null) {
        mListener.onUpgrade(db, oldVersion, newVersion);
      } 
      
//      else {
//        DBUtil.dropDb(db);
//      }
    }

  }
}
