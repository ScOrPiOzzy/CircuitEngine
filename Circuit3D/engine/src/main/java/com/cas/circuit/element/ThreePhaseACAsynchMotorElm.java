package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getVoltageText;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.max;
import static java.lang.Math.sin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.cas.circuit.component.Terminal;

/**
 * 三相交流异步点击,<br>
 * 转速n = 60 * 电源频率f / 磁极对数p
 */
public class ThreePhaseACAsynchMotorElm extends MotorElm {
	public static final int POST_COUNT = 6;

//	U1,V1,W1,U2,V2,W2
	protected List<Terminal> posts;
	private double coilCurrent[];

	private double coilR = 2e-1;

//	磁极对数
	private int p;

	// 临界点
	private double maxmin, vmax = 220;
	private double maxphase, minphase;
	int cnt, sample = 3;

	private double phase_v;

	public ThreePhaseACAsynchMotorElm() {
		super();
	}

	public ThreePhaseACAsynchMotorElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);

		posts = new ArrayList<>(6);

		String value = params.get("posts");
		String[] arr = value.split(",");
		Terminal t = null;
		for (int i = 0; i < POST_COUNT; i++) {
			t = f.apply(arr[i]);
			t.setIndexInElm(posts.size());
			posts.add(t);
			t.setElm(this);
		}

		value = params.get("coilR");
		coilR = value == null ? coilR : Double.parseDouble(value);

		value = params.get("p");
		p = value == null ? p : Integer.parseInt(value);

		coilCurrent = new double[3];

		allocNodes();
	}

	@Override
	public void stamp() {
		// resistor from coil post 1 to coil post 2
		sim.stampResistor(nodes[0], nodes[3], coilR);
		sim.stampResistor(nodes[1], nodes[4], coilR);
		sim.stampResistor(nodes[2], nodes[5], coilR);

//		for (int i = 0; i != getPostCount(); i++) {
//			sim.stampNonLinear(nodes[i]);
//		}
	}

	@Override
	public void startIteration() {
		double volt_u = volts[0] - volts[3];
		double volt_v = volts[1] - volts[4];
		double volt_w = volts[2] - volts[5];

//		三相电特性1：任意时刻，线电压代数和为近似为0（精度问题）
//		不满足条件的情况：电压全为0，或者是电压代数和远大于0，这里认为偏差1伏
		if ((volt_u == 0 && volt_v == 0 && volt_w == 0) || (abs(volt_u + volt_v + volt_w) > 1e-7)) {
			state = STATE_STATIC;
			return;
		}

//		System.out.println(abs(volt_u + volt_v + volt_w));

		if (maxmin == 0) {
			maxmin = volt_v;
		}

//		if (cnt < sample * 2) {
//			vmax = max(abs(volt_v), vmax);
//
//			double tmp = volt_v - maxmin;
//			if (tmp > 0) {
////				System.out.printf("max[%d] = %s\r\n", cnt / 2, volt_v);
//				if (slope == -1) {
//					// 过了最小值的临界点附近
//					cnt++;
//				}
//				slope = 1;
//			} else if (tmp < 0) {
////				System.out.printf("min[%d] = %s\r\n", cnt / 2, volt_v);
//				if (slope == 1) {
//					// 过了最大值的临界点附近
//					cnt++;
//				}
//				slope = -1;
//			}
//			return;
//		}
		vmax = max(max(max(volt_u, volt_v), volt_w), vmax);

//		选择v相作为标准
//		double phase_u = asin(volt_u / vmax);

//		double phase_w = asin(volt_w / vmax);
//		满足相位差 uvw

		double phase = asin(volt_v / vmax);
		if (phase_v == 0) {
			phase_v = phase;
			return;
		}

		double preu, prew;

		if (phase > phase_v) {
			preu = vmax * sin(phase_v - PI * 2 / 3);
			prew = vmax * sin(phase_v + PI * 2 / 3);
		} else if (phase < phase_v) {
			preu = vmax * sin(phase_v + PI * 2 / 3);
			prew = vmax * sin(phase_v - PI * 2 / 3);
		} else {
			preu = prew = vmax * sin(PI / 2);
		}
		phase_v = phase;

//		System.out.println(Math.abs(volt_w - prew) + " ,  " + Math.abs(volt_u - preu));
		if (Math.abs(volt_w - prew) < 8 && Math.abs(volt_u - preu) < 8) {
			System.out.println("正转");
			
		} else {
			System.out.println("反转");
		}

//		System.out.printf("volt_v[%.5f], \tprew[%.5f], \tvolt_w[[%.5f]], \t[%.5f]\r\n", volt_v, prew, volt_w, volt_w - prew);
//		System.out.printf("volt_w[%.5f], \tpreu[%.5f], \tvolt_u[[%.5f]], \t[%.5f]\r\n", volt_w, preu, volt_u, volt_u - preu);
//		System.out.printf("volt_u[%.5f], \tprev[%.5f], \tvolt_v[[%.5f]], \t[%.5f]\r\n", volt_u, prev, volt_v, volt_v - prev);

//		preu = vmax * sin(phase_v + PI * 2 / 3);
//		prev = vmax * sin(phase_u + PI * 2 / 3);
//		prew = vmax * sin(phase_w + PI * 2 / 3);
//		System.out.printf("volt_v[%.5f], \tprew[%.5f] = \tvolt_w[[%.5f]], \t[%.5f]\r\n", volt_v, prew, volt_w, volt_w - prew);
//		System.out.printf("volt_w[%.5f], \tpreu[%.5f] = \tvolt_u[[%.5f]], \t[%.5f]\r\n", volt_w, preu, volt_u, volt_u - preu);
//		System.out.printf("volt_u[%.5f], \tprev[%.5f] = \tvolt_v[[%.5f]], \t[%.5f]\r\n", volt_u, prev, volt_v, volt_v - prev);
	}

	@Override
	void calculateCurrent() {
		coilCurrent[0] = (volts[0] - volts[3]) / coilR;
		coilCurrent[1] = (volts[1] - volts[4]) / coilR;
		coilCurrent[2] = (volts[2] - volts[5]) / coilR;
	}

	@Override
	void buildInfo() {
		info.add(getClass().getSimpleName());
		super.buildInfo();
		for (int i = 0; i < 3; i++) {
			info.add(String.format("coil Vd%d = %s", i, getVoltageText(volts[i] - volts[i + 3])));
		}
	}

	@Override
	public int getPostCount() {
		return POST_COUNT;
	}

	@Override
	public Terminal getPostPoint(int n) {
		return posts.get(n);
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return abs(n1 - n1) == 3;
	}
}
