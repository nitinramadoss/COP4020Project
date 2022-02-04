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
        StringBuilder replaced = new StringBuilder();

        for (int i = 1; i < literal.length()-1; i++) {
            if (literal.charAt(i) == '\\') {
                if (literal.charAt(i+1) == 'n') {
                    replaced.append('\n');
                } else if (literal.charAt(i+1) == '"') {
                    replaced.append('"');
                } else if (literal.charAt(i+1) == 'r') {
                    replaced.append('\r');
                } else if (literal.charAt(i+1) == 'b') {
                    replaced.append('\b');
                } else if (literal.charAt(i+1) == 't') {
                    replaced.append('\t');
                } else if (literal.charAt(i+1) == 'f') {
                    replaced.append('\f');
                } else if (literal.charAt(i+1) == '\'') {
                    replaced.append('\'');
                } else {
                    replaced.append('\\');
                }

                i++;
            } else {
                replaced.append(literal.charAt(i));
            }
        }

        return replaced.toString();
       // return literal.replace("\\n", "\n").replace("\"", "").replace("\\\\", "\\");
    }
}
