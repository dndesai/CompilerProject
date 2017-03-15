import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;













public class Parser {
	
	
		public StringBuilder ir_code = new StringBuilder();
		private String nextexpression;
		private static String newFileName;
		
		public String fetchRef() {
	    	return nextexpression;
	    }
		 
		public String myIr() {
			String result = ir_code.toString();
	    	ir_code = new StringBuilder();			
	    	return result;
	    }
		
		public void code_gen(String s) {
	    	if (s.length() > 0) {
	    		
	    		ir_code.append(s);
	    		
	    	}
	    }
		
		public void setAccess(String s) {
	    	nextexpression = s;
	    	
	    }
		
		 public boolean checkflag;
		public boolean globalcheck;
		public int index;
		public String type;
		
		
		public String arrayreference(int may) {
        	if (index != -1) {
	        	if (globalcheck) {
	        		return "global[" + Integer.toString(may) + "]";
	        	}
	        	return "local[" + Integer.toString(may) + "]";
        	}
        	return "nulll";
        }
		public String gettype() {
			return type;
		}
		public void settype(String stringtype)
		{
			type = stringtype;
		}
		public int getIndex() {
        	return index;
        }
		
		public boolean globalcheck() {
        	return globalcheck;
        }
		
	
	
	
		ArrayList<String> entryloops = new ArrayList<String>(); ;
		ArrayList<String> exitloop = new ArrayList<String>();
		
	
		private void labeling(String start, String end) {
			entryloops.add(start);
			exitloop.add(end);
		}
		
	private ArrayList<String> globalstorage  = new ArrayList<String>();
	private ArrayList<String> localstorage = new ArrayList<String>();
	
	private int labelnum= 0 ;
	
	private boolean localvarisfac;
	
	public Parser(String fileName) throws FileNotFoundException, IOException {
		
		
		
		
		
	}
	
		
		
	
	
	private ArrayList<String> intracode = new ArrayList<String>();
	
	
	public int locatePos(String code, boolean isArray) {
		if (isArray) {
			
			code += "[0]"; 
		}
		
		
		for (int i = 0; i < localstorage.size(); i++) {
			
			if (localstorage.get(i).equals(code)) {
				globalcheck = false;
				return i;
			}
		}
		
		for (int i = 0; i < globalstorage.size(); i++) {
			if (globalstorage.get(i).equals(code)) {
				globalcheck =true;
				return i;
			}
		}
		
		
		checkflag = false;
		mystorage(code);	
		return localstorage.size()-1;
		
	}
	
	
		
