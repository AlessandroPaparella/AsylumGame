package engine;

import game.GameDescription;
import java.io.Serializable;
import java.util.function.Consumer;

public interface TrapHandler extends Consumer<GameDescription>, Serializable{
	
	public void execute(GameDescription game);
}
