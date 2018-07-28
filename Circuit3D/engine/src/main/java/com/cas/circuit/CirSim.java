package com.cas.circuit;// circuit.CirSim.java (c) 2010 by Paul Falstad

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.cas.circuit.component.Terminal;
import com.cas.circuit.component.Wire;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.CurrentElm;
import com.cas.circuit.element.GroundElm;
import com.cas.circuit.element.RailElm;
import com.cas.circuit.element.VoltageElm;
import com.cas.circuit.util.Util;
import com.jme3.app.Application;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CirSim implements Runnable {

	@Getter
	@Setter
	private double timer;

	private boolean analyzeFlag;

	private String stopMessage;

	private List<CircuitElm> elmList = new ArrayList<>();
	private double circuitMatrix[][], circuitRightSide[], origMatrix[][], origRightSide[];
	private RowInfo circuitRowInfo[];
	private int circuitPermute[];
	private boolean nonLinearCircuit;
	private int circuitMatrixSize, circuitMatrixFullSize;
	private boolean circuitNeedsMap;

	private long lastFrameTime, lastIterTime;
	private List<CircuitNode> nodeList;

	private CircuitElm voltageSources[];

	@Setter
	private boolean converged;
	@Getter
	private double tpf;

	private Application app;

	private ConcurrentLinkedQueue<Runnable> enqueue = new ConcurrentLinkedQueue<>();

	public CirSim() {
	}

	public CirSim(Application app) {
		this.app = app;
	}

	public void updateCircuit(double tpf) {
//System.out.println(tpf);
		if (analyzeFlag) {
			analyzeCircuit();
			analyzeFlag = false;
		}
		try {
			runCircuit(tpf);
		} catch (Exception e) {
			e.printStackTrace();
			analyzeFlag = true;
			return;
		}

		elmList.forEach(CircuitElm::printInfo);
	}

	private CircuitNode getCircuitNode(int n) {
		if (n >= nodeList.size()) {
			return null;
		}
		return nodeList.get(n);
	}

	private void printMatrix() {
//		StringBuffer buf = new StringBuffer();
//		buf.append("\r\n");
//		for (int i = 0; i < circuitMatrix.length; i++) {
//			for (int j = 0; j < circuitMatrix[i].length; j++) {
//				buf.append("\t").append(String.format("%.10f", circuitMatrix[i][j]));
//			}
//			buf.append("\r\n");
//		}
//		log.info(buf.toString());
	}

	private void printRightMatrix() {
//		StringBuffer buf = new StringBuffer();
//		buf.append("\r\n");
//
//		for (int i = 0; i < circuitRightSide.length; i++) {
//			buf.append(String.format("%.8f", circuitRightSide[i])).append("\r\n");
//		}
//		log.info(buf.toString());
	}

	private void printRow() {
//		for (int i = 0; i < circuitRowInfo.length; i++) {
//			log.info("row {} will be remove ? : {}", i, circuitRowInfo[i].isDropRow());
//		}
	}

	protected void analyzeCircuit() {
		if (elmList.isEmpty()) {
			return;
		}
		stopMessage = null;
		nodeList = new ArrayList<CircuitNode>();

		// System.out.println("ac1");
		// look for voltage or ground element
//		lookForVoltOrGroundElm(elmList);
		isoElectricTerminalMap.clear();
		passedWire.clear();
		passedTerminal.forEach(t -> t.setIsoElectricNum(null));
		passedTerminal.clear();

		shrinkWire(elmList);

//		log.info(isoElectricTerminalMap.toString());

		// System.out.println("ac2");
		// allocate nodes and voltage sources
		int vscount = allocateNodeAndVoltageSource(elmList);

		voltageSources = new CircuitElm[vscount];

		// System.out.println("ac3");
		// determine if circuit is nonlinear
		determineNonlinear();

		int matrixSize = nodeList.size() - 1 + vscount;
		initMatrix(matrixSize);

		// stamp linear circuit elements
		for (int i = 0; i != elmList.size(); i++) {
			CircuitElm ce = getCircuitElm(i);
			ce.stamp();
//			System.out.println(ce);
		}
//		log.info("stamp linear circuit elements");
		// System.out.println("ac4");
		// determine nodes that are unconnected
		determineNodeUnconnected();
		// System.out.println("ac5");

		findPathInfo();

		// System.out.println("ac6");
		// simplify the matrix; this speeds things up quite a bit
//		log.info("simplify the matrix; this speeds things up quite a bit");
		simplifyMatrix();

		// System.out.println("ac7");
		// find size of new matrix
		int newsize = findSizeOfNewMatrix(matrixSize);
//		log.info("find size of new matrix {}", newsize);

		// System.out.println("ac8");
		// make the new, simplified matrix
		makeNewSimplifiedMatrix(matrixSize, newsize);
//		log.info("make the new, simplified matrix");

		matrixSize = circuitMatrixSize = newsize;
		for (int i = 0; i != matrixSize; i++) {
			origRightSide[i] = circuitRightSide[i];
		}
		for (int i = 0; i != matrixSize; i++) {
			for (int j = 0; j != matrixSize; j++) {
				origMatrix[i][j] = circuitMatrix[i][j];
			}
		}
		circuitNeedsMap = true;

		printRow();
		printMatrix();
		printRightMatrix();
//		if a matrix is linear, we can do the lu_factor here instead of needing to do it every frame
		if (!nonLinearCircuit) {
			if (!Util.lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
				stop("Singular matrix!", null);
				return;
			}
		}
	}

	private void initMatrix(int matrixSize) {
		circuitMatrix = new double[matrixSize][matrixSize];
		circuitRightSide = new double[matrixSize];

		origMatrix = new double[matrixSize][matrixSize];
		origRightSide = new double[matrixSize];

		circuitMatrixSize = circuitMatrixFullSize = matrixSize;
		circuitRowInfo = new RowInfo[matrixSize];
		circuitPermute = new int[matrixSize];
		for (int i = 0; i != matrixSize; i++) {
			circuitRowInfo[i] = new RowInfo();
		}

		circuitNeedsMap = false;
	}

	private void determineNodeUnconnected() {
		boolean closure[] = new boolean[nodeList.size()];
		closure[0] = true;

		boolean changed = true;
		while (changed) {
			changed = false;
			for (int i = 0; i != elmList.size(); i++) {
				CircuitElm ce = getCircuitElm(i);
				// loop through all ce's nodes to see if they are connected to other nodes not in closure
				for (int psotIndex = 0; psotIndex < ce.getPostCount(); psotIndex++) {
					if (!closure[ce.getNodeIndex(psotIndex)]) {
						if (ce.hasGroundConnection(psotIndex)) {
							closure[ce.getNodeIndex(psotIndex)] = changed = true;
						}
						continue;
					}
					for (int k = 0; k != ce.getPostCount(); k++) {
						if (psotIndex == k) {
							continue;
						}
						int kn = ce.getNodeIndex(k);
						if (ce.getConnection(psotIndex, k) && !closure[kn]) {
//							if (ce instanceof com.cas.circuit.element.RelayElm) {
//								log.info("{} - {} : connected {}", psotIndex, k, kn);
//							}
							closure[kn] = true;
							changed = true;
						}
					}
				}
			}
			if (changed) {
				continue;
			}

			// connect unconnected nodes
			for (int i = 0; i != nodeList.size(); i++) {
				if (!closure[i] && !getCircuitNode(i).isInternal()) {
//					System.out.println("node " + i + " unconnected");
					stampResistor(0, i, 1e8);
					closure[i] = true;
					changed = true;
					break;
				}
			}
		}
	}

	private void findPathInfo() {
		for (int i = 0; i != elmList.size(); i++) {
			CircuitElm ce = getCircuitElm(i);
			// look for inductors with no current path
//			if (ce instanceof InductorElm) {
//				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNodeIndex(1), nodeList.size(), elmList);
//				// first try findPath with maximum depth of 5, to avoid slowdowns
//				if (!fpi.findPath(ce.getNodeIndex(0), 5) && !fpi.findPath(ce.getNodeIndex(0))) {
//					System.out.println(ce + " no path");
//					ce.reset();
//				}
//			}
			// look for current sources with no current path
			if (ce instanceof CurrentElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNodeIndex(1), nodeList.size(), elmList);
				if (!fpi.findPath(ce.getNodeIndex(0))) {
					stop("No path for current source!", ce);
					return;
				}
			}
			// look for voltage source loops
//			if ((ce instanceof VoltageElm && ce.getPostCount() == 2) || ce instanceof WireElm) {
			if ((ce instanceof VoltageElm && ce.getPostCount() == 2)) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce, ce.getNodeIndex(1), nodeList.size(), elmList);
				if (fpi.findPath(ce.getNodeIndex(0))) {
					stop("Voltage source/wire loop with no resistance!", ce);
					return;
				}
			}
			// look for shorted caps, or caps w/ voltage but no R
