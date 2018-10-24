package com.cas.circuit;


public interface IBroken {

	public enum BrokenState {
		NORMAL, OPEN, CLOSE;
		
		public static BrokenState getBrokenStateByKey(String key) {
			for (BrokenState c : values()) {
				if (c.name().equals(key)) {
					return c;
				}
			}
			return null;
		}
	}

	public void setBroken(BrokenState state);
	
	public String getName();
	
	public String getDesc();
}
