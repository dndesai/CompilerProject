
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

	
	ArrayList<Pair<TokenNames, String>> finalFile = new ArrayList<Pair<TokenNames, String>>();

	boolean isMain = false;
	int currentLabel = 0;
	int currentCount = 0;
	int currentParamCount = 0;

	int labelCounter = 1;
	int currFuncgng = 0;
	String currentFunction;
	String currentParamID = "";
	ArrayList<String> printtokens = new ArrayList<String>();
	static ArrayList<String> insideFunc = new ArrayList<String>();
	static ArrayList<ArrayList<String>> storeFunCode = new ArrayList<ArrayList<String>>();
	boolean brace = false;
	private static HashMap<String, Integer> numberOfLocalVars;
	private static HashMap<String, HashMap<String, Integer>> symtab;
	private static HashMap<String, HashMap<String, Integer>> paramTable = new HashMap<String, HashMap<String, Integer>>();
	private static HashMap<String, Integer> paraStore = new HashMap<String, Integer>();
	private static HashMap<String, Integer> paramVar = new HashMap<String, Integer>();

	private int whileEntry;
	private int whileExit;

	

	private static String newFileName;

	public Parser(String fileName) throws FileNotFoundException, IOException {

	}

	int i = 0;

	public static void main(String[] args) throws FileNotFoundException, IOException {
		if (args.length < 1) {
			System.err.println("file not found");
			System.exit(1);
		}

		String fileName = args[0];
		newFileName = args[0];
		Parser parser = new Parser(fileName);
		Proscanner.proscan(fileName);
		Parsing parsing = new Parsing(fileName);
		numberOfLocalVars = parsing.getNumberVars();

		symtab = parsing.getSymbolTable();
		for (int i = 0; i < 40; i++) {

			storeFunCode.add(new ArrayList<String>(insideFunc));
		}

		parser.program();

	}

	public void fail() {
		System.out.println("Fail");

	}

	public void pass() {

		try {
			String[] input = newFileName.split("\\.");

			File file = new File(input[0] + "_gen.c");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw;
			fw = new FileWriter(file.getAbsolutePath());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < Proscanner.metatokens.size(); i++) {
				bw.write(Proscanner.metatokens.get(i).getValue());
			}
			bw.write("\n#include <assert.h>\n");
			bw.write("#include <stdlib.h>\n");
			bw.write(
					"#define N 2000\nint mem[N];\n#define top mem[0]\n#define base mem[1]\n#define jumpReg mem[2]\n#define membase 3\n");
			for (int i = 0; i < printtokens.size(); i++) {

				bw.write(printtokens.get(i));

				bw.write(" ");
			}

			bw.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * <program> --> <type name> ID <data decls> <func list> | empty
	 * 
	 * @return A boolean indicating pass or error
	 */
	private boolean program() {
		
		if (Proscanner.tokens.get(i).getValue().equals("EOF")) {
			return true;
		}

		currentFunction = "global";
		if (type_name()) {
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
				String idtoken = Proscanner.tokens.get(i).getValue();
				finalFile.add(Proscanner.tokens.get(i));
				i++;

				if (Proscanner.tokens.get(i).getValue().equals("(")) {

					currentFunction = idtoken;

					currFuncgng++;

					currentCount = symtab.get(currentFunction).size();

				} else {

					data_decls();
				}

				if (func_list()) {

					if (Proscanner.tokens.get(i).getValue().equals("EOF")) {
						pass();
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
	private boolean func_list() {

		
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			if (parameter_list()) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {
					finalFile.add(Proscanner.tokens.get(i));
					i++;
					
					Pair<TokenNames, String> tokenpair = null;
					while (finalFile.size() > 0) {

						tokenpair = finalFile.remove(0);
						if (true) {

							if (currentFunction == null || currentFunction.equals("main")) {

								printtokens.add(tokenpair.getValue());
							} else {

								storeFunCode.add(new ArrayList<String>(insideFunc));
								storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

							}

						}
					}

					paraStore.put(currentFunction, currentParamCount);

					currentParamCount = 0;

					paramTable.put(currentFunction, paramVar);

					paramVar = new HashMap<String, Integer>();

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
			finalFile.add(Proscanner.tokens.get(i));
			i++;

			// printArray(true);
			Pair<TokenNames, String> tokenpair = null;
			while (finalFile.size() > 0) {

				tokenpair = finalFile.remove(0);
				if (true) {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add(tokenpair.getValue());
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng).add(tokenpair.getValue());

					}

				}
			}
			return true;
		}

		if (Proscanner.tokens.get(i).getValue().equals("{")) {
			finalFile.add(Proscanner.tokens.get(i));
			i++;

			Pair<TokenNames, String> tokenpair = null;
			while (finalFile.size() > 0) {

				tokenpair = finalFile.remove(0);
				if (true) {
					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add(tokenpair.getValue());
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

					}

				}
			}

			if (!currentFunction.equals("main")) {

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("\n" + currentFunction + "Func:;");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("\n" + currentFunction + "Func:;");

				}
			}

			if (currentFunction.equals("main")) {
				isMain = true;
			}
			if (isMain == true) {

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("\n\ntop = membase;\n" + "mem[top] = 0; \n" + "base = top + 1; \n" + "top = base + "
							+ numberOfLocalVars.get("main").intValue() + ";\n\n" + "mainFunc:;\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1)
							.add("\n\ntop = membase;\n" + "mem[top] = 0; \n" + "base = top + 1; \n" + "top = base + "
									+ numberOfLocalVars.get("main").intValue() + ";\n\n" + "mainFunc:;\n");

				}

			}

			if (data_decls_Z()) {
				//printArraySpaces(false);
				//Pair<TokenNames, String> tokenpair = null;
				while (finalFile.size() > 0 && finalFile.get(0).getKey() == TokenNames.Space) {
					tokenpair = finalFile.remove(0);
					
				}
				finalFile.clear();

				if (statements()) {
					if (Proscanner.tokens.get(i).getValue().equals("}")) {
						finalFile.add(Proscanner.tokens.get(i));
						i++;

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("\n\n//epilogue of " + currentFunction + " function \n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1)
									.add("\n\n//epilogue of " + currentFunction + " function \n");

						}

						if (isMain) {

							if (currentFunction == null || currentFunction.equals("main")) {

								printtokens.add("\n\njumpReg = mem[base-1];  \ngoto jumpTable;");
							} else {

								storeFunCode.add(new ArrayList<String>(insideFunc));
								storeFunCode.get(currFuncgng - 1)
										.add("\n\njumpReg = mem[base-1];  \ngoto jumpTable;");

							}

							if (currentFunction == null || currentFunction.equals("main")) {

								printtokens.add("\n\njumpTable:;\nswitch(jumpReg)\n{\n case 0: exit(0);");
							} else {

								storeFunCode.add(new ArrayList<String>(insideFunc));
								storeFunCode.get(currFuncgng - 1)
										.add("\n\njumpTable:;\nswitch(jumpReg)\n{\n case 0: exit(0);");

							}

							for (int i = 1; i < labelCounter; i++) {

								if (currentFunction == null || currentFunction.equals("main")) {

									printtokens.add(" case " + (i) + ":\n" + " goto label_" + (i) + ";");
								} else {

									storeFunCode.add(new ArrayList<String>(insideFunc));
									storeFunCode.get(currFuncgng - 1)
											.add(" case " + (i) + ":\n" + " goto label_" + (i) + ";");

								}

							}

							if (currentFunction == null || currentFunction.equals("main")) {

								printtokens.add(" default:assert(0);\n}\n");
							} else {

								storeFunCode.add(new ArrayList<String>(insideFunc));
								storeFunCode.get(currFuncgng - 1).add(" default:assert(0);\n}\n");

							}

							int funcIndex = currFuncgng - 1;
							while (funcIndex >= 0) {

								int opendBrace = storeFunCode.get(funcIndex).indexOf("{") + 1;

								int closedBrace = storeFunCode.get(funcIndex).lastIndexOf("}") - 1;

								for (int code = opendBrace; code <= closedBrace; code++) {

									printtokens.add(storeFunCode.get(funcIndex).get(code));
								}

								funcIndex--;
							}

						} else {

							if (currentFunction == null || currentFunction.equals("main")) {

								printtokens.add("\ntop = mem[base-3];");
							} else {

								storeFunCode.add(new ArrayList<String>(insideFunc));
								storeFunCode.get(currFuncgng - 1).add("\ntop = mem[base-3];");

							}

							if (currentFunction == null || currentFunction.equals("main")) {

								printtokens
										.add("\njumpReg = mem[base-1];\nbase = mem[base-4];\ngoto jumpTable;\n" + "\n");
							} else {

								storeFunCode.add(new ArrayList<String>(insideFunc));
								storeFunCode.get(currFuncgng - 1)
										.add("\njumpReg = mem[base-1];\nbase = mem[base-4];\ngoto jumpTable;\n" + "\n");

							}
						}

						while (finalFile.size() > 0) {

							tokenpair = finalFile.remove(0);
							if (true) {

								if (currentFunction == null || currentFunction.equals("main")) {

									printtokens.add(tokenpair.getValue());
								} else {

									storeFunCode.add(new ArrayList<String>(insideFunc));
									storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

								}

							}
						}

						isMain = false;
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
				finalFile.add(Proscanner.tokens.get(i));
				String idtoken = (Proscanner.tokens.get(i).getValue());
				i++;
				currentFunction = idtoken;
				currFuncgng++;
				currentCount = symtab.get(currentFunction).size();
				if (Proscanner.tokens.get(i).getValue().equals("(")) {
					finalFile.add(Proscanner.tokens.get(i));
					i++;
					if (parameter_list()) {
						if (Proscanner.tokens.get(i).getValue().equals(")")) {
							finalFile.add(Proscanner.tokens.get(i));
							i++;

							Pair<TokenNames, String> tokenpair = null;
							while (finalFile.size() > 0) {

								tokenpair = finalFile.remove(0);
								if (true) {

									
									if (currentFunction == null || currentFunction.equals("main")) {

										printtokens.add(tokenpair.getValue());
									} else {

										storeFunCode.add(new ArrayList<String>(insideFunc));
										storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

									}

								}
							}
							paraStore.put(currentFunction, currentParamCount);
							currentParamCount = 0;
							paramTable.put(currentFunction, paramVar);
							paramVar = new HashMap<String, Integer>();

							if (func_Z()) {
								return func_list_Z();
							}
						}
					}
				}
			}
			return false;
		}
		
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
			finalFile.add(Proscanner.tokens.get(i));
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
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			return parameter_list_Z();
		}

		else if (non_empty_list()) {
			return true;
		}
		
		return true;
	}

	/**
	 * <parameter list Z> --> empty | ID <non-empty list prime>
	 * 
	 * @return a boolean
	 */
	private boolean parameter_list_Z() {
		
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			finalFile.add(Proscanner.tokens.get(i));
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
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
				String idtoken = (Proscanner.tokens.get(i).getValue());

				currentParamID = idtoken;
				finalFile.add(Proscanner.tokens.get(i));
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
		
		currentParamCount++;

		paramVar.put(currentParamID, currentParamCount - 1);

		if (Proscanner.tokens.get(i).getValue().equals(",")) {
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			if (type_name()) {
				if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {

					currentParamID = Proscanner.tokens.get(i).getValue();
					finalFile.add(Proscanner.tokens.get(i));
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
	private Node data_decls() {

		
		Node result = new Node();
		result.retValue = 1;
		if (id_list_Z()) {
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				finalFile.add(Proscanner.tokens.get(i));
				i++;
				if (currentFunction.equals("global")) {
					finalFile.clear();
				}

				program(); 
				return result;
			}
		}
		if (id_list_prime()) {
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				finalFile.add(Proscanner.tokens.get(i));
				i++;
				if (currentFunction.equals("global")) {
					finalFile.clear();
				}

				program();
				return result;
			}
			
		}

		result.retValue = 2;
		return result;
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
					finalFile.add(Proscanner.tokens.get(i));
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
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			Node expr = expression();
			if (expr.retValue != 0) {
				finalFile.add(new Pair<TokenNames, String>(TokenNames.Identifiers, expr.getValue()));
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					finalFile.add(Proscanner.tokens.get(i));
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
			finalFile.add(Proscanner.tokens.get(i));
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
			finalFile.add(Proscanner.tokens.get(i));
			i++;
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
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			if (expression().retValue != 0) {
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					finalFile.add(Proscanner.tokens.get(i));
					i++;

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
			if (brace == false) {
				finalFile.add(Proscanner.tokens.get(i));
				i++;
			}

			else {

				i++;
			}

			Pair<TokenNames, String> tokenpair = null;
			while (finalFile.size() > 0) {

				tokenpair = finalFile.remove(0);
				if (true) {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add(tokenpair.getValue());
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

					}

				}
			}
			if (statements()) {
				if (Proscanner.tokens.get(i).getValue().equals("}")) {
					if (brace == false) {
						finalFile.add(Proscanner.tokens.get(i));
						i++;
					}

					else {

						i++;
					}

					while (finalFile.size() > 0) {

						tokenpair = finalFile.remove(0);
						if (true) {

							
							if (currentFunction == null || currentFunction.equals("main")) {

								printtokens.add(tokenpair.getValue());
							} else {

								storeFunCode.add(new ArrayList<String>(insideFunc));
								storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

							}

						}
					}
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

			String id = Proscanner.tokens.get(i).getValue();
			i++;
			Node st = statement_Z();

			if (st.retValue == 1) {

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("//prologue of func to " + id + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("//prologue of func to " + id + "\n");

				}

				String parameterStr = st.getValue().replace("(", "").replace(")", "").replace(" ", "");
				String params[] = parameterStr.split(",");
				int paramCount = 0;
				int k = 0;
				paramCount = params.length;
				paramCount = paraStore.get(id).intValue();

				for (; k < paramCount; k++) {
					if (params[k].trim().length() > 0) {

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("mem[top+" + k + "]=" + params[k] + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1)
									.add("mem[top+" + k + "]=" + params[k] + ";" + "\n");

						}
					}
				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[top+" + k + "]=base;" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("mem[top+" + k + "]=base;" + "\n");

				}
				k++;

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[top+" + k + "]=top;" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("mem[top+" + k + "]=top;" + "\n");

				}
				k++;

				k++;

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[top+" + k + "]=" + labelCounter + ";");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("mem[top+" + k + "]=" + labelCounter + ";");

				}
				k++;

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("base= top+" + (4 + paramCount) + ";" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("base= top+" + (4 + paramCount) + ";" + "\n");

				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("top=base+" + numberOfLocalVars.get(id).intValue() + ";");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1)
							.add("top=base+" + numberOfLocalVars.get(id).intValue() + ";");

				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("\ngoto " + id + "Func;\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("\ngoto " + id + "Func;\n");

				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("\nlabel_" + labelCounter + ":;" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("\nlabel_" + labelCounter + ":;" + "\n");

				}
				labelCounter++;

				if (currentFunction == null || currentFunction.equals("main")) {

					// printtokens.add("goto " + id + "Func;\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("goto " + id + "Func;\n");

				}

				return true;
			} else {

				if (st.retValue == 2) {
					
					if (symtab.get("global").get(id) != null) {

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("mem[base+" + currentCount + "]=" + symtab.get("global").get(id) + "+"
									+ st.futureuse + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("mem[base+" + currentCount + "]="
									+ symtab.get("global").get(id) + "+" + st.futureuse + ";" + "\n");

						}

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("mem[mem[base+" + currentCount + "]]" + st.getValue() + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1)
									.add("mem[mem[base+" + currentCount + "]]" + st.getValue() + ";" + "\n");

						}
						currentCount++;
					}

					else {

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("mem[base+" + currentCount + "]=" + symtab.get(currentFunction).get(id)
									+ "+" + st.futureuse + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("mem[base+" + currentCount + "]="
									+ symtab.get(currentFunction).get(id) + "+" + st.futureuse + ";" + "\n");

						}

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("mem[mem[base+" + currentCount + "]]" + st.getValue() + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1)
									.add("mem[mem[base+" + currentCount + "]]" + st.getValue() + ";" + "\n");

						}

						currentCount++;
					}
				}
				
				else {
					
					if (symtab.get("global").get(id) != null) {

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add(
									"mem[" + symtab.get("global").get(id) + "]" + st.getValue() + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add(
									"mem[" + symtab.get("global").get(id) + "]" + st.getValue() + ";" + "\n");

						}
					}

					else {

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("mem[base+" + symtab.get(currentFunction).get(id) + "]" + st.getValue()
									+ ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("mem[base+"
									+ symtab.get(currentFunction).get(id) + "]" + st.getValue() + ";" + "\n");

						}

					}
				}

				return true;
			}
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
			finalFile.add(Proscanner.tokens.get(i));

			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {

				finalFile.add(Proscanner.tokens.get(i));

				finalFile.add(Proscanner.tokens.get(i));

				i++;
				if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {

					Pair<TokenNames, String> l = Proscanner.tokens.get(i);
					Pair<TokenNames, String> l1;
					if (symtab.get(currentFunction).get(l.getValue()) != null) {

						l1 = new Pair<TokenNames, String>(TokenNames.Identifiers,
								"mem[base+" + symtab.get(currentFunction).get(l.getValue()) + "]");
					} else {
						l1 = new Pair<TokenNames, String>(TokenNames.Identifiers,
								"mem[" + symtab.get("global").get(l.getValue()) + "]");
					}
					
					i++;

					finalFile.set(finalFile.size() - 1, l1);
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						finalFile.add(Proscanner.tokens.get(i));
						i++;
						if (Proscanner.tokens.get(i).getValue().equals(";")) {
							finalFile.add(Proscanner.tokens.get(i));
							i++;
							
							Pair<TokenNames, String> tokenpair = null;
							while (finalFile.size() > 0) {

								tokenpair = finalFile.remove(0);
								if (true) {

									if (currentFunction == null || currentFunction.equals("main")) {

										printtokens.add(tokenpair.getValue());
									} else {

										storeFunCode.add(new ArrayList<String>(insideFunc));
										storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

									}

								}
							}

							return true;
						}
					}
				}
			}
			return false;
		}

		if (Proscanner.tokens.get(i).getValue().equals("write")) {
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {
				finalFile.add(Proscanner.tokens.get(i));
				i++;
				Node ret = expression();
				if (ret.retValue != 0) {
					finalFile.add(new Pair<TokenNames, String>(TokenNames.Identifiers, ret.getValue()));
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						finalFile.add(Proscanner.tokens.get(i));
						i++;
						if (Proscanner.tokens.get(i).getValue().equals(";")) {
							finalFile.add(Proscanner.tokens.get(i));
							i++;
							
							Pair<TokenNames, String> tokenpair = null;
							while (finalFile.size() > 0) {

								tokenpair = finalFile.remove(0);
								if (true) {

									
									if (currentFunction == null || currentFunction.equals("main")) {

										printtokens.add(tokenpair.getValue());
									} else {

										storeFunCode.add(new ArrayList<String>(insideFunc));
										storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

									}

								}
							}
							return true;
						}
					}
				}
			}
			return false;
		}

		if (Proscanner.tokens.get(i).getValue().equals("print")) {
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			if (Proscanner.tokens.get(i).getValue().equals("(")) {
				finalFile.add(Proscanner.tokens.get(i));
				i++;
				if ((Proscanner.tokens.get(i).getKey()) == TokenNames.String) {
					finalFile.add(Proscanner.tokens.get(i));
					i++;
					if (Proscanner.tokens.get(i).getValue().equals(")")) {
						finalFile.add(Proscanner.tokens.get(i));
						i++;
						if (Proscanner.tokens.get(i).getValue().equals(";")) {
							finalFile.add(Proscanner.tokens.get(i));
							i++;

							
							Pair<TokenNames, String> tokenpair = null;
							while (finalFile.size() > 0) {

								tokenpair = finalFile.remove(0);
								if (true) {

									
									if (currentFunction == null || currentFunction.equals("main")) {

										printtokens.add(tokenpair.getValue());
									} else {

										storeFunCode.add(new ArrayList<String>(insideFunc));
										storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

									}

								}
							}
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
	private Node statement_Z() {

		Node assign = assignment_Z();
		Node func;

		if (assign.retValue != 0) {
			return assign;
		} else if ((func = func_call()).retValue != 0) {
			func.retValue = 1;
			return func;
		}
		return new Node();
	}

	/**
	 * <assignment Z> --> equal_sign <expression> semicolon | left_bracket
	 * <expression> right_bracket equal_sign <expression> semicolon
	 * 
	 * @return a Node
	 */
	private Node assignment_Z() {

		Node result = new Node();

		if (Proscanner.tokens.get(i).getValue().equals("=")) {

			i++;
			Node expr = expression();
			if (expr.retValue != 0) {
				if (Proscanner.tokens.get(i).getValue().equals(";")) {

					i++;

					result.setValue(" = " + expr.getValue());
					result.retValue = 3;
					return result;
				}
			}
			result.retValue = 0;
			return result;
		}
		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			i++;
			Node expr2 = expression();
			if (expr2.retValue != 0) {
				result.retValue = 2;
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					i++;
					if (Proscanner.tokens.get(i).getValue().equals("=")) {
						i++;
						Node expr3 = expression();
						if (expr3.retValue != 0) {
							if (Proscanner.tokens.get(i).getValue().equals(";")) {
								i++;
								result.setValue(" = " + expr3.getValue());
								result.futureuse = expr2.getValue();
								return result;
							}
						}
					}
				}
			}
			result.retValue = 0;
			return result;
		}
		result.retValue = 0;
		return result;
	}

	/**
	 * <func call> --> left_parenthesis <expr list> right_parenthesis semicolon
	 * 
	 * @return a Node
	 */
	private Node func_call() {

		Node result = new Node();
		if (Proscanner.tokens.get(i).getValue().equals("(")) {

			i++;

			Node expr = expr_list();
			if (expr.retValue != 0) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {

					i++;
					if (Proscanner.tokens.get(i).getValue().equals(";")) {

						i++;
						result.setValue("( " + expr.getValue() + " )");
						result.retValue = 1;
						return result;
					}
				}
			}
		}
		result.retValue = 0;
		return result;
	}

	/**
	 * <expr list> --> empty | <non-empty expr list>
	 * 
	 * @return a Node
	 */
	private Node expr_list() {
		
		Node result = new Node();
		Node list = non_empty_expr_list();
		if (list.retValue != 0) {
			return list;
		}
		result.retValue = 2;
		return result;
	}

	/**
	 * <non-empty expr list> --> <expression> <non-empty expr list prime>
	 * 
	 * @return a Node
	 */
	private Node non_empty_expr_list() {
		
		Node result = new Node();
		Node expr = expression();
		if (expr.retValue != 0) {
			Node list = non_empty_expr_list_prime();
			if (list.retValue != 0) {
				result.setValue(expr.getValue() + list.getValue());
				result.retValue = 1;
				return result;
			}
		}
		result.retValue = 0;
		return result;
	}

	/**
	 * <non-empty expr list prime> --> comma <expression> <non-empty expr list
	 * prime> | empty
	 * 
	 * @return a Node
	 */
	private Node non_empty_expr_list_prime() {
		
		Node result = new Node();
		if (Proscanner.tokens.get(i).getValue().equals(",")) {

			i++;
			Node exp = expression();
			if (exp.retValue != 0) {
				Node list = non_empty_expr_list_prime();
				if (list.retValue != 2) {
					result.setValue("," + exp.getValue() + list.getValue());
					result.retValue = 1;
					return result;
				} else {
					result.setValue("," + exp.getValue());
					result.retValue = 1;
					return result;
				}

			}
			result.retValue = 2;
			return result;
		}
		result.retValue = 2;
		return result;
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
				Node cond = condition_expression();
				if (cond.retValue != 0) {
					if (Proscanner.tokens.get(i).getValue().equals(")")) {

						i++;
						
						Pair<TokenNames, String> tokenpair = null;
						while (finalFile.size() > 0) {

							tokenpair = finalFile.remove(0);
							if (true) {

								
								if (currentFunction == null || currentFunction.equals("main")) {

									printtokens.add(tokenpair.getValue());
								} else {

									storeFunCode.add(new ArrayList<String>(insideFunc));
									storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

								}

							}
						}
						int label = currentLabel;

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("if (" + cond.getValue() + " ) goto c" + currentLabel + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1)
									.add("if (" + cond.getValue() + " ) goto c" + currentLabel + ";" + "\n");

						}
						currentLabel++;

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("goto c" + currentLabel + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("goto c" + currentLabel + ";" + "\n");

						}
						currentLabel++;

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("c" + (currentLabel - 2) + ":;" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("c" + (currentLabel - 2) + ":;" + "\n");

						}
						brace = true;
						block_statements();

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("c" + (label + 1) + ":;" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("c" + (label + 1) + ":;" + "\n");

						}

						return true;
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
	private Node condition_expression() {
		
		Node result = new Node();
		Node cond = condition();
		if (cond.retValue != 0) {
			Node ce = condition_expression_Z();

			if (currentFunction == null || currentFunction.equals("main")) {

				printtokens.add("mem[base+" + currentCount + "]=" + cond.getValue() + ce.getValue() + ";" + "\n");
			} else {

				storeFunCode.add(new ArrayList<String>(insideFunc));
				storeFunCode.get(currFuncgng - 1)
						.add("mem[base+" + currentCount + "]=" + cond.getValue() + ce.getValue() + ";" + "\n");

			}
			result.setValue("mem[base+" + currentCount + "]");
			currentCount++;
			result.retValue = 1;
			return result;
		}
		result.retValue = 0;
		return result;
	}

	/**
	 * <condition expression Z> --> <condition op> <condition> | empty
	 * 
	 * @return a boolean
	 */
	private Node condition_expression_Z() {
		
		String comp = condition_op();
		Node result = new Node();
		if (comp != null) {
			Node cond = condition();
			result.setValue(comp + cond.getValue());
			result.retValue = 1;
			return result;
		}
		result.retValue = 2;
		return result;
	}

	/**
	 * <condition op> --> double_end_sign | double_or_sign
	 * 
	 * @return a String
	 */
	private String condition_op() {
		
		if (Proscanner.tokens.get(i).getValue().equals("&&") || Proscanner.tokens.get(i).getValue().equals("||")) {
			String x = Proscanner.tokens.get(i).getValue();
			i++;
			
			return x;
		}
		return null;
	}

	/**
	 * <condition> --> <expression> <comparison op> <expression>
	 * 
	 * @return a Node
	 */
	private Node condition() {
		
		Node result = new Node();
		Node expr = expression();
		if (expr.retValue == 1) {
			String op = comparison_op();
			if (op != null) {
				Node expr1 = expression();

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[base+" + currentCount + "]=" + expr.getValue() + " " + op + " "
							+ expr1.getValue() + ";");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("mem[base+" + currentCount + "]=" + expr.getValue()
							+ " " + op + " " + expr1.getValue() + ";");
				}
				result.setValue("mem[base+" + currentCount + "]");
				currentCount++;
				result.retValue = 1;
				return result;
			}
		}
		result.retValue = 0;
		return result;
	}

	/**
	 * <comparison op> --> == | != | > | >= | < | <=
	 * 
	 * @return a String
	 */
	private String comparison_op() {
		
		if (Proscanner.tokens.get(i).getValue().equals("==") || Proscanner.tokens.get(i).getValue().equals("!=")
				|| Proscanner.tokens.get(i).getValue().equals(">") || Proscanner.tokens.get(i).getValue().equals(">=")
				|| Proscanner.tokens.get(i).getValue().equals("<")
				|| Proscanner.tokens.get(i).getValue().equals("<=")) {
			String result = Proscanner.tokens.get(i).getValue();
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			finalFile.remove(finalFile.size() - 1);
			return result;
		}
		return null;
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

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("c" + currentLabel + ":;" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("c" + currentLabel + ":;" + "\n");
				}

				whileEntry = currentLabel;
				whileExit = currentLabel + 2;
				currentLabel++;
				Node cond = condition_expression();
				if (cond.retValue != 0) {
					if (Proscanner.tokens.get(i).getValue().equals(")")) {

						i++;
						int label = currentLabel - 1;

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("if(" + cond.getValue() + ") " + "goto c" + currentLabel + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1)
									.add("if(" + cond.getValue() + ") " + "goto c" + currentLabel + ";" + "\n");
						}
						currentLabel++;

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("goto c" + currentLabel + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("goto c" + currentLabel + ";" + "\n");
						}

						currentLabel++;

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("c" + (currentLabel - 2) + ":;" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("c" + (currentLabel - 2) + ":;" + "\n");
						}
						brace =true;
						block_statements();

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("goto c" + (label) + ";" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("goto c" + (label) + ";" + "\n");
						}

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add("c" + (label + 2) + ":;" + "\n");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add("c" + (label + 2) + ":;" + "\n");
						}

						return true;
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
			finalFile.add(Proscanner.tokens.get(i));
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
		
		Node expr = expression();
		if (expr.retValue == 1) {
			if (Proscanner.tokens.get(i).getValue().equals(";")) {
				finalFile.add(new Pair<TokenNames, String>(TokenNames.Identifiers, " " + expr.getValue()));
				finalFile.add(Proscanner.tokens.get(i));
				i++;
				if (!isMain) {
					Pair<TokenNames, String> localToken = new Pair<TokenNames, String>(TokenNames.None, "");
					for (Pair<TokenNames, String> tmpToken : finalFile) {
						if (tmpToken.getKey() == TokenNames.Identifiers) {
							localToken = tmpToken;
							break;
						}
					}

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("mem[base-2]=" + localToken.getValue() + ";" + "\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1)
								.add("mem[base-2]=" + localToken.getValue() + ";" + "\n");
					}

				}
				
				Pair<TokenNames, String> tokenpair = null;
				while (finalFile.size() > 0) {

					tokenpair = finalFile.remove(0);

				}

				if (isMain) {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("\n\njumpReg = mem[base-1];  \ngoto jumpTable;");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1).add("\n\njumpReg = mem[base-1];  \ngoto jumpTable;");

					}

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("\n\njumpTable:;\nswitch(jumpReg)\n{\n case 0: exit(0);");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1)
								.add("\n\njumpTable:;\nswitch(jumpReg)\n{\n case 0: exit(0);");

					}

					for (int i = 1; i < labelCounter; i++) {

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add(" case " + (i) + ":\n" + " goto label_" + (i) + ";");
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1)
									.add(" case " + (i) + ":\n" + " goto label_" + (i) + ";");

						}

					}

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add(" default:assert(0);\n}\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1).add(" default:assert(0);\n}\n");

					}

					int funcIndex = currFuncgng - 1;
					while (funcIndex >= 0) {

						int opendBrace = storeFunCode.get(funcIndex).indexOf("{") + 1;

						int closedBrace = storeFunCode.get(funcIndex).lastIndexOf("}") - 1;

						for (int code = opendBrace; code <= closedBrace; code++) {

							printtokens.add(storeFunCode.get(funcIndex).get(code));
						}

						funcIndex--;
					}

				} else {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("\ntop = mem[base-3];");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1).add("\ntop = mem[base-3];");

					}

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("\njumpReg = mem[base-1];\nbase = mem[base-4];\ngoto jumpTable;\n" + "\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1)
								.add("\njumpReg = mem[base-1];\nbase = mem[base-4];\ngoto jumpTable;\n" + "\n");

					}
				}

				while (finalFile.size() > 0) {

					tokenpair = finalFile.remove(0);
					if (true) {

						if (currentFunction == null || currentFunction.equals("main")) {

							printtokens.add(tokenpair.getValue());
						} else {

							storeFunCode.add(new ArrayList<String>(insideFunc));
							storeFunCode.get(currFuncgng - 1).add(tokenpair.getValue());

						}

					}
				}

				isMain = false;
				return true;
			}
			return false;
		}
		if (Proscanner.tokens.get(i).getValue().equals(";")) {
			finalFile.add(Proscanner.tokens.get(i));
			i++;
			
			Pair<TokenNames, String> tokenpair = null;
			while (finalFile.size() > 0) {

				tokenpair = finalFile.remove(0);
				if (true) {

					
					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add(tokenpair.getValue());
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng).add(tokenpair.getValue());

					}

				}
			}
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

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("goto c" + whileExit + ";" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("goto c" + whileExit + ";" + "\n");
				}
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

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("goto c" + whileEntry + ";" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("goto c" + whileEntry + ";" + "\n");
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * <expression> --> <term> <expression prime>
	 * 
	 * @return a Node
	 */
	private Node expression() {
	
		Node result = new Node();
		Node t = term();
		if (t.retValue == 1) {
			result.retValue = 1;
			Node epr = expression_prime(t.getValue());
			if (epr.retValue == 1) {
				return epr;
			} else {
				return t;
			}
		}
		result.retValue = 0;
		return result;
	}

	/**
	 * <expression prime> --> <addop> <term> <expression prime> | empty
	 * 
	 * @return
	 */

	private Node expression_prime(String tvalue) {
		
		Node result = new Node();
		String addop = addop();
		if (addop != null) {
			Node t = term();
			if (t.retValue != 0) {

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[base+" + currentCount + "]=" + tvalue + addop + t.getValue() + ";" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1)
							.add("mem[base+" + currentCount + "]=" + tvalue + addop + t.getValue() + ";" + "\n");
				}

				currentCount++;

				Node tp = expression_prime("mem[base+" + (currentCount - 1) + "]");
				if (tp.retValue == 2) {

					result.setValue("mem[base+" + (currentCount - 1) + "]");
					result.retValue = 1;
					return result;
				} else if (tp.retValue == 1) {
					return tp;
				}
			}
		}
		result.retValue = 2;
		return result;
	}

	/**
	 * <addop> --> plus_sign | minus_sign
	 * 
	 * @return a String
	 */
	private String addop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("+") || Proscanner.tokens.get(i).getValue().equals("-")) {
			Pair<TokenNames, String> tokenpair = Proscanner.tokens.get(i);
			finalFile.add(Proscanner.tokens.get(i));
			i++;

			finalFile.remove(finalFile.size() - 1);
			return tokenpair.getValue();
		}
		return null;
	}

	/**
	 * <term> --> <factor> <term prime>
	 * 
	 * @returns a Node
	 */
	private Node term() {
		
		Node result = new Node();
		Node fac = factor();
		if (fac.retValue != 0) {
			Node x = term_prime(fac.getValue());
			result.retValue = 1;
			if (x.retValue == 2) {
				result.setValue(fac.getValue());
				result.retValue = 1;
				return result;
			} else {
				return x;
			}
		}
		result.retValue = 0;
		return result;
	}

	/**
	 * <term prime> --> <mulop> <factor> <term prime> | empty
	 * 
	 * @return
	 */

	private Node term_prime(String fvalue) {
		
		Node result = new Node();
		String mulop = mulop();
		if (mulop != null) {
			Node t = factor();
			if (t.retValue != 0) {

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[base+" + currentCount + "]=" + fvalue + mulop + t.getValue() + ";" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1)
							.add("mem[base+" + currentCount + "]=" + fvalue + mulop + t.getValue() + ";" + "\n");
				}

				currentCount++;

				Node tp = term_prime("mem[base+" + (currentCount - 1) + "]");
				if (tp.retValue == 2) {

					result.setValue("mem[base+" + (currentCount - 1) + "]");
					result.retValue = 1;
					return result;
				} else if (tp.retValue == 1) {
					return tp;
				}
			}
		}
		result.retValue = 2;
		return result;
	}

	/**
	 * <mulop> --> star_sign | forward_slash
	 * 
	 * @return a string
	 */
	private String mulop() {
		
		if (Proscanner.tokens.get(i).getValue().equals("*") || Proscanner.tokens.get(i).getValue().equals("/")) {

			String returnmulop = Proscanner.tokens.get(i).getValue();
			i++;
			return returnmulop;
		}
		return null;
	}

	/**
	 * <factor> --> ID <factor Z> | NUMBER | minus_sign NUMBER |
	 * left_parenthesis <expression>right_parenthesis
	 * 
	 * @return
	 */
	private Node factor() {
		
		Node result = new Node();
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Identifiers) {
			Pair<TokenNames, String> lastElement = Proscanner.tokens.get(i);
			String idd = Proscanner.tokens.get(i).getValue();
			i++;
			Node x = factor_Z();

			if (x.retValue == 2) {
				if ((symtab.get(currentFunction).get(lastElement.getValue()) == null)
						&& symtab.get("global").get(lastElement.getValue()) == null) {

					if (paramTable.get(currentFunction).get(lastElement.getValue()) != null) {
						
						lastElement.setValue("mem[base -4-" + (paraStore.get(currentFunction).intValue() - paramTable.get(currentFunction).get(lastElement.getValue()).intValue()) + "]");
					}

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("mem[base+" + currentCount + "]=" + lastElement.getValue() + ";" + "\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1)
								.add("mem[base+" + currentCount + "]=" + lastElement.getValue() + ";" + "\n");
					}
					symtab.get(currentFunction).put(idd, currentCount);
					symtab.get(currentFunction).put(lastElement.getValue(), currentCount);

					currentCount++;

				}
				if (symtab.get("global").get(lastElement.getValue()) != null) {
					result.setValue("mem[" + symtab.get("global").get(lastElement.getValue()) + "]");

				} else

					result.setValue("mem[base+" + symtab.get(currentFunction).get(lastElement.getValue()) + "]");
				result.retValue = 1;
				return result;
			}
			
			else if (x.retValue == 3) {
				if (symtab.get(currentFunction).get(lastElement.getValue()) != null) {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("mem[base+" + currentCount + "]="
								+ symtab.get(currentFunction).get(lastElement.getValue()) + "+" + x.getValue()
								+ ";" + "\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1)
								.add("mem[base+" + currentCount + "]="
										+ symtab.get(currentFunction).get(lastElement.getValue()) + "+"
										+ x.getValue() + ";" + "\n");
					}

					result.setValue("mem[base+mem[base+" + currentCount + "]]");
				} else if (paramTable.get(currentFunction).get(lastElement.getValue()) != null) {

				} else {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("mem[base+" + currentCount + "]="
								+ symtab.get("global").get(lastElement.getValue()) + "+" + x.getValue() + ";"
								+ "\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1)
								.add("mem[base+" + currentCount + "]="
										+ symtab.get("global").get(lastElement.getValue()) + "+" + x.getValue()
										+ ";" + "\n");
					}

					result.setValue("mem[mem[base+" + currentCount + "]]");
				}
				currentCount++;
				result.retValue = 1;
				return result;
			}

			else {

				String parameterStr = x.getValue().replace("(", "").replace(")", "");
				String params[] = parameterStr.split(",");
				int paramCount = 0;
				int i = 0;
				paramCount = params.length;
				paramCount = paraStore.get(lastElement.getValue()).intValue();

				for (; i < paramCount; i++) {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("mem[top+" + i + "]=" + params[i] + ";" + "\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));
						storeFunCode.get(currFuncgng - 1).add("mem[top+" + i + "]=" + params[i] + ";" + "\n");
					}
				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[top+" + i + "]=base;" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("mem[top+" + i + "]=base;" + "\n");
				}

				i++;

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[top+" + i + "]=top;" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("mem[top+" + i + "]=top;" + "\n");
				}

				i++;

				i++;

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[top+" + i + "]=" + labelCounter + ";");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("mem[top+" + i + "]=" + labelCounter + ";");
				}

				i++;

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("base= top+" + (4 + paramCount) + ";" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("base= top+" + (4 + paramCount) + ";" + "\n");
				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("top=base+" + numberOfLocalVars.get(lastElement.getValue()).intValue() + ";");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1)
							.add("top=base+" + numberOfLocalVars.get(lastElement.getValue()).intValue() + ";");
				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("\ngoto " + lastElement.getValue() + "Func;\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("\ngoto " + lastElement.getValue() + "Func;\n");
				}

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("label_" + labelCounter + ":;" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1).add("label_" + labelCounter + ":;" + "\n");
				}

				labelCounter++;

				if (currentFunction == null || currentFunction.equals("main")) {

					printtokens.add("mem[base+" + currentCount + "]=mem[top+" + (paramCount + 2) + "];" + "\n");
				} else {

					storeFunCode.add(new ArrayList<String>(insideFunc));
					storeFunCode.get(currFuncgng - 1)
							.add("mem[base+" + currentCount + "]=mem[top+" + (paramCount + 2) + "];" + "\n");
				}

				result.setValue("mem[base+" + currentCount + "]");
				currentCount++;
				result.retValue = 1;
				return result;
			}
		}
		// NUMBER
		if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Numbers) {
			String num = (Proscanner.tokens.get(i).getValue());
			result.setValue(num);
			result.retValue = 1;
			i++;
			return result;
		}

		// minus_sign NUMBER
		if (Proscanner.tokens.get(i).getValue().equals("-")) {
			i++;
			if ((Proscanner.tokens.get(i).getKey()) == TokenNames.Numbers) {
				String num = Proscanner.tokens.get(i).getValue();
				result.setValue("-" + num);
				result.retValue = 1;
				i++;
				return result;
			}
			return result;
		}

		// left_parenthesis <expression>right_parenthesis
		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			i++;
			Node ex = expression();
			if (ex.retValue == 1) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {
					i++;
					result.setValue(ex.getValue());
					result.retValue = 1;
					return result;
				}
			}
			return result;
		}
		return result;
	}

	/**
	 * <factor Z> --> left_bracket <expression> right_bracket | left_parenthesis
	 * <expr list> right_parenthesis | empty
	 * 
	 * @return
	 */
	private Node factor_Z() {
		
		Node result = new Node();

		if (Proscanner.tokens.get(i).getValue().equals("[")) {
			i++;
			Node expr = expression();
			if (expr.retValue != 0) {
				if (Proscanner.tokens.get(i).getValue().equals("]")) {
					i++;
					result.setValue(expr.getValue());

					result.retValue = 3;
					return result;
				}
			}
			result.retValue = 0;
			return result;
		}

		if (Proscanner.tokens.get(i).getValue().equals("(")) {
			i++;

			Node expr = expr_list();
			if (expr.retValue != 0) {
				if (Proscanner.tokens.get(i).getValue().equals(")")) {

					if (currentFunction == null || currentFunction.equals("main")) {

						printtokens.add("\n\n//prologue of func\n");
					} else {

						storeFunCode.add(new ArrayList<String>(insideFunc));

						storeFunCode.get(currFuncgng - 1).add("\n\n//prologue of func\n");
					}

					i++;
					result.setValue("(" + expr.getValue() + ")");
					result.retValue = 1;
					return result;
				}
			}
			result.retValue = 0;
			return result;
		}

		result.retValue = 2;
		return result;
	}

}
