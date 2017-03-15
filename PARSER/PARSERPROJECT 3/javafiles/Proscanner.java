

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * This is the main class for the Scanner
 */

/**
 * @author Danny Reinheimer
 *
 */
public class Proscanner {

	/**
	 * @param args The file name 
	 */
	public static ArrayList<Pair<TokenNames,String>> tokens = new ArrayList<Pair<TokenNames,String>>();
	int size = tokens.size();
	public static void proscan(String s) {
		// checks to see if we are given any arguments
		
		String fileName = s;
		
		Scan scan = new Scan(fileName);
		Pair<TokenNames,String> tokenPair;
		
		try {
			// get the name of the file minus the dot 
			int pos = fileName.lastIndexOf(".");
			String newFileName = fileName.substring(0, pos) + "_gen.c";
			PrintWriter writer = new PrintWriter(newFileName,"UTF-8");
			
			// keep getting the next token until we get a null
			while((tokenPair = scan.getNextToken()) != null) {
				// check to see if the token is an identifer but not main
				if(tokenPair.getKey() == TokenNames.Identifiers && !tokenPair.getValue().equals("main")) {
					String newName = "cs512" + tokenPair.getValue();
					writer.print(newName);
					tokens.add(tokenPair);
				}
				else if(tokenPair.getKey() == TokenNames.Identifiers || tokenPair.getKey() == TokenNames.ReserveWord || tokenPair.getKey() == TokenNames.Numbers || tokenPair.getKey() == TokenNames.String || tokenPair.getKey() == TokenNames.Symbol) {
					// just add it to the output with out modifying the values
					writer.print(tokenPair.getValue());
					tokens.add(tokenPair);
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not create output file");
			System.exit(1);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Error encoding output file.  Not my fault though");
			System.exit(1);
		}
		
		
		

	}

}
