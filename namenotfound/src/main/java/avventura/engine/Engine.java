package engine;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import engine.launcher.Launcher;

public class Engine {

	private final GameDescription game;
	static Locale locale = Locale.getDefault();
	public Parser parser;
	private ResourceBundle bundle;

    public Engine(GameDescription game) {
    	locale = Launcher.locale;
    	bundle = ResourceBundle.getBundle("engine", locale);
        this.game = game;
        try {
            this.game.init();
            if(Thread.interrupted()) {
    			return;
    		}
        } catch (Exception ex) {
            System.err.println(ex);
        }
        parser = null;
        //parser = new game.ParserIT();
        /*
        if() parser = new Parser(this.loadDictionary("./resources/articoli.txt"),
        							this.loadDictionary("./resources/preposizioni.txt"));
        else parser = new Parser(this.loadDictionary("./resources/articles.txt"),
        							this.loadDictionary("./resources/preposition.txt"));*/
    }

    public void run() throws Exception {
        System.out.println("================================================");
        System.out.println(game.getCurrentRoom().getDescription());
        Scanner scanner = new Scanner(System.in);
        while (!Thread.interrupted() && scanner.hasNextLine()) {
            String command = scanner.nextLine();
            try {
            	ParserOutput p = parser.parse(command, game.getCommands(), game.getCurrentRoom().getObjects(),
                		game.getInventory(), game.getCurrentRoom().getEnemies());
                if (p.getCommand() != null && p.getCommand().getType() == CommandType.END) {
                	System.out.println(bundle.getString("end"));
                	Thread.currentThread().interrupt();
                    break;
                } else {
                    game.nextMove(p, System.out);
                    System.out.println("================================================");
                }
            }catch (InvalidCommandException e) {
				System.out.println(e.getMessage());
				System.out.println("================================================");
			}

        }
        return;
    }
}