//			if (ce instanceof CapacitorElm) {
//				FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce, ce.getNodeIndex(1), nodeList.size(), elmList);
//				if (fpi.findPath(ce.getNodeIndex(0))) {
//					System.out.println(ce + " shorted");
//					ce.reset();
//				} else {
//					fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNodeIndex(1), nodeList.size(), elmList);
//					if (fpi.findPath(ce.getNodeIndex(0))) {
//						stop("Capacitor loop with no resistance!", ce);
//						return;
//					}
//				}
//			}
		}
	}

	private void makeNewSimplifiedMatrix(int matrixSize, int newsize) {
		double newmatx[][] = new double[newsize][newsize];
		double newrs[] = new double[newsize];
		int ii = 0;
		for (int i = 0; i != matrixSize; i++) {
			RowInfo rri = circuitRowInfo[i];
			if (rri.isDropRow()) {
				rri.setMapRow(-1);
				continue;
			}
			newrs[ii] = circuitRightSide[i];
			rri.setMapRow(ii);
//			log.info("Row {} maps to {}", i, ii);
			for (int j = 0; j != matrixSize; j++) {
				RowInfo ri = circuitRowInfo[j];
				if (ri.getType() == RowInfo.ROW_CONST) {
					newrs[ii] -= ri.getValue() * circuitMatrix[i][j];
				} else {
					newmatx[ii][ri.getMapCol()] += circuitMatrix[i][j];
				}
			}
			ii++;
		}

		circuitMatrix = newmatx;
		circuitRightSide = newrs;
	}

	private int findSizeOfNewMatrix(int matrixSize) {
		int nn = 0;
		for (int i = 0; i != matrixSize; i++) {
			RowInfo elt = circuitRowInfo[i];
			if (elt.getType() == RowInfo.ROW_NORMAL) {
				elt.setMapCol(nn++);
				// System.out.println("col " + i + " maps to " + elt.mapCol);
				continue;
			}
			if (elt.getType() == RowInfo.ROW_EQUAL) {
				RowInfo e2 = null;
				// resolve chains of equality; 100 max steps to avoid loops
				for (int j = 0; j != 100; j++) {
					e2 = circuitRowInfo[elt.getNodeEq()];
					if (e2.getType() != RowInfo.ROW_EQUAL) {
						break;
					}
					if (i == e2.getNodeEq()) {
						break;
					}
					elt.setNodeEq(e2.getNodeEq());
				}
			}
			if (elt.getType() == RowInfo.ROW_CONST) {
				elt.setMapCol(-1);
			}
		}
		for (int i = 0; i != matrixSize; i++) {
			RowInfo elt = circuitRowInfo[i];
			if (elt.getType() == RowInfo.ROW_EQUAL) {
				RowInfo e2 = circuitRowInfo[elt.getNodeEq()];
				if (e2.getType() == RowInfo.ROW_CONST) {
					// if something is equal to a const, it's a const
					elt.setType(e2.getType());
					elt.setValue(e2.getValue());
					elt.setMapCol(-1);
					// System.out.println(i + " = [late]const " + elt.value);
				} else {
					elt.setMapCol(e2.getMapCol());
					// System.out.println(i + " maps to: " + e2.mapCol);
				}
			}
		}
		return nn;
	}

	private void simplifyMatrix() {
		int matrixSize = circuitMatrix.length;

		for (int row = 0; row != matrixSize; row++) {
			int qm = -1, qp = -1;
			double qv = 0;
			RowInfo rowInfo = circuitRowInfo[row];
			/*
			 * System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " " + re.dropRow);
			 */
			if (rowInfo.isLsChanges() || rowInfo.isDropRow() || rowInfo.isRsChanges()) {
				continue;
			}
			double rsadd = 0;

			// look for rows that can be removed
			int col;
			for (col = 0; col != matrixSize; col++) {
				double matrixValue = circuitMatrix[row][col];
				if (circuitRowInfo[col].getType() == RowInfo.ROW_CONST) {
//					keep a running total of const values that have been removed already
					rsadd -= circuitRowInfo[col].getValue() * matrixValue;
					continue;
				}
				if (matrixValue == 0) {
					continue;
				}
				if (qp == -1) {
					qp = col;
					qv = matrixValue;
					continue;
				}
				if (qm == -1 && matrixValue == -qv) {
					qm = col;
					continue;
				}
				break;
			}
//			log.info("row:{}.\t列qp:{}\t与列qm:{}，\t查找的列数col:{}", row, qp, qm, col);
			// System.out.println("line " + i + " " + qp + " " + qm + " " + j);
			if (col == matrixSize) {
				if (qp == -1) {
					stop("Matrix error", null);
					return;
				}
				RowInfo elt = circuitRowInfo[qp];
				if (qm == -1) {
					// we found a row with only one nonzero entry; that value is a constant
					int k;
					for (k = 0; elt.getType() == RowInfo.ROW_EQUAL && k < 100; k++) {
						// follow the chain
						log.info("following equal chain from {} to {}  {}", row, qp, elt.getNodeEq());
						qp = elt.getNodeEq();
						elt = circuitRowInfo[qp];
					}
					if (elt.getType() == RowInfo.ROW_EQUAL) {
						// break equal chains
						// System.out.println("Break equal chain");
						elt.setType(RowInfo.ROW_NORMAL);
						continue;
					}
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						System.out.println("type already " + elt.getType() + " for " + qp + "!");
						continue;
					}
					elt.setType(RowInfo.ROW_CONST);
					elt.setValue((circuitRightSide[row] + rsadd) / qv);
					circuitRowInfo[row].setDropRow(true);
					// System.out.println(qp + " * " + qv + " = const " + elt.value);
					row = -1; // start over from scratch
				} else if (circuitRightSide[row] + rsadd == 0) {
					// we found a row with only two nonzero entries, and one is the negative of the other; the values are equal
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						// System.out.println("swapping");
						int qq = qm;
						qm = qp;
						qp = qq;
						elt = circuitRowInfo[qp];
						if (elt.getType() != RowInfo.ROW_NORMAL) {
							// we should follow the chain here, but this
							// hardly ever happens so it's not worth worrying
							// about
							System.out.println("swap failed");
							continue;
						}
					}
					elt.setType(RowInfo.ROW_EQUAL);
					elt.setNodeEq(qm);
					circuitRowInfo[row].setDropRow(true);
					// System.out.println(qp + " = " + qm);
				}
			}
		}
	}

	private void determineNonlinear() {
		nonLinearCircuit = false;
		int vscount = 0;
		for (int i = 0; i != elmList.size(); i++) {
			CircuitElm ce = getCircuitElm(i);
			if (ce.nonLinear()) {
				nonLinearCircuit = true;
			}
			int ivs = ce.getVoltageSourceCount();
			for (int j = 0; j != ivs; j++) {
				voltageSources[vscount] = ce;
				ce.setVoltageSource(j, vscount++);
			}
		}
	}

