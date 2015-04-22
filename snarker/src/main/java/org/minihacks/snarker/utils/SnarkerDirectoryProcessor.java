package org.minihacks.snarker.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.minihacks.snarker.SnarkReport;
import org.minihacks.snarker.Snarker;
import org.minihacks.snarker.config.SnarkerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import ch.qos.logback.classic.Level;

public class SnarkerDirectoryProcessor {
	Logger logger = LoggerFactory.getLogger(SnarkerDirectoryProcessor.class);
	
	private Snarker snarker;
	public Snarker getSnarker() {
		return snarker;
	}
	public void setSnarker(Snarker snarker) {
		this.snarker = snarker;
	}

	public double processDirectory(String path) throws FileNotFoundException, IOException {
		double totalScore = 0;
		
		List<String> filePaths = getFilesFromPath(path);
		
		for (String filePath : filePaths) {
			String filePathLower = filePath.toLowerCase();
			SnarkReport report = snarker.processFile(filePathLower, filePath);
			logger.info(report.getArticle() + ": " + report.getScore() + "-" + report.getSummary());
			totalScore += report.getScore();
		}
		
		return (totalScore/filePaths.size());
	}
	
	public double processResourcesOnPath(String path) throws IOException {
		double totalScore = 0;
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // Ant-style path matching
        Resource[] resources = resolver.getResources(path);

        for (Resource resource : resources) {
            InputStream is = resource.getInputStream();
            SnarkReport report = snarker.processInputStream(resource.getFilename(), is);
			logger.info(report.getArticle() + ": " + report.getScore() + "-" + report.getSummary());
			totalScore += report.getScore();
        }
        
        double averageScore = totalScore/resources.length;
        logger.info("{} average score: {}", path, averageScore);
        return averageScore;
	}

	private List<String> getFilesFromPath(String path) {
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
	
	public static void main(String[] args) throws Exception {
		String path = "/Users/christinasickelco/GitSandbox/snarker/src/test/resources/notsnark_justinformal"; 
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.INFO);
		
		@SuppressWarnings("resource")
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SnarkerConfig.class);
		ctx.registerShutdownHook();
		
		Snarker snarker = ctx.getBean(Snarker.class);
		SnarkerDirectoryProcessor p = new SnarkerDirectoryProcessor();
		p.setSnarker(snarker);
		
		System.out.println("Average snark score: " + p.processDirectory(path));
	}
}
