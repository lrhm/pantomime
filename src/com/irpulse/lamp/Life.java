package com.irpulse.lamp;

import android.util.Log;

public class Life {

	public interface OnStateChangeListener {
		public void onActivated(int index);
		public void onDeActivated(int index);
	}

	OnStateChangeListener onStateChangeListener;

	public void setOnStateChangeListener(
			OnStateChangeListener onStateChangeListener) {
		this.onStateChangeListener = onStateChangeListener;
	}

	public boolean isAlive = true;
	public long willActiveAtTime = -1;
	public int reChargeFactor = 1;
	int id;

	public void setActive() {
		isAlive = true;
		willActiveAtTime = -1;
		if (onStateChangeListener != null)
			onStateChangeListener.onActivated(id);
		if(LifeSystem.lifeListener != null){
			LifeSystem.lifeListener.onLifeReborend(id);
		}
	}

	public Life(int id) {
		this.id = id;
	}

	public void setDeActive() {
		
		
		long reChargeTime = reChargeFactor * 60000;
		long max = System.currentTimeMillis();
		for(Life life : LifeSystem.lifes){
			if(!life.isAlive){
				if(life.willActiveAtTime > max)
					max = life.willActiveAtTime;
			}
		}
		isAlive = false;

		willActiveAtTime = max + reChargeTime;

		if (onStateChangeListener != null)
			onStateChangeListener.onDeActivated(id);
		if(LifeSystem.lifeListener != null){
			LifeSystem.lifeListener.onLifeDeactived(id);
		}

	}

}
