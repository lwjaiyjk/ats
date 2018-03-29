/**
 * CTDBUtils.java
 * author: yujiakui
 * 2017年9月4日
 * 下午3:40:56
 */
package com.ctfin.framework.ats;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * @author yujiakui
 *
 *         下午3:40:56
 *
 *         长天数据库操作工具类
 */
public class DBUtils {

	/** 日志 */
	private final static Logger LOGGER = LoggerFactory.getLogger(DBUtils.class);

	/**
	 * 将准备的数据插入数据库中：默认在准备数据之前将对应的准备数据按照条件进行删除
	 *
	 * @param prepareDateMap
	 * @param deleteBefPrepare
	 *            准备前删除标记
	 * @throws SQLException
	 */
	public static void prepare(Map<String, Object> prepareDateMap) {
		prepare(prepareDateMap, true);
	}

	/**
	 * 将准备的数据插入数据库中
	 *
	 * @param prepareDateMap
	 * @param deleteBefPrepare
	 *            准备前删除标记
	 * @throws SQLException
	 */
	public static void prepare(Map<String, Object> prepareDateMap, boolean deleteBefPrepare) {

		for (Map.Entry<String, Object> tableEle : prepareDateMap.entrySet()) {
			String tableName = tableEle.getKey();// 获取表名称
			List<Map<String, Object>> tableDatas = (List<Map<String, Object>>) tableEle.getValue();

			// 获取数据库链接
			Connection connection = DBConnection.getConnection(tableName);
			try {

				Statement statement = connection.createStatement();

				// 遍历表数据
				for (Map<String, Object> tableDataEle : tableDatas) {
					// 1. 解析数据，对于有特定标记的用值替换
					Map<String, Object> parserResultTableDataMap = parseTableDataValue(
							tableDataEle);
					// 2. 解析字段
					Map<String, Object> cndMap = parseTableField(parserResultTableDataMap, false);
					// 2.1 判断是否在准备前进行删除，如果需要删除，则组装对应的语句
					if (deleteBefPrepare && !CollectionUtils.isEmpty(cndMap)) {
						// 组装对应的删除语句
						String delSql = assembleDelSqlForTableData(cndMap, tableName);
						statement.addBatch(delSql);
					}
					// 3. 组装表中的数据，形成对应的插入语句
					String insertSql = assembleInsertSqlForTableData(parserResultTableDataMap,
							tableName);
					statement.addBatch(insertSql);
				}
				statement.executeBatch();
				statement.clearBatch();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				DBConnection.closeConnection(connection);
			}
		}

	}

	/**
	 * @param cndMap
	 * @param tableName
	 * @return
	 */
	private static String assembleDelSqlForTableData(Map<String, Object> cndMap, String tableName) {
		String delSql = "delete from " + tableName;
		delSql += " where ";

		int index = 0;
		int size = cndMap.size();
		// 增加删除的条件
		for (Map.Entry<String, Object> fieldEle : cndMap.entrySet()) {
			String fieldName = fieldEle.getKey();
			Object fieldValue = fieldEle.getValue();

			delSql += fieldName;
			delSql += " = ";
			if (fieldValue instanceof String) {
				delSql += "'";
				delSql += fieldValue;
				delSql += "'";
			} else {
				delSql += fieldValue;
			}
			if (index != size - 1) {
				delSql += " and ";
			}
			index++;
		}
		return delSql;
	}

