package org.minihacks.snarker.tells;

import java.util.LinkedList;
import java.util.List;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class OneWordSentence implements SnarkTellDetector {

	@Override
	public SnarkTell detect(Annotation annotation) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for(CoreMap sentence : sentences)
		{
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			if( tokens.size() == 2 ) {
				offenders.add(sentence.toString());
			}
		}
		
		retval.setName("One Word Sentence");
		retval.setOffenders(offenders);
		retval.setDimension(SnarkDimension.HOSTILE);
		
		return retval;
	}

}
