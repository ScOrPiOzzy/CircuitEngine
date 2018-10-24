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
import com.cas.circuit.control.MotorControl;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.ThreePhaseAsynMotorElm;
import com.cas.circuit.element.VoltageElm;

public class TestMotorStar {
	private long time;

	public static void main(String[] args) {
		TestMotorStar relay = new TestMotorStar();
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
		r.setMaxVoltage(200);
		Terminal r_0 = new Terminal("r_0");
		Terminal r_1 = new Terminal("r_1");
		r.setPostPoint(0, r_0);
		r.setPostPoint(1, r_1);

		VoltageElm s = new VoltageElm(1);
		s.setMaxVoltage(200);
		s.setPhaseShift(120);
		Terminal s_0 = new Terminal("s_0");
		Terminal s_1 = new Terminal("s_1");
		s.setPostPoint(0, s_0);
		s.setPostPoint(1, s_1);

		VoltageElm t = new VoltageElm(1);
		t.setMaxVoltage(200);
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
		ThreePhaseAsynMotorElm motor = new ThreePhaseAsynMotorElm(f, params);
		motor.setControl(new MotorControl());
//		直流部分
		new Wire(r_0, s_0);
		new Wire(s_0, t_0);

//		正转1
//		new Wire(r_1, u1);
//		new Wire(s_1, v1);
//		new Wire(t_1, w1);
//		正转2
		new Wire(r_1, v1);
		new Wire(s_1, w1);
		new Wire(t_1, u1);
//		正转3
//		new Wire(r_1, w1);
//		new Wire(s_1, u1);
//		new Wire(t_1, v1);
//		反转1
//		new Wire(r_1, w1);
//		new Wire(s_1, v1);
//		new Wire(t_1, u1);
//		反转2
//		new Wire(r_1, v1);
//		new Wire(s_1, u1);
//		new Wire(t_1, w1);
//		反转3
//		new Wire(r_1, u1);
//		new Wire(s_1, w1);
//		new Wire(t_1, v1);

		new Wire(u2, v2);
		new Wire(v2, w2);

		sim.addCircuitElm(r);
		sim.addCircuitElm(s);
		sim.addCircuitElm(t);
		sim.addCircuitElm(motor);
	}

	private void startTest() {
		CirSim sim = CirSim.ins;

		prepareCircuit(sim);

		sim.needAnalyze();

		ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
		pool.scheduleAtFixedRate(sim, 0, (long) (1 / CirSim.TPF / 10), TimeUnit.NANOSECONDS);
	}
}
