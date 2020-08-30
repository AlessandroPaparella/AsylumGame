

public interface Parser {

	public String checkInDictionary(String token, String dictionary) throws Exception;
    
    public ParserOutput parse(String command, Inventory inv) throws Exception;
}
