package test.circuit;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cas.circuit.CirSim;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.component.Wire;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.ResistorElm;
import com.cas.circuit.element.VoltageElm;

public class TestCircuit {
	private long time;

	@Before
	public void testBefore() {
		time = System.currentTimeMillis();
	}

	@After
	public void testAfter() {
		System.out.println(System.currentTimeMillis() - time);
	}

	@Test
	public void testTerminal() throws Exception {
		Terminal a = new Terminal("0");
		Terminal b = new Terminal("1");

		VoltageElm elm1 = new VoltageElm(0);
		elm1.setPostPoint(0, a);
		elm1.setPostPoint(1, b);

		Terminal c = new Terminal("2");
		ResistorElm resis1 = new ResistorElm(100);
		resis1.setPostPoint(0, a);
		resis1.setPostPoint(1, c);

		Terminal d = new Terminal("3");
		ResistorElm resis2 = new ResistorElm(200);
		resis2.setPostPoint(0, c);
		resis2.setPostPoint(1, d);

		Terminal e = new Terminal("4");
		ResistorElm resis3 = new ResistorElm(100);
		resis3.setPostPoint(0, c);
		resis3.setPostPoint(1, e);

		ResistorElm resis4 = new ResistorElm(100);
		resis4.setPostPoint(0, d);
		resis4.setPostPoint(1, e);

		Terminal f = new Terminal("5");
		ResistorElm resis5 = new ResistorElm(300);
		resis5.setPostPoint(0, d);
		resis5.setPostPoint(1, f);

		ResistorElm resis6 = new ResistorElm(50);
		resis6.setPostPoint(0, e);
		resis6.setPostPoint(1, f);

		ResistorElm resis7 = new ResistorElm(100);
		resis7.setPostPoint(0, f);
		resis7.setPostPoint(1, b);

		CirSim sim = new CirSim();

		sim.addCircuitElm(elm1);
		sim.addCircuitElm(resis1);
		sim.addCircuitElm(resis2);
		sim.addCircuitElm(resis3);
		sim.addCircuitElm(resis4);
		sim.addCircuitElm(resis5);
		sim.addCircuitElm(resis6);
		sim.addCircuitElm(resis7);

		sim.needAnalyze();

		CircuitElm.initClass(sim);

		sim.updateCircuit(1);

//		elmList.forEach(elm -> {
//			String[] info = new String[10];
//			elm.getInfo(info);
//			System.out.printf("%s - %s : %s\r\n", elm.getPostPoint(0), elm.getPostPoint(1), Arrays.toString(info));
//		});

		double volt = elm1.getVoltageDiff();
		double cur = elm1.getCurrent();
		System.out.println(volt / cur);
	}

	long t = 0;

	public static void main(String[] args) throws Exception {
		TestCircuit test = new TestCircuit();
		test.testBefore();

		test.testWireConnect();

		test.testAfter();
	}

	@Test
	public void testWireConnect() throws Exception {
//		preapreThreePhase(elmList);
//		preapreRail(elmList);

		CirSim sim = new CirSim();
		prepareWheatstoneBridge(sim);

		sim.needAnalyze();

		CircuitElm.initClass(sim);

		ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
		pool.scheduleAtFixedRate(() -> {
			sim.updateCircuit(5e-6);
//			elmList.forEach(e -> {
//				String[] info = new String[10];
//				e.getInfo(info);
//				System.out.println(e.getClass() + ":" + Arrays.toString(info));
//			});
		}, 10, 10, TimeUnit.MILLISECONDS);
	}

	void preapreThreePhase(Vector<CircuitElm> elmList) {
		VoltageElm r = new VoltageElm(1);
		Terminal r_0 = new Terminal("r_0");
		Terminal r_1 = new Terminal("r_1");
		r.setPostPoint(0, r_0);
		r.setPostPoint(1, r_1);

		VoltageElm s = new VoltageElm(1);
		s.setPhaseShift(Math.toRadians(120));
		Terminal s_0 = new Terminal("s_0");
		Terminal s_1 = new Terminal("s_1");
		s.setPostPoint(0, s_0);
		s.setPostPoint(1, s_1);

		VoltageElm t = new VoltageElm(1);
		t.setPhaseShift(Math.toRadians(240));
		Terminal t_0 = new Terminal("t_0");
		Terminal t_1 = new Terminal("t_1");
		t.setPostPoint(0, t_0);
		t.setPostPoint(1, t_1);

		ResistorElm resis1 = new ResistorElm(100);
		Terminal resis1_0 = new Terminal("resis1_0");
		Terminal resis1_1 = new Terminal("resis1_1");
		resis1.setPostPoint(0, resis1_0);
		resis1.setPostPoint(1, resis1_1);

		ResistorElm resis2 = new ResistorElm(100);
		Terminal resis2_0 = new Terminal("resis2_0");
		Terminal resis2_1 = new Terminal("resis2_1");
		resis2.setPostPoint(0, resis2_0);
		resis2.setPostPoint(1, resis2_1);

		ResistorElm resis3 = new ResistorElm(100);
		Terminal resis3_0 = new Terminal("resis3_0");
		Terminal resis3_1 = new Terminal("resis3_1");
		resis3.setPostPoint(0, resis3_0);
		resis3.setPostPoint(1, resis3_1);

		Wire wire = new Wire();
		wire.bind(r_0);
		wire.bind(s_0);

		wire = new Wire();
		wire.bind(t_0);
		wire.bind(s_0);

		wire = new Wire();
		wire.bind(r_1);
		wire.bind(resis1_0);

		wire = new Wire();
		wire.bind(s_1);
		wire.bind(resis2_0);

		wire = new Wire();
		wire.bind(t_1);
		wire.bind(resis3_0);

		wire = new Wire();
		wire.bind(resis1_1);
		wire.bind(resis2_1);

		wire = new Wire();
		wire.bind(resis2_1);
		wire.bind(resis3_1);

		elmList.add(r);
		elmList.add(s);
		elmList.add(t);
		elmList.add(resis1);
		elmList.add(resis2);
		elmList.add(resis3);
	}

