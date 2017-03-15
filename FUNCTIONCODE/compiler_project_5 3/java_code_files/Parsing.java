import java.util.HashMap;
import java.util.Vector;

// whenever a token is encountered, it is parsed and index of arraylist is incremented hence when arraylist gets last token EOF it reports pass
public class Parsing {
	private static HashMap<String, HashMap<String, Integer>> symtab;
	private static HashMap<String, Integer> numberOfLocalVars;
	String currentFunction;
	int currentCount;
	int i = 0;
	public HashMap<String, Integer> getNumberVars() {
		return numberOfLocalVars;
	}
	
	public HashMap<String, HashMap<String, Integer>> getSymbolTable() {
		return symtab;
	}
	
	String fileName;
	public Parsing(String fileName) {
		
		fileName = this.fileName;
		numberOfLocalVars = new HashMap<String, Integer>();
		symtab = new HashMap<String, HashMap<String, Integer>>();
		
		 program();

	}

	
	private boolean program() {
		
		if (Proscanner.tokens.get(i).getValue().equals("EOF")) {
			return true;
		} else if (type_name()) {
			currentFunction = "global";
			if (symtab.get("global") == null) {
				symtab.put(currentFunction, new HashMap<String, Integer>());
				currentCount = 0;
			}
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
				String idtoken = Proscanner.tokens.get(i).getValue();
				i++;

				if (Proscanner.tokens.get(i).getValue().equals("(")) {
					if (currentFunction != null) {
						numberOfLocalVars.put(currentFunction, currentCount);
					}
					currentFunction = idtoken;

					symtab.put(currentFunction, new HashMap<String, Integer>());
					currentCount = 0;
				} else {
					if (symtab.get(currentFunction).get(idtoken) == null) {
						
						symtab.get(currentFunction).put(idtoken, currentCount);
						currentCount++;
					}
				}
				if (data_decls() && func_list()) {
					
					if (Proscanner.tokens.get(i).getValue().equals("EOF")) {
						
						
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * <func list> --> empty | left_parenthesis <parameter list>
	 * right_parenthesis <func Z> <func list Z>
	 * 
	 * @return A boolean indicating if the rule passed or failed
	 */
	private boolean func_list()
	
	{
		
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			i++;
			if (parameter_list()) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {
					i++;
					if (func_Z()) {
						return func_list_Z();
					}
					return false;
				}
				return false;
			}
			return false;
		}
		return true;
	}

	/**
	 * <func Z> --> semicolon | left_brace <data decls Z> <statements>
	 * right_brace
	 * 
	 * @return A boolean indicating if the rule passed or failed
	 */
	private boolean func_Z() {
		
		if (Proscanner.tokens.get(i).getValue().equals(";")) {
			i++; 
			return true;
		}

		if (Proscanner.tokens.get(i).getValue().equals("{")) {
			i++;
			if (data_decls_Z()) {
				if (statements()) {
					if (Proscanner.tokens.get(i).getValue().equals("}")) {
						i++;
						
						return true;
					}
					return false;
				}
				return false;
			}
			return false;
		}
		return false;
	}

	/**
	 * <func list Z> --> empty | <type name> ID left_parenthesis <parameter
	 * list> right_parenthesis <func Z> <func list Z>
	 * 
	 * @return a boolean
	 */
	private boolean func_list_Z() {
		
		if (type_name()) {
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
				String idtoken = (Proscanner.tokens.get(i).getValue());
				i++;
				if (currentFunction != null) {
					numberOfLocalVars.put(currentFunction, currentCount);
				}
				currentFunction = idtoken;
				symtab.put(currentFunction, new HashMap<String, Integer>());
				currentCount = 0;
				if (Proscanner.tokens.get(i).getValue().equals("(")) {
					i++;
					if (parameter_list()) {
						if (Proscanner.tokens.get(i).getValue().equals(")")) {
							i++;
							if (func_Z()) {
								return func_list_Z();
							}
						}
					}
				}
			}
			return false;
		}
		// return true for the empty rule
		numberOfLocalVars.put(currentFunction, currentCount);
		return true;
	}

	/**
	 * <type name> --> int | void | binary | decimal
	 * 
	 * @return A boolean indicating if the rule passed or failed
	 */
	private boolean type_name() {
		
		if (Proscanner.tokens.get(i).getValue().equals("int") || Proscanner.tokens.get(i).getValue().equals("void")
				|| Proscanner.tokens.get(i).getValue().equals("binary")
				|| Proscanner.tokens.get(i).getValue().equals("decimal")) {
			i++;
			return true;
		}
		return false;
	}

	/**
	 * <parameter list> --> empty | void <parameter list Z> | <non-empty list>
	 * 
	 * @return a boolean
	 */
	private boolean parameter_list() {
		
		if (Proscanner.tokens.get(i).getValue().equals("void")) {
			i++;
			return parameter_list_Z();
		}
		
		else if (non_empty_list()) {
			return true;
		}
		// empty
		return true;
	}

	/**
	 * <parameter list Z> --> empty | ID <non-empty list prime>
	 * 
	 * @return a boolean
	 */
	private boolean parameter_list_Z() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			i++;
			return non_empty_list_prime();
		}
		return true;
	}

	/**
	 * <non-empty list> --> int ID <non-empty list prime> | binary ID <non-empty
	 * list prime> | decimal ID <non-empty list prime>
	 * 
	 * @return a boolean
	 */
	private boolean non_empty_list() {
		
		if (Proscanner.tokens.get(i).getValue().equals("int") || Proscanner.tokens.get(i).getValue().equals("void")
				|| Proscanner.tokens.get(i).getValue().equals("binary")
				|| Proscanner.tokens.get(i).getValue().equals("decimal")) {
			i++;
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
				i++;
				return non_empty_list_prime();
			}
		}
		return false;
	}

	/**
	 * <non-empty list prime> --> comma <type name> ID <non-empty list prime> |
	 * empty
	 * 
	 * @return a boolean
	 */
	private boolean non_empty_list_prime() {
		
		if (Proscanner.tokens.get(i).getValue().equals(",")) {
			i++;
			if (type_name()) {
				if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
					i++;
					return non_empty_list_prime();
				}
				return false;
			}
			return false;
		}
		return true;
	}

	/**
	 * <data decls> --> empty | <id list Z> semicolon <program> | <id list
	 * prime> semicolon <program>
	 * 
	 * @return a boolean
	 */
	private boolean data_decls() {
		
		if (id_list_Z()) {
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				i++;
				
				return program(); 
			}
			return false;
		}
		if (id_list_prime()) {
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				i++;
				
				return program(); 
			}
			
		}
		return true;
	}

	/**
	 * <data decls Z> --> empty | int <id list> semicolon <data decls Z> | void
	 * <id list> semicolon <data decls Z> | binary <id list> semicolon <data
	 * decls Z> | decimal <id list> semicolon <data decls Z>
	 * 
	 * @return A boolean indicating if the rule passed or failed
	 */
	private boolean data_decls_Z() {
		
		if (type_name()) {
			if (id_list()) {
				if (Proscanner.tokens.get(i).getValue().equals(";")) {
					i++;
					return data_decls_Z();
				}
				return false;
			}
			return false;
		}
		return true;
	}

	/**
	 * <id list> --> <id> <id list prime>
	 * 
	 * @return a boolean
	 */
	private boolean id_list() {
		
		if (id()) {
			return id_list_prime();
		}
		return false;
	}

	/**
	 * <id list Z> --> left_bracket <expression> right_bracket <id list prime>
	 * 
	 * @return a boolean indicating if the rule passed or failed
	 */
	private boolean id_list_Z() {
		
		
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			String el = Proscanner.tokens.get(i).getValue();
			i++;
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Numbers) {
				el = Proscanner.tokens.get(i).getValue();
				int num = Integer.parseInt(el);
				currentCount += num - 1;
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					i++;
					return id_list_prime();
				}
			}
		}
		return false;
	}

	/**
	 * <id list prime> --> comma <id> <id list prime> | empty
	 * 
	 * @return a boolean indicating if the rule passed or failed
	 */
	private boolean id_list_prime() {
		
		if (Proscanner.tokens.get(i).getValue().equals(",")) {
			i++;
			if (id()) {
				return id_list_prime();
			}
			return false;
		}
		return true;
	}

	/**
	 * <id> --> ID <id Z>
	 * 
	 * @return a boolean
	 */
	private boolean id() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			String idtoken = Proscanner.tokens.get(i).getValue();
			i++;
			if (symtab.get(currentFunction).get(idtoken) == null) {
				symtab.get(currentFunction).put(idtoken, currentCount);
				currentCount++;
			}
			return id_Z();
		}
		return false;
	}

	/**
	 * <id Z> --> left_bracket <expression> right_bracket | empty
	 * 
	 * @return a boolean
	 */
	private boolean id_Z() {
		
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			String el = Proscanner.tokens.get(i).getValue();
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Numbers) {
				el = Proscanner.tokens.get(i).getValue();
				int num = Integer.parseInt(el);
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					i++;
					currentCount += num - 1;
					
					return true;
				}
				return false;
			}
			return false;
		}
		
		return true;
	}

	/**
	 * <block statements> --> left_brace <statements> right_brace
	 * 
	 * @return a boolean
	 */
	private boolean block_statements() {
		
		if (Proscanner.tokens.get(i).getValue().equals("{")) {
			i++;
			if (statements()) {
				if (Proscanner.tokens.get(i).getValue().equals("}")) {
					i++;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * <statements> --> empty | <statement> <statements>
	 * 
	 * @return a boolean
	 */
	private boolean statements() {
		
		if (statement()) {
			return statements();
		}
		return true;
	}

	/**
	 * <statement> --> ID <statement Z> | <if statement> | <while statement> |
	 * <return statement> | <break statement> | <continue statement> | read
	 * left_parenthesis ID right_parenthesis semicolon | write left_parenthesis
	 * <expression> right_parenthesis semicolon | print left_parenthesis STRING
	 * right_parenthesis semicolon
	 * 
	 * @return a boolean indicating if the rule passed or failed
	 */
	private boolean statement() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			String x = Proscanner.tokens.get(i).getValue();
			if (symtab.get(currentFunction).get(x) == null && symtab.get("global").get(x) == null) {
				currentCount++;
			} 
			i++;
			return statement_Z();
		}
		if (if_statement()) {
			return true;
		}
		if (while_statement()) {
			return true;
		}
		if (return_statement()) {
			return true;
		}
		if (break_statement()) {
			return true;
		}
		if (continue_statement()) {
			return true;
		}
		if (Proscanner.tokens.get(i).getValue().equals("read")) {
			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {
				i++;
				if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
					i++;
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						i++;
						if (Proscanner.tokens.get(i).getValue().equals(";")) {
							i++;
							return true;
						}
					}
				}
			}
			return false;
		}

		// write left_parenthesis <expression> right_parenthesis semicolon
		if (Proscanner.tokens.get(i).getValue().equals("write")) {
			
			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {
				i++;
				if (expression()) {
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						i++;
						;
						if (Proscanner.tokens.get(i).getValue().equals(";")) {
							i++;
							return true;
						}
					}
				}
			}
			return false;
		}

		
		if (Proscanner.tokens.get(i).getValue().equals("print")) {
			
			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {
				i++;
				if ((Proscanner.tokens.get(i).getKey()) == TokenNames.String) {
					i++;
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						i++;
						if (Proscanner.tokens.get(i).getValue().equals(";")) {
							i++;
							return true;
						}
					}
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * <statement Z> --> <assignment Z> | <func call>
	 * 
	 * @return a boolean indicating if the rule passed or failed
	 */
	private boolean statement_Z() {
		
		if (assignment_Z()) {
			return true;
		} else if (func_call()) {
			return true;
		}
		return false;
	}

	/**
	 * <assignment Z> --> equal_sign <expression> semicolon | left_bracket
	 * <expression> right_bracket equal_sign <expression> semicolon
	 * 
	 * @return a boolean
	 */
	private boolean assignment_Z() {
		
		if (Proscanner.tokens.get(i).getValue().equals("=")) {
			i++;
			if (expression()) {
				if (Proscanner.tokens.get(i).getValue().equals(";")) {
					i++;
					return true;
				}
			}
			return false;
		}
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			i++;
			if (expression()) {
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					i++;
					if (Proscanner.tokens.get(i).getValue().equals("=")) {
						i++;
						if (expression()) {
							if (Proscanner.tokens.get(i).getValue().equals(";")) {
								i++;
								currentCount++;
								return true;
							}
						}
					}
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * <func call> --> left_parenthesis <expr list> right_parenthesis semicolon
	 * 
	 * @return a boolean
	 */
	private boolean func_call() {
		
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			i++;
			;
			if (expr_list()) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {
					i++;
					;
					if (Proscanner.tokens.get(i).getValue().equals(";")) {
						i++;
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * <expr list> --> empty | <non-empty expr list>
	 * 
	 * @return a boolean
	 */
	private boolean expr_list() {
		
		if (non_empty_expr_list()) {
			return true;
		}
		return true;
	}

	/**
	 * <non-empty expr list> --> <expression> <non-empty expr list prime>
	 * 
	 * @return a boolean
	 */
	private boolean non_empty_expr_list() {
		
		if (expression()) {
			return non_empty_expr_list_prime();
		}
		return false;
	}

	/**
	 * <non-empty expr list prime> --> comma <expression> <non-empty expr list
	 * prime> | empty
	 * 
	 * @return a boolean
	 */
	private boolean non_empty_expr_list_prime() {
		
		if (Proscanner.tokens.get(i).getValue().equals(",")) {
			i++;
			if (expression()) {
				return non_empty_expr_list_prime();
			}
			return false;
		}
		return true;
	}

	/**
	 * <if statement> --> if left_parenthesis <condition expression>
	 * right_parenthesis <block statements>
	 * 
	 * @return a boolean
	 */
	private boolean if_statement() {
		
		if (Proscanner.tokens.get(i).getValue().equals("if")) {
			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {
				i++;
				if (condition_expression()) {
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						i++;
						return block_statements();
					}
				}
			}
		}
		return false;
	}

	/**
	 * <condition expression> --> <condition> <condition expression Z>
	 * 
	 * @return a boolean
	 */
	private boolean condition_expression() {
		
		if (condition()) {
			currentCount++;
			return condition_expression_Z();
		}
		return false;
	}

	/**
	 * <condition expression Z> --> <condition op> <condition> | empty
	 * 
	 * @return a boolean
	 */
	private boolean condition_expression_Z() {
		
		if (condition_op()) {
			return condition();
		}
		return true;
	}

	/**
	 * <condition op> --> double_end_sign | double_or_sign
	 * 
	 * @return a boolean
	 */
	private boolean condition_op() {
		
		if (Proscanner.tokens.get(i).getValue().equals("&&") || Proscanner.tokens.get(i).getValue().equals("||")) {
			i++;
			return true;
		}
		return false;
	}

	/**
	 * <condition> --> <expression> <comparison op> <expression>
	 * 
	 * @return a boolean
	 */
	private boolean condition() {
		
		if (expression()) {
			if (comparison_op()) {
				currentCount++;
				return expression();
			}
		}
		return false;
	}

	/**
	 * <comparison op> --> == | != | > | >= | < | <=
	 * 
	 * @return a boolean
	 */
	private boolean comparison_op() {
		
		if (Proscanner.tokens.get(i).getValue().equals("==") || Proscanner.tokens.get(i).getValue().equals("!=")
				|| Proscanner.tokens.get(i).getValue().equals(">") || Proscanner.tokens.get(i).getValue().equals(">=")
				|| Proscanner.tokens.get(i).getValue().equals("<")
				|| Proscanner.tokens.get(i).getValue().equals("<=")) {
			i++;
			return true;
		}
		return false;
	}

	/**
	 * <while statement> --> while left_parenthesis <condition expression>
	 * right_parenthesis <block statements>
	 * 
	 * @return
	 */
	private boolean while_statement() {
		
		if (Proscanner.tokens.get(i).getValue().equals("while")) {
			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {
				i++;
				if (condition_expression()) {
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						i++;
						return block_statements();
					}
				}
			}
		}
		return false;
	}

	/**
	 * <return statement> --> return <return statement Z>
	 * 
	 * @return a boolean
	 */
	private boolean return_statement() {
		
		if (Proscanner.tokens.get(i).getValue().equals("return")) {
			i++;
			return return_statement_Z();
		}
		return false;
	}

	/**
	 * <return statement Z> --> <expression> semicolon | semicolon
	 * 
	 * @return a boolean
	 */
	private boolean return_statement_Z() {
		
		if (expression()) {
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				i++;
				return true;
			}
			return false;
		}
		if (Proscanner.tokens.get(i).getValue().equals(";")) {
			i++;
			return true;
		}
		return false;
	}

	/**
	 * <break statement> ---> break semicolon
	 * 
	 * @return a boolean
	 */
	private boolean break_statement() {
		
		if (Proscanner.tokens.get(i).getValue().equals("break")) {
			i++;
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				i++;
				return true;
			}
		}
		return false;
	}

	/**
	 * <continue statement> ---> continue semicolon
	 * 
	 * @return a boolean
	 */
	private boolean continue_statement() {
		
		if (Proscanner.tokens.get(i).getValue().equals("continue")) {
			i++;
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				i++;
				return true;
			}
		}
		return false;
	}

	/**
	 * <expression> --> <term> <expression prime>
	 * 
	 * @return a boolean
	 */
	private boolean expression() {
		
		if (term()) {
			int ret = expression_prime();
			if (ret == 0)
				return false;
			else if (ret == 1) {
				currentCount++;
				return true;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * <expression prime> --> <addop> <term> <expression prime> | empty
	 * 
	 * @return
	 */
	private int expression_prime() {
		
		if (addop()) {
			if (term()) {
				int ret = expression_prime();
				if (ret == 0)
					return 0;
				else if (ret == 1) {
					currentCount++;
					return 1;
				} else
					return 1;
			}
			return 0;
		}
		return 2;
	}

	/**
	 * <addop> --> plus_sign | minus_sign
	 * 
	 * @return a boolean
	 */
	private boolean addop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("+") || Proscanner.tokens.get(i).getValue().equals("-")) {
			i++;
			return true;
		}
		return false;
	}

	/**
	 * <term> --> <factor> <term prime>
	 * 
	 * @return a boolean
	 */
	private boolean term() {
		
		if (factor()) {
			int ret = term_prime();
			if (ret == 0)
				return false;
			else if (ret == 1) {
				currentCount++;
				return true;
			} else {
				return true;
			}
			
		}
		return false;
	}

	/**
	 * <term prime> --> <mulop> <factor> <term prime> | empty
	 * 
	 * @return
	 */
	private int term_prime() {
		
		if (mulop()) {
			if (factor()) {
				int ret = term_prime();
				if (ret == 0) {
					return 0;
				} else if (ret == 1) {
					currentCount++;
					return 1;
				} else
					return 1;
			}
			return 0;
		}
		return 2;
	}

	/**
	 * <mulop> --> star_sign | forward_slash
	 * 
	 * @return a boolean
	 */
	private boolean mulop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("*") || Proscanner.tokens.get(i).getValue().equals("/")) {
			i++;
			return true;
		}
		return false;
	}

	/**
	 * <factor> --> ID <factor Z> | NUMBER | minus_sign NUMBER |
	 * left_parenthesis <expression>right_parenthesis
	 * 
	 * @return
	 */
	private boolean factor() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			String idtoken = Proscanner.tokens.get(i).getValue();
			i++;
			if (symtab.get(currentFunction).get(idtoken) == null
					&& (!Proscanner.tokens.get(i).getValue().equals("("))) {
				currentCount++;
			}
			return factor_Z();
		}
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Numbers) {
			i++;
			return true;
		}

		
		if (Proscanner.tokens.get(i).getValue().equals("-")) {
			i++;
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Numbers) {
				i++;
				return true;
			}
			return false;
		}

		
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			currentCount++;
			i++;
			if (expression()) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {
					i++;
					currentCount++;
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * <factor Z> --> left_bracket <expression> right_bracket | left_parenthesis
	 * <expr list> right_parenthesis | empty
	 * 
	 * @return
	 */
	private boolean factor_Z() {
		
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			i++;
			if (expression()) {
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					i++;
					currentCount++;
					return true;
				}
			}
			return false;
		}
		
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			i++;
			if (expr_list()) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {
					i++;
					currentCount++;
					return true;
				}
			}
			return false;
		}
		
		return true;
	}

}
