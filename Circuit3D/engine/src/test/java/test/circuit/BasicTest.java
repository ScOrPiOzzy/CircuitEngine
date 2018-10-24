package test.circuit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cas.circuit.util.JmeUtil;
import com.jme3.math.Vector3f;

public class BasicTest {
	@Test
	public void testName() throws Exception {
		String str = "[{\"mdlName\":\"NP4-11BN_22\", \"location\": \"[1.622663E-4, 0.014685796, 0.008508424]\", \"rotation\": \"[0.0, 0.0, 0.0]\"},{\"mdlName\":\"NP4-11BN_19\", \"location\": \"[1.593859E-4, 0.0146835465, -0.009745091]\", \"rotation\": \"[0.0, 0.0, 0.0]\"}]";
		JSONArray arr = JSON.parseArray(str);
		arr.forEach(json->{
			JSONObject obj = (JSONObject) json;
			String v3 = obj.getString("location");
			System.out.println(JmeUtil.parseVector3f(v3));
		});
	}
}
