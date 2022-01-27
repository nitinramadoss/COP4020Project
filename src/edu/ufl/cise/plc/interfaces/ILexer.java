package edu.ufl.cise.plc.interfaces;

import edu.ufl.cise.plc.exceptions.LexicalException;

public interface ILexer {

	IToken next() throws LexicalException;
	IToken peek() throws LexicalException;
}
