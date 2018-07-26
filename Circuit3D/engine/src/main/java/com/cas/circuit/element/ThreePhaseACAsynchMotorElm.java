package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getVoltageText;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.asin;
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
	private double max[], min[], maxmin, slope = 0, vmax;
	int cnt, sample = 3;
	
	public ThreePhaseACAsynchMotorElm() {
		super();
	}

	public ThreePhaseACAsynchMotorElm(Function<String, Terminal> f, Map<String, String> params) {
		super(f, params);

		max = new double[sample];
		min = new double[sample];
		
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
		if ((volt_u == 0 && volt_v == 0 && volt_w == 0) || (abs(volt_u + volt_v + volt_w) > 1)) {
			state = STATE_STATIC;
			return;
		}

		if(maxmin == 0) {
			maxmin = volt_v;
		}
		
		if (cnt < sample * 2) {
			double tmp = volt_v - maxmin;
			if (tmp > 0) {
				max[cnt / 2] = volt_v;
//				System.out.printf("max[%d] = %s\r\n", cnt / 2, volt_v);
				if (slope == -1) {
					// 过了最小值的临界点附近
					cnt++;
				}
				slope = 1;
			} else if (tmp < 0) {
				min[cnt / 2] = volt_v;
//				System.out.printf("min[%d] = %s\r\n", cnt / 2, volt_v);
				if (slope == 1) {
					// 过了最大值的临界点附近
					cnt++;
				}
				slope = -1;
			}

			maxmin = volt_v;
			
			return;
		}
		vmax = (max[0] + max[1] + max[2]) / 3;

		double phase_u = asin(volt_u / vmax);
		double phase_v = asin(volt_v / vmax);
		double phase_w = asin(volt_w / vmax);
//		满足相位差 uvw

		double preu = vmax * sin(phase_w + PI * 2 / 3);
		double prev = vmax * sin(phase_u + PI * 2 / 3);
		double prew = vmax * sin(phase_v + PI * 2 / 3);

//		System.out.printf("phase_v[%.5f], phase_w[%.5f], phase_u[%.5f]\r\n", phase_v, phase_w, phase_u);
//		System.out.printf("volt_v[%.5f], volt_w[%.5f], volt_u[%.5f]\r\n", volt_v, volt_w, volt_u);
//		System.out.printf("prev[%.5f], prew[%.5f], preu[%.5f]\r\n", prev, prew, preu);

		System.out.printf("volt_v[%.5f] - prev[%.5f] = [%.5f]\r\n", volt_v, prev, volt_v - prev);
		System.out.printf("volt_w[%.5f] - prew[%.5f] = [%.5f]\r\n", volt_w, prew, volt_w - prew);
		System.out.printf("volt_u[%.5f] - preu[%.5f] = [%.5f]\r\n", volt_u, preu, volt_u - preu);
		
		preu = vmax * sin(phase_v + PI * 2 / 3);
		prev = vmax * sin(phase_w + PI * 2 / 3);
		prew = vmax * sin(phase_u + PI * 2 / 3);
		System.out.printf("volt_v[%.5f] - prev[%.5f] = [%.5f]\r\n", volt_v, prev, volt_v - prev);
		System.out.printf("volt_w[%.5f] - prew[%.5f] = [%.5f]\r\n", volt_w, prew, volt_w - prew);
		System.out.printf("volt_u[%.5f] - preu[%.5f] = [%.5f]\r\n", volt_u, preu, volt_u - preu);
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
