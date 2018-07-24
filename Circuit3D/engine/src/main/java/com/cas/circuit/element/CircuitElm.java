package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getCurrentDText;
import static com.cas.circuit.util.Util.getVoltageDText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.bind.Unmarshaller;

import com.cas.circuit.CirSim;
import com.cas.circuit.component.Terminal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CircuitElm {
	public static double voltageRange = 5;
	public static double currentMult, powerMult;
	static CirSim sim;

	// nodes数组， 数组索引对应post的编号， 值对应节点node[CircuitNode]在集合nodeList[Vector]中的索引
	int nodes[], voltSource;
	int dx, dy, dsign;
	double dn, dpx1, dpy1;
	Terminal term1, term2;// , lead1, lead2;
	double volts[];
	double current, curcount;
	boolean noDiagonal;

	protected List<String> info;

	public static void initClass(CirSim s) {
		sim = s;
	}

	protected CircuitElm() {
		allocNodes();
	}

	protected CircuitElm(Unmarshaller u, Function<String, Terminal> f, Map<String, String> params) {
		this();
		try {
			setPostPoint(0, f.apply(params.get("term1")));
			setPostPoint(1, f.apply(params.get("term2")));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void allocNodes() {
		nodes = new int[getPostCount() + getInternalNodeCount()];
		volts = new double[getPostCount() + getInternalNodeCount()];
	}

	public void reset() {
		for (int i = 0; i != getPostCount() + getInternalNodeCount(); i++) {
			volts[i] = 0;
		}
		curcount = 0;
	}

	public void setCurrent(int x, double c) {
		current = c;
	}

	public double getCurrent() {
		return current;
	}

	public void doStep() {
	}

	public void delete() {
	}

	public void startIteration() {
	}

	public double getPostVoltage(int x) {
		return volts[x];
	}

	public void setNodeVoltage(int n, double c) {
		volts[n] = c;
		calculateCurrent();

		getPostPoint(n).voltageChanged(c);
	}

	void calculateCurrent() {
	}

	public void stamp() {
	}

	public int getVoltageSourceCount() {
		return 0;
	}

	public int getInternalNodeCount() {
		return 0;
	}

	public void setNode(int p, int n) {
		nodes[p] = n;
	}

	public void setVoltageSource(int n, int v) {
		voltSource = v;
	}

	int getVoltageSource() {
		return voltSource;
	}

	public double getVoltageDiff() {
		return volts[0] - volts[1];
	}

	public boolean nonLinear() {
		return false;
	}

	public int getPostCount() {
		return 2;
	}

	public int getNodeIndex(int postIndex) {
		return nodes[postIndex];
	}

	public Terminal getPostPoint(int n) {
		return (n == 0) ? term1 : (n == 1) ? term2 : null;
	}

	public int getPostIndex(Terminal t) {
		int index = (t == term1) ? 0 : (t == term2) ? 1 : -1;
//		if(index == -1) {
//			System.out.println("CircuitElm.getPostIndex()");
//		}
		return index;
	}

	public void setPostPoint(int n, Terminal t) {
		if (n == 0) {
			term1 = t;
		} else if (n == 1) {
			term2 = t;
		}
		t.setIndexInElm(n);
		t.setElm(this);
	}

	public boolean isCenteredText() {
		return false;
	}

	public void printInfo() {
		if (this instanceof RelayElm) {
			info = new ArrayList<>();
			buildInfo();
//			log.info(info.toString());
		}
	};

	void buildInfo() {
		info.add(String.format("I = %s", getCurrentDText(getCurrent())));
		info.add(String.format("Vd = %s", getVoltageDText(getVoltageDiff())));
	}

	double getPower() {
		return getVoltageDiff() * current;
	}

	public double getScopeValue(int x) {
		return (x == 1) ? getPower() : getVoltageDiff();
	}

	public String getScopeUnits(int x) {
		return (x == 1) ? "W" : "V";
	}

	public boolean getConnection(int n1, int n2) {
		return true;
	}

	public boolean hasGroundConnection(int n1) {
		return false;
	}

	public boolean isWire() {
		return false;
	}

	public boolean canViewInScope() {
		return getPostCount() <= 2;
	}

	boolean comparePair(int x1, int x2, int y1, int y2) {
		return ((x1 == y1 && x2 == y2) || (x1 == y2 && x2 == y1));
	}

	boolean isGraphicElmt() {
		return false;
	}

	@Override
	public String toString() {
		return "CircuitElm [term1=" + term1 + ", term2=" + term2 + "]";
	}

}
