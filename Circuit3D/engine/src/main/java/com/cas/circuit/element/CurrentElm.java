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
	public void getInfo(String arr[]) {
		arr[0] = "current source";
		getBasicInfo(arr);
	}

	@Override
	public double getVoltageDiff() {
		return volts[1] - volts[0];
	}
}
