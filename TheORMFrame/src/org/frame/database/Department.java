package org.frame.database;


import java.util.List;

import org.frame.database.db.annotation.Column;
import org.frame.database.db.annotation.Table;
import org.frame.database.db.annotation.Column.ColumnType;

@Table(name = "department")
public class Department {

  @Column(id = true)
  private String id;
  @Column
  private String name;
  @Column
  private String desc;
  
  @Column(type = ColumnType.TMANY)
  private List<Developer> developers;

  @Column(type = ColumnType.SERIALIZABLE)
  private List<Skill> skills;
  
  @Column(type = ColumnType.SERIALIZABLE)
  private Skill skill;
  
  @Column(type = ColumnType.TONE)
  private DeptManager manager;
  
  
  
  
  public Skill getSkill() {
    return skill;
  }

  public void setSkill(Skill skill) {
    this.skill = skill;
  }

  public List<Developer> getDevelopers() {
    return developers;
  }

  public void setDevelopers(List<Developer> developers) {
    this.developers = developers;
  }

  public List<Skill> getSkills() {
    return skills;
  }

  public void setSkills(List<Skill> skills) {
    this.skills = skills;
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

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  // public List<Developer> getDevelopers() {
  // return developers;
  // }
  //
  // public void setDevelopers(List<Developer> developers) {
  // this.developers = developers;
  // }

  public DeptManager getManager() {
    return manager;
  }

  public void setManager(DeptManager manager) {
    this.manager = manager;
  }


}