//	等电位连接头集合
	private Map<Integer, Set<Terminal>> isoElectricTerminalMap = new HashMap<>();
	private Set<Wire> passedWire = new HashSet<>();
	private Set<Terminal> passedTerminal = new HashSet<>();
	private Terminal ground;

	private boolean exitFlag;

	private long start;

	private void shrinkWire(List<CircuitElm> elmList) {
		elmList.forEach(e -> {
			int postCnt = e.getPostCount();
			for (int i = 0; i < postCnt; i++) {
				Terminal terminal = e.getPostPoint(i);

				Integer group = terminal.getIsoElectricNum();
				if (group == null) {
					group = isoElectricTerminalMap.size();
				}

				findTerminal(terminal, group);
			}
		});
	}

	private void findTerminal(Terminal terminal, Integer group) {
		if (terminal == null) {
			return;
		}
		if (passedTerminal.contains(terminal)) {
			return;
		}
		passedTerminal.add(terminal);

		terminal.setIsoElectricNum(group);
		Set<Terminal> sets = isoElectricTerminalMap.get(group);
		if (sets == null) {
			isoElectricTerminalMap.put(group, sets = new HashSet<>());
		}
		isoElectricTerminalMap.get(group).add(terminal);

		terminal.getWires().stream().filter(w -> !passedWire.contains(w)).forEach(wire -> {
			if (wire.getTerm1() == terminal) {
				findTerminal(wire.getTerm2(), group);
			} else {
				findTerminal(wire.getTerm1(), group);
			}
		});
	}

	private int allocateNodeAndVoltageSource(List<CircuitElm> elmList) {
		int vscount = 0, nodeIdx, posts;

		for (int elmIdx = 0; elmIdx != elmList.size(); elmIdx++) {
			CircuitElm elm = getCircuitElm(elmIdx);
//			元器件内部节点数
//			int ivs = elm.getVoltageSourceCount();
			posts = elm.getPostCount();

//			allocate a node for each post and match posts to nodes
//			为每一个post[接线柱]分配节点
			for (int postIdx = 0; postIdx != posts; postIdx++) {
				nodeIdx = -1;

				Terminal terminal = elm.getPostPoint(postIdx);
//				从nodeList中找到与pt点坐标相同的点。
				for (int i = 0; i != nodeList.size(); i++) {
					CircuitNode node = getCircuitNode(i);
					if (node.getTerminalSize() == 0) {
						continue;
					}

//					if (pt.x == node.getX() && pt.y == node.getY()) {
//					if (node.hasTerminal(terminal)) {
					if (node.getNum() == terminal.getIsoElectricNum()) {
						nodeIdx = i;
						break;
					}
				}
				if (nodeIdx == -1) { // 表示没有找到，就在当前坐标处创建一个node
					CircuitNode node = new CircuitNode();
					node.setNum(terminal.getIsoElectricNum());
					node.addTerminal(terminal);
					elm.setNode(postIdx, nodeList.size()); // 元器件每一个post对应的节点Node在集合中的序号
					nodeList.add(node);
				} else {
					getCircuitNode(nodeIdx).addTerminal(terminal);
					elm.setNode(postIdx, nodeIdx);
					// if it's the ground node, make sure the node voltage is 0, cause it may not get set later
					if (nodeIdx == 0) {
						elm.setNodeVoltage(postIdx, 0);
					}
				}
			}

//			int inodes = elm.getInternalNodeCount();
//			for (int j = 0; j != inodes; j++) {
//				Terminal t = new Terminal("Internal");
//				t.setElm(elm);
//				t.setIndexInElm(posts + j);
//
//				CircuitNode node = new CircuitNode();
//				node.setInternal(true);
//				node.addTerminal(t);
//
//				elm.setNode(posts + j, nodeList.size()); // 元器件每一个post对应的节点Node在集合中的序号
//
//				nodeList.addElement(node);
//			}

			vscount += elm.getVoltageSourceCount();
		}
		return vscount;
	}

