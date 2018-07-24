package com.cas.circuit.element;

/**
 * 时间继电器
 * @author Administrator
 */
public class TimeRelayElm extends RelayElmEx {

	// 时间
	private float time = 10;
	// 单位
	private int unit;
	
//	TODO
	@Override
	public void startIteration() {
		if (force) {
			i_position = 1;
			return;
		}

		if (!lock) {
			// magic value to balance operate speed with reset speed semi-realistically
			double magic = 1.3;
			double pmult = Math.sqrt(magic + 1);
			double p = coilCurrent * pmult / onCurrent;
			double f = Math.abs(p * p) - 1.3;

			d_position = f;
			if (d_position < 0) {
				d_position = 0;
			}
		} else if (Math.abs(delta - coilCurrent) < 1e-8) {
			if (lock) {
				button.unstuck();
				lock = false;
			}
		}

		if (d_position > 1) {
			d_position = 1;
			if (!lock) {
				lock = true;
				button.absorbed();
			}
		}

		if (d_position < .1) {
			i_position = 0;
		} else if (d_position > .9) {
			i_position = 1;
		} else {
			i_position = 2;
		}
		delta = coilCurrent;
	}

}
