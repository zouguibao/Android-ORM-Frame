package org.frame.database;

import org.frame.database.db.OnUpdateListener;
import org.frame.database.db.manager.DBManager;
import org.frame.database.db.util.Config;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class BaseApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Config config = new Config();
    config.context = getApplicationContext();
    config.dbName = "demo.db";
    config.dbVersion = 1;
    config.listener = new OnUpdateListener() {
      
      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
      }
    };
    DBManager.getInstance().init(config);
  }
}
