/**
 * AbstractAutoTestFramework.java
 * author: yujiakui
 * 2017年9月5日
 * 上午11:49:21
 */
package com.ctfin.framework.ats;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;

/**
 * @author yujiakui
 *
 *         上午11:49:21
 *
 *         单纯的测试，不需要spring 上下文的
 */
public abstract class AbstractAutoTestFramework {

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
