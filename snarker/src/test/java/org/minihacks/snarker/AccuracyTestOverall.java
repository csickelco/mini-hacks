package org.minihacks.snarker;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.minihacks.snarker.config.SnarkerConfig;
import org.minihacks.snarker.utils.SnarkerDirectoryProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import ch.qos.logback.classic.Level;

public class AccuracyTestOverall {
	Logger logger = LoggerFactory.getLogger(AccuracyTestOverall.class);

	private SnarkerDirectoryProcessor p;
	
	@Before
	public void setup() throws IOException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.INFO);
	    
	    @SuppressWarnings("resource")
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SnarkerConfig.class);
		ctx.registerShutdownHook();
		
		Snarker snarker = ctx.getBean(Snarker.class);
		p = new SnarkerDirectoryProcessor();
		p.setSnarker(snarker);
	}

	@Test
	public void testAllDirectories() throws Exception {
		StringBuilder sb = new StringBuilder(" ======= RESULTS =======\n");
		
		String path = "/Users/christinasickelco/GitSandbox/snarker/src/test/resources";
		File folder = new File(path);
		if( folder.isDirectory() ) {
			File[] files = folder.listFiles();
			for (File file : files) {
				logger.info("Processing: " + file.getAbsolutePath());
				double score = p.processDirectory(file.getAbsolutePath());
				sb.append(file.getName()).append(": ").append(score).append("\n");
			}
		} else {
			throw new Exception("Expecting a directory, received " + path);
		}
		
		logger.info(sb.toString());
		
		/*
		 * 
borderline_snark: 2.0
notsnark_analysis: 3.0
notsnark_justinformal: 1.0
notsnark_news: 0.375
notsnark_opinion: 3.076923076923077
notsnark_satire: 1.0
snark: 3.76
		 */
	}

}
