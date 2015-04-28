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

public class RegexAllDetector implements SnarkTellDetector {

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
			Pattern p = Pattern.compile(".*" + tellExpression + ".*");
			tellExpressions.add(p);
		}
	}
	
	@Override
	public SnarkTell detect(Annotation annotation) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		StringBuilder sb = new StringBuilder();
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sentence : sentences) {
			String sentenceString = sentence.toString().toLowerCase();
			sb.append(sentenceString.toString()).append(" ");
		}
		
		String fullText = sb.toString();
		for (Pattern tellPhrase : tellExpressions) {
			Matcher m = tellPhrase.matcher(fullText);
			if(m.matches()) {
				offenders.add(m.group(1));
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
		
		RegexAllDetector d2 = new RegexAllDetector();
		d2.setName("SarcasmDetector");
		d2.setDimension(SnarkDimension.HOSTILE);
		Set<String> phrases2 = new HashSet<>();
		String[] pattern2 = new String[]{
				"((\\b\\w+\\b){1,5}\\?\\s*(\\b\\w+\\b){1,3})"
		};
		phrases2.addAll(Arrays.asList(pattern2));
		d2.setTellExpressions(phrases2);
		
		Annotation annotation = pipeline.process("Private jets everywhere? Of course!");

		SnarkTell t = d2.detect(annotation);
		System.out.println(t);
		
		//Sarcasm - Excessive exclamations, particularly ,(few words*)…! (And have the buttler draw a warm batch, please!”
		RegexAllDetector d3 = new RegexAllDetector();
		d3.setName("SarcasmDetector2");
		d3.setDimension(SnarkDimension.HOSTILE);
		Set<String> phrases3 = new HashSet<>();
		String[] pattern3 = new String[]{
				"((\\b\\w+\\b){1,7}\\!\\s*(\\b\\w+\\b){1,7}\\!)"
		};
		phrases3.addAll(Arrays.asList(pattern3));
		d3.setTellExpressions(phrases3);
		
		annotation = pipeline.process("It would have been more tolerable if the hilariously inappropriate cultural decor (fire-breathers! Kabobs! Turbans!)");
		t = d3.detect(annotation);
		System.out.println(t);
	}

}
