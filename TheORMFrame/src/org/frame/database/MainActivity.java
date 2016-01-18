package org.frame.database;


import java.util.ArrayList;
import java.util.List;

import org.frame.database.db.manager.DBManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    DBManager.getInstance();
    addDepartment();
    queryDepartment();
    
//    addCompany();
//    queryCompany();
  }


  public void queryDepartment() {
    Department department = DBManager.getInstance().queryById("id", "00003", Department.class);
    if (department != null) {
      Log.e("zouguibao", "id = " + department.getId() + " name = " + department.getName()
          + " desc = " + department.getDesc());
      
      
      Log.e("zouguibao", "skill = " + department.getSkill().toString());
      
      for (Skill skill : department.getSkills()) {
        Log.e("zouguibao", "skills = " + skill.toString());
      }
      
      Log.e("zouguibao", "managerId = " + department.getManager().toString());
      
      
      for (Developer d : department.getDevelopers()) {
        Log.e("zouguibao", "Developers = " + d.toString());
      }
    }
  }

  public void addDepartment() {
    Department department = new Department();
    department.setId("00003");
    department.setName("创新工厂部");
    department.setDesc("这是一个神奇的部门");
    DeptManager manager = new DeptManager();
    manager.id = 1000;
    manager.name = "Noah";
    manager.age = 40;
    department.setManager(manager);
    
    Skill ski = new Skill();
    ski.setName("skill10" );
    ski.setDesc("这是我掌握的第10项技术");
    department.setSkill(ski);

    List<Skill> skills = new ArrayList<Skill>();
    for (int k = 0; k < 3; k++) {
      Skill skill = new Skill();
      skill.setName("skill" + k);
      skill.setDesc("这是我掌握的第" + (k + 1) + "项技术");
      skills.add(skill);
    }
    department.setSkills(skills);
    
    List<Developer> developers = new ArrayList<Developer>();
    for (int k = 0; k < 3; k++) {
      Developer developer = new Developer();
      developer.setId("001"+(k+1));
      developer.setName("Developer" + k);
      developer.setAge(20+k);
      developer.setDepartmentId(department.getId());
      developers.add(developer);
    }
    department.setDevelopers(developers);
    DBManager.getInstance().saveOrUpdate(department);
  }
  
  
  
  public void addCompany(){
    List<Company> list = new ArrayList<Company>();
    for (int i = 0; i < 5; i++) {
      Company company = new Company();
      company.setId("001"+(i+1));
      company.setName("宜信我在修改的地方"+i);
      company.setUrl("http://www.baodu.com"+i);
      company.setTel("123445677"+i);
      company.setAddress("朝阳区朗园路"+i);
      list.add(company);
    }
Log.e("zouguibao", "list size = "+list.size());
    DBManager.getInstance().batchSaveOrUpdate(list);
  }
  
  public void queryCompany(){
    List<Company> lists = DBManager.getInstance().queryAll(Company.class);
    
    for (Company c : lists) {
Log.e("zouguibao", "Company = " + c.toString());
    }
  }

  
}
