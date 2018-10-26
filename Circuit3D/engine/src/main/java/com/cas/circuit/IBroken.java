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
	
	/**
	 * 故障记录保存键
	 * @return
	 */
	public String getKey();
	
	public String getName();
	/**
	 * 设置的故障的描述
	 * @return
	 */
	public String getDesc();
}
