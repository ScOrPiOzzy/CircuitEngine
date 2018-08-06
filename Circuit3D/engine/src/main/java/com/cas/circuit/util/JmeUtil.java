package com.cas.circuit.util;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public final class JmeUtil {

	public static final Vector3f UNIT_XY = new Vector3f(1, 1, 0);
	public static final Vector3f UNIT_XZ = new Vector3f(1, 0, 1);
	public static final Vector3f UNIT_YZ = new Vector3f(0, 1, 1);

	/**
	 * 模型半透明，模型材质必须含有Diffuse
	 * @param sp
	 * @param alpha
	 */
	public static void transparent(Spatial sp, float alpha) {
		if (sp == null) {
			return;
		}
		if (sp instanceof Geometry) {
			Material mat = ((Geometry) sp).getMaterial();
			sp.setUserData("Transparent", mat);

			Material transMat = mat.clone();
			MatParam diffuseParam = transMat.getParam("Diffuse");
			if (diffuseParam != null) {
				ColorRGBA color = (ColorRGBA) transMat.getParam("Diffuse").getValue();
				transMat.setColor("Diffuse", new ColorRGBA(color.r, color.g, color.b, alpha));
				transMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

				sp.setMaterial(transMat);
				sp.setQueueBucket(Bucket.Transparent);
			}
		} else if (sp instanceof Node) {
			for (Spatial child : ((Node) sp).getChildren()) {
				JmeUtil.transparent(child, alpha);
			}
		}
	}

	/**
	 * 取消透明效果
	 * @param sp
	 */
	public static void untransparent(Spatial sp) {
		if (sp == null) {
			return;
		}
		if (sp instanceof Geometry) {
			Material mat = sp.getUserData("Transparent");
			if (mat != null) {
				sp.setUserData("Transparent", null);
				sp.setMaterial(mat);
				sp.setQueueBucket(Bucket.Opaque);
			}
		} else if (sp instanceof Node) {
			for (Spatial child : ((Node) sp).getChildren()) {
				JmeUtil.untransparent(child);
			}
		}
	}

	/**
	 * 改变材质颜色
	 * @param sp
	 * @param color
	 */
	public static void color(Spatial sp, ColorRGBA color, boolean saveMat) {
		if (sp == null) {
			return;
		}
		if (sp instanceof Geometry) {
			Material mat = ((Geometry) sp).getMaterial();
			if (saveMat) {
				if (sp.getUserData("Color") == null) {
					sp.setUserData("Color", mat);
				}
			}

			Material colorMat = mat.clone();
			MatParam diffuseParam = colorMat.getParam("Diffuse");
			if (diffuseParam != null) {
				colorMat.setColor("Diffuse", color);
				sp.setMaterial(colorMat);
			}
		} else if (sp instanceof Node) {
			for (Spatial child : ((Node) sp).getChildren()) {
				JmeUtil.color(child, color, saveMat);
			}
		}
	}

	/**
	 * 恢复材质本来颜色
	 * @param sp
	 */
	public static void uncolor(Spatial sp) {
		if (sp == null) {
			return;
		}
		if (sp instanceof Geometry) {
			Material mat = sp.getUserData("Color");
			if (mat != null) {
				sp.setUserData("Color", null);
				sp.setMaterial(mat);
			}
		} else if (sp instanceof Node) {
			for (Spatial child : ((Node) sp).getChildren()) {
				JmeUtil.uncolor(child);
			}
		}
	}

	public static String[] parseArray(String value) {
		if (value == null) {
			return null;
		}

		value = trim(value);
		String[] arr = value.split(",");
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
		}
		return arr;
	}

	public static float[] parseFloatArray(String value) {
		String[] arr = parseArray(value);

		float[] result = new float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			result[i] = Float.parseFloat(arr[i]);
		}

		return result;
	}

	/**
	 * @param value eg.(-0.017576016, 0.011718482, -0.99977684, 1)
	 * @return
	 */
	public static Quaternion parseQuaternion(String value) {
		if (value == null) {
			return null;
		}

		value = trim(value);
		String[] arr = value.split(",");

		Quaternion result = new Quaternion(//
				Float.parseFloat(arr[0]), //
				Float.parseFloat(arr[1]), //
				Float.parseFloat(arr[2]), //
				Float.parseFloat(arr[3])//
		);

		return result;
	}

	/**
	 * @param value eg.(-0.017576016, 0.011718482, -0.99977684)
	 * @return
	 */
	public static Vector3f parseVector3f(String value) {
		if (value == null) {
			return null;
		}

		value = trim(value);

		String[] arr = value.split(",");

		Vector3f result = new Vector3f();
		result.x = Float.parseFloat(arr[0]);
		result.y = Float.parseFloat(arr[1]);
		result.z = Float.parseFloat(arr[2]);

		return result;
	}

	private static String trim(String value) {
		if (value == null) {
			return null;
		}
//		这个方法的局限性,只能针对形如：(1,2,3)|[1,2,3]的字符串
//		无法处理[[1,2],[3,4]]|((1,2),(3,4)),这样的字符串建议用用JSON,或StringUtil
		value = value.replace("(", "");
		value = value.replace(")", "");
		value = value.replace("[", "");
		value = value.replace("]", "");

		value = value.trim();
		return value;
	}

}
