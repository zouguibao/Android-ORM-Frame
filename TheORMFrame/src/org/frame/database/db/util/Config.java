package org.frame.database.db.util;

import org.frame.database.db.OnUpdateListener;

import android.content.Context;

public class Config {
  public Context context;
  public String dbName;
  public int dbVersion;
  public OnUpdateListener listener;
}
