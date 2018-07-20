package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getCurrentText;
import static com.cas.circuit.util.Util.getUnitText;
import static com.cas.circuit.util.Util.getVoltageText;
import static com.cas.circuit.util.Util.pi;

public class VoltageElm extends CircuitElm {
	static final int FLAG_COS = 2;
	int waveform;
	public static final int WF_DC = 0, WF_AC = 1, WF_SQUARE = 2, WF_TRIANGLE = 3, WF_SAWTOOTH = 4, WF_PULSE = 5, WF_VAR = 6;
	double frequency, maxVoltage, freqTimeZero, bias, phaseShift, dutyCycle;

	public VoltageElm(int waveform) {
		super();
		this.waveform = waveform;
		maxVoltage = 220;
		frequency = 50;
		dutyCycle = .5;
		reset();
	}

	@Override
	public void reset() {
		freqTimeZero = 0;
		curcount = 0;
	}

	public double triangleFunc(double x) {
		if (x < pi) {
			return x * (2 / pi) - 1;
		}
		return 1 - (x - pi) * (2 / pi);
	}

	@Override
	public void stamp() {
		if (waveform == WF_DC) {
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
		} else {
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource);
		}
	}

	@Override
	public void doStep() {
		if (waveform != WF_DC) {
			sim.updateVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
		}
	}

	public double getVoltage() {
		double w = 2 * pi * (sim.getT() - freqTimeZero) * frequency + phaseShift;

		switch (waveform) {
		case WF_DC:
			return maxVoltage + bias;
		case WF_AC:
			return Math.sin(w) * maxVoltage + bias;
		case WF_SQUARE:
			return bias + ((w % (2 * pi) > (2 * pi * dutyCycle)) ? -maxVoltage : maxVoltage);
		case WF_TRIANGLE:
			return bias + triangleFunc(w % (2 * pi)) * maxVoltage;
		case WF_SAWTOOTH:
			return bias + (w % (2 * pi)) * (maxVoltage / pi) - maxVoltage;
		case WF_PULSE:
			return ((w % (2 * pi)) < 1) ? maxVoltage + bias : bias;
		default:
			return 0;
		}
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public double getPower() {
		return -getVoltageDiff() * current;
	}

	@Override
	public double getVoltageDiff() {
		return volts[1] - volts[0];
	}

	public void setPhaseShift(double phaseShift) {
		this.phaseShift = phaseShift;
	}

	public void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}

	@Override
	public void getInfo(String arr[]) {
		switch (waveform) {
		case WF_DC:
		case WF_VAR:
			arr[0] = "voltage source";
			break;
		case WF_AC:
			arr[0] = "A/C source";
			break;
		case WF_SQUARE:
			arr[0] = "square wave gen";
			break;
		case WF_PULSE:
			arr[0] = "pulse gen";
			break;
		case WF_SAWTOOTH:
			arr[0] = "sawtooth gen";
			break;
		case WF_TRIANGLE:
			arr[0] = "triangle gen";
			break;
		}
		arr[1] = "I = " + getCurrentText(getCurrent());
		arr[2] = ((this instanceof RailElm) ? "V = " : "Vd = ") + getVoltageText(getVoltageDiff());
		
		int i = 3;
		if (waveform != WF_DC && waveform != WF_VAR) {
			arr[i++] = "f = " + getUnitText(frequency, "Hz");
			arr[i++] = "Vmax = " + getVoltageText(maxVoltage);
			if (bias != 0) {
				arr[i++] = "Voff = " + getVoltageText(bias);
			} else if (frequency > 500) {
				arr[i++] = "wavelength = " + getUnitText(2.9979e8 / frequency, "m");
			}
			arr[i++] = "P = " + getUnitText(getPower(), "W");
		}
		if(getCurrent() != 0) {
			arr[i++] = "R = " + getUnitText(getVoltageDiff() / getCurrent(), "");
		}
	}
}
