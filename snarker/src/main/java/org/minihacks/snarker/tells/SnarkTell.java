package org.minihacks.snarker.tells;

import java.util.List;

public class SnarkTell {

	private List<String> offenders;
	private String name;
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
	@Override
	public String toString() {
		return "SnarkTell [offenders=" + offenders + ", name=" + name + "]";
	}
}