//	private Terminal ground;

	private void lookForVoltOrGroundElm(List<CircuitElm> elmList) {
		boolean gotGround = false;
		boolean gotRail = false;
		CircuitElm volt = null;

		for (int i = 0; i != elmList.size(); i++) {
			CircuitElm ce = getCircuitElm(i);
			if (ce instanceof GroundElm) {
				gotGround = true;
				break;
			}
			if (ce instanceof RailElm) {
				gotRail = true;
			}
			if (volt == null && ce instanceof VoltageElm) {
				volt = ce;
			}
		}

		// if no ground, and no rails, then the voltage elm's first terminal is ground
		CircuitNode cn = new CircuitNode();
		if (!gotGround && volt != null && !gotRail) {
			ground = volt.getPostPoint(0);
		} else {
			ground = new Terminal("ground");
			cn.addTerminal(ground);
		}
		ground.setNode(cn);

		nodeList.add(cn);
	}

	private void stop(String s, CircuitElm ce) {
		stopMessage = s;
		circuitMatrix = null;
		analyzeFlag = false;
		log.error("元器件错误{}->{}", ce, stopMessage);
	}

	// control voltage source vs with voltage from n1 to n2 (must also call stampVoltageSource())
	public void stampVCVS(int n1, int n2, double coef, int vs) {
		int vn = nodeList.size() + vs;
		stampMatrix(vn, n1, coef);
		stampMatrix(vn, n2, -coef);
	}

	// stamp independent voltage source #vs, from n1 to n2, amount v
	public void stampVoltageSource(int n1, int n2, int vs, double v) {
		int vn = nodeList.size() + vs;
		stampMatrix(vn, n1, -1);
		stampMatrix(vn, n2, 1);
		stampRightSide(vn, v);
		stampMatrix(n1, vn, 1);
		stampMatrix(n2, vn, -1);
	}

	// use this if the amount of voltage is going to be updated in doStep()
	public void stampVoltageSource(int n1, int n2, int vs) {
		int vn = nodeList.size() + vs;
		stampMatrix(vn, n1, -1);
		stampMatrix(vn, n2, 1);
		stampRightSide(vn);
		stampMatrix(n1, vn, 1);
		stampMatrix(n2, vn, -1);
	}

	public void updateVoltageSource(int n1, int n2, int vs, double v) {
		int vn = nodeList.size() + vs;
		stampRightSide(vn, v);
	}

	public void stampResistor(int row, int col, double value) {
		double r0 = 1 / value;
		if (Double.isNaN(r0) || Double.isInfinite(r0)) {
			System.out.print("bad resistance " + value + " " + r0 + "\n");
			int a = 0;
			a /= a;
		}
		stampMatrix(row, row, r0);
		stampMatrix(col, col, r0);
		stampMatrix(row, col, -r0);
		stampMatrix(col, row, -r0);
	}

	public void stampConductance(int n1, int n2, double r0) {
		stampMatrix(n1, n1, r0);
		stampMatrix(n2, n2, r0);
		stampMatrix(n1, n2, -r0);
		stampMatrix(n2, n1, -r0);
	}

	// current from cn1 to cn2 is equal to voltage from vn1 to 2, divided by g
	public void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
		stampMatrix(cn1, vn1, g);
		stampMatrix(cn2, vn2, g);
		stampMatrix(cn1, vn2, -g);
		stampMatrix(cn2, vn1, -g);
	}

	public void stampCurrentSource(int n1, int n2, double i) {
		stampRightSide(n1, -i);
		stampRightSide(n2, i);
	}

	// stamp a current source from n1 to n2 depending on current through vs
	public void stampCCCS(int n1, int n2, int vs, double gain) {
		int vn = nodeList.size() + vs;
		stampMatrix(n1, vn, gain);
		stampMatrix(n2, vn, -gain);
	}

	// stamp value x in row i, column j, meaning that a voltage change
	// of dv in node j will increase the current into node i by x dv.
	// (Unless i or j is a voltage source node.)
	public void stampMatrix(int row, int column, double value) {
		if (row > 0 && column > 0) {
			if (circuitNeedsMap) {
				row = circuitRowInfo[row - 1].getMapRow();
				RowInfo ri = circuitRowInfo[column - 1];
				if (ri.getType() == RowInfo.ROW_CONST) {
					System.out.println("Stamping constant " + row + " " + column + " " + value);
					circuitRightSide[row] -= value * ri.getValue();
					return;
				}
				column = ri.getMapCol();
				// System.out.println("stamping " + i + " " + j + " " + x);
			} else {
				row--;
				column--;
			}
			circuitMatrix[row][column] += value;
		}
	}

	// stamp value x on the right side of row i, representing an
	// independent current source flowing into node i
	public void stampRightSide(int row, double x) {
		if (row > 0) {
			if (circuitNeedsMap) {
				row = circuitRowInfo[row - 1].getMapRow();
				// System.out.println("stamping " + i + " " + x);
			} else {
				row--;
			}
			circuitRightSide[row] += x;
		}
	}

	// indicate that the value on the right side of row i changes in doStep()
	public void stampRightSide(int i) {
		// System.out.println("rschanges true " + (i-1));
		if (i > 0) {
			circuitRowInfo[i - 1].setRsChanges(true);
		}
	}

	// indicate that the values on the left side of row i change in doStep()
	public void stampNonLinear(int i) {
		if (i > 0) {
			circuitRowInfo[i - 1].setLsChanges(true);
		}
	}

	protected void runCircuit(double timeStep) {
		if (circuitMatrix == null || elmList.size() == 0) {
			circuitMatrix = null;
			return;
		}
		// int maxIter = getIterCount();
//		步进频率
//		long steprate = (long) (160 * getIterCount());
		long steprate = 164;
		long tm = System.currentTimeMillis();
		long lit = lastIterTime;
//		(tm - lastIterTime): 两次执行runCircuit 的时间差， 单位:毫秒
		if (1000 >= steprate * (tm - lastIterTime)) {
			return;
		}
//		System.out.println(tm);
		for (int iter = 1;; iter++) {
			int subiter;
			for (int i = 0; i != elmList.size(); i++) {
				CircuitElm ce = getCircuitElm(i);
				ce.startIteration();
			}
			final int subiterCount = 5000;
			for (subiter = 0; subiter != subiterCount; subiter++) {
				converged = true;
				for (int i = 0; i != circuitMatrixSize; i++) {
					circuitRightSide[i] = origRightSide[i];
				}
//				非线性电路的处理
				if (nonLinearCircuit) {
					for (int i = 0; i != circuitMatrixSize; i++) {
						for (int j = 0; j != circuitMatrixSize; j++) {
//							if( i == j) {
//								if(origMatrix[i][j] == 0) {
//									System.err.println("error!!!");
//								}
//							}
							circuitMatrix[i][j] = origMatrix[i][j];
						}
					}
				}
				for (int i = 0; i != elmList.size(); i++) {
					CircuitElm ce = getCircuitElm(i);
					ce.doStep();
				}
				if (!validate()) {
					return; // 终止
				}
//				if (printit) {
//					for (j = 0; j != circuitMatrixSize; j++) {
//						for (i = 0; i != circuitMatrixSize; i++) {
//							System.out.print(String.format("\t%.8f", circuitMatrix[j][i]));
//						}
//						System.out.print("\t" + circuitRightSide[j] + "\n");
//					}
//					System.out.print("\n");
//				}
				if (nonLinearCircuit) {
					if (converged && subiter > 0) {
//						log.info("break");
						break;
					}
					boolean result = Util.lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute);
					if (!result) {
						stop("Singular matrix!", null); // 奇异矩阵
						return;
					}
				}
				Util.lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute, circuitRightSide);

				for (int j = 0; j != circuitMatrixFullSize; j++) {
					RowInfo ri = circuitRowInfo[j];
					final double res;
					if (ri.getType() == RowInfo.ROW_CONST) {
						res = ri.getValue();
					} else {
						res = circuitRightSide[ri.getMapCol()];
					}
					/*
					 * System.out.println(j + " " + res + " " + ri.type + " " + ri.mapCol);
					 */
					if (Double.isNaN(res)) {
						converged = false;
						// debugprint = true;
						break;
					}
					if (j < nodeList.size() - 1) {
						CircuitNode node = getCircuitNode(j + 1);
						// 遍历每一个节点，将节点的每一个link对应的节点
						for (int idx = 0; idx != node.getTerminalSize(); idx++) {
							Terminal terminal = node.getTerminal(idx);
//							log.info("{}: {}", link, res);
							terminal.getElm().setNodeVoltage(terminal.getIndexInElm(), res);
						}

						isoElectricTerminalMap.get(node.getNum()).forEach(t -> {
							t.voltageChanged(res);
						});
					} else {
						int ji = j - (nodeList.size() - 1);
						// System.out.println("setting vsrc " + ji + " to " + res);
						voltageSources[ji].setCurrent(ji, res);
					}
				}
				if (!nonLinearCircuit) {
//					log.info("break");
					break;
				}
			}
			if (subiter > 5) {
				System.out.print("converged after " + subiter + " iterations\n");
			}
			if (subiter == subiterCount) {
				stop("Convergence failed!", null);
//				log.info("break");
				break;
			}
			tm = System.currentTimeMillis();
			lit = tm;
			if (iter * 1000 >= steprate * (tm - lastIterTime) || (tm - lastFrameTime > 500)) {
//				log.info("break{}", iter);
				break;
			}
		}
		lastIterTime = lit;
		// System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
	}

	private boolean validate() {
		if (stopMessage != null) {
			log.error(stopMessage);
			return false;
		}
		for (int j = 0; j != circuitMatrixSize; j++) {
			for (int i = 0; i != circuitMatrixSize; i++) {
				double x = circuitMatrix[i][j];
				if (Double.isNaN(x) || Double.isInfinite(x)) {
					stop("nan/infinite matrix!", null); // 矩阵中有非数字
					return false;
				}
			}
		}
		return true;
	}

