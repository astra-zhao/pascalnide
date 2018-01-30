package com.duy.pascal.interperter.tokens.basic;

import android.support.annotation.Nullable;

import com.duy.pascal.interperter.linenumber.LineNumber;
import com.duy.pascal.interperter.tokens.Token;

public abstract class BasicToken extends Token {

    public BasicToken(@Nullable LineNumber line) {
        super(line);
        if (this.line != null) {
            this.line.setLength(toString().length());
        }
    }

    public abstract String toString();

}
