package org.minihacks.snarker.tells;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

import ch.qos.logback.classic.Level;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class AccuracyConversationalPhrasesTellDetector {
	Logger logger = LoggerFactory.getLogger(AccuracyConversationalPhrasesTellDetector.class);
	
	private FilePhraseDetector d;
	StanfordCoreNLP pipeline;
	
	/*
	 * Consists of:
	 * General Inquirer: intrj
	 * Own observations
	 * https://www.speechink.com/transcribers/insurance_quickstart#Glossary
	 * 
	 */

	@Before
	public void setup() throws IOException {
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    root.setLevel(Level.INFO);
	    
		d = new FilePhraseDetector();
		d.setName("Conversational Phrases Detector");
		d.setFile("conversational-phrases.txt");
		d.setDimension(SnarkDimension.IRREVERENT);
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	@Test
	public void testBorderlineSnark() throws IOException {
		outputResults("borderline_snark");
	}
	
	@Test
	public void testNotSnarkAnalysis() throws IOException {
		outputResults("notsnark_analysis");
	}
	
	@Test
	public void testNotSnarkJustInformal() throws IOException {
		outputResults("notsnark_justinformal");
	}
	
	@Test
	public void testNotSnarkNews() throws IOException {
		outputResults("notsnark_news");
	}
	
	@Test
	public void testNotSnarkOpinion() throws IOException {
		outputResults("notsnark_opinion");
	}
	
	@Test
	public void testNotSnarkSatire() throws IOException {
		outputResults("notsnark_satire");
	}
	
	@Test
	public void testSnark() throws IOException {
		outputResults("snark");
	}
	
	private void outputResults(String path) throws IOException {
		logger.info("=== " + path.toUpperCase() + " ===");
		String processPath= "/" + path + "/*";
		PathMatchingResourcePatternResolver r = new PathMatchingResourcePatternResolver();
		Resource[] resources=r.getResources(processPath);
		for (Resource resource : resources) {
			Annotation annotation = pipeline.process(IOUtils.toString(new FileReader(resource.getFile())));
			List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
			SnarkTell st = d.detect(sentences);
			logger.info("Result {}", st.toString());
		}
	}
}
