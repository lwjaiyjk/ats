/**
 * AtsFrameworkContextCfg.java
 * author: yujiakui
 * 2017年9月18日
 * 上午11:03:46
 */
package com.ctfin.framework.ats;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
/**
 * @author yujiakui
 *
 *         上午11:03:46
 *
 */
@ComponentScan
@ActiveProfiles
@Import({})
public @interface AtsFrameworkBaseContextCfg {
	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
	String[] basePackages() default {};

	@AliasFor(annotation = ActiveProfiles.class, attribute = "profiles")
	String[] activeProfiles() default {};

	@AliasFor(annotation = Import.class, attribute = "value")
	Class<?>[] importClasses() default {};

	@AliasFor(annotation = ComponentScan.class, attribute = "useDefaultFilters")
	boolean useDefaultFilters() default true;

	@AliasFor(annotation = ComponentScan.class, attribute = "includeFilters")
	Filter[] includeFilters() default {};

	@AliasFor(annotation = ComponentScan.class, attribute = "excludeFilters")
	Filter[] excludeFilters() default {};
}
