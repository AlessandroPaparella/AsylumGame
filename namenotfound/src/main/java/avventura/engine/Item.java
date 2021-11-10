package engine;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Item implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 5059157097169486201L;
	public static Integer istances = 0;
	private String name, description;
	private Integer id;
	private Set<String> alias;
	private Boolean pushed, opened;
	private Set<CommandType> commands;
	private CommandHandler handler;

	public Item(String name, String description, CommandHandler handler) {
		super();
		this.name = name;
		this.description = description;
		id = istances+1;
		istances++;
		pushed=false;
		opened=false;
		this.handler = handler;
		this.alias = new HashSet<String>();
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

	public Set<String> getAlias() {
		return alias;
	}

	public void setAlias(Set<String> alias) {
		this.alias = alias;
	}

	public Boolean isPushed() {
		return pushed;
	}

	public void setPushed(Boolean pushed) {
		this.pushed = pushed;
	}

	public Boolean isOpened() {
		return opened;
	}

	public void setOpened(Boolean opened) {
		this.opened = opened;
	}

	public Set<CommandType> getCommands() {
		return commands;
	}

	public void setCommands(Set<CommandType> commands) {
		this.commands = commands;
	}

	public CommandHandler getHandler() {
		return handler;
	}

	public void setHandler(CommandHandler handler) {
		this.handler = handler;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}







}
