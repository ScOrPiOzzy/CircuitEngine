package test.circuit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import com.cas.circuit.CirSim;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.component.Wire;
import com.cas.circuit.control.MotorControl;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.ThreePhaseAsynMotorElm;
import com.cas.circuit.element.VoltageElm;

public class TestMotorTriangle {
	private long time;

	public static void main(String[] args) {
		TestMotorTriangle relay = new TestMotorTriangle();
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
		Map<String, String> params = new HashMap<>();
		Map<String, Terminal> termMap = new HashMap<>();
		params.put("term1", "r_0");
		params.put("term2", "r_1");
		params.put("waveform", "1");
		params.put("frequency", "50");
		params.put("maxVoltage", "220");
		params.put("phaseShift", "-120");

		Terminal r_0, r_1, s_0, s_1, t_0, t_1;
		termMap.put("r_0", r_0 = new Terminal("r_0"));
		termMap.put("r_1", r_1 = new Terminal("r_1"));

		VoltageElm r = new VoltageElm((k) -> {
			return termMap.get(k);
		}, params);

		params.put("phaseShift", "0");
		params.put("term1", "s_0");
		params.put("term2", "s_1");
		termMap.put("s_0", s_0 = new Terminal("s_0"));
		termMap.put("s_1", s_1 = new Terminal("s_1"));

		VoltageElm s = new VoltageElm((k) -> {
			return termMap.get(k);
		}, params);

		params.put("phaseShift", "120");
		params.put("term1", "t_0");
		params.put("term2", "t_1");
		termMap.put("t_0", t_0 = new Terminal("t_0"));
		termMap.put("t_1", t_1 = new Terminal("t_1"));

		VoltageElm t = new VoltageElm((k) -> {
			return termMap.get(k);
		}, params);

		Terminal u1, v1, w1, u2, v2, w2;
		params = new HashMap<>();
		params.put("posts", "U1,V1,W1,U2,V2,W2");
		params.put("coilR", "2e-2");
		params.put("p", "2");

		termMap.put("U1", u1 = new Terminal("U1"));
		termMap.put("V1", v1 = new Terminal("V1"));
		termMap.put("W1", w1 = new Terminal("W1"));

		termMap.put("U2", u2 = new Terminal("U2"));
		termMap.put("V2", v2 = new Terminal("V2"));
		termMap.put("W2", w2 = new Terminal("W2"));
		ThreePhaseAsynMotorElm motor = new ThreePhaseAsynMotorElm((k) -> {
			return termMap.get(k);
		}, params);

		motor.setControl(new MotorControl());

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

		new Wire(u1, v2);
		new Wire(v1, w2);
		new Wire(w1, u2);

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
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
		pool.scheduleAtFixedRate(sim, 10, (long) 1e4, TimeUnit.NANOSECONDS);
	}
}
