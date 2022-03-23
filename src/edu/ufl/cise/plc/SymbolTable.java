package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.Declaration;

import java.util.HashMap;

public class SymbolTable {

    // TODO:  Implement a symbol table class that is appropriate for this language.
    HashMap<String, Declaration> entries = new HashMap<>();

    public boolean insert(String name, Declaration dec) {
        return entries.putIfAbsent(name, dec) == null;
    }

    public Declaration lookup(String name) {
        return entries.get(name);
    }


}


