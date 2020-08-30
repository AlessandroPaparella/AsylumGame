package engine;

public class Gateway {
	private Integer lockedBy = null;

	private Boolean locked = false;
	
	private Direction dir;
	
	public Gateway(Direction dir, Integer idKey, Boolean locked) {
	        lockedBy = idKey;
		this.locked = locked;
		this.dir = dir;
	}
	
	public Gateway(Direction dir){
		this.dir = dir;
	}
	
	public Integer getLockedBy() {
		return lockedBy;
	}
	
	public void setLockedBy(Integer idKey){
		lockedBy = idKey;
	}

	public void setLocked(Boolean locked) {
	        this.locked = locked;
	}

	public Boolean isLocked() {
    		return locked;
	}
	
	public void unlock() {
		locked = false;
	}
	
	public void lock() {
		locked = true;
	}
	
	public Direction getDirection(){
		return direction;
	}
	
	public void setDirection(Direction dir) {
		this.dir = dir;
	}
}
