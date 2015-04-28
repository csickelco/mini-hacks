package org.minihacks.snarker.tells;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class FilePhraseDetector implements SnarkTellDetector {
	Logger logger = LoggerFactory.getLogger(FilePhraseDetector.class);
	
	private String name;
	private Set<Pattern> tellExpressions = new HashSet<>();
	private SnarkDimension dimension;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SnarkDimension getDimension() {
		return dimension;
	}
	public void setDimension(SnarkDimension dimension) {
		this.dimension = dimension;
	}
	public void setFile(String file) throws IOException {
		logger.info("Reading in file {}", file);
		InputStream in = null;
		try {
			in = this.getClass().getClassLoader().getResourceAsStream(file);
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while((line=r.readLine()) != null ) {
				String lineNormalized = line.toLowerCase().trim();
				logger.debug("Phrase: " + lineNormalized);
				if( lineNormalized.length() > 0 ) {
					//TODO: Can we just concatenate all these expressions?
					Pattern p = Pattern.compile(".*\\b" + lineNormalized + "\\b.*");
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
	public SnarkTell detect(Annotation annotation) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
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
		retval.setDimension(dimension);
		retval.setOffenders(offenders);
		return retval;
	}

	public static void main(String[] args) throws Exception {
		StanfordCoreNLP pipeline;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);

		Annotation annotation = pipeline.process("something something something Kick ass something something");

		//http://fffff.at/googles-official-list-of-bad-words/
		FilePhraseDetector d = new FilePhraseDetector();
		d.setName("Profanity Detector");
		d.setFile("profanity.txt");
		SnarkTell t = d.detect(annotation);
		System.out.println(t);
	}
}
