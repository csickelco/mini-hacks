package org.minihacks.snarker.tells;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class FileLemmaTellDetector implements SnarkTellDetector {
	Logger logger = LoggerFactory.getLogger(FileLemmaTellDetector.class);
	
	private String name;
	private Set<String> tellWords = new HashSet<>();
	private SnarkDimension dimension;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<String> getTellWords() {
		return tellWords;
	}
	public void setTellWords(Set<String> tellWords) {
		this.tellWords = tellWords;
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
					tellWords.add(lineNormalized);
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
	        for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        	String lemma = token.lemma();
	        	if( tellWords.contains(lemma) ) {
	        		logger.debug("Found '{}' in '{}'", lemma, sentence.toString());
	        		offenders.add(token.value() + "-'" + sentence.toString() + "'");
	        	}
	        }
		}
		
		retval.setName(name);
		retval.setOffenders(offenders);
		retval.setDimension(dimension);
		return retval;
	}
}
