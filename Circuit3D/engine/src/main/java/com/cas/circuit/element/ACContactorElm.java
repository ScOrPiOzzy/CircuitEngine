package com.cas.circuit.element;

public class ACContactorElm extends RelayElm {
	// 表示每一组触电有4个连接头，当前类中的4个连接头两两一对
	private static final int NC1 = 0, NC2 = 1, NO1 = 2, NO2 = 3;

	public ACContactorElm() {
		super();
		pairs = 2;
	}

	@Override
	public void stamp() {
		// resistor from coil post 1 to coil post 2
		sim.stampResistor(nodes[nCoil1], nodes[nCoil2], coilR);

		for (int p = 0; p != poleCount; p++) {
			sim.stampNonLinear(nodes[NC1 + p * pairs]);
			sim.stampNonLinear(nodes[NC2 + p * pairs]);
			sim.stampNonLinear(nodes[NO1 + p * pairs]);
			sim.stampNonLinear(nodes[NO2 + p * pairs]);
		}
	}

	@Override
	public void doStep() {
		for (int p = 0; p != poleCount; p++) {
			sim.stampResistor(nodes[NC1 + p * pairs], nodes[NC2 + p * pairs], i_position == 0 ? r_on : r_off);
			sim.stampResistor(nodes[NO1 + p * pairs], nodes[NO2 + p * pairs], i_position == 1 ? r_on : r_off);
		}
	}

	@Override
	void calculateCurrent() {
		double voltdiff = volts[nCoil1] - volts[nCoil2];
		coilCurrent = voltdiff / coilR;

		for (int p = 0; p != poleCount; p++) {
			if (i_position == 0) {
				switchCurrent[p] = (volts[NC1 + p * pairs] - volts[NC2 + p * pairs]) / r_on;
			} else if (i_position == 1) {
				switchCurrent[p] = (volts[NO1 + p * pairs] - volts[NO2 + p * pairs]) / r_on;
			} else if (i_position == 2) {
				switchCurrent[p] = 0;
			}
		}
	}
}
