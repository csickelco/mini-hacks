package org.minihacks.snarker.tells;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class FilePhraseDetector implements SnarkTellDetector {

	private String name;
	private Set<Pattern> tellExpressions = new HashSet<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setFile(String file) throws IOException {
		InputStream in = null;
		try {
			in = this.getClass().getClassLoader().getResourceAsStream(file);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=r.readLine()) != null ) {
				String lineNormalized = line.trim();
				if( lineNormalized.length() > 0 ) {
					Pattern p = Pattern.compile(".*\\b" + line + "\\b.*");
					tellExpressions.add(p);
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
	}

	@Override
	public SnarkTell detect(List<CoreMap> sentences) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		
		for (CoreMap sentence : sentences) {
			String sentenceString = sentence.toString().toLowerCase();
			for (Pattern tellPhrase : tellExpressions) {
				Matcher m = tellPhrase.matcher(sentenceString);
				if(m.matches()) {
					offenders.add(sentenceString);
				}
			}
		}
		
		retval.setName(name);
		retval.setOffenders(offenders);
		return retval;
	}

	public static void main(String[] args) throws Exception {
		StanfordCoreNLP pipeline;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);

		Annotation annotation = pipeline.process("something something something Kick ass something something");
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		//http://fffff.at/googles-official-list-of-bad-words/
		FilePhraseDetector d = new FilePhraseDetector();
		d.setName("Profanity Detector");
		d.setFile("profanity.txt");
		SnarkTell t = d.detect(sentences);
		System.out.println(t);
	}
}
