package edu.ufl.cise.plc;

public class CodeGenStringBuilder {
    StringBuilder delegate;

    public CodeGenStringBuilder() {
        delegate = new StringBuilder();
    }

    public CodeGenStringBuilder append(String s){
        delegate.append(s);
        return this;
    }

    public CodeGenStringBuilder comma(){
        delegate.append(",");
        return this;
    }
}
