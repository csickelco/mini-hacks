package org.minihacks.snarker.utils;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class TestQuote {

	public static void main(String[] args) {
		StanfordCoreNLP pipeline;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, quote, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
		
		Annotation annotation = pipeline.process("Sentence A. Sentence B. Sentence C. Christina: \"why here's a fancy quotation for you.\" They call me Christina \"Danger\" Sickelco");
		List<CoreMap> sentences = annotation.get(CoreAnnotations.QuotationsAnnotation.class);
		for (CoreMap coreMap : sentences) {
			System.out.println(coreMap);
		}
	}

}
