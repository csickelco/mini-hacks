package org.minihacks.snarker;

import java.util.HashSet;
import java.util.Set;

import org.minihacks.snarker.tells.SnarkTell.SnarkDimension;

public class SnarkReport {

	String article;
	Set<SnarkDimension> dimensions = new HashSet<>();
	
	public String getArticle() {
		return article;
	}
	public void setArticle(String article) {
		this.article = article;
	}
	public Set<SnarkDimension> getDimensions() {
		return dimensions;
	}
	public void setDimensions(Set<SnarkDimension> dimensions) {
		this.dimensions = dimensions;
	}
	public void addDimension(SnarkDimension dimension) {
		this.dimensions.add(dimension);
	}
	
	@Override
	public String toString() {
		return (dimensions.size() == 3 ? "!!!" : "") + "SnarkReport [article=" + article + ", dimensions=" + dimensions + "]";
	}
}
