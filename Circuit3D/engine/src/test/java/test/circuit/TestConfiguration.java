package test.circuit;

import java.net.URL;

import org.junit.Test;

import com.cas.circuit.component.ElecCompDef;
import com.cas.circuit.util.JaxbUtil;

public class TestConfiguration {
	@Test
	public void testParse() throws Exception {
		ElecCompDef comp = null;
		URL url = new URL("http://172.16.30.2:8082/configurations/Relay/CZY08A.xml");
		comp = JaxbUtil.converyToJavaBean(url, ElecCompDef.class);
		System.out.println(comp);
		url = new URL("http://172.16.30.2:8082/configurations/Relay/CZY08A.xml");
		comp = JaxbUtil.converyToJavaBean(url, ElecCompDef.class);
		System.out.println(comp);
		url = new URL("http://172.16.30.2:8082/configurations/Relay/CZY08A.xml");
		comp = JaxbUtil.converyToJavaBean(url, ElecCompDef.class);
		System.out.println(comp);
		url = new URL("http://172.16.30.2:8082/configurations/Relay/CZY08A.xml");
		comp = JaxbUtil.converyToJavaBean(url, ElecCompDef.class);
		System.out.println(comp);
		url = new URL("http://172.16.30.2:8082/configurations/Relay/CZY08A.xml");
		comp = JaxbUtil.converyToJavaBean(url, ElecCompDef.class);
		System.out.println(comp);
		url = new URL("http://172.16.30.2:8082/configurations/Relay/CZY08A.xml");
		comp = JaxbUtil.converyToJavaBean(url, ElecCompDef.class);
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
