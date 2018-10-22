package com.cas.circuit;

public interface IBroken {

	public enum BrokenState {
		NORMAL, OPEN, CLOSE;
	}

	public void setBroken(boolean broken);
	
	public String getName();
	
	public String getDesc();
}
