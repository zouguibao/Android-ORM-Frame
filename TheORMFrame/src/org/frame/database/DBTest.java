package org.frame.database;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.frame.database.db.manager.DBManager;

import android.test.AndroidTestCase;
import android.util.Log;

public class DBTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DBManager.getInstance();
	}

	public void dropTable() {
		DBManager.getInstance().dropTable(Developer.class);
	}
	
	public void addDepartment(){
		Department department = new Department();
		department.setId("00003");
		department.setName("创新工厂部");
		department.setDesc("这是一个神奇的部门");
		DeptManager manager = new DeptManager();
		manager.id = 1000;
		manager.name = "Noah";
		manager.age = 40;
		department.setManager(manager);
		
		List<Developer> developers = new LinkedList<Developer>();
		for (int i = 0; i < 3; i++) {
			Developer developer = new Developer();
			developer.setId("00007"+i);
			developer.setName("zouguibao");
			developer.setAge(27);
			developer.setAge(27);
			developer.setDepartmentId("00003");
//			Company company = new Company();
//			company.setId("00005");
//			company.setName("宜信我在修改的地方");
//			company.setUrl("http://www.baodu.com");
//			company.setTel("123445677");
//			company.setAddress("朝阳区朗园路");
//			developer.setCompany(company);
			List<Skill> skills = new ArrayList<Skill>();
			for (int k = 0; k < 3; k++) {
				Skill skill = new Skill();
				skill.setName("skill" + k);
				skill.setDesc("这是我掌握的第" + (k + 1) + "项技术");
				skills.add(skill);
			}
			developers.add(developer);
		}
//		department.setDevelopers(developers);
		DBManager.getInstance().saveOrUpdate(department);
	}
	
	public void queryDepartment(){
		Department department = DBManager.getInstance().queryById("id","00003", Department.class);
//		if(department != null){
//			Log.e("zouguibao", "id = " + department.getId() + " name = " + department.getName() + " skillSize = "
//					+ department.getDesc()+"  developer Id = "+department.getDevelopers().size());
//			for(Developer developer : department.getDevelopers()){
//				Log.e("zouguibao", "id = " + developer.getId() + " name = " + developer.getName() + " skillSize = "
//						+ developer.getSkill().get(0).getName()+"  company Id = "+developer.getCompany().getId());
//				
////				Log.e("zouguibao", "id = " + developer.getCompany().getId() + " name = " + developer.getCompany().getName() + " url " + developer.getCompany().getUrl()
////				+ "  tel = " + developer.getCompany().getTel() + "  address=" + developer.getCompany().getAddress());
//				
//				Log.e("zouguibao", "manager = " + department.getManager().toString());
//			}
//		}
	}

//	public void addDeveloper() {
//		Developer developer = new Developer();
//		developer.setId("00002");
//		developer.setName("zouguibao");
//		developer.setAge(27);
//		Company company = new Company();
//		company.setId("00005");
//		company.setName("宜信我在修改的地方");
//		company.setUrl("http://www.baodu.com");
//		company.setTel("123445677");
//		company.setAddress("朝阳区朗园路");
//		developer.setCompany(company);
//		ArrayList<Skill> skills = new ArrayList<Skill>();
//		for (int i = 0; i < 5; i++) {
//			Skill skill = new Skill();
//			skill.setName("skill" + i);
//			skill.setDesc("这是我掌握的第" + (i + 1) + "项技术");
//			skills.add(skill);
//		}
//
//		developer.setSkill(skills);
//		DBManager.getInstance(mContext).saveOrUpdate(developer);
//	}
//
//	public void queryDeveloper() {
//		Developer developer = DBManager.getInstance(mContext).queryById("00002", Developer.class);
//		if (developer != null) {
//Log.e("zouguibao", "id = " + developer.getId() + " name = " + developer.getName() + " skillSize = "
//					+ developer.getSkill().get(0).getName()+"  company Id = "+developer.getCompany().getId());
//		}
//	}

//	public void addCompany() {
//		Company company = new Company();
//		company.setId("00005");
//		company.setName("宜信创新工厂部");
//		company.setUrl("http://www.baodu.com");
//		company.setTel("123445677");
//		company.setAddress("朝阳区朗园路");
//		DBManager.getInstance(mContext).saveOrUpdate(company);
//	}
//
//	public void queryCompany() {
//		Company company = DBManager.getInstance(mContext).queryById("00002", Company.class);
//		if (company != null) {
//			Log.e("zouguibao", "id = " + company.getId() + " name = " + company.getName() + " url " + company.getUrl()
//					+ "  tel = " + company.getTel() + "  address=" + company.getAddress());
//		}
//	}
//
//	public void queryList() {
//		List<Company> list = DBManager.getInstance(mContext).queryList(Company.class);
//		if (list != null && list.size() > 0) {
//			for (Company company : list) {
//				Log.e("zouguibao", "id = " + company.getId() + " name = " + company.getName() + " url "
//						+ company.getUrl() + "  tel = " + company.getTel() + "  address=" + company.getAddress());
//			}
//		}
//	}
}
