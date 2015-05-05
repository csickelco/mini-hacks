package org.minihacks.snarker.tells;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class LemmaRegexSentenceDetector implements SnarkTellDetector {

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
	
	public Set<Pattern> getTellExpressions() {
		return tellExpressions;
	}
	public void setTellExpressions(Set<String> tellExpressionStrings) {
		for (String tellExpression : tellExpressionStrings) {
			Pattern p = Pattern.compile(tellExpression);
			tellExpressions.add(p);
		}
	}
	
	@Override
	public SnarkTell detect(Annotation annotation) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			StringBuilder sb = new StringBuilder();
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        	String lemma = token.lemma();
	        	sb.append(lemma).append(" ");
	        }
			String lemmatizedSentence = sb.toString();
			
			for (Pattern tellPhrase : tellExpressions) {
				Matcher m = tellPhrase.matcher(lemmatizedSentence);
				if(m.matches()) {
					offenders.add(sentence.toString());
				}
			}
		}
		
		retval.setName(name);
		retval.setOffenders(offenders);
		retval.setDimension(dimension);
		return retval;
	}

}
