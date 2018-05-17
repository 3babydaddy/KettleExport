package com.tf.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @ClassName: PropertiesUtils
 * @Description: property �ļ�����������
 * @author haiwang.wang
 * @date 2018��5��17�� ����3:16:45
 * 
 */
public class PropertiesUtils {
	private static final Logger logger = LogManager.getLogger(PropertiesUtils.class);
	private static Properties props;
	static {
		loadProps();
	}

	private synchronized static void loadProps() {
		logger.info("��ʼ����properties�ļ�����.......");
		props = new Properties();
		InputStream in = null;
		try {
			// Ҫ���ص������ļ�
			in = PropertiesUtils.class.getClassLoader().getResourceAsStream("conf/export.properties");
			props.load(in);
		} catch (FileNotFoundException e) {
			logger.error("export.properties�ļ�δ�ҵ�");
		} catch (IOException e) {
			logger.error("����IOException");
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				logger.error("export.properties�ļ����رճ����쳣");
			}
		}
		logger.info("����properties�ļ��������...........");
		logger.info("properties�ļ����ݣ�" + props);
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
