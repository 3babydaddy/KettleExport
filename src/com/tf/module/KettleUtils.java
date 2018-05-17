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
 * @Description: kettle �������������
 * @author haiwang.wang
 * @date 2018��5��17�� ����3:16:36
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
	 * ����ת���ļ�����
	 * 
	 * @param params
	 *            �����������ֵ
	 * @param ktrPath
	 *            ת���ļ���·������׺ktr
	 */
	public static void runTransfer(String[] params, String ktrPath) {
		Trans trans = null;
		try {
			// // ��ʼ��
			// ת��Ԫ����
			KettleEnvironment.init();// ��ʼ��
			EnvUtil.environmentInit();
			TransMeta transMeta = new TransMeta(ktrPath);
			// ת��
			trans = new Trans(transMeta);

			// ִ��ת��
			trans.execute(params);
			// �ȴ�ת��ִ�н���
			trans.waitUntilFinished();

			Result result = trans.getResult();
			System.out.println(result.toString());

			// �׳��쳣
			if (trans.getErrors() > 0) {
				logger.error("[exception = {}]", "There are errors during transformation exception!(��������з����쳣)");
				throw new Exception("There are errors during transformation exception!(��������з����쳣)");
			}
		} catch (Exception e) {
			logger.error("[exception = {}]", "runTransfer has an  exception!!!", e);
		}
	}

	/**
	 * java ���� kettle ��job
	 * 
	 * @param jobname
	 *            �磺 String fName= "D:\\kettle\\informix_to_am_4.ktr";
	 */
	public static void runJob(String[] params, String jobPath) {
		try {
			KettleEnvironment.init();
			// jobname ��Job�ű���·��������
			JobMeta jobMeta = new JobMeta(jobPath, null);
			Job job = new Job(null, jobMeta);
			// ��Job �ű����ݲ������ű��л�ȡ����ֵ��${������}
			// job.setVariable(paraname, paravalue);
			job.setVariable("id", params[0]);
			job.setVariable("content", params[1]);
			job.setVariable("file", params[2]);
			job.start();
			job.waitUntilFinished();
			if (job.getErrors() > 0) {
				throw new Exception("There are errors during job exception!(ִ��job�����쳣)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ִ�д洢�����ݿ���Դ���е�ת��
	 * 
	 * @Description:
	 * @throws KettleException
	 */
	public static void executeTrans(String transName) throws KettleException {
		// ��ʼ��kettle����
		KettleEnvironment.init();
		// ������Դ����󣬴�ʱ�Ķ�����һ���ն���
		KettleDatabaseRepository repository = new KettleDatabaseRepository();
		// ������Դ�����ݿ��������������spoon���洴����Դ��
		DatabaseMeta dataMeta = new DatabaseMeta("ETL", "Oracle", "Native(JDBC)", "127.0.0.1", "DBname", "1521",
				"username", "password");
		// ��Դ��Ԫ����,���Ʋ�����id�����������ȿ�����㶨��
		KettleDatabaseRepositoryMeta kettleDatabaseMeta = new KettleDatabaseRepositoryMeta("ETL", "ETL",
				"ETL description", dataMeta);
		// ����Դ�⸳ֵ
		repository.init(kettleDatabaseMeta);
		// ������Դ��
		repository.connect("admin", "admin");
		// ���ݱ������ҵ�ģ�����ڵ�Ŀ¼����,�˲������Ҫ��
		RepositoryDirectoryInterface directory = repository.findDirectory("/test");
		// ����ktrԪ����
		TransMeta transMeta = ((Repository) repository).loadTransformation(transName, directory, null, true, null);
		// ִ�в���
		String[] params = { "1", "07bb40f7200448", "d:\\haha.txt" };
		// ����ktr
		Trans trans = new Trans(transMeta);
		// ִ��ktr
		trans.execute(params);
		// �ȴ�ִ�����
		trans.waitUntilFinished();

		if (trans.getErrors() > 0) {
			System.err.println("Transformation run Failure!");
		} else {
			System.out.println("Transformation run successfully!");
		}
	}
}
