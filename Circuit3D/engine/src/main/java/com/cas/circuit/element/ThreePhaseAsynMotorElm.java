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
public class ThreePhaseAsynMotorElm extends MotorElm {
	public static final int POST_COUNT = 6;

//	U1,V1,W1,U2,V2,W2
	protected List<Terminal> posts;
	private double coilCurrent[];

	private double coilR = 2e-1;

//	磁极对数
	private int p;

//	电压临界点
	private double maxmin, vmax = 0;
	int cnt, sample = 3;

	private double phase_v;

	public ThreePhaseAsynMotorElm() {
		super();
	}

	public ThreePhaseAsynMotorElm(Function<String, Terminal> f, Map<String, String> params) {
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
	}

	@Override
	public void startIteration() {
		double volt_u = volts[0] - volts[3];
		double volt_v = volts[1] - volts[4];
		double volt_w = volts[2] - volts[5];

//		三相电特性1：任意时刻，线电压代数和为近似为0（精度问题）
//		不满足条件的情况：电压全为0，或者是电压代数和远大于0，这里认为偏差1e-10伏
		if ((abs(volt_u) < 1e-10 && (abs(volt_v) < 1e-10 && (abs(volt_w) < 1e-10) || (abs(volt_u + volt_v + volt_w) > 1e-8)))) {
			state = STATE_STATIC;

			control.setDir(0);
			return;
		}
		if (maxmin == 0) {
			maxmin = volt_v;
		}
		vmax = max(max(max(volt_u, volt_v), volt_w), vmax);
//		选择v相作为标准
//		double phase_u = asin(volt_u / vmax);
		double phase = asin(volt_v / vmax);
//		double phase_w = asin(volt_w / vmax);
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

		if (Math.abs(volt_w - prew) < 10 && Math.abs(volt_u - preu) < 10) {
//			System.out.println("正转" + Math.toDegrees(sim.getTpf() * 25 * PI));
			control.setDir(1);
		} else {
			control.setDir(-1);
//			System.out.printf("prew:%.5f,\tvolt_w:%.5f\r\npreu:%.5f,\tvolt_u:%.5f\r\n", prew, (volt_w - prew), preu, (volt_u - preu));
//			System.out.println("反转" + (volt_w - prew) + ",  "+(volt_u - preu));
		}

		phase_v = phase;
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
