/**
 * TestYamlList.java
 * author: yujiakui
 * 2018年1月24日
 * 上午8:42:17
 */
package com.ctfin.framework.ats.test;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.ctfin.framework.ats.AbstractAutoTestFramework;
import com.ctfin.framework.ats.XTest;

/**
 * @author yujiakui
 *
 *         上午8:42:17
 *
 */
@XTest(relatePath = "usecase/yaml", desc = "yaml测试")
public class TestYamlList extends AbstractAutoTestFramework {

	@Test(dataProvider = "TestDataProvider")
	@XTest(testOnly = "*")
	public void test(Map<String, Object> param) {
		System.out.println(param);
		List list = (List) param.get("contactInfos");
		System.out.println(list);
		Object innerObj = list.get(0);
		System.out.println(innerObj);
	}
}
