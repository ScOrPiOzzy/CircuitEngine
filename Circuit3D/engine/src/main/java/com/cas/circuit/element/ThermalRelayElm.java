package com.cas.circuit.element;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.CirSim;
import com.cas.circuit.component.Terminal;

/**
 * 热继电器
 * @author Administrator
 */
public class ThermalRelayElm extends RelayElmEx {

	private double resistance;
	private double joule;
	private double q;
	private double t; // 线圈通电时长

	private double heatCurrent[];

	public ThermalRelayElm(Function<String, Terminal> f, Map<String, String> params) {
		pairs = 2;
		poleCount = 0;
		posts = new ArrayList<>();
		setPosts(f, params.get("nc"));

		flag = poleCount;

		setPosts(f, params.get("no"));

		String value = null;
//		线圈（产生热量）
		value = params.get("heater");
		String[] arr = value.split("\\|");
//		nCoil1;
		for (int i = 0; i < arr.length; i++) {
			String[] coil = arr[i].split(",");
			addCoils(f.apply(coil[0])//
					, f.apply(coil[1]));
		}
		heatCurrent = new double[arr.length];
//		线圈阻值
		value = params.get("resistance");
		resistance = value == null ? resistance : Double.parseDouble(value);
//		使形变的热量
		value = params.get("joule");
		joule = value == null ? joule : Double.parseDouble(value);

		setupPoles();

		allocNodes();
	}

	@Override
	void setupPoles() {
		nCoil1 = pairs * poleCount;

		if (switchCurrent == null || switchCurrent.length != poleCount) {
			switchCurrent = new double[poleCount];
		}
	}

	@Override
	public void stamp() {
//		电热片电阻
		for (int i = nCoil1, j = 0; i < posts.size(); i += 2, j++) {
			CircuitElm.sim.stampResistor(nodes[nCoil1 + j * 2], nodes[nCoil1 + j * 2 + 1], resistance);
		}

		for (int p = 0; p != poleCount; p++) {
			CircuitElm.sim.stampNonLinear(nodes[p * pairs]);
			CircuitElm.sim.stampNonLinear(nodes[p * pairs + 1]);
		}
	}

	@Override
	public void startIteration() {
		q -= 5e-1; // 散热
		if (q < 0) {
			q = 0;
		}
		double max = 0;
		for (int i = 0; i < heatCurrent.length; i++) {
			max += heatCurrent[i] * heatCurrent[i] * resistance * CirSim.TPF * 1e3;
		}
		max /= heatCurrent.length;
		q += max;

		if (q > 1.1 * joule) {
			q = 1.1 * joule;
		}

		if (q > joule) {
			d_position = 1;
//			System.out.println("on");
			if (!lock) {
				lock = true;
				button.on();
			}
		} else {
//			System.out.println("off");
			d_position = 0;
			if (lock) {
				button.off();
				lock = false;
			}
		}

		if (d_position < .1) {
			i_position = 0;
		} else if (d_position > .9) {
			i_position = 1;
		} else {
			i_position = 2;
		}
		delta = coilCurrent;
	}

	@Override
	void calculateCurrent() {
		for (int i = 0; i < heatCurrent.length; i++) {
			heatCurrent[i] = (volts[nCoil1 + 2 * i] - volts[nCoil1 + 2 * i + 1]) / resistance;
		}

//		NC
		for (int p = 0; p != flag; p++) {
			if (i_position == 0) {
				switchCurrent[p] = (volts[p * pairs] - volts[p * pairs + 1]) / r_on;
			} else {
				switchCurrent[p] = 0;
			}
		}

//		NO
		for (int p = flag; p != poleCount; p++) {
			if (i_position == 1) {
				switchCurrent[p] = (volts[p * pairs] - volts[p * pairs + 1]) / r_on;
			} else {
				switchCurrent[p] = 0;
			}
		}
	}

	@Override
	public int getPostCount() {
		return posts == null ? 0 : posts.size();
	}

}
