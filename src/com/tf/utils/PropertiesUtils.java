package com.tf.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName: PropertiesUtils
 * @Description: property 文件解析工具类
 * @author haiwang.wang
 * @date 2018年5月17日 下午3:16:45
 * 
 */
public class PropertiesUtils {
	private static final Logger logger = LogManager.getLogger(PropertiesUtils.class);
	private static Properties props;
	static {
		loadProps();
	}

	private synchronized static void loadProps() {
		logger.info("开始加载properties文件内容.......");
		props = new Properties();
		InputStream in = null;
		try {
			// 要加载的属性文件
			in = PropertiesUtils.class.getClassLoader().getResourceAsStream("conf/export.properties");
			props.load(in);
		} catch (FileNotFoundException e) {
			logger.error("export.properties文件未找到");
		} catch (IOException e) {
			logger.error("出现IOException");
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("export.properties文件流关闭出现异常");
			}
		}
		logger.info("加载properties文件内容完成...........");
		logger.info("properties文件内容：" + props);
	}

	public static String getProperty(String key) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key);
	}

	public static String getProperty(String key, String defaultValue) {
		if (null == props) {
			loadProps();
		}
		return props.getProperty(key, defaultValue);
	}

}
