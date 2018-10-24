package com.cas.circuit.element;

import com.cas.circuit.CirSim;

public class CurrentElm extends CircuitElm {
	double currentValue;

	public CurrentElm() {
		currentValue = .01;
	}

	@Override
	public void stamp() {
		current = currentValue;
		CirSim.ins.stampCurrentSource(nodes[0], nodes[1], current);
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
