package org.minihacks.snarker.tells;

import java.util.LinkedList;
import java.util.List;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

import edu.stanford.nlp.util.CoreMap;

/**
 * 
 * @author christinasickelco
 *
 */
public class ExcessiveExclamationPoints implements SnarkTellDetector {
	
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
		
		for(CoreMap sentence : sentences)
		{
			if(sentence.toString().endsWith("!"))
			{
				offenders.add(sentence.toString());
			}
		}
		
		retval.setName("Excessive exclamation points");
		retval.setDimension(SnarkDimension.IRREVERENT);
		if( offenders.size() >= threshold ) {
			retval.setOffenders(offenders);
		} else {
			retval.setOffenders(new LinkedList<>());
		}
		
		return retval;
	}

}
