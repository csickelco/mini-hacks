package org.minihacks.snarker.tells;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class TestLemmaRegexSentenceDetector {
	Logger logger = LoggerFactory.getLogger(TestLemmaRegexSentenceDetector.class);
	
	private LemmaRegexSentenceDetector d;
	StanfordCoreNLP pipeline;
	
	@Before
	public void setup() throws IOException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.INFO);
	    
	    d = new LemmaRegexSentenceDetector();
		d.setName("Test detector");
		d.setDimension(SnarkDimension.KNOWING);
		Set<String> regexSet = new HashSet<>();
		String[] regexArray = new String[]{
				".*can not emphasize enough.*"
		};
		regexSet.addAll(Arrays.asList(regexArray));
		d.setTellExpressions(regexSet);
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	@Test
	public void testExact() {
		String sentence = "I can not emphasize enough just how dumb this is.";
		Annotation annotation = pipeline.process(sentence);

		SnarkTell st = d.detect(annotation);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
	}
	
	@Test
	public void testVariation2() {
		String sentence = "I cannot emphasize enough just how dumb this is.";
		Annotation annotation = pipeline.process(sentence);

		SnarkTell st = d.detect(annotation);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
	}
	
	@Test
	public void testVariation3() {
		String sentence = "I can't emphasize enough just how dumb this is.";
		Annotation annotation = pipeline.process(sentence);

		SnarkTell st = d.detect(annotation);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
	}
	
	@Test
	public void testNoHit() {
		String sentence = "I can emphasize enough this blah blah blah";
		Annotation annotation = pipeline.process(sentence);

		SnarkTell st = d.detect(annotation);
		logger.info("Result {}", st.toString());
		assertFalse(st.isTellFound());
	}
}
