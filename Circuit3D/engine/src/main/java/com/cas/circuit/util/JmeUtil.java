package com.cas.circuit.util;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;

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
				transparent(child, alpha);
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
				untransparent(child);
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
				color(child, color, saveMat);
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
				uncolor(child);
			}
		}
	}

	public static void updateWiringBox(Geometry boxGeo, Spatial ref) {
		WireBox box = (WireBox) boxGeo.getMesh();
		BoundingBox bound = ((BoundingBox) ref.getWorldBound());
		boxGeo.setLocalTranslation(bound.getCenter());
		box.updatePositions(bound.getXExtent(), bound.getYExtent(), bound.getZExtent());
	}

	public static ColorRGBA convert(javafx.scene.paint.Color color) {
		ColorRGBA colorRGBA = new ColorRGBA();
		colorRGBA.set((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity());
//		colorRGBA.set(color.r, g, b, a);
		return colorRGBA;
	}

	public static Geometry createLineGeo(AssetManager assetManager, Mesh line, ColorRGBA color) {
		Geometry geom = new Geometry("TempWire", line);

		Material ballMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		ballMat.setColor("Diffuse", color);
		ballMat.setFloat("Shininess", 10f);
		ballMat.setColor("Specular", ColorRGBA.White);
		ballMat.setBoolean("UseMaterialColors", true);
//		ballMat.getAdditionalRenderState().setLineWidth(width);
		geom.setMaterial(ballMat);
		return geom;
	}

}
