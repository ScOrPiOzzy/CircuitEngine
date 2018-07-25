package com.cas.circuit.element;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

import javax.xml.bind.Unmarshaller;

import com.cas.circuit.component.Terminal;

/**
 * 时间继电器
 * @author Administrator
 */
public class TimeRelayElm extends RelayElm {

	// 设定值, 默认10秒
	private float preset = 10;
//	// 量程:如10s、100s、10min、60min
//	private int range = 10;

	private float count = 0;

	private Timer timer;

	public TimeRelayElm() {
		super();
	}

	public TimeRelayElm(Unmarshaller u, Function<String, Terminal> f, Map<String, String> params) {
		super(u, f, params);

		String value = params.get("preset");
		preset = value == null ? preset : Integer.parseInt(value);
	}

	// TODO
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
		} else if (delta == 0 && delta == coilCurrent) {
			if (lock) {

				count = 0;
				if (timer != null) {
					timer.cancel();
					timer = null;
				}

				lock = false;
			}
		}

		if (d_position > 1) {
			d_position = 1;
			if (!lock) {
				startCount();

				lock = true;
			}
		}

		if (d_position < .1) {
			i_position = 0;
		} else if (d_position > .9) {
			if (count >= preset) {
				i_position = 1;
				timer.cancel();
			}
		} else {
			i_position = 2;
		}
		delta = coilCurrent;
	}

	private void startCount() {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		count = 0;
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				count += 0.1;
			}
		}, 0, 100);
	}

}
