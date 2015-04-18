package org.minihacks.snarker.tells;

import java.util.List;

public class SnarkTell {
	
	public enum SnarkDimension { IRREVERENT, KNOWING, HOSTILE };

	private List<String> offenders;
	private String name;
	private SnarkDimension dimension;
	
	public List<String> getOffenders() {
		return offenders;
	}
	public void setOffenders(List<String> offenders) {
		this.offenders = offenders;
	}
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
	
	public boolean isTellFound() {
		return offenders.size() > 0;
	}
	
	@Override
	public String toString() {
		return "SnarkTell [offenders=" + offenders + ", name=" + name + ", dimension=" + dimension + "]";
	}
}
