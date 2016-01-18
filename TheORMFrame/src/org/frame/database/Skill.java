package org.frame.database;

import java.io.Serializable;

public class Skill implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 650686987959339743L;

	private String name;
	private String desc;
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
	
	
	@Override
	public String toString() {
	return "name = "+name+"  desc = "+desc;
	}
}
