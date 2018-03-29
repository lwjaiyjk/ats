/**
 * TraverseFolder.java
 * author: yujiakui
 * 2017年9月4日
 * 下午3:09:29
 */
package com.ctfin.framework.ats;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author yujiakui
 *
 *         下午3:09:29
 *
 *         遍历文件夹，取得所有的文件名称
 */
public class TraverseFolderUtils {

	/**
	 * 遍历路径获得对应的文件列表 ： 正则表达式对应的文件名称
	 *
	 * @param path
	 * @param regexFileName
	 *            正则表达式对应的文件名称 带扩展名的
	 */
	public static List<String> traverse(String path, List<String> regexFileNames) {

		List<String> pathFileNames = new ArrayList<String>();
		File file = new File(path);
		if (file.exists()) {
			LinkedList<File> list = new LinkedList<File>();
			File[] files = file.listFiles();
			for (File file2 : files) {
				if (file2.isDirectory()) {
					list.add(file2);
				} else {

					for (String regexFileName : regexFileNames) {
						if (Pattern.matches(regexFileName, file2.getName())) {
							pathFileNames.add(file2.getAbsolutePath());
							break;
						}
					}

				}
			}
			File temp_file;
			while (!list.isEmpty()) {
				temp_file = list.removeFirst();
				files = temp_file.listFiles();
				for (File file2 : files) {
					if (file2.isDirectory()) {
						list.add(file2);
					} else {
						for (String regexFileName : regexFileNames) {
							if (Pattern.matches(regexFileName, file2.getName())) {
								pathFileNames.add(file2.getAbsolutePath());
								break;
							}
						}
					}
				}
			}
		} else {
			throw new RuntimeException(MessageFormat.format("路径path={0}对应的文件夹不存在", path));
		}
		return pathFileNames;
	}

}
