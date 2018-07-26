package test.circuit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;

import com.cas.circuit.CirSim;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.component.Wire;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.ThreePhaseACAsynchMotorElm;
import com.cas.circuit.element.VoltageElm;

public class TestMotor {
	private long time;

	public static void main(String[] args) {
		TestMotor relay = new TestMotor();
		relay.testBefore();

		relay.startTest();

		relay.testAfter();
	}

	@Before
	public void testBefore() {
		time = System.currentTimeMillis();
	}

	@After
	public void testAfter() {
		System.out.println(System.currentTimeMillis() - time);
	}

	private void prepareCircuit(CirSim sim) {
		VoltageElm r = new VoltageElm(1);
		Terminal r_0 = new Terminal("r_0");
		Terminal r_1 = new Terminal("r_1");
		r.setPostPoint(0, r_0);
		r.setPostPoint(1, r_1);

		VoltageElm s = new VoltageElm(1);
		s.setPhaseShift(120);
		Terminal s_0 = new Terminal("s_0");
		Terminal s_1 = new Terminal("s_1");
		s.setPostPoint(0, s_0);
		s.setPostPoint(1, s_1);

		VoltageElm t = new VoltageElm(1);
		t.setPhaseShift(240);
		Terminal t_0 = new Terminal("t_0");
		Terminal t_1 = new Terminal("t_1");
		t.setPostPoint(0, t_0);
		t.setPostPoint(1, t_1);

		Terminal u1, v1, w1, u2, v2, w2;
		Map<String, String> params = new HashMap<>();
		params.put("posts", "U1,V1,W1,U2,V2,W2");
		params.put("coilR", "2e2");
		params.put("p", "2");

		Map<String, Terminal> moterTermMap = new HashMap<>();
		moterTermMap.put("U1", u1 = new Terminal("U1"));
		moterTermMap.put("V1", v1 = new Terminal("V1"));
		moterTermMap.put("W1", w1 = new Terminal("W1"));

		moterTermMap.put("U2", u2 = new Terminal("U2"));
		moterTermMap.put("V2", v2 = new Terminal("V2"));
		moterTermMap.put("W2", w2 = new Terminal("W2"));
		Function<String, Terminal> f = (k) -> {
			return moterTermMap.get(k);
		};
		ThreePhaseACAsynchMotorElm motor = new ThreePhaseACAsynchMotorElm(f, params);

//		直流部分
		new Wire(r_0, s_0);
		new Wire(s_0, t_0);
		
		new Wire(r_1, u1);
		new Wire(s_1, v1);
		new Wire(t_1, w1);
		
		new Wire(u2, v2);
		new Wire(v2, w2);

		sim.addCircuitElm(r);
		sim.addCircuitElm(s);
		sim.addCircuitElm(t);
		sim.addCircuitElm(motor);
	}

	private void startTest() {
		CirSim sim = new CirSim();
		CircuitElm.initClass(sim);

		prepareCircuit(sim);
		// preapreRail(elmList);
		// prepareCircuit(elmList);

		sim.needAnalyze();

		// SwitchElm s = (SwitchElm) elmList.get(1);
		// s.toggle();
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
		pool.scheduleAtFixedRate(() -> {
			// System.out.println("TestRelay.startTest()");
			try {
				sim.updateCircuit(5e-5);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// elmList.forEach(e -> {
//			CircuitElm e = sim.getCircuitElm(5);
//			String[] info = new String[8];
//			e.getInfo(info);
//			System.out.println(Arrays.toString(info));
//			e = elmList.get(2);
//			info = new String[8];
//			e.getInfo(info);
//			System.out.println(Arrays.toString(info));

			// });
		}, 10, 10, TimeUnit.MILLISECONDS);
	}
}
