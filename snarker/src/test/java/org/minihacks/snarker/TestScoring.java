package org.minihacks.snarker;

import static org.junit.Assert.*;

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

public class TestScoring {
	Logger logger = LoggerFactory.getLogger(TestScoring.class);

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
	public void testBorderlineSnark() throws Exception {
		double averageScore = p.processResourcesOnPath("/borderline_snark/**");
		assertTrue(
				"Expected 2.0-3.2, received: " + String.valueOf(averageScore),
				2.0 <= averageScore && averageScore <= 3.2);
	}
	
	@Test
	public void testNotSnarkJustInformal() throws Exception {
		double averageScore = p.processResourcesOnPath("/notsnark_justinformal/**");
		assertTrue(
				"Expected 0.5-2.5, received: " + String.valueOf(averageScore),
				0.5 <= averageScore && averageScore <= 2.5);
	}
	
	@Test
	public void testNotSnarkNews() throws Exception {
		double averageScore = p.processResourcesOnPath("/notsnark_news/**");
		assertTrue(
				"Expected 0-1, received: " + String.valueOf(averageScore),
				0 <= averageScore && averageScore <= 1);
	}
	
	@Test
	public void testNotSnarkOpinion() throws Exception {
		double averageScore = p.processResourcesOnPath("/notsnark_opinion/**");
		assertTrue("Expected 1.5-2.75, received: " + String.valueOf(averageScore), 
				1.5 <= averageScore && averageScore <= 2.75);
	}
	
	@Test
	public void testSnark() throws Exception {
		double averageScore = p.processResourcesOnPath("/snark/**");
		assertTrue("Expected 3-5, received: " + String.valueOf(averageScore), 
				3.0 <= averageScore && averageScore <= 5);
	}
}
