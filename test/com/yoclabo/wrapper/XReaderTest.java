package com.yoclabo.wrapper;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XReaderTest {

    @Test
    public void test01() throws IOException {
        XReader r = new XReader();
        r.setDirectory("/Users/yoshiiyuuichi/IdeaProjects/SAXWrapper/out/test/SAXWrapper/com/yoclabo/wrapper");
        r.setFileName("test01.xml");
        r.parse();
        NodeEntity n = r.getNode();
        assertEquals(1, n.getChildren().size());
    }
}