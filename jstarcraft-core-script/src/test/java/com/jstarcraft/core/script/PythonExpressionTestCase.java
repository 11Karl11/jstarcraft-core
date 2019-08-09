package com.jstarcraft.core.script;

import org.junit.BeforeClass;

import com.jstarcraft.core.utility.StringUtility;

public class PythonExpressionTestCase extends ScriptExpressionTestCase {

    private String method = "_data = fibonacciMethod(number);";

    private String object = "mock = Mock(index, 'birdy', 'mickey' + bytes(index), size, Instant.now(), MockEnumeration.TERRAN); mock.toString(); _data = mock";

    private String fibonacci = "fibonacci = [0.0] * (size + 1)\r\nfibonacci[0] = 0.0\r\nfibonacci[1] = 1.0\r\nfor index in range(2, size + 1):\r\n\tfibonacci[index] = fibonacci[index - 2] + fibonacci[index - 1]\r\n_data = fibonacci[size]";

    @BeforeClass
    public static void setProperty() {
        System.setProperty("python.console.encoding", StringUtility.CHARSET.name());
    }

    @Override
    protected ScriptExpression getMethodExpression(ScriptContext context, ScriptScope scope) {
        PythonExpression expression = new PythonExpression(context, scope, method);
        return expression;
    }

    @Override
    protected ScriptExpression getObjectExpression(ScriptContext context, ScriptScope scope) {
        PythonExpression expression = new PythonExpression(context, scope, object);
        return expression;
    }

    @Override
    protected ScriptExpression getFibonacciExpression(ScriptContext context, ScriptScope scope) {
        PythonExpression expression = new PythonExpression(context, scope, fibonacci);
        return expression;
    }

}
