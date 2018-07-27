package test.circuit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import com.cas.circuit.CirSim;
import com.cas.circuit.component.Terminal;
import com.cas.circuit.component.Wire;
import com.cas.circuit.element.AuxiliaryRelayElm;
import com.cas.circuit.element.CircuitElm;
import com.cas.circuit.element.LEDElm;
import com.cas.circuit.element.RelayElm;
import com.cas.circuit.element.ResistorElm;
import com.cas.circuit.element.SwitchElm;
import com.cas.circuit.element.VoltageElm;

public class TestAuxiliaryRelayElm {
	private long time;
	private Wire switchWire;
	private Terminal switchTerm;
	private boolean toggle;
	private SwitchElm switchElm;

	public static void main(String[] args) {
		TestAuxiliaryRelayElm relay = new TestAuxiliaryRelayElm();
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

	private void wire(CirSim sim) {
//		模拟动态接线
		try {
			toggle = !toggle;
			if (toggle) {
				switchWire.unbind(switchTerm);
				System.err.println("unbind");
			} else {
				switchWire.bind(switchTerm);
				System.out.println("bind");
			}
			sim.needAnalyze();
			System.out.println("s.toggle()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sw(CirSim sim) {
//		模拟开关闭合
		try {
			switchElm.doSwitch(false);
			sim.needAnalyze();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepareCircuit(CirSim sim) {
		VoltageElm ac = new VoltageElm(1);
		Terminal ac_0 = new Terminal("ac_0");
		Terminal ac_1 = new Terminal("ac_1");
		ac.setPostPoint(0, ac_0);
		ac.setPostPoint(1, ac_1);

		SwitchElm sw = new SwitchElm();
		this.switchElm = sw;
		Terminal sw_0 = new Terminal("Switch_0");
		Terminal sw_1 = new Terminal("Switch_1");
		sw.setPostPoint(0, sw_0);
		sw.setPostPoint(1, sw_1);

		RelayElm relay = new AuxiliaryRelayElm();
		Terminal coil_0 = new Terminal("coil_0");
		Terminal coil_1 = new Terminal("coil_1");
		Terminal nc0 = new Terminal("nc0");
		Terminal nc1 = new Terminal("nc1");
		Terminal no0 = new Terminal("no0");
		Terminal no1 = new Terminal("no1");
		List<List<Terminal>> terms = new ArrayList<>();
		List<Terminal> post1 = new ArrayList<>();
		post1.add(nc0);
		post1.add(nc1);
		post1.add(no0);
		post1.add(no1);
		terms.add(post1);
		relay.setPosts(coil_0, coil_1, terms);

		VoltageElm dc = new VoltageElm(0);
		dc.setMaxVoltage(5);
		Terminal dc_0 = new Terminal("dc_0");
		Terminal dc_1 = new Terminal("dc_1");
		dc.setPostPoint(0, dc_0);
		dc.setPostPoint(1, dc_1);

		ResistorElm resis = new ResistorElm(5);
		Terminal resis_0 = new Terminal("resis_0");
		Terminal resis_1 = new Terminal("resis_1");
		resis.setPostPoint(0, resis_0);
		resis.setPostPoint(1, resis_1);

		LEDElm led = new LEDElm();
		Terminal led_0 = new Terminal("led_0");
		Terminal led_1 = new Terminal("led_1");
		led.setPostPoint(0, led_0);
		led.setPostPoint(1, led_1);

//		直流部分
//		电源接在电阻一端
//		直流部分
		Wire wire = null;

		wire = new Wire();
		wire.bind(dc_1);
		wire.bind(resis_0);

		wire = new Wire();
		wire.bind(resis_1);
		wire.bind(led_0);

		wire = new Wire();
		wire.bind(led_1);
		wire.bind(no1);

		wire = new Wire();
		wire.bind(dc_0);
		wire.bind(no0);

////		《《《《
//
//		交流部分
		wire = new Wire();
		wire.bind(ac_1);
		wire.bind(coil_0);

		wire = new Wire();
		wire.bind(coil_1);
		wire.bind(sw_0);

		wire = new Wire();
		this.switchWire = wire;
		this.switchTerm = ac_0;
		wire.bind(sw_1);
		wire.bind(ac_0);

		sim.addCircuitElm(ac);
		sim.addCircuitElm(sw);
		sim.addCircuitElm(relay);
		sim.addCircuitElm(dc);
		sim.addCircuitElm(resis);
		sim.addCircuitElm(led);
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

			accept();

			// System.out.println("TestRelay.startTest()");
			try {
				sim.updateCircuit(5e-6);
			} catch (Exception e) {
				e.printStackTrace();
			}
//			elmList.forEach(e -> {
			CircuitElm e = sim.getCircuitElm(5);
			e.printInfo();
//			e = elmList.get(2);
//			info = new String[8];
//			e.getInfo(info);
//			System.out.println(Arrays.toString(info));

//			});
		}, 10, 1, TimeUnit.MILLISECONDS);
//
//		pool.schedule(() -> {
////			sw(sim);
//			wire(sim);
//		}, 3, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(() -> {
			events.add(() -> sw(sim));
//			wire(sim);
		}, 3, 3, TimeUnit.SECONDS);
	}

	List<Runnable> events = new ArrayList<>();

	private void accept() {
		if (events.size() > 0) {
			events.forEach(e -> e.run());
			events.clear();
		}
	}
}
