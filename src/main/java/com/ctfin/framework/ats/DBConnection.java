/**
 * DBConfigure.java
 * author: yujiakui
 * 2017年9月4日
 * 下午3:46:31
 */
package com.ctfin.framework.ats;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * @author yujiakui
 *
 *         下午3:46:31
 *
 *         数据库配置
 */
public class DBConnection {

	/** 数据库地址 */
	private final static String JDBC_URL = "jdbc.url";

	/** 数据库驱动器 */
	private final static String JDBC_DRIVER = "jdbc.driver";

	/** 数据库用户名 */
	private final static String JDBC_USERNAME = "jdbc.username";

	/** 数据库密码 */
	private final static String JDBC_PASSWORD = "jdbc.password";

	/** 可以进行修改，ats配置文件 */
	public static String ATS_CONFIG_URL = "src/test/resources/ats-config.properties";

	/**
	 * 根据表名获取对应的数据库配置，有一个默认的配置，如果不指定的化，属性文件properties
	 *
	 * @param tableName
	 * @return
	 */
	public static Connection getConnection(String tableName) {

		Properties properties = new Properties();
		try {
			// 加载Java项目根路径下的配置文件
			InputStream inputStream = new FileInputStream(ATS_CONFIG_URL);

			properties.load(inputStream);// 加载属性文件
			String driver = getPropertyValueFromProperties(JDBC_DRIVER, tableName, properties);
			String url = getPropertyValueFromProperties(JDBC_URL, tableName, properties);
			String userName = getPropertyValueFromProperties(JDBC_USERNAME, tableName, properties);
			String password = getPropertyValueFromProperties(JDBC_PASSWORD, tableName, properties);

			// 1、加载数据库驱动（ 成功加载后，会将Driver类的实例注册到DriverManager类中）
			Class.forName(driver);

			// 2、获取数据库连接
			return DriverManager.getConnection(url, userName, password);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	/**
	 * 从属性文件中获取对应的属性值
	 *
	 * @param key
	 * @param tableName
	 * @param properties
	 * @return
	 */
	private static String getPropertyValueFromProperties(String key, String tableName,
			Properties properties) {
		String tableNameKey = tableName + "." + key;
		String value = properties.getProperty(tableNameKey);
		if (null == value) {
			// 如果对应的表的属性不存在，则使用默认值
			value = properties.getProperty(key);
			if (null == value) {
				throw new RuntimeException(
						MessageFormat.format("属性key={0}在ats-config.properties对应的值为空", key));
			}
		}
		return value;
	}

	/**
	 * 连接关闭
	 *
	 * @param connection
	 */
	public static void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
