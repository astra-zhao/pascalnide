package com.duy.pascal.interperter.ast.instructions.conditional;

import com.duy.pascal.interperter.ast.codeunit.RuntimeExecutableCodeUnit;
import com.duy.pascal.interperter.ast.expressioncontext.CompileTimeContext;
import com.duy.pascal.interperter.ast.expressioncontext.ExpressionContext;
import com.duy.pascal.interperter.ast.instructions.Node;
import com.duy.pascal.interperter.ast.instructions.ExecutionResult;
import com.duy.pascal.interperter.ast.instructions.NopeInstruction;
import com.duy.pascal.interperter.ast.variablecontext.VariableContext;
import com.duy.pascal.interperter.ast.runtime.value.RuntimeValue;
import com.duy.pascal.interperter.ast.runtime.value.access.ConstantAccess;
import com.duy.pascal.interperter.debugable.DebuggableNode;
import com.duy.pascal.interperter.exceptions.parsing.syntax.WrongStatementException;
import com.duy.pascal.interperter.linenumber.LineInfo;
import com.duy.pascal.interperter.exceptions.parsing.convert.UnConvertibleTypeException;
import com.duy.pascal.interperter.exceptions.parsing.syntax.ExpectDoTokenException;
import com.duy.pascal.interperter.exceptions.parsing.syntax.ExpectedTokenException;
import com.duy.pascal.interperter.exceptions.runtime.RuntimePascalException;
import com.duy.pascal.interperter.tokens.Token;
import com.duy.pascal.interperter.tokens.basic.BasicToken;
import com.duy.pascal.interperter.tokens.basic.DoToken;
import com.duy.pascal.interperter.tokens.grouping.GrouperToken;
import com.duy.pascal.interperter.declaration.lang.types.BasicType;

public class WhileStatement extends DebuggableNode {
    private RuntimeValue condition;
    private Node command;
    private LineInfo line;

    /**
     * constructor
     * Declare while statement
     * <p>
     * while <condition> do <command>
     * <p>
     */
    public WhileStatement(ExpressionContext context, GrouperToken grouperToken, LineInfo lineNumber)
            throws Exception {

        //check condition return boolean type
        RuntimeValue condition = grouperToken.getNextExpression(context);
        RuntimeValue convert = BasicType.Boolean.convert(condition, context);
        if (convert == null) {
            throw new UnConvertibleTypeException(condition, BasicType.Boolean,
                    condition.getRuntimeType(context).declType, context);
        }

        //check "do' token
        Token next;
        next = grouperToken.take();
        if (!(next instanceof DoToken)) {
            if (next instanceof BasicToken) {
                throw new ExpectedTokenException("do", next);
            } else {
                throw new ExpectDoTokenException(next.getLineNumber(), WrongStatementException.Statement.WHILE_DO);
            }
        }

        //get command
        Node command = grouperToken.getNextCommand(context);

        this.condition = condition;
        this.command = command;
        this.line = lineNumber;
    }

    public WhileStatement(RuntimeValue condition, Node command,
                          LineInfo line) {
        this.condition = condition;
        this.command = command;
        this.line = line;
    }

    @Override
    public ExecutionResult executeImpl(VariableContext context,
                                       RuntimeExecutableCodeUnit<?> main) throws RuntimePascalException {
        while_loop:
        while ((Boolean) condition.getValue(context, main)) {
            switch (command.visit(context, main)) {
                case CONTINUE:
                    continue while_loop;
                case BREAK:
                    break while_loop;
                case EXIT:
                    return ExecutionResult.EXIT;
            }
        }
        return ExecutionResult.NOPE;
    }

    @Override
    public String toString() {
        return "while (" + condition + ") do " + command;
    }

    @Override
    public LineInfo getLineNumber() {
        return line;
    }

    @Override
    public Node compileTimeConstantTransform(CompileTimeContext c)
            throws Exception {
        Node comm = command.compileTimeConstantTransform(c);
        Object cond = condition.compileTimeValue(c);
        if (cond != null) {
            if (!((Boolean) cond)) {
                return new NopeInstruction(line);
            } else {
                return new WhileStatement(new ConstantAccess<>(true, condition.getLineNumber()),
                        comm, line);
            }
        }
        return new WhileStatement(condition, comm, line);
    }
}
