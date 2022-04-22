package edu.ufl.cise.plc;

public class CodeGenStringBuilder {
    private StringBuilder delegate;

    public CodeGenStringBuilder() {
        delegate = new StringBuilder();
    }

    public String getString() {
        return delegate.toString();
    }

    public CodeGenStringBuilder append(String s){
        delegate.append(s);
        return this;
    }

    public CodeGenStringBuilder comma(){
        delegate.append(",");
        return this;
    }

    public CodeGenStringBuilder semi(){
        delegate.append(";");
        return this;
    }

    public CodeGenStringBuilder lparen(){
        delegate.append("(");
        return this;
    }

    public CodeGenStringBuilder rparen(){
        delegate.append(")");
        return this;
    }

    public CodeGenStringBuilder newline(){
        delegate.append("\n");
        return this;
    }

    public CodeGenStringBuilder quotes(){
        delegate.append("\"\"\"");
        return this;
    }

    public CodeGenStringBuilder quote(){
        delegate.append("\"");
        return this;
    }

    public CodeGenStringBuilder space(){
        delegate.append(" ");
        return this;
    }

    public CodeGenStringBuilder eq(){
        delegate.append("=");
        return this;
    }

    public CodeGenStringBuilder question(){
        delegate.append("?");
        return this;
    }

    public CodeGenStringBuilder colon(){
        delegate.append(":");
        return this;
    }

    public CodeGenStringBuilder tab(){
        delegate.append("\t");
        return this;
    }

    public CodeGenStringBuilder type(String type){
        delegate.append("(").append(type).append(")");
        return this;
    }
}