//	boolean doSwitch(SwitchElm se) {
//		se.toggle();
//		analyzeFlag = true;
//		return true;
//	}

	CircuitElm constructElement(Class<?> c, int x0, int y0) {
		// find element class
		Class<?> carr[] = new Class[2];
		// carr[0] = getClass();
		carr[0] = carr[1] = int.class;
		Constructor<?> cstr = null;
		try {
			cstr = c.getConstructor(carr);
		} catch (NoSuchMethodException ee) {
			System.out.println("caught NoSuchMethodException " + c);
			return null;
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}

		// invoke constructor with starting coordinates
		Object oarr[] = new Object[2];
		oarr[0] = new Integer(x0);
		oarr[1] = new Integer(y0);
		try {
			return (CircuitElm) cstr.newInstance(oarr);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return null;
	}

	public void needAnalyze() {
		analyzeFlag = true;
	}

	public void addCircuitElm(CircuitElm elm) {
		enqueue.add(() -> {
			this.elmList.add(elm);
		});
	}
	
	public void removeCircuitElm(CircuitElm elm) {
		enqueue.add(() -> {
			this.elmList.remove(elm);
		});
	}

	public CircuitElm getCircuitElm(int n) {
		if (n >= elmList.size()) {
			return null;
		}
		return elmList.get(n);
	}

	@Override
	public void run() {
		exitFlag = false;
//		while (true) {
//			if (exitFlag) {
//				break;
//			}

		tpf = (System.nanoTime() - start) * (1e-9);

		runQueuedTasks();

		synchronized (this) {
			updateCircuit(tpf);
		}

		start = System.nanoTime();
//			System.out.println("tpf" + (System.nanoTime() - start));
		timer += tpf;
//		}
	}

	private void runQueuedTasks() {
		Runnable task;
		while ((task = enqueue.poll()) != null) {
			task.run();
		}
	}

	public void exit() {
		exitFlag = true;
	}

	public void enqueue(Runnable e) {
		app.enqueue(e);
	}

}
