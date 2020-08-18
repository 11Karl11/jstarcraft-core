package com.jstarcraft.core.antlr;

import org.antlr.v4.gui.TestRig;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Assert;
import org.junit.Test;

public class AntlrTestCase {

    @Test
    public void testAntlr() throws Exception {
        String formula = "0 + 1 - 2 * 3 / 4";
        CharStream characters = CharStreams.fromString(formula);
        CalculatorLexer lexer = new CalculatorLexer(characters); // 词法分析
        TokenStream tokens = new CommonTokenStream(lexer); // 转成token流
        CalculatorParser parser = new CalculatorParser(tokens); // 语法分析

        ParseTree tree = parser.formula();
        Assert.assertEquals(3, tree.getChildCount());
        Assert.assertEquals("0+1", tree.getChild(0).getText());
        Assert.assertEquals("-", tree.getChild(1).getText());
        Assert.assertEquals("2*3/4", tree.getChild(2).getText());

        TestRig rig = new TestRig(new String[] { "com.jstarcraft.core.antlr.Calculator", "formula", "-gui", "-tokens", "-tree", "src/test/resources/formula" });
        rig.process();
        Thread.sleep(5000L);
    }

}
