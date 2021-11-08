package engine;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import engine.launcher.Launcher;

public class Engine {

	private final GameDescription game;
	static Locale locale = Locale.getDefault();
	static public Parser parser;
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
