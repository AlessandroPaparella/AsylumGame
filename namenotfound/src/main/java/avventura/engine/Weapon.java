package engine;

public class Weapon extends Item {
	private Integer shots, minDamage, maxDamage;

	public Weapon(String name, String description, CommandHandler handler, Integer shots, Integer minDamage, Integer maxDamage) {
		super(name, description, handler);
		this.shots = shots;
		this.maxDamage = maxDamage;
		this.minDamage = minDamage;
	}

	public Integer getShots() {
		return shots;
	}

	public void setShots(Integer shots) {
		this.shots = shots;
	}

	public Integer getMinDamage() {
		return minDamage;
	}

	public void setMinDamage(Integer minDamage) {
		this.minDamage = minDamage;
	}

	public Integer getMaxDamage() {
		return maxDamage;
	}

	public void setMaxDamage(Integer maxDamage) {
		this.maxDamage = maxDamage;
	}

	public Integer getDamage() {
		Double dam = (Math.random()*(maxDamage-minDamage))+minDamage;
		return dam.intValue();
	}
}
