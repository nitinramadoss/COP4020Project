package edu.ufl.cise.plc;

public interface ILexer {

	IToken next() throws LexicalException;
	IToken peek() throws LexicalException;
}
