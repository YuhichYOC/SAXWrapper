package com.yoclabo.wrapper.xml;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XReaderTest {

    @Test
    public void test01() throws IOException {
        XReader r = new XReader();
        r.setDirectory(System.getProperty("user.home") + "/workspace/test_data");
        r.setFileName("test01.xml");
        r.parse();
        NodeEntity n = r.getNode();
        assertEquals(1, n.getChildren().size());
    }

    @Test
    public void test02() throws IOException {
        XReader r = new XReader();
        r.setDirectory(System.getProperty("user.home") + "/workspace/test_data");
        r.setFileName("test02.conf");
        r.parse();
        NodeEntity n = r.getNode();
        assertEquals(2, n.getChildren().size());
    }
}