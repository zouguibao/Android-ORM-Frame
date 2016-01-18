package org.frame.database.db;

import android.database.sqlite.SQLiteDatabase;

public interface OnUpdateListener {
  void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
