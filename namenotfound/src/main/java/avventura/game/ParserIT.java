package game;

import java.util.ArrayList;
import java.util.List;

import engine.AdventureCharacter;
import engine.Command;
import engine.CommandType;
import engine.InvalidCommandException;
import engine.Inventory;
import engine.Item;
import engine.Parser;
import engine.ParserOutput;

public class ParserIT implements Parser{

	List<String> articles = new ArrayList<String>();
	List<String> prepositions = new ArrayList<String>();
	List<String> whitespace = new ArrayList<String>();


	public ParserIT() {
		this.loadArticles();
		this.loadPrepositions();
		this.loadWhitespace();
	}

	public boolean checkInDictionary(String token, List<String> dictionary) throws Exception {
		// TODO Auto-generated method stub
		if(dictionary.contains(token)) return true;
		else return false;
	}

	public int checkForCommand(String token, List<Command> commands) {
	        for (int i = 0; i < commands.size(); i++) {
	            if (commands.get(i).getName().equals(token) || commands.get(i).getAlias().contains(token)) {
	                return i;
	            }
	        }
	        return -1;
	}

	public int checkForSingleCommand(String token, Command commands) {
	       if (commands.getName().equals(token) || commands.getAlias().contains(token)) {
	                return 0;
	          }
	        return -1;
	}

	public int checkForObject(String token, List<Item> obejcts) {
        for (int i = 0; i < obejcts.size(); i++) {
            if (obejcts.get(i).getName().equals(token) || obejcts.get(i).getAlias().contains(token)) {
                return i;
            }
        }
        return -1;
    }

	private Command searchWalkto(List<Command> commands) {
		for(Command c: commands) {
			if(c.getType()==CommandType.WALK_TO) {
				return c;
			}
		}
		return null;
	}

