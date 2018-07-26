package com.cas.circuit.element;

import static com.cas.circuit.util.Util.getVoltageText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		if ((volt_u == 0 && volt_v == 0 && volt_w == 0) || (Math.abs(volt_u + volt_v + volt_w) > 1)) {
			state = STATE_STATIC;
			return;
		}

//		以V为参考电压，判断另外两相的电压是否满足相位差
		if (volt_u == 0) {
//			前面已经判断出满足特性1， 这里只需要判断另外两相的正负即可。
//			if (volt_u < 0 && volt_w > 0) {
//				if (state == 0) {
//					state = STATE_CW; // 正转
//				}
//			} else if (volt_u < 0 && volt_w > 0) {
//				if (state == 0) {
//					state = STATE_ACW; // 反转
//				}
//			}
		} else if (volt_v == 0) {

		} else if (volt_w == 0) {

		} else {
//			计算最大电压, 公式： V(瞬) = V(max) * sin(2*pi*f*t + delta)
			double w = Math.PI * 2 * 50 * sim.getTimer();
//			计算出符合正转的电压之比

			double delta0 = Math.sin(w); // 初相 0
			double delta1 = Math.sin(w + Math.PI * 2 / 3); // 初相 120
			double delta2 = Math.sin(w + Math.PI * 2 / 3 * 2); // 初相 240

			List<double[]> phase = new ArrayList<>(3);
			phase.add(new double[] { delta0, 0 });
			phase.add(new double[] { delta1, 120 });
			phase.add(new double[] { delta2, 240 });
			Collections.sort(phase, new Comparator<double[]>() {
				@Override
				public int compare(double[] o1, double[] o2) {
					if (o1[0] - o2[0] > 0) {
						return 1;
					}
					return 0;
				}
			});
//
//			double voltMax = Math.max(volt_u, Math.max(volt_v, volt_w));
//			double voltMin = Math.min(volt_u, Math.min(volt_v, volt_w));
//			double voltCenter = volt_u + volt_v + volt_w - voltMin - voltMax;
//
////			正转的情况：uvw的顺序
//			boolean uvw = false;
//
//			uvw = uvw || (volt_u == voltMax && volt_v == voltCenter && volt_w == voltMin);
//			uvw = uvw || (volt_v == voltMax && volt_w == voltCenter && volt_u == voltMin);
//			uvw = uvw || (volt_w == voltMax && volt_u == voltCenter && volt_v == voltMin);
//
//			System.out.println("" + uvw);

			// System.out.println(Arrays.toString(volts));
			// System.out.printf("volt_u:\t%.5f,\tvolt_v:\t%.5f,\tvolt_w:\t%.5f\r\n", volt_u, volt_v, volt_w);
			// System.out.printf("cw1:\t%.5f,\tcw2:\t%.5f,\tcw3:\t%.5f\r\n", cw1, cw2, cw3);
			// System.out.printf("r1:\t%.5f,\tr2:\t%.5f,\tr3:\t%.5f\r\n", r1, r2, r3);
		}

//		uvw 为正转，反之为反转
//		volt_u

//		满足电压输入条件
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
		return Math.abs(n1 - n1) == 3;
	}
}
