package org.minihacks.snarker;

import java.util.LinkedList;
import java.util.List;

import org.minihacks.snarker.tells.SnarkTell;
import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

public class SnarkReport {

	String article;
	List<String> knowingOffenders = new LinkedList<>();
	List<String> hostileOffenders = new LinkedList<>();
	List<String> irreverentOffenders = new LinkedList<>();
		
	public String getArticle() {
		return article;
	}
	public void setArticle(String article) {
		this.article = article;
	}
	public List<String> getKnowingOffenders() {
		return knowingOffenders;
	}
	public void setKnowingOffenders(List<String> knowingOffenders) {
		this.knowingOffenders = knowingOffenders;
	}
	public List<String> getHostileOffenders() {
		return hostileOffenders;
	}
	public void setHostileOffenders(List<String> hostileOffenders) {
		this.hostileOffenders = hostileOffenders;
	}
	public List<String> getIrreverentOffenders() {
		return irreverentOffenders;
	}
	public void setIrreverentOffenders(List<String> irreverentOffenders) {
		this.irreverentOffenders = irreverentOffenders;
	}
	public void addSnarkTell(SnarkTell snarkTell) {
		if( snarkTell.getDimension() == SnarkDimension.KNOWING ) {
			this.knowingOffenders.addAll(snarkTell.getOffenders());
		} else if( snarkTell.getDimension() == SnarkDimension.IRREVERENT ) {
			this.irreverentOffenders.addAll(snarkTell.getOffenders());
		} else if( snarkTell.getDimension() == SnarkDimension.HOSTILE ) {
			this.hostileOffenders.addAll(snarkTell.getOffenders());
		}
	}
	
	public int getScore() {
		int retval = 0;
		
		double dimensionScore = 0;
		
		if( knowingOffenders.size() > 0 ) {
			dimensionScore++;
		}
		if( hostileOffenders.size() > 0 ) {
			dimensionScore++;
		}
		if( irreverentOffenders.size() > 0 ) {
			dimensionScore++;
		}
		dimensionScore = dimensionScore/3.0;
		
		double offenderScore = 0;
		offenderScore += Math.min(knowingOffenders.size(), 3);
		offenderScore += Math.min(hostileOffenders.size(), 3);
		offenderScore += Math.min(irreverentOffenders.size(), 3);
		offenderScore = offenderScore/9.0;
		
		double totalScorePercent = ((dimensionScore*.8)+(offenderScore*.2))*100;
		retval = (int)Math.round(totalScorePercent/20); //Out of 5
		
		return retval;
	}
	
	public String getSummary() {
		StringBuilder sb = new StringBuilder();
		if( knowingOffenders.size() > 0 ) {
			sb.append("KNOWING, ");
		}
		if( hostileOffenders.size() > 0 ) {
			sb.append("HOSTILE, ");
		}
		if( irreverentOffenders.size() > 0 ) {
			sb.append("IRREVERENT, ");
		}
		if( sb.length() > 0 ) {
			sb.delete(sb.length()-2, sb.length());
		}
		
		return "Dimensions: [" + sb.toString() + "]";
	}
	
	@Override
	public String toString() {
		return "SnarkReport [article=" + article + ", knowingOffenders=" + knowingOffenders + ", hostileOffenders="
				+ hostileOffenders + ", irreverentOffenders=" + irreverentOffenders + "]";
	}
}
