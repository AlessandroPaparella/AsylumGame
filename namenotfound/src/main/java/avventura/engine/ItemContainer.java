package engine;

import java.util.ArrayList;
import java.util.List;

public class ItemContainer extends Item {
	private Integer lockedBy;
	private Boolean locked;
	private List<Item> content = new ArrayList<Item>();

	public ItemContainer(String name, String description, CommandHandler handler, Integer lockedBy, Boolean locked) {
		super(name, description, handler);
		this.locked = locked;
		this.lockedBy = lockedBy;
	}

	public ItemContainer(String name, String description, CommandHandler handler) {
		super(name, description, handler);
		this.locked = false;
		this.lockedBy = null;
	}

	public Integer getLockedBy() {
		return lockedBy;
	}

	public void setLockedBy(Integer lockedBy) {
		this.lockedBy = lockedBy;
	}

	public Boolean isLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public List<Item> getContent() {
		return content;
	}

	public void setContent(List<Item> content) {
		this.content = content;
	}

	public void add(Item o) {
        content.add(o);
    }

    public void remove(Item o) {
        content.remove(o);
    }

}
