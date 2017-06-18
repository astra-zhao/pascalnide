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

package com.duy.pascal.backend.ast.instructions.assign_statement;

import android.support.annotation.NonNull;

import com.duy.pascal.backend.ast.codeunit.RuntimeExecutableCodeUnit;
import com.duy.pascal.backend.ast.expressioncontext.CompileTimeContext;
import com.duy.pascal.backend.ast.expressioncontext.ExpressionContext;
import com.duy.pascal.backend.ast.instructions.ExecutionResult;
import com.duy.pascal.backend.ast.variablecontext.VariableContext;
import com.duy.pascal.backend.ast.runtime_value.operators.BinaryOperatorEval;
import com.duy.pascal.backend.ast.runtime_value.references.Reference;
import com.duy.pascal.backend.ast.runtime_value.value.AssignableValue;
import com.duy.pascal.backend.ast.runtime_value.value.RuntimeValue;
import com.duy.pascal.backend.debugable.DebuggableExecutable;
import com.duy.pascal.backend.linenumber.LineInfo;
import com.duy.pascal.backend.parse_exception.ParsingException;
import com.duy.pascal.backend.runtime_exception.RuntimePascalException;
import com.duy.pascal.backend.declaration.types.OperatorTypes;
import com.duy.pascal.frontend.debug.CallStack;

/**
 * a *= b
 * Multiply a with b, and stores the result in a
 */
public class MulAssignStatement extends DebuggableExecutable implements AssignExecutable {
    private AssignableValue left;
    private RuntimeValue mulOp;
    private LineInfo line;

    public MulAssignStatement(@NonNull AssignableValue left, @NonNull RuntimeValue mulOp,
                              @NonNull LineInfo line) throws ParsingException {
        this.left = left;
        this.mulOp = mulOp;
        this.line = line;
    }

    public MulAssignStatement(@NonNull ExpressionContext f,
                              @NonNull AssignableValue left, RuntimeValue value,
                              LineInfo line) throws ParsingException {
        this.left = left;
        this.line = line;
        this.mulOp = BinaryOperatorEval.generateOp(f, left, value, OperatorTypes.MULTIPLY, line);
    }


    @Override
    @SuppressWarnings("unchecked")
    public ExecutionResult executeImpl(VariableContext context,
                                       RuntimeExecutableCodeUnit<?> main) throws RuntimePascalException {

        Reference ref = left.getReference(context, main);
        Object v = this.mulOp.getValue(context, main);
        ref.set(v);

        if (main.isDebug()) main.getDebugListener().onVariableChange(new CallStack(context));

        return ExecutionResult.NOPE;
    }

    @Override
    public String toString() {
        return left + " := " + mulOp;
    }

    @Override
    public LineInfo getLineNumber() {
        return this.line;
    }

    @Override
    public AssignExecutable compileTimeConstantTransform(CompileTimeContext c)
            throws ParsingException {
        return new MulAssignStatement(left, mulOp.compileTimeExpressionFold(c), line);
    }
}
