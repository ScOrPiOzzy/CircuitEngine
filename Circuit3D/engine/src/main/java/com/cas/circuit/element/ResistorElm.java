package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getUnitText;

import com.cas.circuit.util.Util;

public class ResistorElm extends CircuitElm {
	public double resistance;

	public ResistorElm() {
		resistance = 100;
	}

	public ResistorElm(int r) {
		resistance = r;
	}

	@Override
	void calculateCurrent() {
		current = (volts[0] - volts[1]) / resistance;
	}

	@Override
	public void stamp() {
		sim.stampResistor(nodes[0], nodes[1], resistance);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "resistor";
		getBasicInfo(arr);
		arr[3] = "R = " + getUnitText(resistance, Util.ohmString);
		arr[4] = "P = " + getUnitText(getPower(), "W");
	}
}
