package edu.ufl.cise.plc;

import edu.ufl.cise.plc.ast.Declaration;

import java.util.HashMap;
import java.util.Objects;

public class SymbolTable {

    // Implement a symbol table class that is appropriate for this language.
    HashMap<String, Declaration> entries = new HashMap<>();
    String programName;

    public boolean insert(String name, Declaration dec) {
        if (Objects.equals(name, programName)) return false;
        return entries.putIfAbsent(name, dec) == null;
    }

    public Declaration lookup(String name) {
        return entries.get(name);
    }

    public void setProgramName(String name) {
        programName = name;
    }

    public boolean contains(String name) {
        return entries.containsKey(name);
    }

    public void remove(String name) {
        entries.remove(name);
    }
}


