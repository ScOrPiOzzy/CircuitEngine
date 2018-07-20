package com.cas.circuit.element;

public class LEDElm extends DiodeElm {
	public LEDElm() {
		super();
		fwdrop = 2.1024259;
		setup();
	}

	@Override
	public void getInfo(String arr[]) {
		super.getInfo(arr);
		arr[0] = "LED";
	}
}
