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

package com.duy.pascal.ui.editor.highlight;

import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;

import com.duy.pascal.ui.editor.highlight.spans.BracketSpan;

/**
 * Created by Duy on 21-Jul-17.
 */

public class BracketHighlighter {
    private static final String TAG = "BracketHighlighter";
    private EditText editText;
    private IEditorColorScheme codeTheme;

    public BracketHighlighter(EditText editText, IEditorColorScheme codeTheme) {
        this.editText = editText;
        this.codeTheme = codeTheme;
    }

    public void setCodeTheme(IEditorColorScheme codeTheme) {
        this.codeTheme = codeTheme;
    }

    public void onSelectChange(int selStart, int selEnd) {
        try {
            if (selEnd > -1 && selEnd < editText.length()) {
                Editable text = editText.getText();
                char chatAtCursor = text.charAt(selEnd);
                boolean bracket = isBracket(chatAtCursor);
                if (bracket && isOpen(chatAtCursor)) { //open
                    findClose(chatAtCursor, selEnd);
                } else if (bracket) { //close
                    findOpen(chatAtCursor, selEnd);
                } else {
                    char before = selEnd > 0 ? text.charAt(selEnd - 1) : 0;
                    bracket = isBracket(before);
                    if (bracket && isOpen(before)) { //open
                        findClose(before, selEnd - 1);
                    } else if (bracket) {
                        findOpen(before, selEnd - 1);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isBracket(char chatAtCursor) {
        switch (chatAtCursor) {
            case '(':
            case '{':
            case '[':
            case ']':
            case '}':
            case ')':
                return true;
            default:
                return false;
        }
    }

    private void findClose(char open, int selEnd) {
        Editable text = editText.getText();
        int cursor = selEnd + 1;
        int count = 1;
        char close = getClose(open);

        boolean find = false;
        while (cursor < text.length()) {
            char chatAtCursor = text.charAt(cursor);
            if (chatAtCursor == open) {
                count++;
            } else if (chatAtCursor == close) {
                count--;
            }
            if (count == 0) {
                find = true;
                break;
            }
            cursor++;
        }
        BracketSpan[] spans = text.getSpans(0, text.length(), BracketSpan.class);
        for (BracketSpan span : spans) {
            text.removeSpan(span);
        }
        text.setSpan(new BracketSpan(codeTheme.getBracketColor(),
                codeTheme.getTextColor()), selEnd, selEnd + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (find) {
            text.setSpan(new BracketSpan(codeTheme.getBracketColor(),
                    codeTheme.getTextColor()), cursor, cursor + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }


    private void findOpen(char close, int selEnd) {
        Editable text = editText.getText();
        int cursor = selEnd - 1;
        int count = 1;
        boolean find = false;
        char open = getOpen(close);

        while (cursor > 0) {
            char chatAtCursor = text.charAt(cursor);
            if (chatAtCursor == open) {
                count--;
            } else if (chatAtCursor == close) {
                count++;
            }
            if (count == 0) {
                find = true;
                break;
            }
            cursor--;
        }
        BracketSpan[] spans = text.getSpans(0, text.length(), BracketSpan.class);
        for (BracketSpan span : spans) {
            text.removeSpan(span);
        }
        text.setSpan(new BracketSpan(codeTheme.getBracketColor(),
                codeTheme.getTextColor()), selEnd, selEnd + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (find) {
            text.setSpan(new BracketSpan(codeTheme.getBracketColor(),
                    codeTheme.getTextColor()), cursor, cursor + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private char getClose(char open) {
        switch (open) {
            case '[':
                return ']';
            case '{':
                return '}';
            case '(':
                return ')';
            default:
                return 0;
        }
    }

    private char getOpen(char close) {
        switch (close) {
            case ']':
                return '[';
            case '}':
                return '{';
            case ')':
                return '(';
            default:
                return 0;
        }
    }

    private boolean isOpen(char bracket) {
        return bracket == '(' || bracket == '[' || bracket == '{';
    }
}
