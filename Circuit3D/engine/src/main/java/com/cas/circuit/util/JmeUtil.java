package com.cas.circuit.util;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.BufferUtils;

import jme3tools.optimize.GeometryBatchFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JmeUtil {

	public static final Vector3f UNIT_XY = new Vector3f(1, 1, 0);
	public static final Vector3f UNIT_XZ = new Vector3f(1, 0, 1);
	public static final Vector3f UNIT_YZ = new Vector3f(0, 1, 1);

	/**
	 * Get a context point on spatial from cursor position.
	 * @param spatial the spatial.
	 * @return the contact point or null.
	 */
	@Nullable
	public static Vector3f getContactPointFromCursor(@Nonnull final Spatial spatial, @Nonnull final Camera camera, @Nonnull Vector2f point) {
		final CollisionResult collision = getCollisionFromCursor(spatial, camera, point);
		return collision == null ? null : collision.getContactPoint();
	}

	@Nullable
	public static Geometry getNodeFromCursor(@Nonnull final Spatial spatial, @Nonnull final Camera camera, @Nonnull Vector2f point) {
		@Nullable
		CollisionResult collision = getCollisionFromCursor(spatial, camera, point);
		return collision == null ? null : collision.getGeometry();
	}

	/**
	 * Get a collision on spatial from screen position.
	 * @param spatial
	 * @param camera
	 * @param point
	 * @param filter
	 * @return the collision or null.
	 */
	@Nullable
	public static CollisionResult getCollisionFromCursor( //
			@Nonnull final Spatial spatial, //
			@Nonnull final Camera camera, //
			@Nonnull Vector2f point) {
		Vector3f origin = camera.getWorldCoordinates(point, 0.0f);
		Vector3f direction = camera.getWorldCoordinates(point, 0.3f);
		direction.subtractLocal(origin).normalizeLocal();

		Ray ray = new Ray();
		ray.setOrigin(origin);
		ray.setDirection(direction);

		CollisionResults results = new CollisionResults();

//		spatial.updateModelBound();
		spatial.collideWith(ray, results);

		if (results.size() == 0) {
			return null;
		}

//		for (CollisionResult collisionResult : results) {
//			if (collisionResult.getGeometry().getCullHint() == CullHint.Always) {
//				continue;
//			}
//			return collisionResult;
//		}

		return results.getClosestCollision();
	}

	public static String getSpatailPath(Spatial sp, Node parent) {
		StringBuffer buf = new StringBuffer();
		getSpatailPath0(sp, parent, buf);
		return buf.toString();
	}

	private static void getSpatailPath0(Spatial sp, Node parent, StringBuffer buf) {
		if (sp == null) {
			return;
		}
		if (sp == parent) {
			return;
		}
		buf.insert(0, sp.getName());

		if (sp.getParent() != parent) {
			buf.insert(0, "-");
		}
		getSpatailPath0(sp.getParent(), parent, buf);
	}

	public static Spatial findByPath(Node elevatorNode, String mdlPath) {
		String[] nameList = mdlPath.split("-");
		String name = null;
		Node node = elevatorNode;
		try {
			for (int i = 0; i < nameList.length; i++) {
				name = nameList[i];
				node = (Node) node.getChild(name);
			}
		} catch (Exception e) {
			node = null;
			e.printStackTrace();
			return null;
		}
		return node;
	}

	/**
	 * 创建球体
	 */
	public static Geometry getSphere(AssetManager assetManager, int sample, float radius, ColorRGBA color) {
		Geometry ballMod = new Geometry("ball", new Sphere(sample, sample, radius));

		Material ballMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		ballMat.setColor("Diffuse", color);
		ballMat.setFloat("Shininess", 10f);
		ballMat.setColor("Specular", ColorRGBA.White);
		ballMat.setBoolean("UseMaterialColors", true);
		ballMod.setMaterial(ballMat);
		return ballMod;
	}

	public static void updateWiringBox(Geometry boxGeo, Spatial ref) {
		WireBox box = (WireBox) boxGeo.getMesh();
		BoundingBox bound = ((BoundingBox) ref.getWorldBound());
		boxGeo.setLocalTranslation(bound.getCenter());
		box.updatePositions(bound.getXExtent(), bound.getYExtent(), bound.getZExtent());
	}

	public static Geometry getWiringBox(AssetManager assetManager, Spatial spatial) {
		BoundingBox bound = ((BoundingBox) spatial.getWorldBound());
		return getWiringBox(assetManager, bound.getCenter(), bound.getExtent(null));
	}

	public static Geometry getWiringBox(AssetManager assetManager, Vector3f center, Vector3f ext) {
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", new ColorRGBA(1F, 1F, 1F, 1F));
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.getAdditionalRenderState().setDepthWrite(true);
		mat.getAdditionalRenderState().setWireframe(true);

		WireBox mesh = new WireBox(ext.x, ext.y, ext.z);
		Geometry ballMod = new Geometry("WiringBox", mesh);
		ballMod.setMaterial(mat);
		ballMod.setLocalTranslation(center);
		return ballMod;
	}

	public static ColorRGBA convert(javafx.scene.paint.Color color) {
		ColorRGBA colorRGBA = new ColorRGBA();
		colorRGBA.set((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity());
//		colorRGBA.set(color.r, g, b, a);
		return colorRGBA;
	}

	/**
	 * 获取Mesh line的真实的Start点，预设
	 * @param line com.jme3.scene.shape.Line
	 */
	public static Vector3f getLineStart(Line line) {
		VertexBuffer position = line.getBuffer(Type.Position);
		float[] arr = BufferUtils.getFloatArray((FloatBuffer) position.getDataReadOnly());
		return new Vector3f(arr[0], arr[1], arr[2]);
	}

	/**
	 * 获取Mesh line的真实的End点
	 * @param line com.jme3.scene.shape.Line
	 */
	public static Vector3f getLineEnd(Line line) {
		VertexBuffer position = line.getBuffer(Type.Position);
		float[] arr = BufferUtils.getFloatArray((FloatBuffer) position.getDataReadOnly());
		return new Vector3f(arr[3], arr[4], arr[5]);
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

	// 使用Cylinder
	public static Geometry createCylinderLine(AssetManager assetManager, List<Vector3f> pointList, float radius, ColorRGBA color) {

		Material ballMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		ballMat.setColor("Diffuse", color);
		ballMat.setFloat("Shininess", 10f);
		ballMat.setColor("Specular", ColorRGBA.White);
		ballMat.setBoolean("UseMaterialColors", true);
//		ballMat.getAdditionalRenderState().setLineWidth(width);
		Node ret = new Node();

//		List<Geometry> realLineList = new ArrayList<>();
//		两点之间穿件一条线
//		为了方便，将pointList集合转换为List<Vector3f[]>, Vector3f[]表示线段的两点
		List<Vector3f[]> lineList = new ArrayList<>();
		for (int i = 0; i < pointList.size() - 1; i++) {
			lineList.add(new Vector3f[2]);
		}
		for (int i = 0; i < pointList.size() - 1; i++) {
			lineList.get(i)[0] = pointList.get(i);
		}
		for (int i = 0; i < pointList.size() - 1; i++) {
			lineList.get(i)[1] = pointList.get(i + 1);
		}

		lineList.forEach(line -> {
			// 获取导线起始点和方向
			Vector3f start = line[0];
			Vector3f end = line[1];
			Vector3f lineDir = end.subtract(start);
			// 创建一段导线实体模型width / 500
			log.debug("线段半径:{} ,起点：{}, 中点：{}, 长度：{} ", radius, start, end, start.distance(end));
			Cylinder mesh = new Cylinder(6, 6, radius, start.distance(end));
			Geometry real = createLineGeo(assetManager, mesh, color);
			// 设置导线坐标
			real.setLocalTranslation(start.add(end.subtract(start).mult(0.5f)));
			// 设置导线旋转量。
			Matrix3f rotation = new Matrix3f();
			// 注意：Cylinder默认是水平方向的，任取X或Z轴作为参考。计算与导线方向lineDir的夹角
			rotation.fromAngleAxis(lineDir.angleBetween(Vector3f.UNIT_Z), lineDir.cross(Vector3f.UNIT_Z));
			real.setLocalRotation(rotation);
			real.setMaterial(ballMat);
//			realLineList.add(real);
			ret.attachChild(real);
		});

		pointList.forEach(point -> {
			Geometry real = getSphere(assetManager, 6, radius, color);
			real.setLocalTranslation(point);
			real.setMaterial(ballMat);
//			realLineList.add(real);
			ret.attachChild(real);
		});

//		Mesh outMesh = new Mesh();
//		GeometryBatchFactory.mergeGeometries(realLineList, outMesh);
//		Geometry ret = new Geometry("wireGeo", outMesh);

		GeometryBatchFactory.optimize(ret);

//		批处理后实际就是一个Geometry
		return (Geometry) ret.getChild(0);
	}

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
			MaterialDef def = colorMat.getMaterialDef();
			try {
				def.addTechniqueDef(mat.getActiveTechnique().getDef());
			} catch (Exception e) {
//				e.printStackTrace();
			}
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

	public static Quaternion parseDegree(String value) {
		if (value == null) {
			return null;
		}

		value = trim(value);
		String[] arr = value.split(",");

		Quaternion result = new Quaternion(//
				new float[] { Float.parseFloat(arr[0]) * FastMath.DEG_TO_RAD, //
						Float.parseFloat(arr[1]) * FastMath.DEG_TO_RAD, //
						Float.parseFloat(arr[2]) * FastMath.DEG_TO_RAD//
				});

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