	/**
	 * 从数据库中删除指定条件的数据
	 *
	 * @param tableName
	 * @param deleteCondition
	 */
	public static void deleteDataInDB(String tableName, String deleteCondition) {

		// 获取数据库链接
		Connection connection = DBConnection.getConnection(tableName);
		Statement statement;
		try {
			statement = connection.createStatement();
			String sql = "delete from " + tableName;
			sql += " where ";
			sql += deleteCondition;
			statement.execute(sql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			DBConnection.closeConnection(connection);
		}
	}

	/**
	 * 数据库对比:默认在数据库比较之后按照期望条件进行删除
	 *
	 * @param expectedDataMap
	 *            期望数据结果映射
	 * @param params
	 *            参数
	 * @param useCaseInfo
	 *            用例信息
	 * @return
	 */
	public static boolean dbCheck(Map<String, Object> expectedDataMap, Map<String, Object> params,
			UseCaseInfo useCaseInfo) {
		return dbCheck(expectedDataMap, params, useCaseInfo, true);
	}

	/**
	 * 数据库对比
	 *
	 * @param expectedDataMap
	 *            期望数据结果映射
	 * @param params
	 *            参数
	 * @param useCaseInfo
	 *            用例信息
	 * @param deleteAfterCheck
	 *            对比之后是否进行数据库删除
	 * @return
	 */
	public static boolean dbCheck(Map<String, Object> expectedDataMap, Map<String, Object> params,
			UseCaseInfo useCaseInfo, boolean deleteAfterCheck) {

		boolean dbCheckEqualFlag = true;
		// 校验每一张表
		for (Map.Entry<String, Object> tableDataEle : expectedDataMap.entrySet()) {

			String tableName = tableDataEle.getKey();// 表名称
			List<Map<String, Object>> tableDatas = (List<Map<String, Object>>) tableDataEle
					.getValue();

			// 获取数据库链接
			Connection connection = DBConnection.getConnection(tableName);

			// 遍历表数据
			for (Map<String, Object> dataEle : tableDatas) {
				// 1. 解析字段值
				Map<String, Object> fieldValueParserResult = parseTableDataValue(dataEle);
				// 2. 解析字段
				Map<String, Object> queryCndMap = parseTableField(fieldValueParserResult, true);

				// 3. 组装sql,获得数据库结果
				String sql = assembleQuerySql(tableName, queryCndMap);
				// 4. 执行数据库查询,并进行对比
				List<Map<String, Object>> queryResults = queryDBResult(connection, sql);
				// 5. 查询结果对比
				if (queryResults.size() != 1) {
					LOGGER.error(MessageFormat.format("根据sql={0}查询结果个数对不上", sql));
				} else {
					// 字段比较
					if (!checkMapData(fieldValueParserResult, queryResults.get(0), useCaseInfo)) {
						dbCheckEqualFlag = false;
					}
				}

				// 6. 对比之后删除期望数据
				if (deleteAfterCheck && !CollectionUtils.isEmpty(queryCndMap)) {
					// 组装删除语句
					deleteAfterCheckUsingQueryCnd(connection, queryCndMap, tableName);
				}
			}

			// 关闭数据库链接
			DBConnection.closeConnection(connection);
		}
		return dbCheckEqualFlag;
	}

	/**
	 * @param connection
	 * @param queryCndMap
	 * @param tableName
	 */
	private static void deleteAfterCheckUsingQueryCnd(Connection connection,
			Map<String, Object> queryCndMap, String tableName) {
		String delSql = assembleDelSqlForTableData(queryCndMap, tableName);
		Statement statement;
		try {
			statement = connection.createStatement();
			statement.execute(delSql);
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据sql查询数据库的结果
	 *
	 * @param connection
	 * @param sql
	 */
	private static List<Map<String, Object>> queryDBResult(Connection connection, String sql) {
		Statement statement;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			List<Map<String, Object>> queryResults = new ArrayList<Map<String, Object>>();
			while (resultSet.next()) {
				Map<String, Object> rowDataMap = new HashMap<String, Object>();
				for (int i = 1; i < columnCount; i++) {
					rowDataMap.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
				}
				queryResults.add(rowDataMap);
			}

			// 关闭
			resultSet.close();
			statement.close();

			return queryResults;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 两个map中的值进行比较，以expectedResult为准
	 *
	 * @param expectedResult
	 * @param queryResult
	 * @param useCaseInfo
	 * @return
	 */
	protected static boolean checkMapData(Map<String, Object> expectedResult,
			Map<String, Object> queryResult, UseCaseInfo useCaseInfo) {

		boolean checkEqualFlag = true;
		for (Map.Entry<String, Object> expectedEle : expectedResult.entrySet()) {
			String fieldName = expectedEle.getKey();
			Object fieldValue = expectedEle.getValue();

			Object queryFieldValue = queryResult.get(fieldName);

			// Integer 和Long进行对比，数据库查询出来的Long类型

			if (isDigitType(fieldValue)) {
				fieldValue = String.valueOf(fieldValue);
			}
			if (isDigitType(queryFieldValue)) {
				queryFieldValue = String.valueOf(queryFieldValue);
			}

			if (!fieldValue.equals(queryFieldValue)) {
				LOGGER.error(MessageFormat.format("用例id={0},fieldName={1},expected={2},query={3}",
						useCaseInfo.getId(), fieldName, fieldValue, queryFieldValue));
				checkEqualFlag = false;
			}
		}
		return checkEqualFlag;
	}

	/**
	 * 判断一个字段值是否是数字类型
	 *
	 * @param fieldValue
	 * @return
	 */
	private static boolean isDigitType(Object fieldValue) {
		if (fieldValue instanceof Integer || fieldValue instanceof Long
				|| fieldValue instanceof BigInteger || fieldValue instanceof Float
				|| fieldValue instanceof Double) {
			return true;
		}
		return false;
	}

	/**
	 * 组装查询sql
	 *
	 * @param tableName
	 * @param queryCndMap
	 * @return
	 */
	protected static String assembleQuerySql(String tableName, Map<String, Object> queryCndMap) {
		StringBuilder sqlBuilder = new StringBuilder("select * from ");
		sqlBuilder.append(tableName);
		sqlBuilder.append(" where ");
		int size = queryCndMap.size();
		int index = 0;
		for (Map.Entry<String, Object> cndEle : queryCndMap.entrySet()) {
			String cndName = cndEle.getKey();
			Object cndValue = cndEle.getValue();

			sqlBuilder.append(cndName);
			sqlBuilder.append("=");
			if (cndValue instanceof String) {
				sqlBuilder.append("'");
				sqlBuilder.append(cndValue);
				sqlBuilder.append("'");
			} else {
				sqlBuilder.append(cndValue);
			}

			if (index++ != size - 1) {
				sqlBuilder.append(" and ");
			}
		}
		return sqlBuilder.toString();
	}

	/**
	 * 组装对应的插入sql语句
	 *
	 * @param tableData
	 * @param tableName
	 * @return
	 */
	protected static String assembleInsertSqlForTableData(Map<String, Object> tableData,
			String tableName) {
		String prefixSql = "insert into " + tableName;
		prefixSql += " ( ";
		String suffixSql = " values(";

		int size = tableData.size();
		int index = 0;
		for (Map.Entry<String, Object> fieldEle : tableData.entrySet()) {
			String fieldName = fieldEle.getKey();
			Object fieldValue = fieldEle.getValue();

			prefixSql += fieldName;
			if (fieldValue instanceof String) {
				suffixSql += "'";
				suffixSql += fieldValue;
				suffixSql += "'";
			} else {
				suffixSql += fieldValue;
			}

			if (index == size - 1) {
				prefixSql += ")";
				suffixSql += ")";
			} else {
				prefixSql += ",";
				suffixSql += ",";
			}
			++index;
		}

		return prefixSql + suffixSql;
	}

	/**
	 * 解析数据表对应的字段名称，因为字段名称可能有[C],[N]等标记，其中[C]表示查询条件,[N]表示不校验
	 *
	 * @param tableDataMap
	 *            会修改对应tableDataMap
	 * @param deleteFlag
	 *            对于标记[N]的字段是否进行删除
	 * @return 查询条件
	 */
	protected static Map<String, Object> parseTableField(Map<String, Object> tableDataMap,
			boolean deleteFlag) {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		Map<String, Object> tempOriginTableDateMap = new HashMap<String, Object>(tableDataMap);
		for (Map.Entry<String, Object> tableDataEle : tempOriginTableDateMap.entrySet()) {
			String fieldName = tableDataEle.getKey();
			Object fieldValue = tableDataEle.getValue();

			if (fieldName.trim().endsWith("[C]")) {
				tableDataMap.remove(fieldName);
				// 说明是条件标记
				int indexLastOccr = fieldName.lastIndexOf("[C]");
				fieldName = fieldName.substring(0, indexLastOccr);
				conditionMap.put(fieldName, fieldValue);
				tableDataMap.put(fieldName, fieldValue);

			} else if (fieldName.trim().endsWith("[N]")) {
				tableDataMap.remove(fieldName);
				if (!deleteFlag) {
					int indexLastOccr = fieldName.lastIndexOf("[N]");
					fieldName = fieldName.substring(0, indexLastOccr);
					tableDataMap.put(fieldName, fieldValue);
				}
			}
		}

		return conditionMap;
	}

	/**
	 * 解析表数据值
	 *
	 * @param originTableDataMap
	 * @return
	 */
	protected static Map<String, Object> parseTableDataValue(
			Map<String, Object> originTableDataMap) {

		Map<String, Object> parseResultTableDataMap = new HashMap<String, Object>();
		for (Map.Entry<String, Object> ele : originTableDataMap.entrySet()) {
			String fieldName = ele.getKey(); // 字段名称
			Object fieldValue = ele.getValue();// 字段值

			if (fieldValue instanceof String) {
				// 字段值是String类型
				String fieldValueStr = (String) fieldValue;
				if (fieldValueStr.trim().startsWith("@now()")) {
					// 将对应的值设置为当前时间 @now()#yyyyMMdd
					String dateFormat = fieldValueStr.split("#")[1];
					Date currentTime = new Date();
					SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
					String dateString = formatter.format(currentTime);
					parseResultTableDataMap.put(fieldName, dateString);
				} else {
					parseResultTableDataMap.put(fieldName, fieldValue);
				}
			} else {
				parseResultTableDataMap.put(fieldName, fieldValue);
			}

		}

		return parseResultTableDataMap;
	}

}