	void prepareWheatstoneBridge(CirSim sim) {
		VoltageElm elm1 = new VoltageElm(1);
		Terminal elm1_0 = new Terminal("elm1_0");
		Terminal elm1_1 = new Terminal("elm1_1");
		elm1.setPostPoint(0, elm1_0);
		elm1.setPostPoint(1, elm1_1);

		ResistorElm resis1 = new ResistorElm(100);
		Terminal resis1_0 = new Terminal("resis1_0");
		Terminal resis1_1 = new Terminal("resis1_1");
		resis1.setPostPoint(0, resis1_0);
		resis1.setPostPoint(1, resis1_1);

		ResistorElm resis2 = new ResistorElm(100);
		Terminal resis2_0 = new Terminal("resis2_0");
		Terminal resis2_1 = new Terminal("resis2_1");
		resis2.setPostPoint(0, resis2_0);
		resis2.setPostPoint(1, resis2_1);

		ResistorElm resis3 = new ResistorElm(200);
		Terminal resis3_0 = new Terminal("resis3_0");
		Terminal resis3_1 = new Terminal("resis3_1");
		resis3.setPostPoint(0, resis3_0);
		resis3.setPostPoint(1, resis3_1);

		ResistorElm resis4 = new ResistorElm(100);
		Terminal resis4_0 = new Terminal("resis4_0");
		Terminal resis4_1 = new Terminal("resis4_1");
		resis4.setPostPoint(0, resis4_0);
		resis4.setPostPoint(1, resis4_1);

		ResistorElm resis5 = new ResistorElm(300);
		Terminal resis5_0 = new Terminal("resis5_0");
		Terminal resis5_1 = new Terminal("resis5_1");
		resis5.setPostPoint(0, resis5_0);
		resis5.setPostPoint(1, resis5_1);

		ResistorElm resis6 = new ResistorElm(50);
		Terminal resis6_0 = new Terminal("resis6_0");
		Terminal resis6_1 = new Terminal("resis6_1");
		resis6.setPostPoint(0, resis6_0);
		resis6.setPostPoint(1, resis6_1);

		ResistorElm resis7 = new ResistorElm(100);
		Terminal resis7_0 = new Terminal("resis7_0");
		Terminal resis7_1 = new Terminal("resis7_1");
		resis7.setPostPoint(0, resis7_0);
		resis7.setPostPoint(1, resis7_1);

//		===============
//		电源-电阻1
		Wire wire = new Wire();
		wire.bind(elm1_0);
		wire.bind(resis1_0);

//		电阻1 - 电阻2
		wire = new Wire();
		wire.bind(resis1_1);
		wire.bind(resis2_0);

//		电阻1 - 电阻3
		wire = new Wire();
		wire.bind(resis1_1);
		wire.bind(resis3_0);

//		电阻2 - 电阻4
		wire = new Wire();
		wire.bind(resis2_1);
		wire.bind(resis4_0);

//		电阻3 - 电阻4
		wire = new Wire();
		wire.bind(resis3_1);
		wire.bind(resis4_1);

//		电阻3 - 电阻5
		wire = new Wire();
		wire.bind(resis3_1);
		wire.bind(resis5_0);

//		电阻4 - 电阻6
		wire = new Wire();
		wire.bind(resis4_0);
		wire.bind(resis6_0);

//		电阻5 - 电阻6
		wire = new Wire();
		wire.bind(resis5_1);
		wire.bind(resis6_1);

//		电阻6 - 电阻7
		wire = new Wire();
		wire.bind(resis6_1);
		wire.bind(resis7_1);

//		电源 - 电阻7
		wire = new Wire();
		wire.bind(elm1_1);
		wire.bind(resis7_0);

		sim.addCircuitElm(elm1);
		sim.addCircuitElm(resis1);
		sim.addCircuitElm(resis2);
		sim.addCircuitElm(resis3);
		sim.addCircuitElm(resis4);
		sim.addCircuitElm(resis5);
		sim.addCircuitElm(resis6);
		sim.addCircuitElm(resis7);
	}
}
