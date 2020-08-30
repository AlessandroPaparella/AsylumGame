package engine;

import java.io.Serializable;
import java.util.Set;

public class Item implements Serializable {
	private static Integer istances = 0;
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
		id = istances;
		istances++;
		pushed=false;
		opened=false;
		this.handler = handler;
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

}
