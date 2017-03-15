import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;import java.io.FileInputStream;
import java.io.InputStream;

public class Scanners {
	
	//Creating a list of keywords
	
	HashSet<String> initializeKeywords(){
		HashSet<String> keywords= new HashSet<String>();
		keywords.add("main");
		keywords.add("int");
		keywords.add("void");
		keywords.add("if");
		keywords.add("while");
		keywords.add("return");
		keywords.add("read");
		keywords.add("write");
		keywords.add("print");
		keywords.add("continue");
		keywords.add("break");
		keywords.add("binary");
		keywords.add("decimal");
		
		
		
		return keywords;
		
	}
	
	// list of symbols
	
	HashSet<Character> initializeSymbols(){
		HashSet<Character> symbols= new HashSet<Character>();
		
		symbols.add('{');
		symbols.add('}');
		symbols.add('(');
		symbols.add(')');
		symbols.add('[');
		symbols.add(']');
		symbols.add(',');
		symbols.add(';');
		symbols.add('+');
		symbols.add('-');
		symbols.add('*');
		symbols.add('/');
		
	
		
		
		
		return symbols;
	}
	
	HashSet<Character> initializeOtherSymbols1(){
		HashSet<Character> otherSymbol= new HashSet<Character>();
		otherSymbol.add('=');
		otherSymbol.add('!');
		otherSymbol.add('>');
		otherSymbol.add('<');
		otherSymbol.add('&');
		otherSymbol.add('|');
		
		
		return otherSymbol;
	}
	
	//valid symbols that can occur twice
	HashSet<Character> initializeOtherSymbols2(){
		HashSet<Character> otherSymbol= new HashSet<Character>();
		otherSymbol.add('=');
		otherSymbol.add('|');
		otherSymbol.add('&');
		
		
		return otherSymbol;
	}
	
	public static void main (String[] args){
		ArrayList<String> tokens = new ArrayList<String>();
		ArrayList<String> printingList = new ArrayList<String>();
		HashSet<String> identifier = new HashSet<String>();
		HashSet<String> keywords = new HashSet<String>();
		HashSet<Character> symbols = new HashSet<Character>();
		HashSet<Character> otherSymbol1 = new HashSet<Character>();
		HashSet<Character> otherSymbol2 = new HashSet<Character>();
		boolean error=false;
		boolean notDouble=false;
		String temp="";
		
		Scanners scan = new Scanners();
		keywords=scan.initializeKeywords();
		symbols=scan.initializeSymbols();
		otherSymbol1=scan.initializeOtherSymbols1();
		otherSymbol2=scan.initializeOtherSymbols2();
		
		try {
			FileInputStream fileInput = new FileInputStream(args[0]);
			
				
				int read;
				read=fileInput.read();
				char c = (char) read;
				
				
				while(read !=-1 && !error){
					
					if(c == '#'){
						
						temp="";
						while(c != '\n'){
							
							if(c=='\"' ){
								
								temp+=c;
								while(c != '\"'){
									temp+=c;
									read=fileInput.read();
									c =(char)read;
									
								}
							}
							temp+=c;
							read=fileInput.read();
							c =(char)read;
							
						}
		
						tokens.add(temp);
						printingList.add(temp);
					
						
		
					         
				
					}
					else if(c=='/'){
						temp="";
						temp+=c;
						read=fileInput.read();
						c =(char)read;
						if(c=='/'){
							
							while(c != '\n'){
								
								if(c=='\"' ){
									
									temp+=c;
									while(c != '\"'){
										temp+=c;
										read=fileInput.read();
										c =(char)read;
										
									}
								}
								temp+=c;
								read=fileInput.read();
								c =(char)read;
								
							}
			
							tokens.add(temp);
							printingList.add(temp);
						
					    }
						else{
						
							read=fileInput.read();
							c = (char) read;

							tokens.add(temp);
							printingList.add(temp);
					
						}
					}
					else if(c=='\"' ){
		
					    temp="";
						temp+=c;
						while(c != '\"'){
							temp+=c;
							read=fileInput.read();
							c =(char)read;
							
						}
		
						tokens.add(temp);
						printingList.add(temp);
				
						
					}
					
					else if(Character.isLetter(c) || c == '_'){
		
						temp="";
						temp+=c;
						read=fileInput.read();
						 c = (char) read;
						
						
						while(Character.isLetter(c) || c=='_' || Character.isDigit(c)){
							
							temp+=c;
							read=fileInput.read();
							 c = (char) read;
							
						}
						if(!keywords.contains(temp)){
							identifier.add(temp);
							temp="CSC_512"+temp;
							printingList.add(temp);
							
						}
						else{
							tokens.add(temp);
							printingList.add(temp);
						}
			
						
					}
					else if (Character.isDigit(c)){
						boolean enter=false;
						temp="";
						temp+=c;
						read=fileInput.read();
						 c = (char) read;
						 while(Character.isDigit(c)){
							 temp+=c;
							 read=fileInput.read();
							 c = (char) read;
							 enter=true;
						 }
					
							tokens.add(temp);
							printingList.add(temp);
			
					}
					else if (otherSymbol1.contains(c)){
					    temp="";
						temp+=c;
						boolean gotChar=false;
						
							
							char toHandleSingleOperators=c;
							read=fileInput.read();
							 c = (char) read;
							 
							
							if(otherSymbol2.contains(c)){
								temp+=c;
							}
							else if(toHandleSingleOperators=='=' || toHandleSingleOperators=='>' || toHandleSingleOperators=='<'){
								gotChar=true;
								notDouble=true;
							}
							else{
								error=true;
								System.out.print("Error");
							}
						
						if(!gotChar){
							read=fileInput.read();
							c = (char) read;
						}
	
						tokens.add(temp);
						printingList.add(temp);
			
					}
					
					else if(symbols.contains(c)){
						
			
						
					    temp="";
						temp+=c;
					
							read=fileInput.read();
							c = (char) read;
				
						tokens.add(temp);
						printingList.add(temp);
			
						
					}
					
					
					else if(c==' ' || c=='\n'){
			
					    temp="";
						temp+=c;
						read=fileInput.read();
						c = (char) read;
						while(c== ' ' || c=='\n'){
							temp+=c;
							read=fileInput.read();
							 c = (char) read;
		
						}
		
						printingList.add(temp);
					}
					else{
						error=true;
						System.out.print("Error");
					}
				}
		}
		catch (Exception e) {
			
			e.printStackTrace();
		}
		if(error){
			System.out.print("Error!!, Please check the input"+temp);
		}
		else{
			try{
			String[] input = args[0].split("\\.");
			System.out.print(args[0]);
			File file = new File(input[0]+"_gen.c");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw;
			fw = new FileWriter(file.getAbsolutePath());			
			BufferedWriter bw = new BufferedWriter(fw);
	
		    for(int i=0; i<printingList.size();i++){
				System.out.print(printingList.get(i));
				bw.write(printingList.get(i));
			}
		    bw.close();

			System.out.println("Done");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	
	

}
