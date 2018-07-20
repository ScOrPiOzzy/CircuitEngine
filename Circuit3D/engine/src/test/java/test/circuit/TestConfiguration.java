package test.circuit;

import java.io.File;

import org.junit.Test;

import com.cas.circuit.component.ElecCompDef;
import com.cas.circuit.util.JaxbUtil;

public class TestConfiguration {
	@Test
	public void testParse() throws Exception {
		ElecCompDef comp = null;
		File file = new File("D:\\HOME_SVN\\simulation\\电工仿真软件3D\\ServerResources\\configurations2\\Switch\\YBLX-K1-111.xml");
		comp = JaxbUtil.converyToJavaBean(file.toURL(), ElecCompDef.class);
		System.out.println(comp);

//		comp = new ElecCompDef();
//		CircuitExchange circuitExchange = new CircuitExchange();
//		circuitExchange.setId("ttest1");
//		circuitExchange.setType("SwitchElm");
//		Map<String, String> params = new HashMap<>();
//		params.put("term1", "t1");
//		params.put("term2", "t2");
//		params.put("momentary", "false");
//		params.put("position", "1");
//		circuitExchange.setParams(params);
//		SwitchElm s = new SwitchElm();
//		comp.getCircuitExchangeList().add(circuitExchange);
//		System.out.println(JaxbUtil.convertToXml(comp));
	}
}
