package engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ParserEn implements Parser{


	public String checkInDictionary(String token, String dictionary) throws Exception {
		// TODO Auto-generated method stub
		Scanner s = null;
		String in;
		boolean find = false;
		try {
		s = new Scanner(new BufferedReader(new FileReader(dictionary)));
		while(s.hasNext() && find == false) {
			in = s.nextLine();
			if(in.equals(token)) return in;
		}
		} finally {
			s.close();
		}
		return null;
	}

	public engine.ParserOutput parse(String command, Inventory inv) throws Exception {
		// TODO Auto-generated method stub
		String cmd = command.toLowerCase().trim();
        String[] tokens = cmd.split("\\s+");
        switch(tokens.length) {
        case 2 : 
        	//verbo oggetto
        	String com = checkInDictionary(tokens[1], "commandi.txt");
        	if(com != null) {
            	String obj = checkInDictionary(tokens[2], "oggetti.txt");
            	if(obj != null) {
            		return new ParserOutput(new Command(null, com), new Item(obj,null,null));
            	} else throw new InvalidCommandException(); 
        	} else throw new InvalidCommandException();
        case 3 : 
        	//verbo articolo oggetto
        	String com1 = checkInDictionary(tokens[1], "commandi.txt");
        	if(com1 != null) {
        		String art = checkInDictionary(tokens[2], "articoli.txt");
        		if(art != null) {
		        	String obj = checkInDictionary(tokens[3], "oggetti.txt");
		        	if(obj != null) {
		        		return new ParserOutput(new Command(null, com1), new Item(obj,null,null));
		        	} else throw new InvalidCommandException();
            	} else throw new InvalidCommandException(); 
        	} else throw new InvalidCommandException();
        case 4 :
        	//verbo oggetto preposizione oggetto
        	String com2 = checkInDictionary(tokens[1], "commandi.txt");
        	if(com2 != null) {
            	String obj = checkInDictionary(tokens[2], "oggetti.txt");
            	if(obj != null) {
            		String prep = checkInDictionary(tokens[3], "preposizioni.txt");
            		if(prep != null) {
                    	String obj1 = checkInDictionary(tokens[4], "oggetti.txt");
                    	if(obj == null) {
                    		return new ParserOutput(new Command(null, com2), new Item(obj,null,null), new Item(obj1,null,null));
                    	} else throw new InvalidCommandException();
            		} else throw new InvalidCommandException();
            	} else throw new InvalidCommandException(); 
        	} else throw new InvalidCommandException();
        case 5 : 
        	//verbo articolo oggetto preposizione oggetto
        	String com3 = checkInDictionary(tokens[1], "commandi.txt");
        	if(com3 != null) {
        		String art = checkInDictionary(tokens[2], "articoli.txt");
        		if(art != null) {
		        	String obj = checkInDictionary(tokens[3], "oggetti.txt");
		        	if(obj != null) {
		        		String prep = checkInDictionary(tokens[4], "preposizioni.txt");
	            		if(prep != null) {
	                    	String obj1 = checkInDictionary(tokens[5], "oggetti.txt");
	                    	if(obj == null) {
	                    		return new ParserOutput(new Command(null, com3), new Item(obj,null,null), new Item(obj1,null,null));
	                    	} else throw new InvalidCommandException();
	            		} else throw new InvalidCommandException();
		        	} else throw new InvalidCommandException();
            	} else throw new InvalidCommandException(); 
        	} else throw new InvalidCommandException();	
        default:
        	throw new InvalidCommandException();
        }
	}
}