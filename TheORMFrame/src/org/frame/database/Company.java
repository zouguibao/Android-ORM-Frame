package org.frame.database;

import org.frame.database.db.annotation.Column;
import org.frame.database.db.annotation.Table;

@Table(name = "company")
public class Company {
  
  
  @Column(id = true)
  private String id;
  @Column
  private String name;
  @Column
  private String url;
  @Column
  private String tel;
  @Column
  private String address;


  @Override
  public String toString() {
    return id + " " + name + " " + url + " " + tel + " " + address;
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

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTel() {
    return tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

 
}
