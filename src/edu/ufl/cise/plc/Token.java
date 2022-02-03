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
    public String getText() {
        return literal;
    }

    @Override
    public SourceLocation getSourceLocation() {
        return location;
    }

    @Override
    public int getIntValue() {
        try {
            return Integer.parseInt(literal);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public float getFloatValue() {
        try {
            return Float.parseFloat(literal);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean getBooleanValue() {
        try {
            return Boolean.parseBoolean(literal);
        }
        catch (Exception e) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getStringValue() {
        return literal;
    }
}
