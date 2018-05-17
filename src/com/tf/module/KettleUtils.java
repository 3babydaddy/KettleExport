package com.tf.module;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import com.tf.utils.PropertiesUtils;

/**
 * @ClassName: KettleUtils
 * @Description: kettle 导出工具入口类
 * @author haiwang.wang
 * @date 2018年5月17日 下午3:16:36
 * 
 */
public class KettleUtils {
	private static final Logger logger = LogManager.getLogger(KettleUtils.class);

	public static void main(String[] args) {
		// String[] params = { "1", "content", "d:\\test1.txt" };
		String trans_path = PropertiesUtils.getProperty("trans_path");
		logger.info("[trans_path = {}]", trans_path);
		String[] parmas = new String[] {};
		runTransfer(parmas, trans_path);
	}

	/**
	 * 运行转换文件方法
	 * 
	 * @param params
	 *            多个参数变量值
	 * @param ktrPath
	 *            转换文件的路径，后缀ktr
	 */
	public static void runTransfer(String[] params, String ktrPath) {
		Trans trans = null;
		try {
			// // 初始化
			// 转换元对象
			KettleEnvironment.init();// 初始化
			EnvUtil.environmentInit();
			TransMeta transMeta = new TransMeta(ktrPath);
			// 转换
			trans = new Trans(transMeta);

			// 执行转换
			trans.execute(params);
			// 等待转换执行结束
			trans.waitUntilFinished();

			Result result = trans.getResult();
			System.out.println(result.toString());

			// 抛出异常
			if (trans.getErrors() > 0) {
				logger.error("[exception = {}]", "There are errors during transformation exception!(传输过程中发生异常)");
				throw new Exception("There are errors during transformation exception!(传输过程中发生异常)");
			}
		} catch (Exception e) {
			logger.error("[exception = {}]", "runTransfer has an  exception!!!", e);
		}
	}

	/**
	 * java 调用 kettle 的job
	 * 
	 * @param jobname
	 *            如： String fName= "D:\\kettle\\informix_to_am_4.ktr";
	 */
	public static void runJob(String[] params, String jobPath) {
		try {
			KettleEnvironment.init();
			// jobname 是Job脚本的路径及名称
			JobMeta jobMeta = new JobMeta(jobPath, null);
			Job job = new Job(null, jobMeta);
			// 向Job 脚本传递参数，脚本中获取参数值：${参数名}
			// job.setVariable(paraname, paravalue);
			job.setVariable("id", params[0]);
			job.setVariable("content", params[1]);
			job.setVariable("file", params[2]);
			job.start();
			job.waitUntilFinished();
			if (job.getErrors() > 0) {
				throw new Exception("There are errors during job exception!(执行job发生异常)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 执行存储在数据库资源库中的转换
	 * 
	 * @Description:
	 * @throws KettleException
	 */
	public static void executeTrans(String transName) throws KettleException {
		// 初始化kettle环境
		KettleEnvironment.init();
		// 创建资源库对象，此时的对象还是一个空对象
		KettleDatabaseRepository repository = new KettleDatabaseRepository();
		// 创建资源库数据库对象，类似我们在spoon里面创建资源库
		DatabaseMeta dataMeta = new DatabaseMeta("ETL", "Oracle", "Native(JDBC)", "127.0.0.1", "DBname", "1521",
				"username", "password");
		// 资源库元对象,名称参数，id参数，描述等可以随便定义
		KettleDatabaseRepositoryMeta kettleDatabaseMeta = new KettleDatabaseRepositoryMeta("ETL", "ETL",
				"ETL description", dataMeta);
		// 给资源库赋值
		repository.init(kettleDatabaseMeta);
		// 连接资源库
		repository.connect("admin", "admin");
		// 根据变量查找到模型所在的目录对象,此步骤很重要。
		RepositoryDirectoryInterface directory = repository.findDirectory("/test");
		// 创建ktr元对象
		TransMeta transMeta = ((Repository) repository).loadTransformation(transName, directory, null, true, null);
		// 执行参数
		String[] params = { "1", "07bb40f7200448", "d:\\haha.txt" };
		// 创建ktr
		Trans trans = new Trans(transMeta);
		// 执行ktr
		trans.execute(params);
		// 等待执行完毕
		trans.waitUntilFinished();

		if (trans.getErrors() > 0) {
			System.err.println("Transformation run Failure!");
		} else {
			System.out.println("Transformation run successfully!");
		}
	}
}
