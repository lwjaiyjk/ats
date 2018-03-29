/**
 * XTest.java
 * author: yujiakui
 * 2017年9月4日
 * 下午2:32:11
 */
package com.ctfin.framework.ats;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
/**
 * @author yujiakui
 *
 *         下午2:32:11
 *
 *         定义一个注解
 */
public @interface XTest {

	/**
	 * 测试文件的相对路径
	 *
	 * @return
	 */
	public String relatePath() default "";

	/**
	 * 仅仅测试某个测试用例
	 *
	 * @return
	 */
	public String testOnly() default "";

	/**
	 * 描述
	 *
	 * @return
	 */
	public String desc() default "";
}
