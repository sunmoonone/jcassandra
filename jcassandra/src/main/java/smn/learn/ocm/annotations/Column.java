package smn.learn.ocm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import smn.learn.cqltypes.CqlText;
import smn.learn.cqltypes.CqlType;

/**
 * Annotation for properties that are mapped to columns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	boolean embedded() default false;
	/**
	 * if is empty this will use the field name
	 */
	String name() default "";
	/**
	 * 
	 * @return default value for this column
	 */
	String defaultValue() default "";
	
	boolean allowNull() default true;
	
	boolean unique() default false;
	
	boolean primaryKey() default false;
	
	int primaryOrder() default 0;
	
	boolean primaryNested() default false;
	/**
	 *
	 * @return if this column should be part of the secondary index
	 */
	boolean indexed() default false;

	/**
	 *
	 * @return if this column represents a cassandra counter, bind the value of the column with the counter
	 */
	boolean counter() default false;
	/**
	 *
	 * @return if this basic column should be loaded when its getter  is invoked
	 */
	boolean lazy() default false;
	/**
	 * 
	 * set cqltype by a case in-sensetive string
	 * default is an empty string, the type will be inferred directly from the type of a field
	 * and type String => CqlText
	 */
	String type() default "";
}
