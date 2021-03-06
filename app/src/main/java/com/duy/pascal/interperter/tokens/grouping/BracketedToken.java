package com.duy.pascal.interperter.tokens.grouping;


import android.support.annotation.NonNull;

import com.duy.pascal.interperter.linenumber.LineNumber;
import com.duy.pascal.interperter.tokens.Token;

public class BracketedToken extends GrouperToken {

    public BracketedToken(LineNumber line) {
        super(line);
    }

    @Override
    public String toString() {
        return "[";
    }

    @NonNull
    @Override
    public String toCode() {
        StringBuilder tmp = new StringBuilder("[");
        if (next != null) {
            tmp.append(next.toCode());
        }
        for (Token t : this.queue) {
            tmp.append(t.toCode()).append(' ');
        }
        tmp.append(']');
        return tmp.toString();
    }

    @Override
    protected String getClosingText() {
        return "]";
    }

    @Override
    public Precedence getOperatorPrecedence() {
        return Precedence.Dereferencing;
    }


}
