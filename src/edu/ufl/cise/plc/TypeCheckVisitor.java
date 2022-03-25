package edu.ufl.cise.plc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ufl.cise.plc.IToken.Kind;
import edu.ufl.cise.plc.ast.*;
import edu.ufl.cise.plc.ast.Types.Type;

import javax.naming.Name;

import static edu.ufl.cise.plc.ast.Types.Type.*;

public class TypeCheckVisitor implements ASTVisitor {

	SymbolTable symbolTable = new SymbolTable();
	Program root;
	
	record Pair<T0,T1>(T0 t0, T1 t1) {

	};  // may be useful for constructing lookup tables.
	
	private void check(boolean condition, ASTNode node, String message) throws TypeCheckException {
		if (!condition) {
			throw new TypeCheckException(message, node.getSourceLoc());
		}
	}
	
	//The type of a BooleanLitExpr is always BOOLEAN.  
	//Set the type in AST Node for later passes (code generation)
	//Return the type for convenience in this visitor.  
	@Override
	public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws Exception {
		booleanLitExpr.setType(Type.BOOLEAN);
		return Type.BOOLEAN;
	}

	@Override
	public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws Exception {
		stringLitExpr.setType(Type.STRING);
		return Type.STRING;
	}

	@Override
	public Object visitIntLitExpr(IntLitExpr intLitExpr, Object arg) throws Exception {
		intLitExpr.setType(Type.INT);
		return Type.INT;
	}

	@Override
	public Object visitFloatLitExpr(FloatLitExpr floatLitExpr, Object arg) throws Exception {
		floatLitExpr.setType(Type.FLOAT);
		return Type.FLOAT;
	}

	@Override
	public Object visitColorConstExpr(ColorConstExpr colorConstExpr, Object arg) throws Exception {
		colorConstExpr.setType(Type.COLOR);
		return Type.COLOR;
	}

	@Override
	public Object visitConsoleExpr(ConsoleExpr consoleExpr, Object arg) throws Exception {
		consoleExpr.setType(Type.CONSOLE);
		return Type.CONSOLE;
	}
	
	//Visits the child expressions to get their type (and ensure they are correctly typed)
	//then checks the given conditions.
	@Override
	public Object visitColorExpr(ColorExpr colorExpr, Object arg) throws Exception {
		Type redType = (Type) colorExpr.getRed().visit(this, arg);
		Type greenType = (Type) colorExpr.getGreen().visit(this, arg);
		Type blueType = (Type) colorExpr.getBlue().visit(this, arg);
		check(redType == greenType && redType == blueType, colorExpr, "color components must have same type");
		check(redType == Type.INT || redType == Type.FLOAT, colorExpr, "color component type must be int or float");
		Type exprType = (redType == Type.INT) ? Type.COLOR : Type.COLORFLOAT;
		colorExpr.setType(exprType);
		return exprType;
	}	

	
	
