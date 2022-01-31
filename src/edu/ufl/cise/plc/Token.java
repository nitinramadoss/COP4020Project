package edu.ufl.cise.plc;

public class Token implements IToken {
    private Kind kind;
    private String text;

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return null;
    }

    @Override
    public int getIntValue() {
        return Integer.parseInt(text);
    }

    @Override
    public float getFloatValue() {
        return Float.parseFloat(text);
    }

    @Override
    public boolean getBooleanValue() {
        return Boolean.parseBoolean(text);
    }

    @Override
    public String getStringValue() {
        return text;
    }
}
