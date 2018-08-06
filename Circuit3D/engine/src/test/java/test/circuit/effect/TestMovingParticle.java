/*
 * Copyright (c) 2009-2012 jMonkeyEngine All rights reserved. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package test.circuit.effect;

import com.cas.circuit.effect.ParticleEffect;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Particle that moves in a circle.
 * @author Kirill Vainer
 */
public class TestMovingParticle extends SimpleApplication {

	private Spatial emit;
	private float angle = 0;

	public static void main(String[] args) {
		TestMovingParticle app = new TestMovingParticle();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		assetManager.registerLocator("E:\\JME_SDKPROJ_HOME\\ESimulation3D\\assets", FileLocator.class);

		emit = ParticleEffect.Fire.getEmitter(assetManager);
//		ParticleEmitterControl c = emit.getControl(ParticleEmitterControl.class);
//		c.setEnabled(false);
		rootNode.attachChild(emit);

		cam.setLocation(new Vector3f(.0321f, .056f, 0.117f));
	}

	@Override
	public void simpleUpdate(float tpf) {
//		angle += tpf;
//		angle %= FastMath.TWO_PI;
//		float x = FastMath.cos(angle) ;
//		float y = FastMath.sin(angle) ;
//		emit.setLocalTranslation(x, 0, y);
	}
}
