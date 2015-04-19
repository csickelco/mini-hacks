package org.minihacks.snarker.tells;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

public class TestRegexSentenceDetector {
	Logger logger = LoggerFactory.getLogger(TestRegexSentenceDetector.class);
	
	private RegexSentenceDetector excessivelyConversationalPhraseDetector;
	StanfordCoreNLP pipeline;
	
	@Before
	public void setup() throws IOException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.INFO);
	    
	    excessivelyConversationalPhraseDetector = new RegexSentenceDetector();
		excessivelyConversationalPhraseDetector.setName("Excessively conversational phrase detector");
		excessivelyConversationalPhraseDetector.setDimension(SnarkDimension.IRREVERENT);
		Set<String> conversationalPhrases = new HashSet<>();
		String[] conversationalPhraseArray = new String[]{
				".*\\, you know.*",
				".*or whatever.*",
				".*sure are.*",
				".*\\, you know.*\\?",
				".*\\bsuper\\b.*",
				"because .*", //starting a sentence with because
				"but .*",	  //starting a sentence with but
				".*\\.\\.\\.(\\b\\w+\\b){1,3}", //which is...something.
				"cool.",
				"cool\\,.*",
				"especially .*" //starting a sentence with especially
		};
		conversationalPhrases.addAll(Arrays.asList(conversationalPhraseArray));
		excessivelyConversationalPhraseDetector.setTellExpressions(conversationalPhrases);
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	@Test
	public void testCool() {
		String sentence = "Cool. Glad we had this time to catch up!";
		Annotation annotation = pipeline.process(sentence);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		SnarkTell st = excessivelyConversationalPhraseDetector.detect(sentences);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
	}
	
	@Test
	public void testCoolNotAlone() {
		String sentence = "It was a cool night.";
		Annotation annotation = pipeline.process(sentence);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		SnarkTell st = excessivelyConversationalPhraseDetector.detect(sentences);
		logger.info("Result {}", st.toString());
		assertFalse(st.isTellFound());
	}

	@Test
	public void testEspecially() {
		String sentence = "I cannot emphasize how bad of an idea this is. Especially if you are Canadian.";
		Annotation annotation = pipeline.process(sentence);
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		SnarkTell st = excessivelyConversationalPhraseDetector.detect(sentences);
		logger.info("Result {}", st.toString());
		assertTrue(st.isTellFound());
	}
}
