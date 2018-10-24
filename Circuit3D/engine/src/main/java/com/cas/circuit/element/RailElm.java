package com.cas.circuit.element;

import com.cas.circuit.CirSim;

public class RailElm extends VoltageElm {

	public RailElm(int wf) {
		super(wf);
	}

	final int FLAG_CLOCK = 1;

	@Override
	public int getPostCount() {
		return 1;
	}

	@Override
	public double getVoltageDiff() {
		return volts[0];
	}

	@Override
	public void stamp() {
		if (waveform == VoltageElm.WF_DC) {
			CirSim.ins.stampVoltageSource(0, nodes[0], voltSource, getVoltage());
		} else {
			CirSim.ins.stampVoltageSource(0, nodes[0], voltSource);
		}
	}

	@Override
	public void doStep() {
		if (waveform != VoltageElm.WF_DC) {
			CirSim.ins.updateVoltageSource(0, nodes[0], voltSource, getVoltage());
		}
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return true;
	}

}
