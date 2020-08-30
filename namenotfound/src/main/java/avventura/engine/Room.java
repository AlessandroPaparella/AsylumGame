package engine;

public class Room {
	private static Integer istances = 0;
	private String description, look, name;
	private Boolean visible;
	private Integer id;
	private EventHandler trap;

	public Room(String description, String look, String name, Boolean visible) {
		super();
		this.description = description;
		this.look = look;
		this.name = name;
		this.visible = visible;
		id = istances;
		istances++;
		trap = null;
	}

	public Room(String description, String look, String name) {
		super();
		this.description = description;
		this.look = look;
		this.name = name;
		this.visible = true;
		id = istances;
		istances++;
		trap = null;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLook() {
		return look;
	}

	public void setLook(String look) {
		this.look = look;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public EventHandler getTrap() {
		return trap;
	}

	public void setTrap(EventHandler trap) {
		this.trap = trap;
	}

	public Boolean hasTrap() {
		Boolean hasTrap = false;
		if(trap!=null)
			hasTrap = true;
		return hasTrap;
	}
}
