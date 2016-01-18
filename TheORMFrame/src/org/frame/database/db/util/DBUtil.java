package org.frame.database.db.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.frame.database.db.annotation.Column;
import org.frame.database.db.annotation.Table;
import org.frame.database.db.annotation.Column.ColumnType;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

@SuppressLint("DefaultLocale")
public class DBUtil {
  /**
   * 判断表是否存在
   * 
   * @param tabName
   * @return
   */
  public static boolean isExistTable(String tabName, SQLiteDatabase database) {
    boolean result = false;
    if (tabName == null) {
      return false;
    }
    Cursor cursor = null;
    try {
      String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
          + tabName.trim() + "'";
      cursor = database.rawQuery(sql, null);
      if (cursor.moveToNext()) {
        int count = cursor.getInt(0);
        if (count > 0) {
          result = true;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 创建表
   * 
   * @param db
   * @param clz
   */
  public static void createTable(SQLiteDatabase db, Class<?> clz, String tabName) {
    // 表是否存在
    boolean flag = isExistTable(tabName, db);
    if (!flag) {
      //创建表
      String sql = getCreateTableStmt(clz);
      //在id上创建索引
      String indexSql =
          "CREATE UNIQUE INDEX unique_"+tabName+"_id ON " + tabName + "(" + getIdColumnName(clz) + ")";
      if (!TextUtils.isEmpty(sql)) {
        // 创建表
        db.execSQL(sql);
        // 主键创建为索引，relace方法需要
        db.execSQL(indexSql);
      }
    }
  }

  /**
   * 获取某个类中的主键的value值
   * 
   * @param field
   * @param obj
   * @return
   * @throws Exception
   */
  private static String getIdValue(Field field, Object obj) throws Exception {
    String idName = getIdColumnName(field.getType());
    Field idField = obj.getClass().getDeclaredField(idName);
    idField.setAccessible(true);
    return idField.get(obj).toString();
  }

  /**
   * 返回一个ContentValues
   * 
   * @param clz
   * @return
   */
  @SuppressWarnings("unchecked")
  private static <T> ContentValues getContentValues(SQLiteDatabase db, T t) {
    ContentValues values = new ContentValues();
    try {
      Field[] fields = t.getClass().getDeclaredFields();
      for (Field field : fields) {
        if (field.isAnnotationPresent(Column.class)) {
          field.setAccessible(true);
          Column column = field.getAnnotation(Column.class);
          // 获取name
          String columnName = getColumnName(column, field);
          Class<?> fieldClz = field.getType();
          if (fieldClz == String.class) {
            values.put(columnName, field.get(t).toString());
          } else if (fieldClz == int.class || fieldClz == Integer.class) {
            values.put(columnName, field.getInt(t));
          } else if (fieldClz == boolean.class || fieldClz == Boolean.class) {
            values.put(columnName, field.get(t).toString());
          } else if (fieldClz == float.class || fieldClz == Float.class) {
            values.put(columnName, field.getFloat(t));
          } else if (fieldClz == long.class || fieldClz == Long.class) {
            values.put(columnName, field.getLong(t));
          } else if (fieldClz == double.class || fieldClz == Double.class) {
            values.put(columnName, field.getDouble(t));
          } else {
            /**
             * 特殊类型处理，如： 集合 对象
             */
            ColumnType columnType = column.type();
            if (columnType == ColumnType.TONE) {
              Object tone = field.get(t);
              if (tone != null && !column.isOne2One()) {
                /**
                 * 仅仅处理一对一的关系
                 */
                if (tone.getClass().isAnnotationPresent(Table.class)) {
                  saveOrUpdateObject(db, tone);
                  /**
                   * 将one的对象映射到数据库中为text类型，将tone的id值赋给one的value
                   */
                  String idValue = getIdValue(field, tone);
                  values.put(columnName, idValue);

                }
              } else {
                /**
                 * 处理一对一的关系，处理一方的外键插入
                 */
                if (tone != null) {
                  // 获取一方的主键id保存到另一方的表中
                  String idValue = tone.toString();
                  values.put(columnName, idValue);
                } else {
                  throw new NullPointerException("空指针异常!");
                }
              }

            } else if (columnType == ColumnType.SERIALIZABLE) {
              // byte[] bytes = SeriaUtil.enserializable(field.get(t));
              // values.put(columnName, bytes);
              String str = JsonUtil.toJson(field.get(t));
              values.put(columnName, str);
            } else if (columnType == ColumnType.TMANY) {
              // 如果是集合表示插入的一方的数据
              // 判断属性是否为List集合
              if (field.getType().isAssignableFrom(List.class)
                  || field.getType().isAssignableFrom(ArrayList.class)) {
                // 得到泛型
                List<T> tmanyList = (List<T>) field.get(t);
                if (tmanyList != null && tmanyList.size() > 0) {
                  // 批量插入
                  batchSaveOrUpdate(db, tmanyList);
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return values;
  }

  /**
   * 插入 or 更新表
   * 
   * @param db
   * @param clz
   */
  public static <T> void saveOrUpdateObject(SQLiteDatabase db, T t) {
    // 获取表名
    String tabName = getTableName(t.getClass());
    // 创建表
    createTable(db, t.getClass(), tabName);
    // 创建contentValues
    ContentValues values = getContentValues(db, t);
    if (values != null) {
      db.replace(tabName, null, values);
    }
  }

  /**
   * 批量插入 or 更新数据
   * 
   * @param db
   * @param list
   */
  public static <T> void batchSaveOrUpdate(SQLiteDatabase db, List<T> list) {
    db.beginTransaction();
    try {
      for (T t : list) {
        saveOrUpdateObject(db, t);
      }
      db.setTransactionSuccessful();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      db.endTransaction();
    }
  }

  /**
   * 按id查询出数据
   * 
   * @param db
   * @param id
   * @param clz
   * @return
   */
  private static <T> T queryById(SQLiteDatabase db, String id, Class<T> clz) {
    T t = null;
    // // 获取表名
    String tabName = getTableName(clz);
    // // 创建表
    // createTable(db, clz, tabName);
    String sql = "select * from " + tabName + " where " + getIdColumnName(clz) + "=?";
    Cursor cursor = db.rawQuery(sql, new String[] {id});
    if (cursor.moveToFirst()) {
      t = getOneObject(clz, cursor, db);
    }
    cursor.close();
    return t;
  }
  


  /**
   * 对外使用的查询接口
   * 
   * @param db
   * @param idKey
   * @param id
   * @param clz
   * @return
   */
  public static <T> T queryById(SQLiteDatabase db, String idKey, String id, Class<T> clz) {
    T t = null;
    // // 获取表名
    String tabName = getTableName(clz);
    // // 创建表
    // createTable(db, clz, tabName);
    String sql = "select * from " + tabName + " where " + idKey + "=?";
    Cursor cursor = db.rawQuery(sql, new String[] {id});
    if (cursor.moveToFirst()) {
      t = getOneObject(clz, cursor, db);
    }
    cursor.close();
    return t;
  }




  /**
   * 通过sql加载数据
   * 
   * @param db
   * @param sql
   * @param selectionArgs
   * @param clz
   * @return
   */
  public static <T> T queryObjBySelection(SQLiteDatabase db, String selection, String[] selectionArgs,
      Class<T> clz) {
    T t = null;
    String tabName = getTableName(clz);
    String sql = "select * from " + tabName + " where " + selection;
    Cursor cursor = db.rawQuery(sql, selectionArgs);
    if (cursor.moveToFirst()) {
      t = getOneObject(clz, cursor, db);
    }
    cursor.close();
    return t;
  }

  /**
   * 查询返回集合
   * 
   * @param db
   * @param clz
   * @param selection
   * @return
   */
  public static <T> List<T> queryList(SQLiteDatabase db, Class<T> clz) {
    List<T> list = new ArrayList<T>();
    // 获取表名
    String tabName = getTableName(clz);
    String sql = "select * from " + tabName;
    Cursor cursor = db.rawQuery(sql, null);
    while (cursor.moveToNext()) {
      T t = getOneObject(clz, cursor, db);
      list.add(t);
    }
    return list;
  }


  /**
   * 查询返回集合
   * 
   * @param db
   * @param clz
   * @param selection
   * @return
   */
  public static <T> List<T> queryListBySql(SQLiteDatabase db, Class<T> clz, String selection,String[] selectionArgs) {
    List<T> list = new ArrayList<T>();
    // 获取表名
    String tabName = getTableName(clz);
    String sql = "select * from " + tabName + " where " + selection;
    Cursor cursor = db.rawQuery(sql, selectionArgs);
    while (cursor.moveToNext()) {
      T t = getOneObject(clz, cursor, db);
      list.add(t);
    }
    return list;
  }

  /**
   * 通过外键查询
   * 
   * @param db
   * @param clz
   * @param foreignIdName
   * @param foreignId
   * @return
   */
  private static <T> List<T> queryListByForeignId(SQLiteDatabase db, Class<T> clz,
      String foreignIdName, String foreignId) {
    List<T> list = new ArrayList<T>();
    // 获取表名
    String tabName = getTableName(clz);
    // 创建表
    createTable(db, clz, tabName);
    String sql = "select * from " + tabName + " where " + foreignIdName + "=?";
    Cursor cursor = db.rawQuery(sql, new String[] {foreignId});
    while (cursor.moveToNext()) {
      T t = getOneObject(clz, cursor, db);
      list.add(t);
    }
    return list;
  }

  private static <T> String queryIdValue(Class<T> clz, Cursor cursor) {
    String idValue = null;
    Field[] fields = clz.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      if (field.isAnnotationPresent(Column.class)) {
        Column column = field.getAnnotation(Column.class);
        if (column.id()) {
          String columnName = getColumnName(column, field);
          idValue = cursor.getString(cursor.getColumnIndex(columnName));
          break;
        }
      }
    }
    return idValue;
  }

  /**
   * 将一个数据映射成一个对象
   * 
   * @param clz
   * @param cursor
   * @return
   */
  @SuppressWarnings("unchecked")
  private static <T> T getOneObject(Class<T> clz, Cursor cursor, SQLiteDatabase db) {
    T t = null;
    try {
      if (clz.isAnnotationPresent(Table.class)) {
        t = clz.newInstance();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
          field.setAccessible(true);
          if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            // 获取name
            String columnName = getColumnName(column, field);
            Class<?> fieldClz = field.getType();
            if (fieldClz == String.class) {
              field.set(t, cursor.getString(cursor.getColumnIndex(columnName)));
            } else if (fieldClz == int.class || fieldClz == Integer.class) {
              field.set(t, cursor.getInt(cursor.getColumnIndex(columnName)));
            } else if (fieldClz == boolean.class || fieldClz == Boolean.class) {
              field.set(t,
                  Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(columnName))));
            } else if (fieldClz == float.class || fieldClz == Float.class) {
              field.set(t, cursor.getFloat(cursor.getColumnIndex(columnName)));
            } else if (fieldClz == long.class || fieldClz == Long.class) {
              field.set(t, cursor.getLong(cursor.getColumnIndex(columnName)));
            } else if (fieldClz == double.class || fieldClz == Double.class) {
              field.set(t, cursor.getDouble(cursor.getColumnIndex(columnName)));
            } else {
              ColumnType columnType = column.type();
              if (columnType == ColumnType.TONE) {
                /**
                 * 获取此field对应的class的对象 field.getType() 对应的是类
                 */
                Object toneObj = null;
                if (!column.isOne2One()) {
                  if (field.getType().isAnnotationPresent(Table.class)) {
                    // 获取到主键的id
                    String toneId = cursor.getString(cursor.getColumnIndex(columnName));
                    // query the related table by toneId
                    toneObj = queryById(db, toneId, field.getType());
                  }
                  field.set(t, toneObj);
                } else {
                  field.set(t, cursor.getString(cursor.getColumnIndex(columnName)));
                }
              } else if (columnType == ColumnType.SERIALIZABLE) {
                // Object obj = SeriaUtil
                // .deserializable(cursor.getBlob(cursor.getColumnIndex(columnName)));
                if (fieldClz == List.class || fieldClz == ArrayList.class) {
                  List<T> list = JsonUtil.parsonJson2Obj(
                      cursor.getString(cursor.getColumnIndex(columnName)), field.getGenericType());

                  field.set(t, list);
                } else {
                  Object valueT = JsonUtil
                      .fromJson(cursor.getString(cursor.getColumnIndex(columnName)), fieldClz);
                  field.set(t, valueT);
                }
              } else if (columnType == ColumnType.TMANY) {
                // todo many
                /**
                 * 获取一方的主键值
                 */
                String idValue = queryIdValue(clz, cursor);
                /**
                 * 获取多方的外键名称并把一方的主键值保存到表中
                 */
                // 获取到list集合中的泛型的class
                Class<?> tmanyClz = getGeneric(field);
                /**
                 * 获取泛型对象中的外键名称
                 */
                String foreignName = getForeignKey(tmanyClz);
                /**
                 * 查询多方的表并把数据集合提取出来
                 */
                if (!TextUtils.isEmpty(idValue)) {
                  List<T> tmanyList =
                      (List<T>) queryListByForeignId(db, tmanyClz, foreignName, idValue);
                  if (tmanyList != null && tmanyList.size() > 0) {
                    field.set(t, tmanyList);
                  }
                }
                // else {
                // throw new NullPointerException("idValue is null");
                // }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return t;
  }

  private static String getForeignKey(Class<?> clz) {
    String foreignName = "";
    Field[] foreignFields = clz.getDeclaredFields();
    for (Field foreignField : foreignFields) {
      foreignField.setAccessible(true);
      if (foreignField.isAnnotationPresent(Column.class)) {
        Column foreignColumn = foreignField.getAnnotation(Column.class);
        if (foreignColumn.isOne2Many()) {
          foreignName = foreignField.getName();
          break;
        }
      }
    }
    return foreignName;
  }

  /**
   * 获取List集合中的泛型的Class类型
   * 
   * @param field
   * @return
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <T> Class<T> getGeneric(Field field) {
    // 判断是否为基本类型
    Class<?> fieldClz = field.getType();
    if (fieldClz.isPrimitive()) {
      return null;
    }
    if (fieldClz.isAssignableFrom(List.class)) {
      // 获取泛型
      Type fc = field.getGenericType();
      if (fc == null) {
        return null;
      }

      // 如果是泛型参数的类型
      if (fc instanceof ParameterizedType) {
        ParameterizedType pt = (ParameterizedType) fc;
        // 得到泛型里的class类型对象。
        Class<T> genericClz = (Class) pt.getActualTypeArguments()[0];
        return genericClz;
      }
    }
    return null;
  }

  /**
   * 删除某条数据
   * 
   * @param db
   * @param id
   * @param clz
   */
  public static void deleteColumnById(SQLiteDatabase db, String id, Class<?> clz) {
    // 获取表名
    String tabName = getTableName(clz);
    // 创建表
    createTable(db, clz, tabName);
    String sql = "delete from " + tabName + " where " + getIdColumnName(clz) + "=?";
    db.rawQuery(sql, new String[] {id});
  }

  /**
   * 通过反射机制获取到对象的Table注释的name作为表名
   * 
   * @param clz
   * @return
   */
  private static String getTableName(Class<?> clz) {
    // 是否有Table的注解
    if (clz.isAnnotationPresent(Table.class)) {
      // 获取到注解
      Table table = clz.getAnnotation(Table.class);
      String name = table.name();
      if (TextUtils.isEmpty(name)) {
        return name;
      } else {
        // 返回对象的名称作为表名
        return clz.getSimpleName().toLowerCase();
      }
    }

    throw new IllegalArgumentException("The class can't map to the table");
  }

  /**
   * 删除表
   * 
   * @param db
   * @param clz
   */
  public static void dropTable(SQLiteDatabase db, Class<?> clz) {
    String sql = getDropTableStmt(clz);
    if (!TextUtils.isEmpty(sql)) {
      db.execSQL(sql);
    }
  }

  /**
   * 拼接建表语句
   * 
   * @param clz
   * @return
   */
  private static String getCreateTableStmt(Class<?> clz) {
    StringBuilder mColumnStmt = new StringBuilder();
    String sql = "";
    // class中是否有Table的注解
    if (clz.isAnnotationPresent(Table.class)) {
      // 获取class的所有属性
      Field[] fields = clz.getDeclaredFields();
      for (Field field : fields) {
        // 只取被Column注解的属性，其他的不处理
        if (field.isAnnotationPresent(Column.class)) {
          // 给所有属性赋权限为public
          field.setAccessible(true);
          // 判断属性的类型，映射成相应的数据库的字段类型U
          String columnName = getOneColumnStmt(field);
          if (!TextUtils.isEmpty(columnName)) {
            mColumnStmt.append(columnName);
            mColumnStmt.append(",");
          }
        }
      }
    }
    String columntStr = mColumnStmt.toString();
    // 将末尾的逗号去掉
    if (!TextUtils.isEmpty(columntStr)) {
      columntStr = columntStr.substring(0, columntStr.length() - 1);
      sql = "create table if not exists " + getTableName(clz) + "(" + columntStr + ")";
    }

    return sql;
  }

  /**
   * 
   * @param field
   * @return
   */
  private static String getOneColumnStmt(Field field) {
    // 判断变量中是否有Column注解
    if (field.isAnnotationPresent(Column.class)) {
      Column column = field.getAnnotation(Column.class);
      // 获取name
      String name = getColumnName(column, field);
      /**
       * 判断每个变量的类型
       */
      String type = getColumnType(column, field);
      if (!TextUtils.isEmpty(type)) {
        name += " " + type;
      }
      // /**
      // * 判断变量是否是主键
      // */
      // if (column.id()) {
      // name += "PRIMARY KEY";
      // }
      return name;
    } else {
      // 如果没有Column，也可返回变量名称作为字段名称
      return field.getName();
    }
  }


  /**
   * 根据class生成字段名称
   * 
   * @param column
   * @return
   */
  private static String getColumnName(Column column, Field field) {
    String name = column.name();
    if (TextUtils.isEmpty(name)) {
      // 变量名称映射成字段名字
      name = field.getName();
    }
    return name;
  }

  /**
   * 获取每个字段的类型
   * 
   * @param column
   * @param field
   * @return
   */
  private static String getColumnType(Column column, Field field) {
    String type = null;
    Class<?> clz = field.getType();
    if (clz == String.class) {
      type = "TEXT";
    } else if (clz == int.class || clz == Integer.class) {
      type = "INTEGER";
    } else if (clz == float.class || clz == Float.class) {
      type = "INTEGER";
    } else if (clz == long.class || clz == Long.class) {
      type = "INTEGER";
    } else if (clz == boolean.class || clz == Boolean.class) {
      type = "INTEGER";
    } else {
      /**
       * 特殊类型处理，如： 集合 对象
       */
      ColumnType columnType = column.type();
      if (columnType == ColumnType.TONE) {
        type = "TEXT";
      } else if (columnType == ColumnType.SERIALIZABLE) {
        // type = "BLOB";
        type = "TEXT";
      } else if (columnType == ColumnType.TMANY) {
        // do nothing
        type = "TEXT";
      }
    }
    return type;
  }

  /**
   * 获取表的主键名称
   * 
   * @param clz
   * @return
   */
  private static String getIdColumnName(Class<?> clz) {
    if (clz.isAnnotationPresent(Table.class)) {
      Field[] fields = clz.getDeclaredFields();
      for (Field field : fields) {
        if (field.isAnnotationPresent(Column.class)) {
          field.setAccessible(true);
          Column column = field.getAnnotation(Column.class);
          if (column.id()) {
            if (!TextUtils.isEmpty(column.name())) {
              return column.name();
            } else {
              return field.getName();
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * 判断属性是否为主键值
   * 
   * @param field
   * @return
   */
  @SuppressWarnings("unused")
  private static boolean isIdColumn(Field field) {
    Column column = field.getAnnotation(Column.class);
    if (column.id()) {
      return true;
    }
    return false;
  }

  /**
   * 删除某一张表的语句
   * 
   * @param clz
   * @return
   */
  private static String getDropTableStmt(Class<?> clz) {
    return "drop table if exists " + getTableName(clz);
  }
  

  /**
   * 删除所有数据表
   */
  public static void dropDb(SQLiteDatabase db) {
    Cursor cursor = db.rawQuery(
        "SELECT name FROM sqlite_master WHERE type ='table' AND name != 'sqlite_sequence'", null);
    if (cursor != null) {
      while (cursor.moveToNext()) {
        db.execSQL("DROP TABLE " + cursor.getString(0));
      }
    }
    if (cursor != null) {
      cursor.close();
      cursor = null;
    }
  }
}
