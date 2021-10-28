package engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 6724461873712394218L;
	private static Integer istances = 0;
	private String description, look, name;
	private Boolean hasLight, visible;
	private Integer id;
	private EventHandler trap;
	private Boolean visited = false;
	private List<AdventureCharacter> enemies = new ArrayList<AdventureCharacter>();
	private List<Item> objects = new ArrayList<Item>();

	public Room(String description, String look, String name, Boolean hasLight,
					List<AdventureCharacter> enemies, List<Item> objects) {
		super();
		this.description = description;
		this.look = look;
		this.name = name;
		this.hasLight = hasLight;
		visible = hasLight;
		this.enemies.addAll(enemies);
		this.objects.addAll(objects);
		id = istances;
		istances++;
		trap = null;
	}

	public Room(String description, String look, String name) {
		super();
		this.description = description;
		this.look = look;
		this.name = name;
		this.hasLight = false;
		id = istances;
		istances++;
		trap = null;
		visible = hasLight;
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

	public Boolean hasLight() {
		return hasLight;
	}

	public void setLight(Boolean hasLight) {
		this.hasLight = hasLight;
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

	public Boolean isVisited() {
		return visited;
	}

	public void setVisited(Boolean visited) {
		this.visited = visited;
	}

	public List<AdventureCharacter> getEnemies() {
		return enemies;
	}

	public void setEnemies(List<AdventureCharacter> enemies) {
		this.enemies = enemies;
	}

	public List<Item> getObjects() {
		return objects;
	}

	public void setObjects(List<Item> objects) {
		this.objects = objects;
	}

	public Boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}


}