	//Maps forms a lookup table that maps an operator expression pair into result type.  
	//This more convenient than a long chain of if-else statements. 
	//Given combinations are legal; if the operator expression pair is not in the map, it is an error. 
	Map<Pair<Kind,Type>, Type> unaryExprs = Map.of(
			new Pair<Kind,Type>(Kind.BANG,BOOLEAN), BOOLEAN,
			new Pair<Kind,Type>(Kind.MINUS, FLOAT), FLOAT,
			new Pair<Kind,Type>(Kind.MINUS, INT),INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,INT), INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,COLOR), INT,
			new Pair<Kind,Type>(Kind.COLOR_OP,IMAGE), IMAGE,
			new Pair<Kind,Type>(Kind.IMAGE_OP,IMAGE), INT
			);
	
	//Visits the child expression to get the type, then uses the above table to determine the result type
	//and check that this node represents a legal combination of operator and expression type. 
	@Override
	public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws Exception {
		// !, -, getRed, getGreen, getBlue
		Kind op = unaryExpr.getOp().getKind();
		Type exprType = (Type) unaryExpr.getExpr().visit(this, arg);
		//Use the lookup table above to both check for a legal combination of operator and expression, and to get result type.
		Type resultType = unaryExprs.get(new Pair<>(op, exprType));
		check(resultType != null, unaryExpr, "incompatible types for unaryExpr");
		//Save the type of the unary expression in the AST node for use in code generation later. 
		unaryExpr.setType(resultType);
		//return the type for convenience in this visitor.
		return resultType;
	}


	//This method has several cases. Work incrementally and test as you go. 
	@Override
	public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws Exception {
		Kind op = binaryExpr.getOp().getKind();
		Type lType = (Type) binaryExpr.getLeft().visit(this, arg);
		Type rType = (Type) binaryExpr.getRight().visit(this, arg);

		Type resultType = null;
		switch (op) { // EQUALS, NOT_EQUALS, PLUS, MINUS, TIMES, DIV, MOD, LT, LE, GT, GE
			case AND, OR -> {
				check(lType == Type.BOOLEAN && rType == Type.BOOLEAN, binaryExpr, "Booleans required");
				resultType = Type.BOOLEAN;
			}
			case EQUALS, NOT_EQUALS -> {
				check(lType == rType, binaryExpr, "Incompatible types for comparison");
				resultType = Type.BOOLEAN;
			}
			case PLUS, MINUS -> {
				if (lType == Type.INT && rType == Type.INT) {
					resultType = Type.INT;
				}
				else if (lType == Type.FLOAT && rType == Type.FLOAT) {
					resultType = Type.FLOAT;
				}
				else if (lType == Type.FLOAT && rType == Type.INT) {
					// Coerce to float
					binaryExpr.getRight().setCoerceTo(Type.FLOAT);
					resultType = Type.FLOAT;
				}
				else if (lType == Type.INT && rType == Type.FLOAT) {
					// Coerce to float
					binaryExpr.getLeft().setCoerceTo(Type.FLOAT);
					resultType = Type.FLOAT;
				}
				else if (lType == Type.COLOR && rType == Type.COLOR) {
					resultType = Type.COLOR;
				}
				else if (lType == Type.COLORFLOAT && rType == Type.COLORFLOAT) {
					resultType = Type.COLORFLOAT;
				}
				else if (lType == Type.COLORFLOAT && rType == Type.COLOR) {
					// Coerce to colorfloat
					binaryExpr.getRight().setCoerceTo(Type.COLORFLOAT);
					resultType = Type.COLORFLOAT;
				}
				else if (lType == Type.COLOR && rType == Type.COLORFLOAT) {
					// Coerce to colorfloat
					binaryExpr.getLeft().setCoerceTo(Type.COLORFLOAT);
					resultType = Type.COLORFLOAT;
				}
				else if (lType == Type.IMAGE && rType == Type.IMAGE) {
					resultType = Type.IMAGE;
				}
				else check(false, binaryExpr, "incompatible types for operator");
			}
			case TIMES, DIV, MOD -> {
				if (lType == Type.IMAGE && rType == Type.INT) {
					resultType = Type.IMAGE;
				}
				else if (lType == Type.IMAGE && rType == Type.FLOAT) {
					resultType = Type.IMAGE;
				}
				else if (lType == Type.INT && rType == Type.COLOR) {
					binaryExpr.getLeft().setCoerceTo(Type.COLOR);
					resultType = Type.COLOR;
				}
				else if (lType == Type.COLOR && rType == Type.INT) {
					binaryExpr.getRight().setCoerceTo(Type.COLOR);
					resultType = Type.COLOR;
				}
				else if (lType == Type.FLOAT && rType == Type.COLOR) {
					binaryExpr.getLeft().setCoerceTo(Type.COLORFLOAT);
					binaryExpr.getRight().setCoerceTo(Type.COLORFLOAT);
					resultType = Type.COLORFLOAT;
				}
				else check(false, binaryExpr, "incompatible types for operator");
			}
			case LT, LE, GT, GE -> {
				if (lType == Type.INT && rType == Type.INT) {
					resultType = Type.BOOLEAN;
				}
				else if (lType == Type.FLOAT && rType == Type.FLOAT) {
					resultType = Type.BOOLEAN;
				}
				else if (lType == Type.INT && rType == Type.FLOAT) {
					binaryExpr.getLeft().setCoerceTo(Type.FLOAT);
					resultType = Type.BOOLEAN;
				}
				else if (lType == Type.FLOAT && rType == Type.INT) {
					binaryExpr.getRight().setCoerceTo(Type.FLOAT);
					resultType = Type.BOOLEAN;
				}
				else check(false, binaryExpr, "incompatible types for operator");
			}
			default -> check(false, binaryExpr, "use a real operator");
		}
		binaryExpr.setType(resultType);
		return resultType;
	}

	@Override
	public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws Exception {
		String name = identExpr.getText();
		Declaration dec = symbolTable.lookup(name);
		check(dec != null, identExpr, "Undefined identifier " + name);
		check(dec.isInitialized(), identExpr, "Uninitialized identifier used: " + name);

		identExpr.setDec(dec); // Useful later, apparently

		Type type = dec.getType();
		identExpr.setType(type);
		return type;
	}

	@Override
	public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws Exception {
		Type conditionalType = (Type) conditionalExpr.getCondition().visit(this, arg);

		Type trueCaseType = (Type) conditionalExpr.getTrueCase().visit(this, arg);
		Type falseCaseType = (Type) conditionalExpr.getFalseCase().visit(this, arg);

		check(conditionalType == Type.BOOLEAN, conditionalExpr, "Condition case must be boolean!");
		check(trueCaseType == falseCaseType, conditionalExpr, "True case type must equal false case type!");

		conditionalExpr.setType(trueCaseType);
		return trueCaseType;
	}

	@Override
	public Object visitDimension(Dimension dimension, Object arg) throws Exception {
		Type expr1Type = (Type) dimension.getWidth().visit(this, arg);
		Type expr2Type = (Type) dimension.getHeight().visit(this, arg);

		check(expr1Type == Type.INT, dimension.getWidth(), "Width is not of type int!");
		check(expr2Type == Type.INT, dimension.getHeight(), "Height is not of type int!");
		return null;
	}

	@Override
	//This method can only be used to check PixelSelector objects on the right hand side of an assignment. 
	//Either modify to pass in context info and add code to handle both cases, or when on left side
	//of assignment, check fields from parent assignment statement.
	public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws Exception {
		Type xType = (Type) pixelSelector.getX().visit(this, arg);
		check(xType == Type.INT, pixelSelector.getX(), "only ints as pixel selector components");
		Type yType = (Type) pixelSelector.getY().visit(this, arg);
		check(yType == Type.INT, pixelSelector.getY(), "only ints as pixel selector components");
		return null;
	}

	@Override
	//This method several cases--you don't have to implement them all at once.
	//Work incrementally and systematically, testing as you go.  
	public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws Exception {
		Type targetType = (Type) symbolTable.lookup(assignmentStatement.getName()).visit(this, arg);
		Declaration dec = assignmentStatement.getTargetDec();

		boolean initialized = dec.isInitialized();
		check(initialized, dec, "Target variable is not initialized!");

		Expr expr = assignmentStatement.getExpr();
		Type exprType = (Type) assignmentStatement.getExpr().visit(this, arg);

		if (targetType != Type.IMAGE) {
			// check not pixel selector on lhs?
			boolean assignmentCompatible = targetType == exprType;

			if (targetType == Type.INT && exprType == Type.FLOAT) {
				expr.setCoerceTo(Type.INT);
				assignmentCompatible = true;
			} else if (targetType == Type.FLOAT && exprType == Type.INT) {
				expr.setCoerceTo(Type.FLOAT);
				assignmentCompatible = true;
			} else if (targetType == Type.INT && exprType == Type.COLOR) {
				expr.setCoerceTo(Type.INT);
				assignmentCompatible = true;
			} else if (targetType == Type.COLOR && exprType == Type.INT) {
				expr.setCoerceTo(Type.COLOR);
				assignmentCompatible = true;
			}
			check(assignmentCompatible, assignmentStatement, "Types are not assignment compatible");
		}
		else if (assignmentStatement.getSelector() == null) {
			boolean assignmentCompatible = exprType == targetType;

			if (exprType == Type.INT) {
				expr.setCoerceTo(Type.COLOR);
				assignmentCompatible = true;
			} else if (exprType == Type.FLOAT) {
				expr.setCoerceTo(Type.COLORFLOAT);
				assignmentCompatible = true;
			} else if (exprType == Type.COLOR) {
				assignmentCompatible = true;
			} else if (exprType == Type.COLORFLOAT) {
				assignmentCompatible = true;
			}
			check(assignmentCompatible, assignmentStatement, "Types are not assignment compatible");
		}
		else if (assignmentStatement.getSelector() != null) {
			check(false, dec, "This part has not been done yet!");

			PixelSelector selector = assignmentStatement.getSelector();
			Expr x = selector.getX();
			Expr y = selector.getY();

			// Make sure x and y are not declared yet
			check(x.getClass() == IdentExpr.class && y.getClass() == IdentExpr.class, assignmentStatement, "x and y must be IdentExpr's!");
			check(!symbolTable.contains(x.getText()) && !symbolTable.contains(y.getText()), assignmentStatement, "x and y have already been declared!");

			// Create declaration for these names, add to symbol table
			NameDef xNameDef = new NameDef(x.getFirstToken(), x.getText(), (String) x.visit(this, arg));
			NameDef yNameDef = new NameDef(y.getFirstToken(), y.getText(), (String) y.visit(this, arg));
			symbolTable.insert(x.getText(), xNameDef);
			symbolTable.insert(y.getText(), yNameDef);

			// Process RHS
			Type rhs = (Type) assignmentStatement.getExpr().visit(this, arg);
			if (rhs == Type.COLOR || rhs == Type.COLORFLOAT || rhs == Type.FLOAT || rhs == Type.INT) {
				assignmentStatement.getExpr().setCoerceTo(Type.COLOR);
			}
			else {
				check(false, assignmentStatement, "RHS must be COLOR, COLORFLOAT, FLOAT, or INT!");
			}

			// Remove names from symbol table
			symbolTable.remove(x.getText());
			symbolTable.remove(y.getText());
		}



		throw new UnsupportedOperationException("Unimplemented visit method.");
	}


	@Override
	public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws Exception {
		Type sourceType = (Type) writeStatement.getSource().visit(this, arg);
		Type destType = (Type) writeStatement.getDest().visit(this, arg);
		check(destType == Type.STRING || destType == Type.CONSOLE, writeStatement,
				"illegal destination type for write");
		check(sourceType != Type.CONSOLE, writeStatement, "illegal source type for write");
		return null;
	}

	@Override
	public Object visitReadStatement(ReadStatement readStatement, Object arg) throws Exception {
		Declaration target = symbolTable.lookup(readStatement.getName());
		check(target != null, readStatement, "target variable uninitialized");

		Type targetType = target.getType();
		check(targetType != null, readStatement.getTargetDec(), "Target not declared!");
		check(readStatement.getSelector() == null, readStatement.getSelector(), "Cannot have a pixel selector!");

		Type exprType = (Type) readStatement.getSource().visit(this, arg);

		check(exprType == Type.CONSOLE || exprType == Type.STRING, readStatement.getSource(), "RHS must be of type console or string!");

		target.setInitialized(true);

		return null;
	}

	@Override
	public Object visitVarDeclaration(VarDeclaration declaration, Object arg) throws Exception {
		Type nameType = (Type) declaration.getNameDef().visit(this, arg);
		Type exprType;
		boolean isInitialized = declaration.getExpr() != null;

		if (isInitialized) {
			Expr expr = declaration.getExpr();
			exprType = (Type) declaration.getExpr().visit(this, arg);

			if (nameType == Type.IMAGE) {
				if (exprType == Type.INT) {
					expr.setCoerceTo(Type.COLOR);
				} else if (exprType == Type.FLOAT) {
					expr.setCoerceTo(Type.COLORFLOAT);
				}
			}

			if (nameType == Type.INT && exprType == Type.FLOAT) {
				expr.setCoerceTo(Type.INT);
			} else if (nameType == Type.FLOAT && exprType == Type.INT) {
				expr.setCoerceTo(Type.FLOAT);
			} else if (nameType == Type.INT && exprType == Type.COLOR) {
				expr.setCoerceTo(Type.INT);
			} else if (nameType == Type.COLOR && exprType == Type.INT) {
				expr.setCoerceTo(Type.COLOR);
			}

			boolean valid1 = nameType == Type.IMAGE && expr.getCoerceTo() == Type.COLOR || nameType == Type.IMAGE && exprType == Type.COLOR ||
					nameType == Type.IMAGE && expr.getCoerceTo() == Type.COLORFLOAT || nameType == Type.IMAGE && exprType == Type.COLORFLOAT;
			boolean valid2 = nameType == expr.getCoerceTo() || nameType == exprType;

			check(valid1 || valid2, declaration, "Types of LHS and RHS are not compatible!");

			declaration.setInitialized(true);
		}



		return declaration.getNameDef().getType();
	}


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		
		// Save root of AST so return type can be accessed in return statements
		root = program;

		// Add program name to symbol table (Mark name as initialized)
		String programName = program.getName();
		symbolTable.setProgramName(programName);

		// Check parameters
		List<NameDef> parameters = program.getParams();
		for (NameDef nameDef : parameters) {
			nameDef.visit(this, arg);
			nameDef.setInitialized(true);
		}

		// Check declarations and statements
		List<ASTNode> decsAndStatements = program.getDecsAndStatements();
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		return program;
	}

	@Override
	public Object visitNameDef(NameDef nameDef, Object arg) throws Exception {
		boolean unique = symbolTable.insert(nameDef.getName(), nameDef);
		check(unique, nameDef, "Variable declared twice!");

		return nameDef.getType();
	}

	@Override
	public Object visitNameDefWithDim(NameDefWithDim nameDefWithDim, Object arg) throws Exception {
		boolean unique = symbolTable.insert(nameDefWithDim.getName(), nameDefWithDim);
		check(unique, nameDefWithDim, "Variable declared twice!");

		return nameDefWithDim.getType();
	}
 
	@Override
	public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws Exception {
		Type returnType = root.getReturnType();  //This is why we save program in visitProgram.
		Type expressionType = (Type) returnStatement.getExpr().visit(this, arg);
		check(returnType == expressionType, returnStatement, "return statement with invalid type");
		return null;
	}

	@Override
	public Object visitUnaryExprPostfix(UnaryExprPostfix unaryExprPostfix, Object arg) throws Exception {
		Type expType = (Type) unaryExprPostfix.getExpr().visit(this, arg);
		check(expType == Type.IMAGE, unaryExprPostfix, "pixel selector can only be applied to image");
		unaryExprPostfix.getSelector().visit(this, arg);
		unaryExprPostfix.setType(Type.INT);
		unaryExprPostfix.setCoerceTo(COLOR);
		return Type.COLOR;
	}

}
