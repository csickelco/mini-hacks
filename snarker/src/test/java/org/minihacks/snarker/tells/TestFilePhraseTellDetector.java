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
public class TestFilePhraseTellDetector {
	Logger logger = LoggerFactory.getLogger(TestFilePhraseTellDetector.class);
	
	private FilePhraseDetector profanityDetector;
	private FilePhraseDetector conversationalDetector;
	StanfordCoreNLP pipeline;
	
	@Before
	public void setup() throws IOException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.INFO);
	    
	    profanityDetector = new FilePhraseDetector();
		profanityDetector.setDimension(SnarkDimension.IRREVERENT);
		profanityDetector.setName("Profanity Detector");
		profanityDetector.setFile("profanity.txt");
		
		conversationalDetector = new FilePhraseDetector();
		conversationalDetector.setName("Conversational Phrases Detector");
		conversationalDetector.setFile("conversational-phrases.txt");
		conversationalDetector.setDimension(SnarkDimension.IRREVERENT);
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}

	@Test
	public void profanity() {
		String sentence = "He can be a dick.";
		Annotation annotation = pipeline.process(sentence);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		SnarkTell st = profanityDetector.detect(sentences);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
		assertEquals(1, st.getOffenders().size());
		assertEquals(SnarkDimension.IRREVERENT, st.getDimension());
	}
	
	@Test
	public void notProfanity() {
		String sentence = "Senator Dick Durbin met with his aides.";
		Annotation annotation = pipeline.process(sentence);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		SnarkTell st = profanityDetector.detect(sentences);
		logger.info("Result {}", st.toString());
		assertFalse(st.isTellFound());
	}
	
	@Test
	public void conversational() {
		String sentence = "What they found was this:\n\"Oh yeah the weekend. People are gonna get drunk & think that I’m sexy!\" - fat chicks everywhere.\n— Trevor Noah (@Trevornoah) October 14, 2011";
		Annotation annotation = pipeline.process(sentence);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		SnarkTell st = conversationalDetector.detect(sentences);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
	}
}
