/**
 * AbstractAutoTestFramework.java
 * author: yujiakui
 * 2017年9月4日
 * 下午2:20:06
 */
package com.ctfin.framework.ats;

import java.lang.reflect.Method;

import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;

/**
 * @author yujiakui
 *
 *         下午2:20:06
 *
 *         抽象的自动测试框架 （1）其他测试类直接继承这个类就可以了-->对应的有springApplication的测试
 */
public class AbstractTestSpringContextFramework extends AbstractTestNGSpringContextTests {

	/**
	 * 定义一个数据驱动类
	 *
	 * @return
	 */
	@DataProvider(name = "TestDataProvider")
	public Object[][] getTestData(Method method) {
		return DataProviderFactory.assembleDataProvider(this.getClass(), method);
	}

}
