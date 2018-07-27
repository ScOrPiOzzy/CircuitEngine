package com.cas.circuit.element;

public class CurrentElm extends CircuitElm {
	double currentValue;

	public CurrentElm() {
		currentValue = .01;
	}

	@Override
	public void stamp() {
		current = currentValue;
		sim.stampCurrentSource(nodes[0], nodes[1], current);
	}

	@Override
	void buildInfo() {
		info.add("current source");
		super.buildInfo();
	}

	@Override
	public double getVoltageDiff() {
		return volts[1] - volts[0];
	}
}
