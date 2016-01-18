package org.frame.database;

import org.frame.database.db.annotation.Column;
import org.frame.database.db.annotation.Table;

@Table(name = "developer")
public class Developer {
  @Column(id = true)
  private String id;

  @Column
  private String name;

  @Column
  private int age;


  @Column(isOne2Many = true)
  private String departmentId;

  @Override
  public String toString() {
    return id + "  " + name + "  " + age + "   " + departmentId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(String departmentId) {
    this.departmentId = departmentId;
  }
}
