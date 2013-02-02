package smn.learn.ocm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mapped {
	/**
	 * another field this filed mapping to
	 * @return
	 */
	String to() default "";
}
