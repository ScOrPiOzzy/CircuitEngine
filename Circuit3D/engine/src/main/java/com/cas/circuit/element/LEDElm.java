package com.cas.circuit.element;

public class LEDElm extends DiodeElm {
	public LEDElm() {
		super();
		fwdrop = 2.1024259;
		setup();
	}

	@Override
	void buildInfo() {
		info.add("LED");
		super.buildInfo();
	}
}
