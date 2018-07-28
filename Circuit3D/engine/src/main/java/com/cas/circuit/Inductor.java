package com.cas.circuit;

public class Inductor {
	public static final int FLAG_BACK_EULER = 2;
	private int nodes[];
	private int flags;
	private CirSim sim;

	private double inductance;
	private double compResistance;
	private double current;
	private double curSourceValue;

	public Inductor(CirSim s) {
		sim = s;
		nodes = new int[2];
	}

	public void setup(double ic, double cr, int f) {
		inductance = ic;
		current = cr;
		flags = f;
	}

	public boolean isTrapezoidal() {
		return (flags & FLAG_BACK_EULER) == 0;
	}

	public void reset() {
		current = 0;
	}

	public void stamp(int n0, int n1) {
		// inductor companion model using trapezoidal or backward euler
		// approximations (Norton equivalent) consists of a current
		// source in parallel with a resistor. Trapezoidal is more
		// accurate than backward euler but can cause oscillatory behavior.
		// The oscillation is a real problem in circuits with switches.
		nodes[0] = n0;
		nodes[1] = n1;
//		double timeStep = 5.0E-6;
		if (isTrapezoidal()) {
			compResistance = 2 * inductance / CirSim.TPF;
		} else {// backward euler
			compResistance = inductance / CirSim.TPF;
		}
		sim.stampResistor(nodes[0], nodes[1], compResistance);
		sim.stampRightSide(nodes[0]);
		sim.stampRightSide(nodes[1]);
	}

	public boolean nonLinear() {
		return true;
	}

	public void startIteration(double voltdiff) {
		if (isTrapezoidal()) {
			curSourceValue = voltdiff / compResistance + current;
		} else { // backward euler
			curSourceValue = current;
		}
	}

	public double calculateCurrent(double voltdiff) {
		// we check compResistance because this might get called before stamp(),
//		which sets compResistance, causing infinite current
		if (compResistance > 0) {
			current = voltdiff / compResistance + curSourceValue;
		}
		return current;
	}

	public void doStep(double voltdiff) {
		sim.stampCurrentSource(nodes[0], nodes[1], curSourceValue);
	}
}
