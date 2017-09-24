/*
 *  Copyright (c) 2017 Tran Le Duy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duy.pascal.interperter.tokens.value;

import com.duy.pascal.interperter.linenumber.LineInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public  class CharacterToken extends ValueToken {
    private char aChar;
    private String origin;
    private boolean isRaw;

    public CharacterToken(@Nullable LineInfo line, char character) {
        super(line);
        this.aChar = 32;
        this.aChar = character;
        this.origin = "#" + character;
        if (line != null) {
            line.setLength(this.toCode().length());
        }

    }

    public CharacterToken(@NotNull LineInfo line, @NotNull String nonParse) {
        super(line);
        this.aChar = 32;
        try {
            String e = nonParse.substring(1, nonParse.length());
            int value = Integer.parseInt(e);
            this.aChar = (char) value;
        } catch (Exception var7) {
            this.aChar = 63;
        }

        this.isRaw = true;
        this.origin = nonParse;
    }

    @NotNull
    public String toCode() {
        return !this.isRaw ? "\'" + Character.toString(this.aChar) + "\'" : this.origin;
    }

    @NotNull
    public String toString() {
        return this.toCode();
    }

    @NotNull
    public Object getValue() {
        return this.aChar;
    }
}