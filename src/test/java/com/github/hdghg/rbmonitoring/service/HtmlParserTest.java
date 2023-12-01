package com.github.hdghg.rbmonitoring.service;

import com.github.hdghg.rbmonitoring.model.RbEntry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HtmlParserTest {

    @Test
    void testParsing() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/testpage.html")) {
            List<RbEntry> parse = new HtmlParser().parse(is);
            assertTrue(parse.get(0).isAlive());
            assertFalse(parse.get(1).isAlive());
        }
    }

}