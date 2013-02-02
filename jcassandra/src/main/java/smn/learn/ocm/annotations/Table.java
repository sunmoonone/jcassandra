package smn.learn.ocm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation for properties that are mapped to columns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
//	bloom_filter_fp_chance name-value
//	caching name-value
//	comment name-value
//	compaction map
//	compression map
//	dclocal_read_repair_chance name-value
//	gc_grace_seconds name-value
//	read_repair_chance name-value
//	replicate_on_write name
	/**
	 *
	 * @return the time to wait before garbage collecting tombstones (deletion markers).
	 * defaults to 864000 (10 days). See http://wiki.apache.org/cassandra/DistributedDeletes
	 */
	int gcGraceSeconds() default 864000;
	/**
	 *
	 * @return the probability with which read repairs should be invoked on non-quorum reads.
	 * must be between 0 and 1. defaults to 1.0 (always read repair).
	 */
	double readRepairChance() default 1.0;

	/**
	 *
	 * @return
	 */
	boolean replicateOnWrite() default true;
	
	String comment() default "";
	
	String caching() default "";
	
	String keySpace() default "";
	/**
    *
    * @return the consistency level used for operations on this column family
    */
   ConsistencyLevel consistencyLevel() default ConsistencyLevel.ONE;
}
