package org.minihacks.snarker.tells;

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

public class RegexSentenceDetector implements SnarkTellDetector {

	private String name;
	private Set<Pattern> tellExpressions = new HashSet<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<Pattern> getTellExpressions() {
		return tellExpressions;
	}
	public void setTellExpressions(Set<String> tellExpressionStrings) {
		for (String tellExpression : tellExpressionStrings) {
			Pattern p = Pattern.compile(".*" + tellExpression + ".*");
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
		String[] knowingWords = new String[]{
				"not that hard", 
				"of course", 
				"should make that .* clear", 
				"you might think", 
				"you'd be wrong", 
				"you would be .* wrong",
				"type of .* person"
		};
		phrases.addAll(Arrays.asList(knowingWords));
		d.setTellExpressions(phrases);
		SnarkTell t = d.detect(sentences);
		System.out.println(t);
	}

}
