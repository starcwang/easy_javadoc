package com.star.easydoc.action;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GenerateJavadocActionTest {

    @Test
    public void shouldStripWhitespaceAroundKdoc() {
        String kdoc = " /**\n"
            + "   * test\n"
            + "   * @param [name] name\n"
            + "   */\n";

        assertEquals(
            "/**\n"
                + "   * test\n"
                + "   * @param [name] name\n"
                + "   */",
            GenerateJavadocAction.normalizeKdoc(kdoc)
        );
    }
}