	private String mystorage(String s)  {
		String code = "";
		
		if (checkflag) {
			
			globalstorage.add(s);
			
			 
			code += " " + "global[" + Integer.toString(globalstorage.size() - 1) + "] = " + s + ";";
		} else {
			
			localstorage.add(s);
			
			code += " " + "local[" + Integer.toString(localstorage.size() - 1) + "] = " + s + ";";
		}
		
		return code; 
	}
	
	
	
	
	
	
	int j = 0;
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length < 1) {
			System.err.println("file not found");
			System.exit(1);
		}

		String fileName = args[0];
		newFileName = args[0];
		Parser parser = new Parser(fileName);
		Proscanner.proscan(fileName);

		parser.program();

	}

	public void fail() {
		System.out.println("Fail");
		

	}

	public void pass() {
		
		try{
			String[] input = newFileName.split("\\.");
			
			File file = new File(input[0]+"_gen.c");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw;
			fw = new FileWriter(file.getAbsolutePath());			
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0; i<Proscanner.metatokens.size();i++){
				bw.write(Proscanner.metatokens.get(i).getValue());
			}
				
			for(int i=0; i<intracode.size();i++){
				bw.write(intracode.get(i));	
				}
		    bw.close();

			
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
		
		
	}
	
	
	public boolean program() {
		
		if (Proscanner.tokens.get(j).getValue().equals("int") 
				|| Proscanner.tokens.get(j).getValue().equals("void")
				|| Proscanner.tokens.get(j).getValue().equals("binary")
				|| Proscanner.tokens.get(j).getValue().equals("decimal"))
		 {
			String datatype = Proscanner.tokens.get(j).getValue();
			settype(datatype);
			j++;
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				
				
				return false;

			} 
			String id = Proscanner.tokens.get(j).getValue();
			
			j++;
			
			if (!programprime(datatype, id)) {
				fail();
				return false;
			}
			pass();
			return true;

		}

		else {
			fail();

			return false;
		}
	}

	public boolean programprime(String datatype, String id) {
		
		if (Proscanner.tokens.get(j).getValue().equals("EOF")) { //checks if last element is arraylist is eof and all tokens are consumed calls pass

			return true;
		}
		
		else if (datadeclsprime(id, true)) {
			
			
				return true;
				
		} else if (Proscanner.tokens.get(j).getValue().equals("(")) {
			if (globalstorage.size() > 0) {
				
				intracode.add("int global[" + globalstorage.size() + "];" + " ");
					
				
			}
			
			intracode.add(" " + gettype() + " " + id + " ");
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			j++;
			
			if (!parameterlist()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				
				return false;
			} 
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			j++;
			
			
			
			if (!funcprime()) {
				return false;
			}
			
			localstorage.clear();
			
			if (!funclist()) {
				return false;
			}
			return true;
		}

		 
		else {
			return false;
		}
	}

	public boolean datadeclsprime(String id, boolean globalcheck) {
		
		if (idprime(id, globalcheck)) {
			
			
			if (!idlistprime(globalcheck)) {
				return false;
			}
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				
				return false;
			} 
			j++;
			
			
			
			if (!datadeclsdoubleprime()) {
				return false;
			}
			
			
						
			return true;
		
		} else if (Proscanner.tokens.get(j).getValue().equals(",")) {
			j++;
			
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				return false;
				
				
		
				
			}
			j++;
			
			if (!idprime(id, globalcheck)) {
				return false;
			}
			if (!idlistprime(globalcheck)) {
				return false;
			}
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				
				return false;
			} 
			j++;
			if (!datadeclsdoubleprime()) {
				return false;
			}
			return true;
		
		} else {
			return false;
		}
	}

	public boolean datadeclsdoubleprime() {
		
		if (Proscanner.tokens.get(j).getValue().equals("EOF")) {

			return true;
		}
		else if (typename()) {
			
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				
				
				return false;

			} 
			
			String id = Proscanner.tokens.get(j).getValue();
			
			j++;
			String datatype =gettype();
			if (!programprime( datatype, id)) {

				return false;
			}
			
			return true;

		}
		else {
			return false;
		}
	}

	

	public boolean funclist() {
		
		if (Proscanner.tokens.get(j).getValue().equals("EOF")) {
			
			return true;
		}
		else if (typename()) {

			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				
				return false;
			}
			
			intracode.add(" " + gettype() + " " + Proscanner.tokens.get(j).getValue());
			
			j++;
			if (!Proscanner.tokens.get(j).getValue().equals("(")) {
				return false;
			}
				
			
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
				j++;
				
				if (!parameterlist()) {
					return false;
				}
				
				if (!Proscanner.tokens.get(j).getValue().equals(")")) {
					
					return false;
				} 
				intracode.add(" " + Proscanner.tokens.get(j).getValue());
				j++;
			
			if (!funcprime()) {
				return false;
			}
			localstorage.clear();
			if (!funclist()) {
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}



	public boolean func() {
		
		if (funcdecl()) {
			if (!funcprime()) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean funcprime() {  
		
		if (Proscanner.tokens.get(j).getValue().equals(";")) {
			intracode.add(" " + Proscanner.tokens.get(j).getValue() + " ");
			j++;
			localstorage.clear();
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("{")) {
			intracode.add(" " + Proscanner.tokens.get(j).getValue() + " ");
			j++;
			
			
			ArrayList<String> insidefunc = new ArrayList<String>();				
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < localstorage.size(); i++) {
				
				sb.append("local[" + Integer.toString(i) + "] = " + localstorage.get(i) + ";");
				
			}	
		
			insidefunc.add(sb.toString());
			
			if (!datadecls(false)) {
				return false;
			}
			
			
			
			if (!statements()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(j).getValue().equals("}")) {
				
				return false;
			}
			
			if (localstorage.size() > 0) {
				insidefunc.add(0, "int local[" + Integer.toString(localstorage.size()) + "];");
			}
			
			
			insidefunc.add(myIr());
			
			
			intracode.addAll(insidefunc);
			
			intracode.add(" " + Proscanner.tokens.get(j).getValue() +  " ");
			
			j++;
			
			return true;
		
		}
		
		else {
			return false;
		}
	}

	public boolean datadecls(boolean globalcheck) {
		
		
		if (Proscanner.tokens.get(j).getValue().equals("int") 
				|| Proscanner.tokens.get(j).getValue().equals("void")
				|| Proscanner.tokens.get(j).getValue().equals("binary")
				|| Proscanner.tokens.get(j).getValue().equals("decimal")) {
			j++;
			
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				return false;
			}
			
			
			String id = Proscanner.tokens.get(j).getValue();
			
			
			j++;			
			
			if (!idprime(id, globalcheck)) {
				return false;
			}
			
			if (!idlistprime(globalcheck)) {
				return false;
			}
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				
				return false;
			} 
			j++;
			
			if (!datadecls(globalcheck)) {
				return false;
			}
			return true;
		} else if ((Proscanner.tokens.get(j).getKey()) == TokenNames.Identifiers
				|| Proscanner.tokens.get(j).getValue().equals("if")
				|| Proscanner.tokens.get(j).getValue().equals("while")
				|| Proscanner.tokens.get(j).getValue().equals("return")
				|| Proscanner.tokens.get(j).getValue().equals("continue")
				|| Proscanner.tokens.get(j).getValue().equals("break")
				|| Proscanner.tokens.get(j).getValue().equals("read")
				|| Proscanner.tokens.get(j).getValue().equals("write")
				|| Proscanner.tokens.get(j).getValue().equals("print")
				|| Proscanner.tokens.get(j).getValue().equals("}")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean funcdecl() {
		
		if (typename()) {

			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				
				return false;
			}
			
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			
			j++;
			if (!funcdeclprime()) {
				return false;
			} 
			
			
			return true;
		} else {
			return false;
		}
	}

	public boolean funcdeclprime() {
		
		
		localstorage.clear();
		if (Proscanner.tokens.get(j).getValue().equals("(")) {
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			j++;
			
			if (!parameterlist()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				
				return false;
			} 
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			j++;
			
			return true;
		} else {
			return false;
		}
	} 

	public boolean typename() {
		
		if (Proscanner.tokens.get(j).getValue().equals("int") 
				|| Proscanner.tokens.get(j).getValue().equals("void")
				|| Proscanner.tokens.get(j).getValue().equals("binary")
				|| Proscanner.tokens.get(j).getValue().equals("decimal")) {
			settype(Proscanner.tokens.get(j).getValue());
			
			
			j++;
			
			return true;
		} else {
			return false;
		}

	}

	public boolean parameterlist() {
		
		if (Proscanner.tokens.get(j).getValue().equals("void")) {
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			j++;
			if (!parameterlistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("int")
				|| Proscanner.tokens.get(j).getValue().equals("binary")
				|| Proscanner.tokens.get(j).getValue().equals("decimal")) {
			
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			j++;
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				
				return false;
			} 
			
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			
			checkflag = false;
			mystorage(Proscanner.tokens.get(j).getValue()); 
						
			j++;
			
			if (!nonemptylistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals(")")) {
			
			
			
			return true;
		} else {
			return false;
		}

	}

	public boolean parameterlistprime() {
		
		if ((Proscanner.tokens.get(j).getKey()) == TokenNames.Identifiers) {
			intracode.add(" " + Proscanner.tokens.get(j).getValue());
			
			
				checkflag = false;
				mystorage(Proscanner.tokens.get(j).getValue());
						
			j++;

			return nonemptylistprime();
		} else if (Proscanner.tokens.get(j).getValue().equals(")")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean nonemptylist() {
		
		if (typename()) {

			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				
				return false;

			} 
			j++;

			if (!nonemptylistprime()) {
				return false;
			}
			return true;
		} else {
			return false;
		}

	}

	public boolean nonemptylistprime() {
		
		if (Proscanner.tokens.get(j).getValue().equals(",")) {
			
			intracode.add(Proscanner.tokens.get(j).getValue());
			
			j++;
			if (!typename()) {
				return false;
			}
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				
				return false;

			} 
			intracode.add(" " + gettype() );
			intracode.add(" " + Proscanner.tokens.get(j).getValue() );
			
			
			checkflag = false;
			mystorage(Proscanner.tokens.get(j).getValue());
			
			
			j++;
			if (!nonemptylistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals(")")) {
			
			return true;
		} else {
			return false;
		}

	}

	

	public boolean idlistprime(boolean globalcheck) {
		
		if (Proscanner.tokens.get(j).getValue().equals(",")) {
			j++;
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				return false;
			}
			
			
			String id = Proscanner.tokens.get(j).getValue() ;
			
			
			j++;
			
			if (!idprime(id, globalcheck)) {
				return false;
			}
			
			return idlistprime(globalcheck);
			
			
			
		} else if (Proscanner.tokens.get(j).getValue().equals(";")) {
			
			return true;
		} else {
			return false;
		}
	}

	

	public boolean idprime(String id, boolean globalcheck) {
		
		if (Proscanner.tokens.get(j).getValue().equals("[")) {
			j++;
			
			
			if (!factor()) {
				return false;
			}
			
			if (!termprime()) {
				return false;
			}
			
			if (!expressionprime()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(j).getValue().equals("]")) {
				return false;
			}
			j++;
			
			
				
				int arraySize = Integer.parseInt(fetchRef());
				
				for (int i = 0; i < arraySize; i++) {
					checkflag = globalcheck;
					mystorage(id + "[" + Integer.toString(i) + "]");
					
				
			}

			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals(",") || Proscanner.tokens.get(j).getValue().equals(";")
				|| Proscanner.tokens.get(j).getValue().equals("=")) {
			checkflag = globalcheck;
			
			mystorage(id);
			
			return true;
		} else {
			return false;
		}
	}

	
	public boolean statements() {
		
		
		if ((Proscanner.tokens.get(j).getKey()) == TokenNames.Identifiers) {
			
			String id = Proscanner.tokens.get(j).getValue() ;
			
			j++;
			
			
			if (!statemntprime(id)) {
				return false;
			}
			
			
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("if")) {
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals("(")) {
				return false;
			}
			j++;
			
			
			
			
			
			if (!conditionexpres()) {
				return false;
			}
			
			
			
			String condition_expression = fetchRef();
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				return false;
			}
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals("{")) {
				return false;
			}
			j++;
			
			
			 String ifLabel = "c" + Integer.toString(++labelnum);
			String nextLabel = "c" +  Integer.toString(++labelnum); 
			
			// Add code for the if statement.
			ir_code.append( "if ( "  + condition_expression  + " ) goto " +  ifLabel + ";"
					+ "goto " +  nextLabel + ";" +  ifLabel + ": ;");
			
			
			
			if (!statements()) {
				return false;
			}
			
			ir_code.append(" " + nextLabel + ": ;");
			
			if (!Proscanner.tokens.get(j).getValue().equals("}")) {
				return false;
			}			
			
			
			j++;
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("while")) {
			
			String entryloop = "c" + Integer.toString(++labelnum);
			String ifLabel = " c" + Integer.toString(++labelnum);
			String nextLabel = " c" + Integer.toString(++labelnum);
			
			
			labeling(entryloop, nextLabel);
			
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals("(")) {
				return false;
			}
			j++;
			
			
			ir_code.append( entryloop + ": ;");
			
			
			if (!conditionexpres()) {
				return false;
			}
			
			
			String condition_expression = fetchRef();
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				return false;
			}
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals("{")) {
				return false;
			}
			j++;
			
			
			ir_code.append(  "if ( " + condition_expression + " ) goto " + ifLabel + ";" + "goto " + nextLabel + ";" + ifLabel + ": ;" );
		
			
			
			if (!statements()) {
				return false;
			}
			
			
			ir_code.append( "goto " + entryloop + ";" + nextLabel + ": ;");
			
			
			if (!Proscanner.tokens.get(j).getValue().equals("}")) {
				return false;
			}
			
			
			entryloops.remove(entryloops.size() - 1);
			exitloop.remove(exitloop.size() - 1);
			
			
			j++;
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("return")) {
			j++;
			
			
			if (!returnprime()) {
				return false;
			}
			
			
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("break")) {
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
			
			if (entryloops.size() > 0 && exitloop.size() > 0) {
				String loopEnd = exitloop.get(exitloop.size() - 1);
				ir_code.append( "goto " + loopEnd + ";");
			} else {
				System.out.println("error in break");
				
			}
			
			j++;
			
			
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("continue")) {
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
			
			if (entryloops.size() > 0 && exitloop.size() > 0) {
				String entryloop = entryloops.get(entryloops.size() - 1);
				ir_code.append( "goto " + entryloop + ";");
			} else {
				System.out.println("error in continue ");
				System.exit(1);
			}
			
			j++;
			
			
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("read")) {
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals("(")) {
				return false;
			}
			j++;
			
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Identifiers) {
				return false;
			}
			
			
			String id = Proscanner.tokens.get(j).getValue();
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				return false;
			}
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
			
			int pos = locatePos(id, false);
			
			
			ir_code.append(" " + "read ( " + arrayreference(pos) + " );");
			
			j++;
			
			
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("write")) {
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals("(")) {
				return false;
			}
			j++;
			
			
			if (!expression()) {
				return false;
			}
			
			
			String writest = fetchRef();
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				return false;
			}
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
			
			ir_code.append(" " + "write ( " + writest + " );");
			
			j++;
			
			
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("print")) {
			j++;
			if (!Proscanner.tokens.get(j).getValue().equals("(")) {
				return false;
			}
			j++;
			
			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.String) {
				return false;
			}
			
			
			String printst = Proscanner.tokens.get(j).getValue() ;
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				return false;
			}
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
		ir_code.append(" " + "print ( " + printst + " );");
			
			j++;
			
			
			return statements();
		} else if (Proscanner.tokens.get(j).getValue().equals("}")) {
			
			return true;
		} else {
			return false;
		}

	}

	

	public boolean statemntprime(String id) {
		
		if (Proscanner.tokens.get(j).getValue().equals("=")) {
			j++;
			
			// Generated code for expression.
			if (!factor()) {
				return false;
			}
			
			if (!termprime()) {
				return false;
			}
			
			if (!expressionprime()) {
				return false;
			}
			
			
			String rightchild = fetchRef();
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
			int pos = locatePos(id, false);
			globalcheck= false;
			
			ir_code.append(" " + arrayreference(pos) + " = " + rightchild + ";");		
			
			j++;			
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("[")) {
			j++;
			
			
			if (!factor()) {
				return false;
			}
			if (!termprime()) {
				return false;
			}
			if (!expressionprime()) {
				return false;
			}
			
			
			String arrayOffset = fetchRef();
			
			if (!Proscanner.tokens.get(j).getValue().equals("]")) {
				return false;
			}
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals("=")) {
				return false;
			}
			j++;
			
			
			
			if (!factor()) {
				return false;
			}
			if (!termprime()) {
				return false;
			}
			if (!expressionprime()) {
				return false;
			}
			
			String  rightchild = fetchRef();
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
			
			checkflag = false;
			int indi = locatePos(id, true);
			ir_code.append( mystorage(Integer.toString(indi) + " + " + arrayOffset));
			
			
			String toPrint;
			
			if (globalcheck()) {
				toPrint = "global";
			} else {
				toPrint = "local";
			}
			
			toPrint += "[" + "local[" + Integer.toString(localstorage.size() - 1) + "]"  + "]";			
			
			
			code_gen(" " + toPrint + " = " + rightchild + ";");
			
			j++;		
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("(")) {
			j++;
			
			
			localvarisfac = true;
			
			
			if (!exprlist()) {
				return false;
			}
			
			
			String parameters = fetchRef();
			
			
			localvarisfac = false;
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				return false;
			}
			j++;
			
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {
				return false;
			}
			
			
			ir_code.append(  id + " ( " + parameters + " );");
			
			j++;			
			return true;
		} else {
			return false;
		}
	}

	

	public boolean exprlist() {
		
		if (factor()) {
			if (!termprime()) {
				return false;
			}
			if (!expressionprime()) {
				return false;
			}
			if (!nonemptyexprlistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals(")")) {
			
			setAccess("");
			return true;
		} else {
			return false;
		}
	}

	

	public boolean nonemptyexprlistprime() {
		
		if (Proscanner.tokens.get(j).getValue().equals(",")) {
			j++;
			
			
			String ongoing = fetchRef();
						
			if (!expression()) {

				return false;
			}
			
			
			setAccess(ongoing + ", " + fetchRef());
			if (!nonemptyexprlistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals(")")) {
			

			return true;
		} else {
			return false;
		}

	}

	
	public boolean conditionexpres() {
		
		if (condition()) {
			return conditionexpresprime();
		} else {
			return false;
		}
	}

	public boolean conditionexpresprime() {
		
		if (Proscanner.tokens.get(j).getValue().equals("&&") || Proscanner.tokens.get(j).getValue().equals("||")) {
			
			String doublesign = Proscanner.tokens.get(j).getValue();
			
			j++;
			
			
			String leftchild = fetchRef();
			
			
			
			if (condition()) {
				
				String rightchild = fetchRef();
				checkflag = false;
				
				ir_code.append(mystorage("( " + leftchild + " " + doublesign + " " + rightchild + " )"));
				setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
				return true;
			}
			return false;
		} else if (Proscanner.tokens.get(j).getValue().equals(")")) {
			
			return true;
		} else {
			return false;
		}
	}



	public boolean condition() {
		
		if (expression()) {
			return condition_prime();
		
		} else {
			return false;
		}
	}
	
	public boolean condition_prime() {
		
		if (Proscanner.tokens.get(j).getValue().equals("==") || Proscanner.tokens.get(j).getValue().equals("!=")
				|| Proscanner.tokens.get(j).getValue().equals(">") || Proscanner.tokens.get(j).getValue().equals(">=")
				|| Proscanner.tokens.get(j).getValue().equals("<") || Proscanner.tokens.get(j).getValue().equals("<=")) {
			
			
			String operator = Proscanner.tokens.get(j).getValue();
			
			j++;
			
			
			String leftchild =fetchRef();
			
			
			if (!factor()) {
				return false;
			}
			
			if (!termprime()) {
				return false;
			}
			
			if (!expressionprime()) {
				return false;
			}
			 
				
				String rightchild = fetchRef();
				checkflag = false;
				ir_code.append(mystorage("( " + leftchild + " " + operator + " " + rightchild + " )"));
				
				setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
				
				return true;
			
							
		} else {
			return false;
		}
	}
	
	 public boolean comparisonop() {
		 
		if (Proscanner.tokens.get(j).getValue().equals("==") 
				|| Proscanner.tokens.get(j).getValue().equals("!=")
				|| Proscanner.tokens.get(j).getValue().equals(">") 
				|| Proscanner.tokens.get(j).getValue().equals(">=")
				|| Proscanner.tokens.get(j).getValue().equals("<")
				|| Proscanner.tokens.get(j).getValue().equals("<=")) {
			String op = Proscanner.tokens.get(j).getValue();
			j++;
			String lhsnextexpression = fetchRef();
			return true;
		
		} else {
			return false;
		}

	}  

	

	public boolean returnprime() {				//counting statements at end of semicolon
		
		if (factor()) {
			
		
		if (!termprime()) {
			return false;
		}
		
		if (!expressionprime()) {
			return false;
		}
		 
			if (!Proscanner.tokens.get(j).getValue().equals(";")) {

				
				return false;
			} 
			
			
					checkflag = false;
						
					ir_code.append(mystorage(fetchRef()) + " " + "return " + "local[" + Integer.toString(localstorage.size() - 1) + "]" + ";");			
					 
						
			j++;
			
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals(";")) {
			
			
				ir_code.append(" " + "return;");
			j++;
			
			return true;
		} else {
			return false;
		}
	}

	

	public boolean expression() {
		
		if (term()) {
			return expressionprime();
		} else {
			return false;
		}
	}

	public boolean expressionprime() {
		
		if (Proscanner.tokens.get(j).getValue().equals("+") || Proscanner.tokens.get(j).getValue().equals("-")) {
			
			
			String op = Proscanner.tokens.get(j).getValue();
			j++;
			
			
				String leftchild = fetchRef();
						
						
			if (!term()) {
				return false;
			}
			
			
			String rightchild = fetchRef();
			checkflag = false;
						
						ir_code.append(mystorage(leftchild + " " + op + " " + rightchild));
					setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
					
			if (!expressionprime()) {
				return false;
			}
			
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("]") 
				|| Proscanner.tokens.get(j).getValue().equals(",") 
				|| Proscanner.tokens.get(j).getValue().equals(")") 
				|| Proscanner.tokens.get(j).getValue().equals(";") 
				|| Proscanner.tokens.get(j).getValue().equals("==") 
				|| Proscanner.tokens.get(j).getValue().equals("!=")
				|| Proscanner.tokens.get(j).getValue().equals(">") 
				|| Proscanner.tokens.get(j).getValue().equals(">=")
				|| Proscanner.tokens.get(j).getValue().equals("<")
				|| Proscanner.tokens.get(j).getValue().equals("<=")
				|| Proscanner.tokens.get(j).getValue().equals("&&")
				|| Proscanner.tokens.get(j).getValue().equals("||")) {
			
			
			return true;
		} else {
			return false;
		}

	}

	

	public boolean term() {
		
		if (factor()) {
			
			return termprime();
		} else {
			return false;
		}
	}

	public boolean termprime() {
		
		if (Proscanner.tokens.get(j).getValue().equals("*") || Proscanner.tokens.get(j).getValue().equals("/")) {
			
			String op = Proscanner.tokens.get(j).getValue();
			
			j++;
			
			
			 String leftchild =fetchRef();
			
			if (!factor()) {
				return false;
			}
			
			String rightchild = fetchRef();
			
			checkflag = false;
			
			ir_code.append(mystorage(leftchild + " " + op + " " + rightchild));
			
		setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
	
			if (!termprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("+") 
				|| Proscanner.tokens.get(j).getValue().equals("-") 
				|| Proscanner.tokens.get(j).getValue().equals("]") 
				|| Proscanner.tokens.get(j).getValue().equals(",") 
				|| Proscanner.tokens.get(j).getValue().equals(")") 
				|| Proscanner.tokens.get(j).getValue().equals(";") 
				|| Proscanner.tokens.get(j).getValue().equals("==") 
				|| Proscanner.tokens.get(j).getValue().equals("!=")
				|| Proscanner.tokens.get(j).getValue().equals(">") 
				|| Proscanner.tokens.get(j).getValue().equals(">=")
				|| Proscanner.tokens.get(j).getValue().equals("<")
				|| Proscanner.tokens.get(j).getValue().equals("<=")
				|| Proscanner.tokens.get(j).getValue().equals("&&")
				|| Proscanner.tokens.get(j).getValue().equals("||")) {
			
			return true;
		} else {
			return false;
		}

	}

	
	public boolean factor() {
		
		if ((Proscanner.tokens.get(j).getKey()) == TokenNames.Identifiers) {
			
			
				String id = Proscanner.tokens.get(j).getValue();
			j++;
			
			return factorprime(id);
			
		} else if ((Proscanner.tokens.get(j).getKey()) == TokenNames.Numbers) {
			
			
					if (localvarisfac) {
						checkflag = false;
							
							
							ir_code.append(mystorage(Proscanner.tokens.get(j).getValue()));
							setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
							
					} else {
							
						
							setAccess(Proscanner.tokens.get(j).getValue());
							
					}
			j++;
			
			return true;

		} else if (Proscanner.tokens.get(j).getValue().equals("-")) {
			j++;

			if ((Proscanner.tokens.get(j).getKey()) != TokenNames.Numbers) {
				return false;
			}
			
			
						if (localvarisfac) {
							checkflag = false;
							
						ir_code.append(mystorage("-" + Proscanner.tokens.get(j).getValue() ));
							setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
						} else {
							
						setAccess("-" + Proscanner.tokens.get(j).getValue());
						}
						
			j++;
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("(")) {
			j++;

			if (!factor()) {
				return false;
			}
			
			if (!termprime()) {
				return false;
			}
			
			if (!expressionprime()) {
				return false;
			}
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				
				return false;
			} 
			j++;

			return true;
		} else {
			return false;
		}
	}

	public boolean factorprime(String id) {
		
		if (Proscanner.tokens.get(j).getValue().equals("[")) {
			j++;
			if (!factor()) {
				return false;
			}
			
			if (!termprime()) {
				return false;
			}
			
			if (!expressionprime()) {
				return false;
			}
			
					String actualexpression = fetchRef();
			if (!Proscanner.tokens.get(j).getValue().equals("]")) {
				
				return false;
			} 
			j++;
			
			
						checkflag = false;
						
						int posi = locatePos(id, true);
						ir_code.append(mystorage(Integer.toString(posi) + " + " + actualexpression));
						
						
						String arraycase;
						if (globalcheck()) {
							arraycase = "global";
						} else {
							arraycase = "local";
						}
						
						arraycase += "[" + "local[" + Integer.toString(localstorage.size() - 1) + "]" + "]";
						checkflag = false;
						
						ir_code.append(mystorage(arraycase));
				setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
						
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("(")) {
			j++;
			if (!exprlist()) {
				return false;
			}
			
			
			if (!Proscanner.tokens.get(j).getValue().equals(")")) {
				
				return false;
			} 
			j++;
			checkflag = false;
			
			
			ir_code.append(mystorage(id + " ( " + fetchRef()  + " ) "));
			setAccess("local[" + Integer.toString(localstorage.size() - 1) + "]");
			
			return true;
		} else if (Proscanner.tokens.get(j).getValue().equals("+") 
				|| Proscanner.tokens.get(j).getValue().equals("*") 
				|| Proscanner.tokens.get(j).getValue().equals("/") 
				|| Proscanner.tokens.get(j).getValue().equals("-") 
				|| Proscanner.tokens.get(j).getValue().equals("]") 
				|| Proscanner.tokens.get(j).getValue().equals(",") 
				|| Proscanner.tokens.get(j).getValue().equals(")") 
				|| Proscanner.tokens.get(j).getValue().equals(";") 
				|| Proscanner.tokens.get(j).getValue().equals("==") 
				|| Proscanner.tokens.get(j).getValue().equals("!=")
				|| Proscanner.tokens.get(j).getValue().equals(">") 
				|| Proscanner.tokens.get(j).getValue().equals(">=")
				|| Proscanner.tokens.get(j).getValue().equals("<")
				|| Proscanner.tokens.get(j).getValue().equals("<=")
				|| Proscanner.tokens.get(j).getValue().equals("&&")
				|| Proscanner.tokens.get(j).getValue().equals("||")) {
			
			
			    int ind = locatePos(id, false);
			
				setAccess(arrayreference(ind));	
				
			return true;
		} else {
			return false;
		}
	}

}
