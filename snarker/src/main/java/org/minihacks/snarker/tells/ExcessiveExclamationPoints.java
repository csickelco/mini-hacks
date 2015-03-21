package org.minihacks.snarker.tells;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.util.CoreMap;

public class ExcessiveExclamationPoints implements SnarkTellDetector {

	@Override
	public SnarkTell detect(List<CoreMap> sentences) {
		SnarkTell retval = new SnarkTell();
		List<String> offenders = new LinkedList<String>();
		
		for(CoreMap sentence : sentences)
		{
			if(sentence.toString().endsWith("!"))
			{
				offenders.add(sentence.toString());
			}
		}
		
		retval.setName("Excessive exclamation points");
		retval.setOffenders(offenders);
		
		return retval;
	}

}
