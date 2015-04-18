package org.minihacks.snarker.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import de.l3s.boilerpipe.extractors.ArticleExtractor;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemmatizer {
	
	public void process(String file) throws IOException {
		StanfordCoreNLP pipeline;
		ArticleExtractor ae = ArticleExtractor.INSTANCE;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
		
		System.out.println("=== BEGIN ===");
		InputStream in = null;
		try {
			in = this.getClass().getClassLoader().getResourceAsStream(file);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=r.readLine()) != null ) {
				Annotation annotation = pipeline.process(line);
				List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
				for (CoreMap sentence : sentences) {
			        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
			        	String lemma = token.lemma();
			        	if( !lemma.equalsIgnoreCase(token.value()) ) {
			        		System.out.println(token.value() + ": " + lemma.toUpperCase());
			        	}
			        }
				}
			}
		} finally {
			if( in != null ) {
				try {
					in.close();
				} catch( IOException e ) {
					//TODO
				}
			}
		}
		System.out.println("=== END ===");
	}

	public static void main(String[] args) throws Exception {
		String file = "overstated+vice.txt";
		Lemmatizer l = new Lemmatizer();
		l.process(file);

	}

}
