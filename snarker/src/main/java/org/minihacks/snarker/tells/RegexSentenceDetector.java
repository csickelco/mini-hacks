package org.minihacks.snarker.tells;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class RegexSentenceDetector implements SnarkTellDetector {

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
		retval.setDimension(dimension);
		return retval;
	}

	public static void main(String[] args) {
		StanfordCoreNLP pipeline;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);

		Annotation annotation = pipeline.process("“You might think that Williams' life, by default, is frantic and interesting. But you would be very, very wrong.”");
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		RegexSentenceDetector d = new RegexSentenceDetector();
		d.setName("KnowingPhrases");
		Set<String> phrases = new HashSet<>();
		String[] sarcasmPatterns = new String[]{
				".*not that hard.*", 
				".*of course.*", 
				".*should make that .* clear.*", 
				".*you might think.*", 
				".*you'd be wrong.*", 
				".*you would be .* wrong.*",
				".*type of .* person.*"
		};
		phrases.addAll(Arrays.asList(sarcasmPatterns));
		d.setTellExpressions(phrases);
		SnarkTell t = d.detect(sentences);
		System.out.println(t);
		
		annotation = pipeline.process("And have the buttler draw a warm batch, please! something else. Thanks for the net neutrality, Oligarchs.");
		sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		d = new RegexSentenceDetector();
		d.setName("Sarcasm");
		phrases = new HashSet<>();
		sarcasmPatterns = new String[]{
				".*\\, (\\b\\w+\\b){1,4}\\!",
				"thanks\\b.*"
		};
		phrases.addAll(Arrays.asList(sarcasmPatterns));
		d.setTellExpressions(phrases);
		t = d.detect(sentences);
		System.out.println(t);
	}

}
