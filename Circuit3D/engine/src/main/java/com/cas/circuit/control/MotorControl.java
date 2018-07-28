package com.cas.circuit.control;

import java.util.ArrayList;
import java.util.List;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

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

	@Override
	protected void controlUpdate(float tpf) {
		rotatorList.forEach(r -> r.rotate(dir * tpf, 0, 0));
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
		this.dir = dir;
	}
}
