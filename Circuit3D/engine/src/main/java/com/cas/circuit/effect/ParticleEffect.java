package com.cas.circuit.effect;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Spatial;

public enum ParticleEffect {
	/**
	 * 燃烧效果
	 */
	Fire("Materials/fire.j3o"),
	/**
	 * 烟雾效果
	 */
	Smoke("Materials/smoke.j3o");

	private String effectName;

	private ParticleEffect(String effectName) {
		this.effectName = effectName;
	}

	public Spatial getEmitter(AssetManager assetManager) {
		return assetManager.loadModel(effectName);
	}
}
