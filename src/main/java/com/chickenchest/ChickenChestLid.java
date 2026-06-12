package com.chickenchest;

public class ChickenChestLid {
	private float open;
	private int openCount;

	public void incrementOpeners() {
		this.openCount++;
	}

	public void decrementOpeners() {
		this.openCount = Math.max(0, this.openCount - 1);
	}

	public void tick() {
		float target = this.openCount > 0 ? 1.0F : 0.0F;
		if (this.open < target) {
			this.open = Math.min(target, this.open + 0.1F);
		} else if (this.open > target) {
			this.open = Math.max(target, this.open - 0.1F);
		}
	}

	public float getOpen() {
		return this.open;
	}

	public float getOpen(float tickDelta) {
		float target = this.openCount > 0 ? 1.0F : 0.0F;
		float current = this.open;
		if (current < target) {
			current = Math.min(target, current + 0.1F * tickDelta);
		} else if (current > target) {
			current = Math.max(target, current - 0.1F * tickDelta);
		}
		return current;
	}
}
