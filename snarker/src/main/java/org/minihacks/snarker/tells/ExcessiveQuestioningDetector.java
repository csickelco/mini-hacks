package org.minihacks.snarker.tells;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * 
 * @author christinasickelco
 *
 */
public class ExcessiveQuestioningDetector implements SnarkTellDetector {
	
	private int threshold = 2;
	public int getThreshold() {
		return threshold;
	}
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}


	@Override
	public SnarkTell detect(List<CoreMap> sentences) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		List<String> candidates = new LinkedList<String>();
		
		for(CoreMap sentence : sentences)
		{
			if(sentence.toString().endsWith("?") )
			{
				candidates.add(sentence.toString());
				if( candidates.size() >= threshold ) {
					offenders.addAll(candidates);
				}
			} else {
				candidates.clear();
			}
		}
		
		retval.setName("Excessive questioning");
		retval.setOffenders(offenders);
		retval.setDimension(SnarkDimension.HOSTILE);
		
		return retval;
	}

	public static void main(String[] args) {
		StanfordCoreNLP pipeline;
		
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
		
		Annotation annotation = pipeline.process("Sentence A. Sentence B. Sentence C.");
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		ExcessiveQuestioningDetector d = new ExcessiveQuestioningDetector();
		SnarkTell s = d.detect(sentences);
		System.out.println(s);
		
		annotation = pipeline.process("Am I helping a proprietor of sugary drinks look better by accepting its money to cover something I genuinely care about, or that someone else may not even pay me to cover? If I'm critical, will my article disappear? What about my career?");
		sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		d = new ExcessiveQuestioningDetector();
		s = d.detect(sentences);
		System.out.println(s);
		
		annotation = pipeline.process("A sentence that ends in a question? But then something that doesn't. Followed by another question? Followed by a declarative sentence. Followed by yet another question?");
		sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		d = new ExcessiveQuestioningDetector();
		s = d.detect(sentences);
		System.out.println(s);
	}
}
