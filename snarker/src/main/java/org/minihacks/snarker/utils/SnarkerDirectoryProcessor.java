package org.minihacks.snarker.utils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.minihacks.snarker.SnarkReport;
import org.minihacks.snarker.Snarker;
import org.minihacks.snarker.config.SnarkerConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ch.qos.logback.classic.Level;

public class SnarkerDirectoryProcessor {

	public static void main(String[] args) throws Exception {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);
		
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SnarkerConfig.class);
		ctx.registerShutdownHook();
		
		Snarker snarker = ctx.getBean(Snarker.class);
		List<String> filePaths = getFilesFromPath("/Users/christinasickelco/GitSandbox/snarker/src/test/resources/snark");
		
		for (String filePath : filePaths) {
			String filePathLower = filePath.toLowerCase();
			SnarkReport report = snarker.processFile(filePathLower, filePath);
			System.out.println(report.toString());
		}
	}

	public static List<String> getFilesFromPath(String path) {
		List<String> retval = new LinkedList<>();
		File folder = new File(path);
		if( folder.isDirectory() ) {
			File[] files = folder.listFiles();
			for (File file : files) {
				retval.add(file.getAbsolutePath());
			}
		} else {
			retval.add(path);
		}
		return retval;
	}
}
