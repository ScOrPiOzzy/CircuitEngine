package com.cas.circuit;

import java.util.List;

import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.CurrentElm;
import com.cas.circuit.element.VoltageElm;

public class FindPathInfo {
	static final int INDUCT = 1;
	static final int VOLTAGE = 2;
	static final int SHORT = 3;
	static final int CAP_V = 4;
	boolean used[];
	int dest;
	CircuitElm firstElm;
	int type;
	private List<CircuitElm> elmList;

	FindPathInfo(int type, CircuitElm firstElm, int dest, int nodeSize, List<CircuitElm> elmList) {
		this.dest = dest;
		this.type = type;
		this.firstElm = firstElm;
		this.elmList = elmList;
		used = new boolean[nodeSize];
	}

	boolean findPath(int n1) {
		return findPath(n1, -1);
	}

	boolean findPath(int n1, int depth) {
		if (n1 == dest) {
			return true;
		}
		if (depth-- == 0) {
			return false;
		}
		if (used[n1]) {
			// System.out.println("used " + n1);
			return false;
		}
		used[n1] = true;
		int i;
		for (i = 0; i != elmList.size(); i++) {
			CircuitElm ce = elmList.get(i);

			if (ce == firstElm) {
				continue;
			}
			if (type == FindPathInfo.INDUCT) {
				if (ce instanceof CurrentElm) {
					continue;
				}
			}
			if (type == FindPathInfo.VOLTAGE) {
				if (!(ce.isWire() || ce instanceof VoltageElm)) {
					continue;
				}
			}
			if (type == FindPathInfo.SHORT && !ce.isWire()) {
				continue;
			}
			if (type == FindPathInfo.CAP_V) {
				if (!(ce.isWire() || ce instanceof VoltageElm)) {
//				if (!(ce.isWire() || ce instanceof CapacitorElm || ce instanceof VoltageElm)) {
					continue;
				}
			}
			if (n1 == 0) {
				// look for posts which have a ground connection;
				// our path can go through ground
				int j;
				for (j = 0; j != ce.getPostCount(); j++) {
					if (ce.hasGroundConnection(j) && findPath(ce.getNodeIndex(j), depth)) {
						used[n1] = false;
						return true;
					}
				}
			}
			int j;
			for (j = 0; j != ce.getPostCount(); j++) {
				// System.out.println(ce + " " + ce.getNode(j));
				if (ce.getNodeIndex(j) == n1) {
					break;
				}
			}
			if (j == ce.getPostCount()) {
				continue;
			}
			if (ce.hasGroundConnection(j) && findPath(0, depth)) {
				// System.out.println(ce + " has ground");
				used[n1] = false;
				return true;
			}
//			if (type == INDUCT && ce instanceof InductorElm) {
//				double c = ce.getCurrent();
//				if (j == 0) {
//					c = -c;
//				}
//				// System.out.println("matching " + c + " to " + firstElm.getCurrent());
//				// System.out.println(ce + " " + firstElm);
//				if (Math.abs(c - firstElm.getCurrent()) > 1e-10) {
//					continue;
//				}
//			}
			for (int k = 0; k != ce.getPostCount(); k++) {
				if (j == k) {
					continue;
				}
				// System.out.println(ce + " " + ce.getNode(j) + "-" + ce.getNode(k));
				if (ce.getConnection(j, k) && findPath(ce.getNodeIndex(k), depth)) {
					// System.out.println("got findpath " + n1);
					used[n1] = false;
					return true;
				}
				// System.out.println("back on findpath " + n1);
			}
		}
		used[n1] = false;
		// System.out.println(n1 + " failed");
		return false;
	}
}
