package org.frame.database.db.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SeriaUtil {
	
	/**
	 * 数组转化为对象
	 * @param bytes
	 * @return
	 */
	public static Object deserializable(byte[] bytes){
		Object obj = null;
		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			
			obj = ois.readObject();
			bis.close();
			ois.close();
		}catch(Exception e){
			System.out.println("tanslation " + e.getMessage());
			e.printStackTrace();
		}
		
		return obj;
	}
	
	/**
	 * 将数组转化为对象
	 * @param obj
	 * @return
	 */
	public static byte[] enserializable(Object obj){
		byte[] bytes = null;
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			bytes = bos.toByteArray();
			bos.close();
			oos.close();
			
		}catch(Exception e){
			System.out.println("tanslation " + e.getMessage());
			e.printStackTrace();
		}
		return bytes;
	}
}
