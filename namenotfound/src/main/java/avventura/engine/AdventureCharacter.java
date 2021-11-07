package engine;
import java.io.Serializable;

public class AdventureCharacter implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -6608717241444829961L;
	private static Integer instances = 0;
	private Integer health;
	private String name, description, talk;
	private Integer id;
	private Inventory inv;
	private Item droppable;

	public AdventureCharacter(Integer health, String name, String description, String talk, Inventory inv, Item droppable) {
		super();
		this.health = health;
		this.name = name;
		this.description = description;
		this.talk = talk;
		id = instances+1;
		instances++;
		this.inv = inv;
		this.droppable = droppable;
	}

	public Integer getHealth() {
		return health;
	}

	public void setHealth(Integer health) {
		this.health = health;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTalk() {
		return talk;
	}

	public void setTalk(String talk) {
		this.talk = talk;
	}

	public Integer getId() {
		return id;
	}

	public Inventory getInv() {
		return inv;
	}

	public void setInv(Inventory inv) {
		this.inv = inv;
	}

	public Item getDroppable() {
		return droppable;
	}

	public void setDroppable(Item droppable) {
		this.droppable = droppable;
	}



}
