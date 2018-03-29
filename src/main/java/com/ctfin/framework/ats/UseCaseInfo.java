/**
 * UseCaseInfo.java
 * author: yujiakui
 * 2017年9月4日
 * 下午6:05:55
 */
package com.ctfin.framework.ats;

import java.io.Serializable;

/**
 * @author yujiakui
 *
 *         下午6:05:55
 *
 *         用例信息
 */
public class UseCaseInfo implements Serializable {

	/** serial id */
	private static final long serialVersionUID = 4507379134256282800L;

	/** 用例id */
	private String id;

	/** 用例名称，用例描述 */
	private String name;

	/** 用例描述 */
	private String desc;

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("测试用例信息:");
		stringBuilder.append("[id=").append(id);
		stringBuilder.append(";name=").append(name);
		stringBuilder.append(";desc=").append(desc);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
