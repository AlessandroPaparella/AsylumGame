package engine;

import java.util.ArrayList;
import java.util.List;

public interface Parser {

    public ParserOutput parse(String command,List<Command> commands, List<Item> objects, Inventory inv,
								List<AdventureCharacter> enemies) throws Exception;
}
