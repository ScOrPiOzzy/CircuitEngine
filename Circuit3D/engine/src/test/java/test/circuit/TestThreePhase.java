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
import com.cas.circuit.element.ResistorElm;
import com.cas.circuit.element.ThreePhaseACAsynchMotorElm;
import com.cas.circuit.element.VoltageElm;

public class TestThreePhase {
	private long time;

	public static void main(String[] args) {
		TestThreePhase relay = new TestThreePhase();
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

		ResistorElm resis1 = new ResistorElm(1);
		Terminal u1 = new Terminal("u1");
		Terminal u2 = new Terminal("u2");
		resis1.setPostPoint(0, u1);
		resis1.setPostPoint(1, u2);

		ResistorElm resis2 = new ResistorElm(1);
		Terminal v1 = new Terminal("v1");
		Terminal v2 = new Terminal("v2");
		resis2.setPostPoint(0, v1);
		resis2.setPostPoint(1, v2);

		ResistorElm resis3 = new ResistorElm(1);
		Terminal w1 = new Terminal("w1");
		Terminal w2 = new Terminal("w2");
		resis3.setPostPoint(0, w1);
		resis3.setPostPoint(1, w2);

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
		sim.addCircuitElm(resis1);
		sim.addCircuitElm(resis2);
		sim.addCircuitElm(resis3);
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
				sim.updateCircuit(5e-4);
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
		}, 10, 1, TimeUnit.MILLISECONDS);
	}
}
