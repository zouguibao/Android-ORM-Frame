package org.frame.database;

import org.frame.database.db.annotation.Column;
import org.frame.database.db.annotation.Table;

@Table(name = "dept_manager")
public class DeptManager {
  
  
  @Column(id = true)
  public int id;
  @Column
  public String name;
  @Column
  public int age;


  @Override
  public String toString() {
    return " id=" + id + " name=" + name + " age=" + age;
  }
}
