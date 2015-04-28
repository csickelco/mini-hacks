package org.minihacks.snarker.tells;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

//Hostile words from: http://www.wjh.harvard.edu/~inquirer/homecat.htm
public class TestFileLemmaTellDetector {
	Logger logger = LoggerFactory.getLogger(TestFileLemmaTellDetector.class);
	
	private FileLemmaTellDetector knowingDetector;
	private FileLemmaTellDetector hostileDetector;
	StanfordCoreNLP pipeline;
	
	@Before
	public void setup() throws IOException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.INFO);
	    
		knowingDetector = new FileLemmaTellDetector();
		knowingDetector.setName("Knowing Detector");
		knowingDetector.setFile("knowing-lemmas.txt");
		knowingDetector.setDimension(SnarkDimension.KNOWING);
		
		hostileDetector = new FileLemmaTellDetector();
		hostileDetector.setName("Hostile Detector");
		hostileDetector.setFile("hostile-lemmas.txt");
		hostileDetector.setDimension(SnarkDimension.HOSTILE);
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}

	@Test
	public void testEnlLossButInQuotes() {
		Annotation annotation = pipeline.process("All the justices need to do, DeSanctis explains, is find that the Louisiana court’s determination that Brumfield is not disabled—without giving him a chance to prove that he is—is \"unreasonable\" under federal law.");

		SnarkTell st = knowingDetector.detect(annotation);
		logger.info("Result {}", st.toString());
		assertFalse(st.isTellFound());
	}
	
	@Test
	public void testEnlLossLemma() {
		//Ignore is on the EnlLoss list, this sentence has "ignoring"
		String sentence = "But denying that Calipari gets by far the better end of the deal is ignoring reality.";
		Annotation annotation = pipeline.process(sentence);

		SnarkTell st = knowingDetector.detect(annotation);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
		assertEquals(1, st.getOffenders().size());
		assertTrue(st.getOffenders().get(0).startsWith("ignoring"));
		assertEquals(SnarkDimension.KNOWING, st.getDimension());
	}
	
	@Test
	public void testEvalNegative() {
		String sentence = "It is a sad fact of the criminal justice system that those states that most enthusiastically administer the death penalty are also the most incompetent at doing so.";
		Annotation annotation = pipeline.process(sentence);

		SnarkTell st = hostileDetector.detect(annotation);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
		assertEquals(1, st.getOffenders().size());
		assertTrue(st.getOffenders().get(0).startsWith("incompetent"));
		assertEquals(SnarkDimension.HOSTILE, st.getDimension());
	}
}
