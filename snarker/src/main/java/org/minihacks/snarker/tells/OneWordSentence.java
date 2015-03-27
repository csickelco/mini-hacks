package org.minihacks.snarker.tells;

import java.util.LinkedList;
import java.util.List;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

public class OneWordSentence implements SnarkTellDetector {

	@Override
	public SnarkTell detect(List<CoreMap> sentences) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		
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
