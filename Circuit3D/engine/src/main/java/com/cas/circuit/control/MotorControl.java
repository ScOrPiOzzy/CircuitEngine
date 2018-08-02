package com.cas.circuit.control;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import lombok.Setter;

public class MotorControl extends AbstractControl {
	private String rotator;

	public MotorControl() {
	}

	public MotorControl(String rotator) {
		this.rotator = rotator;
	}

	private int dir;
// 	转子
	protected List<Spatial> rotatorList = new ArrayList<>();
	@Setter
	private int max;

//	实时转速
	private float rr;

	private boolean stop;

	@Override
	protected void controlUpdate(float tpf) {
		if (stop) {
			if (rr > 5) {
				rr = FastMath.extrapolateLinear(0.04f, rr, 0);
			} else {
				rr /= 3;
				if (rr < 1e-4) {
					rr = 0;
				}
			}
		} else {
			if (rr < 100) {
				rr = FastMath.extrapolateLinear(0.3f, rr, 120);
			} else {
				rr = FastMath.extrapolateLinear(0.05f, rr, max);
			}
		}
		rotatorList.forEach(r -> r.rotate(dir * rr * FastMath.TWO_PI / 60 * tpf, 0, 0));
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {

	}

	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);

		if (spatial != null) {
			Node n = (Node) spatial;
			String[] arr = rotator.split(",");
			for (int i = 0; i < arr.length; i++) {
				rotatorList.add(n.getChild(arr[i]));
			}
		}
	}

	public void setDir(int dir) {
		if (this.dir == dir) {
			return;
		}
		this.dir = dir;
	}

	public void start() {
		stop = false;
	}

	public void stop() {
		stop = true;
	}

}