	@Override
	public ParserOutput parse(String command, List<Command> commands, List<Item> objects, Inventory inv,
			List<AdventureCharacter> enemies) throws Exception {
		// TODO Auto-generated method stub
		String cmd = command.toLowerCase().trim();
        String[] tokens = cmd.split("\\s+");
        Command walk = searchWalkto(commands);
        switch(tokens.length) {
        case 1 :
        	//verbo
        	int com = checkForCommand(tokens[0], commands);
        	if(com > -1) return new ParserOutput(commands.get(com));
        	else throw new InvalidCommandException();

        case 2 :
        	//verbo oggetto | verbo nemico | verbo stanza
        	if(checkForSingleCommand(tokens[0], walk) == 0 && !articles.contains(tokens[1])
        												&& !prepositions.contains(tokens[1])) {
        		return new ParserOutput(walk, tokens[1]);
        	}
        	else {
	        	int com2 = checkForCommand(tokens[0], commands);
	        	if(com2 > -1) {
	            	int obj2 = checkForObject(tokens[1], objects);
	            	if(obj2 != -1) return new ParserOutput(commands.get(com2), objects.get(obj2));
	            	int inv2= checkForObject(tokens[1], inv.getList());
	            	if(inv2 != -1) return new ParserOutput(commands.get(com2), inv.getList().get(inv2));
        			for(AdventureCharacter a : enemies) {
        				if(a.getName().equals(tokens[1])) {
        					return new ParserOutput(commands.get(com2), a);
        				}
        			}
        			throw new InvalidCommandException();
	        	} else throw new InvalidCommandException();
        	}
        case 3 :
        	//verbo articolo oggetto | verbo articolo nemico | verbo articolo stanza
        	if(checkForSingleCommand(tokens[0], walk) == 0 && (articles.contains(tokens[1])
						|| prepositions.contains(tokens[1])))  {
        			return new ParserOutput(walk, tokens[2]);
			}
        	else if (checkForSingleCommand(tokens[0], walk) == 0 && whitespace.contains(tokens[2]))  {
    			return new ParserOutput(walk, tokens[1] + " " + tokens[2]);
        	}

        	int com3 = checkForCommand(tokens[0], commands);
        	if(com3 > -1) {
        		if(articles.contains(tokens[1]) || prepositions.contains(tokens[1])) {
        			int obj3 = checkForObject(tokens[2], objects);
	            	if(obj3 != -1) return new ParserOutput(commands.get(com3), objects.get(obj3));
	            	int inv3= checkForObject(tokens[2], inv.getList());
	            	if(inv3 != -1) return new ParserOutput(commands.get(com3), inv.getList().get(inv3));
        			for(AdventureCharacter a : enemies) {
        				if(a.getName().equals(tokens[2])) return new ParserOutput(commands.get(com3), a);
        			}
        			throw new InvalidCommandException();
        		} else throw new InvalidCommandException();
        	} else throw new InvalidCommandException();

        case 4 :
        	//verbo oggetto preposizione oggetto
        	if(checkForSingleCommand(tokens[0], walk) == 0 && whitespace.contains(tokens[3]) &&
						 prepositions.contains(tokens[1]))  {
        			return new ParserOutput(walk, tokens[2] + " " + tokens[3]);
			}
        	int com4 = checkForCommand(tokens[0], commands);
        	if(com4 > -1) {
        		int obj4 = checkForObject(tokens[1], objects);
            	int inv4 = checkForObject(tokens[1], inv.getList());
            	if((obj4 + inv4) > -2) {
            		if(prepositions.contains(tokens[2])) {
            			int secondObj = checkForObject(tokens[3], objects);
            			if(secondObj != -1 && obj4 != -1) return new ParserOutput(commands.get(com4), objects.get(obj4),objects.get(secondObj));
            			if(secondObj != -1 && inv4 != -1) return new ParserOutput(commands.get(com4), inv.getList().get(inv4), objects.get(secondObj));

            			int secondInv = checkForObject(tokens[3], inv.getList());
            			if(secondInv != -1 && obj4 != -1) return new ParserOutput(commands.get(com4), objects.get(obj4), inv.getList().get(secondInv));
            			if(secondInv != -1 && inv4 != -1) return new ParserOutput(commands.get(com4), inv.getList().get(inv4), inv.getList().get(secondInv));
            			throw new InvalidCommandException();
            		} else throw new InvalidCommandException();
            	} else throw new InvalidCommandException();
        	} else throw new InvalidCommandException();

        case 5 :
        	//verbo articolo oggetto preposizione oggetto
        	int com5 = checkForCommand(tokens[0], commands);
        	if(com5 > -1) {
        		if(articles.contains(tokens[1])) {
        			int obj5 = checkForObject(tokens[2], objects);
                	int inv5 = checkForObject(tokens[2], inv.getList());
                	if((obj5 + inv5) > -2) {
                		if(prepositions.contains(tokens[3])) {
                			int secondObj = checkForObject(tokens[4], objects);
                			if(secondObj != -1 && obj5 != -1) return new ParserOutput(commands.get(com5), objects.get(obj5),objects.get(secondObj));
                			if(secondObj != -1 && inv5 != -1) return new ParserOutput(commands.get(com5), inv.getList().get(inv5), objects.get(secondObj));

                			int secondInv = checkForObject(tokens[4], inv.getList());
                			if(secondInv != -1 && obj5 != -1) return new ParserOutput(commands.get(com5), objects.get(obj5),inv.getList().get(secondInv));
                			if(secondInv != -1 && inv5 != -1) return new ParserOutput(commands.get(com5), inv.getList().get(inv5),inv.getList().get(secondInv));
                			throw new InvalidCommandException();
                		} else throw new InvalidCommandException();
                	} else throw new InvalidCommandException();
        		} else throw new InvalidCommandException();
        	} else throw new InvalidCommandException();

        default:
        	throw new InvalidCommandException();
        }
	}

	public void loadArticles() {
		// TODO Auto-generated method stub
		articles.add("il");
		articles.add("lo");
		articles.add("la");
		articles.add("i");
		articles.add("gli");
		articles.add("gle");
	}

	public void loadWhitespace() {
		whitespace.add("operatoria");
		whitespace.add("imbottita");
		whitespace.add("1");
		whitespace.add("2");
		whitespace.add("3");
		whitespace.add("4");
		whitespace.add("5");
		whitespace.add("6");
		whitespace.add("7");
		whitespace.add("8");
	}

	public void loadPrepositions() {
		// TODO Auto-generated method stub
		prepositions.add("di");
		prepositions.add("a");
		prepositions.add("da");
		prepositions.add("in");
		prepositions.add("con");
		prepositions.add("su");
		prepositions.add("per");
		prepositions.add("tra");
		prepositions.add("fra");
		//preposition composte
		prepositions.add("coi");
		prepositions.add("degli");
		prepositions.add("dal");
		prepositions.add("nel");
		prepositions.add("col");
		prepositions.add("sul");
		prepositions.add("alla");
		prepositions.add("dei");
		prepositions.add("dai");
		prepositions.add("nei");
		prepositions.add("degli");
		prepositions.add("con");

		prepositions.add("del");
		prepositions.add("al");
		prepositions.add("dal");
		prepositions.add("nel");
		prepositions.add("nello");
		prepositions.add("nella");
		prepositions.add("col");
		prepositions.add("sul");
	}

}
