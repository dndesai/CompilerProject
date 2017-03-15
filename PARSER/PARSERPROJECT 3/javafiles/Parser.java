
// whenever a token is parsed it is removed from arraylist hence when arraylistv gets empty it reports pass
public class Parser {
	int i = 0;
	// counters for variables, functions, statements
	int varcount = 0; 
	int funcount = 0;
	int statecount = 0;
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Please provide an input file to process");
			System.exit(1);
		}

		String fileName = args[0];
		Parser parser = new Parser();
		Proscanner.proscan(fileName);

		parser.program();

	}

	public void fail() {
		System.out.println("Fail");
		

	}

	public void pass() {
		System.out.println("Pass. ");
		System.out.print("variables  ");
		System.out.println(varcount);
		System.out.print("functions  ");
		System.out.println(funcount);
		
		System.out.print("statements  ");
		System.out.println(statecount);
	}
	
	
	public boolean program() {
		
		if (typename()) {
			
			
			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Identifiers) {
				
				
				
				return false;

			} 
			Proscanner.tokens.remove(0);
			
			if (!programprime()) {
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

	public boolean programprime() {
		
		if (Proscanner.tokens.isEmpty()) { //checks if arraylist is empty and all tokens are consumed calls pass

			return true;
		}
		
		else if (datadeclsprime()) {
			
			return true;
		} else if (funclistprime()) {

			return true;
		}

		 
		else {
			return false;
		}
	}

	public boolean datadeclsprime() {
		
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			Proscanner.tokens.remove(0);
			
			if (!expression()) {
				return false;
			}
			if (!Proscanner.tokens.get(i).getValue().equals("]")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!idlistprime()) {
				return false;
			}
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!datadeclsdoubleprime()) {
				return false;
			}
			return true;
		
		} else if (Proscanner.tokens.get(i).getValue().equals(",")) {
			Proscanner.tokens.remove(0);
			if (!id()) {
				return false;
			}
			if (!idlistprime()) {
				return false;
			}
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!datadeclsdoubleprime()) {
				return false;
			}
			return true;
		
		} else {
			return false;
		}
	}

	public boolean datadeclsdoubleprime() {
		
		if (Proscanner.tokens.isEmpty()) {

			return true;
		}
		else if (typename()) {
			
			
			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Identifiers) {
				
				
				return false;

			} 
			Proscanner.tokens.remove(0);
			
			if (!programprime()) {

				return false;
			}
			
			return true;

		}
		else {
			return false;
		}
	}

	public boolean datafuncdecl() {
		
		if (datadeclsprime()) {
			return true;
		} else if (funclistprime()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean funclist() {
		
		if (Proscanner.tokens.isEmpty()) {
			
			return true;
		}
		else if(func()) {
			if (!funclist()) {
				return false;
			}
			return true;
		}
		else {
			return false;
		}
	}

	public boolean funclistprime() {
		
		if (funcdeclprime()) {
			
			if (!funcprime()) {
				return false;
			}
			
			if (!funclist()) {
				return false;
			}
			return true;
		
		} else {
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

	public boolean funcprime() {       //counts functions at end of rightbrace
		
		if (Proscanner.tokens.get(i).getValue().equals(";")) {
			Proscanner.tokens.remove(0);
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("{")) {
			
			Proscanner.tokens.remove(0);
			
			if (!datadecls()) {
				return false;
			}

			if (!statements()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals("}")) {
				
				return false;
			}
			Proscanner.tokens.remove(0);
			funcount++;
			return true;
		
		} else {
			return false;
		}
	}

	public boolean datadecls() {
		
		
		if (typename()) {
			
			
			if (!idlist()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			if (!datadecls()) {
				return false;
			}
			return true;
		} else if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers
				|| Proscanner.tokens.get(i).getValue().equals("if")
				|| Proscanner.tokens.get(i).getValue().equals("while")
				|| Proscanner.tokens.get(i).getValue().equals("return")
				|| Proscanner.tokens.get(i).getValue().equals("continue")
				|| Proscanner.tokens.get(i).getValue().equals("break")
				|| Proscanner.tokens.get(i).getValue().equals("read")
				|| Proscanner.tokens.get(i).getValue().equals("write")
				|| Proscanner.tokens.get(i).getValue().equals("print")
				|| Proscanner.tokens.get(i).getValue().equals("}")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean funcdecl() {
		
		if (typename()) {

			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Identifiers) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!funcdeclprime()) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean funcdeclprime() {
		
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			
			Proscanner.tokens.remove(0);
			
			if (!parameterlist()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			return true;
		} else {
			return false;
		}
	}

	public boolean typename() {
		
		if (Proscanner.tokens.get(i).getValue().equals("int") || Proscanner.tokens.get(i).getValue().equals("void")
				|| Proscanner.tokens.get(i).getValue().equals("binary")
				|| Proscanner.tokens.get(i).getValue().equals("decimal")) {

			
			Proscanner.tokens.remove(0);
			
			return true;
		} else {
			return false;
		}

	}

	public boolean parameterlist() {
		
		if (Proscanner.tokens.get(i).getValue().equals("void")) {
			Proscanner.tokens.remove(0);
			if (!parameterlistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("int")
				|| Proscanner.tokens.get(i).getValue().equals("binary")
				|| Proscanner.tokens.get(i).getValue().equals("decimal")) {
			Proscanner.tokens.remove(0);
			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Identifiers) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			if (!nonemptylistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals(")")) {
			
			
			
			return true;
		} else {
			return false;
		}

	}

	public boolean parameterlistprime() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			Proscanner.tokens.remove(0);

			return nonemptylistprime();
		} else if (Proscanner.tokens.get(i).getValue().equals(")")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean nonemptylist() {
		
		if (typename()) {

			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Identifiers) {
				
				return false;

			} 
			Proscanner.tokens.remove(0);

			if (!nonemptylistprime()) {
				return false;
			}
			return true;
		} else {
			return false;
		}

	}

	public boolean nonemptylistprime() {
		
		if (Proscanner.tokens.get(i).getValue().equals(";")) {
			Proscanner.tokens.remove(0);
			if (!typename()) {
				return false;
			}
			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Identifiers) {
				
				return false;

			} 
			Proscanner.tokens.remove(0);
			if (!nonemptylistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals(")")) {
			
			return true;
		} else {
			return false;
		}

	}

	public boolean idlist() {
		
		if (id()) {
			
			if (!idlistprime()) {
				return false;
			}
			
			return true;
		} else {
			return false;
		}
	}

	public boolean idlistprime() {
		
		if (Proscanner.tokens.get(i).getValue().equals(",")) {
			Proscanner.tokens.remove(0);
			
			
			if (!id()) {
				return false;
			}
			if (!idlistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals(";")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean id() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			
			
			Proscanner.tokens.remove(0);
			varcount++;
			return idprime();
		} else {
			return false;
		}
	}

	public boolean idprime() {
		
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			Proscanner.tokens.remove(0);

			if (!expression()) {
				return false;
			}

			if (!Proscanner.tokens.get(i).getValue().equals("]")) {
				return false;
			}

			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals(",") || Proscanner.tokens.get(i).getValue().equals(";")
				|| Proscanner.tokens.get(i).getValue().equals("=")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean blockstatements() {
		
		if (Proscanner.tokens.get(i).getValue().equals("{")) {
			Proscanner.tokens.remove(0);
			
			if (!statements()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals("}")) {
				
				return false;
			} Proscanner.tokens.remove(0);
			
			return true;
		} else {
			return false;
		}
	}

	public boolean statements() {
		
	
		  if (Proscanner.tokens.get(i).getValue().equals("}")) {
			
			return true;
		} else if (stmnt()) {
			
			if (!statements()) {
				return false;
			}
			return true;
		}
		  else {
			return false;
		}

	}

	public boolean stmnt() {        //counts statements when each production of statement ends
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			
			Proscanner.tokens.remove(0);
			
			
			if (!statemntprime()) {
				return false;
			}
			
			return true;
		}

		else if (ifstmnt()) {
			return true;
		} else if (whilest()) {
			return true;
		} else if (returnst()) {
			return true;
		} else if (continuest()) {
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("read")) {
			Proscanner.tokens.remove(0);
			
			if (!Proscanner.tokens.get(i).getValue().equals("(")) {
				
				return false;
			}
			
			Proscanner.tokens.remove(0);
			
			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Identifiers) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			return true;

		}

		else if (Proscanner.tokens.get(i).getValue().equals("write")) {
			Proscanner.tokens.remove(0);
			
			if (!Proscanner.tokens.get(i).getValue().equals("(")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			
			
			if (!expression()) {

				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				
				return false;
			}  
			Proscanner.tokens.remove(0);
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			return true;

		}

		else if (Proscanner.tokens.get(i).getValue().equals("print")) {
			Proscanner.tokens.remove(0);
			if (!Proscanner.tokens.get(i).getValue().equals("(")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.String) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			return true;

		} else {
			return true;
		}

	}

	public boolean statemntprime() {
		
		if (assignment()) {
			return true;
		} else if (funccall()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean assignment() {
		
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			Proscanner.tokens.remove(0);
			if (!expression()) {
				return false;
			}
			if (!Proscanner.tokens.get(i).getValue().equals("]")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!Proscanner.tokens.get(i).getValue().equals("=")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!expression()) {
				return false;
			}
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			return true;
		
		} else if (Proscanner.tokens.get(i).getValue().equals("=")) {
			Proscanner.tokens.remove(0);
			
			if (!expression()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			return true;
		}

		else {
			return false;
		}
	}

	public boolean funccall() {
	
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			Proscanner.tokens.remove(0);
			
			if (!exprlist()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			
			return true;
		
		} else {
			return false;
		}
	}

	public boolean exprlist() {
		
		if (nonemptyexprlist()) {
			return true;
		
		} else if (Proscanner.tokens.get(i).getValue().equals(")")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean nonemptyexprlist() {
		
		if (expression()) {
			if (!nonemptyexprlistprime()) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean nonemptyexprlistprime() {
		
		if (Proscanner.tokens.get(i).getValue().equals(",")) {
			Proscanner.tokens.remove(0);

			if (!expression()) {

				return false;
			}

			if (!nonemptyexprlistprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals(")")) {
			

			return true;
		} else {
			return false;
		}

	}

	public boolean ifstmnt() {
		
		if (Proscanner.tokens.get(i).getValue().equals("if")) {
			Proscanner.tokens.remove(0);

			if (!Proscanner.tokens.get(i).getValue().equals("(")) {

				
				return false;
			} 
			Proscanner.tokens.remove(0);

			if (!conditionexpres()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				

				return false;
			} 
			Proscanner.tokens.remove(0);
			
			if (!blockstatements()) {
				return false;
			}
			statecount++;
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
		
		if (conditionop()) {
			if (!condition()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals(")")) {
			
			return true;
		} else {
			return false;
		}
	}

	public boolean conditionop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("&&")) {
			Proscanner.tokens.remove(0);
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("||")) {
			Proscanner.tokens.remove(0);
			return true;
		} else {
			return false;
		}
	}

	public boolean condition() {
		
		if (expression()) {
			if (!comparisonop()) {
				return false;
			}
			if (!expression()) {
				return false;
			}
			return true;
		
		} else {
			return false;
		}
	}

	public boolean comparisonop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("==") 
				|| Proscanner.tokens.get(i).getValue().equals("!=")
				|| Proscanner.tokens.get(i).getValue().equals(">") 
				|| Proscanner.tokens.get(i).getValue().equals(">=")
				|| Proscanner.tokens.get(i).getValue().equals("<")
				|| Proscanner.tokens.get(i).getValue().equals("<=")) {

			Proscanner.tokens.remove(0);
			
			return true;
		
		} else {
			return false;
		}

	}

	public boolean whilest() { 
		
		
		if (Proscanner.tokens.get(i).getValue().equals("while")) {
			Proscanner.tokens.remove(0);
			
			if (!Proscanner.tokens.get(i).getValue().equals("(")) {

				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			if (!conditionexpres()) {
				return false;
			}
			
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {

				
				return false;
			} 
			
			Proscanner.tokens.remove(0);
			
			
			if (!blockstatements()) {
				return false;
			}
			statecount++;
			return true;
		} else {
			return false;
		}

	}

	public boolean returnst() {
		
		if (Proscanner.tokens.get(i).getValue().equals("return")) {
			Proscanner.tokens.remove(0);
			if (!returnprime()) {
				return false;
			}
			return true;
		} else {
			return false;
		}

	}

	public boolean returnprime() {				//counting statements at end of semicolon
		
		if (expression()) {
			if (!Proscanner.tokens.get(i).getValue().equals(";")) {

				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals(";")) {

			Proscanner.tokens.remove(0);
			statecount++;
			return true;
		} else {
			return false;
		}
	}

	public boolean breakstmnt() {
		
		if (Proscanner.tokens.get(i).getValue().equals("break")) {
			Proscanner.tokens.remove(0);

			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
			return true;
		} else {
			return false;
		}
	}

	public boolean continuest() {
		
		if (Proscanner.tokens.get(i).getValue().equals("continue")) {
			Proscanner.tokens.remove(0);

			if (!Proscanner.tokens.get(i).getValue().equals(";")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			statecount++;
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
		
		if (addop()) {
			
			if (!term()) {
				return false;
			}
			
			if (!expressionprime()) {
				return false;
			}
			
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("]") 
				|| Proscanner.tokens.get(i).getValue().equals(",") 
				|| Proscanner.tokens.get(i).getValue().equals(")") 
				|| Proscanner.tokens.get(i).getValue().equals(";") 
				|| Proscanner.tokens.get(i).getValue().equals("==") 
				|| Proscanner.tokens.get(i).getValue().equals("!=")
				|| Proscanner.tokens.get(i).getValue().equals(">") 
				|| Proscanner.tokens.get(i).getValue().equals(">=")
				|| Proscanner.tokens.get(i).getValue().equals("<")
				|| Proscanner.tokens.get(i).getValue().equals("&&")
				|| Proscanner.tokens.get(i).getValue().equals("||")) {
			
			
			return true;
		} else {
			return false;
		}

	}

	public boolean addop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("+") 
				|| Proscanner.tokens.get(i).getValue().equals("-") ) {
			Proscanner.tokens.remove(0);
			
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
		
		if (mulop()) {
			if (!factor()) {
				return false;
			}
			if (!termprime()) {
				return false;
			}
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("+") 
				|| Proscanner.tokens.get(i).getValue().equals("-") 
				|| Proscanner.tokens.get(i).getValue().equals("]") 
				|| Proscanner.tokens.get(i).getValue().equals(",") 
				|| Proscanner.tokens.get(i).getValue().equals(")") 
				|| Proscanner.tokens.get(i).getValue().equals(";") 
				|| Proscanner.tokens.get(i).getValue().equals("==") 
				|| Proscanner.tokens.get(i).getValue().equals("!=")
				|| Proscanner.tokens.get(i).getValue().equals(">") 
				|| Proscanner.tokens.get(i).getValue().equals(">=")
				|| Proscanner.tokens.get(i).getValue().equals("<")
				|| Proscanner.tokens.get(i).getValue().equals("<=")
				|| Proscanner.tokens.get(i).getValue().equals("&&")
				|| Proscanner.tokens.get(i).getValue().equals("||")) {
			
			return true;
		} else {
			return false;
		}

	}

	public boolean mulop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("*")) {
			Proscanner.tokens.remove(0);
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("/")) {
			Proscanner.tokens.remove(0);
			return true;
		} else {
			return false;
		}
	}

	public boolean factor() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			Proscanner.tokens.remove(0);
			
			return factorprime();
			
		} else if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Numbers) {
			Proscanner.tokens.remove(0);
			
			return true;

		} else if (Proscanner.tokens.get(i).getValue().equals("-")) {
			Proscanner.tokens.remove(0);

			if ((Proscanner.tokens.get(i).getKey()) != TokenNames.Numbers) {
				return false;
			}
			Proscanner.tokens.remove(0);
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("(")) {
			Proscanner.tokens.remove(0);

			if (!expression()) {
				return false;
			}

			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);

			return true;
		} else {
			return false;
		}
	}

	public boolean factorprime() {
		
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			Proscanner.tokens.remove(0);
			if (!expression()) {
				return false;
			}
			if (!Proscanner.tokens.get(i).getValue().equals("]")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("(")) {
			Proscanner.tokens.remove(0);
			if (!exprlist()) {
				return false;
			}
			if (!Proscanner.tokens.get(i).getValue().equals(")")) {
				
				return false;
			} 
			Proscanner.tokens.remove(0);
			
			return true;
		} else if (Proscanner.tokens.get(i).getValue().equals("+") 
				|| Proscanner.tokens.get(i).getValue().equals("*") 
				|| Proscanner.tokens.get(i).getValue().equals("/") 
				|| Proscanner.tokens.get(i).getValue().equals("-") 
				|| Proscanner.tokens.get(i).getValue().equals("]") 
				|| Proscanner.tokens.get(i).getValue().equals(",") 
				|| Proscanner.tokens.get(i).getValue().equals(")") 
				|| Proscanner.tokens.get(i).getValue().equals(";") 
				|| Proscanner.tokens.get(i).getValue().equals("==") 
				|| Proscanner.tokens.get(i).getValue().equals("!=")
				|| Proscanner.tokens.get(i).getValue().equals(">") 
				|| Proscanner.tokens.get(i).getValue().equals(">=")
				|| Proscanner.tokens.get(i).getValue().equals("<")
				|| Proscanner.tokens.get(i).getValue().equals("<=")
				|| Proscanner.tokens.get(i).getValue().equals("&&")
				|| Proscanner.tokens.get(i).getValue().equals("||")) {
			
			return true;
		} else {
			return false;
		}
	}

}