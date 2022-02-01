package edu.ufl.cise.plc;

public class Token implements IToken {
    private final Kind kind;
    private final String literal;
    private final SourceLocation location;

    public Token(Kind kind, String literal, SourceLocation location) {
        this.kind = kind;
        this.literal = literal;
        this.location = location;
    }

    @Override
    public Kind getKind() {
        return kind;
    }

    @Override
    public String getLiteral() {
        return literal;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return null;
    }

    @Override
    public int getIntValue() {
        return Integer.parseInt(literal);
    }

    @Override
    public float getFloatValue() {
        return Float.parseFloat(literal);
    }

    @Override
    public boolean getBooleanValue() {
        return Boolean.parseBoolean(literal);
    }

    @Override
    public String getStringValue() {
        return literal;
    }
}
