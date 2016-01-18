package org.frame.database.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @author zgb
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	/**
	 * 
	 * @return
	 */
	boolean id() default false;
	/**
	 * 
	 * @return
	 */
	String name() default "";
	/**
	 * true 表示是一对多的关系 
	 * false 表示不是
	 * @return
	 */
	boolean isOne2Many() default false;
	
	
	/**
     * true 表示是一对一的关系 
     * false 表示不是
     * @return
     */
    boolean isOne2One() default false;
	
	/**
	 * 
	 * @return
	 */
	ColumnType type() default ColumnType.UNKONWN;
	
	public enum ColumnType{
		TONE,
		TMANY,
		SERIALIZABLE,
		UNKONWN
	}
}
