/*
 *
 * XReader.java
 *
 * Copyright 2019 Yuichi Yoshii
 *     吉井雄一 @ 吉井産業  you.65535.kir@gmail.com
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
 *
 */

package com.yoclabo.wrapper.xml;

import com.yoclabo.reader.xml.NodeEntity;
import com.yoclabo.reader.xml.SAXReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class XReader {

    private String directory;
    private String fileName;
    private com.yoclabo.wrapper.xml.NodeEntity node;
    private int currentNodeId;
    private int depth;

    public XReader() {
        currentNodeId = 1;
        depth = 1;
    }

    public void setDirectory(String arg) {
        directory = arg;
    }

    public void setFileName(String arg) {
        fileName = arg;
    }

    private String getFullPath() {
        return directory + "/" + fileName;
    }

    public com.yoclabo.wrapper.xml.NodeEntity getNode() {
        return node;
    }

    public void parse() throws IOException {
        node = new com.yoclabo.wrapper.xml.NodeEntity();
        node.setNodeName(fileName);
        node.setNodeId(0);
        node.setDepth(0);
        node.setType(NodeEntity.TYPE.ELEMENT);
        SAXReader r = new SAXReader(readToEnd());
        do {
            com.yoclabo.reader.xml.NodeEntity n = r.next();
            parseDeclaration(n);
            parseElement(n);
            parseEmpty(n);
            parseText(n);
            parseEndElement(n);
            parseComment(n);
        } while (!r.isEOF());
    }

    private String readToEnd() throws IOException {
        StringBuilder ret = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new FileReader(getFullPath()))) {
            r.lines().forEach(l -> ret.append(l));
        }
        return ret.toString();
    }

    private void parseDeclaration(com.yoclabo.reader.xml.NodeEntity n) {
        if (NodeEntity.TYPE.XML_DECLARATION != n.getType()) {
            return;
        }
        com.yoclabo.wrapper.xml.NodeEntity newNode = new com.yoclabo.wrapper.xml.NodeEntity();
        newNode.setNodeName("Declaration");
        newNode.setNodeId(currentNodeId);
        newNode.setDepth(depth);
        newNode.setType(NodeEntity.TYPE.XML_DECLARATION);
        ++currentNodeId;
        node.findTail(depth).addChild(newNode);
    }

    private void parseElement(com.yoclabo.reader.xml.NodeEntity n) {
        if (NodeEntity.TYPE.ELEMENT != n.getType()) {
            return;
        }
        com.yoclabo.wrapper.xml.NodeEntity newNode = new com.yoclabo.wrapper.xml.NodeEntity();
        newNode.setNodeName(n.getNodeName());
        newNode.setNodeId(currentNodeId);
        newNode.setDepth(depth);
        newNode.setType(NodeEntity.TYPE.ELEMENT);
        ++currentNodeId;
        node.findTail(depth).addChild(newNode);
        parseAttributes(n, newNode);
        ++depth;
    }

    private void parseEmpty(com.yoclabo.reader.xml.NodeEntity n) {
        if (NodeEntity.TYPE.EMPTY_ELEMENT != n.getType()) {
            return;
        }
        com.yoclabo.wrapper.xml.NodeEntity newNode = new com.yoclabo.wrapper.xml.NodeEntity();
        newNode.setNodeName(n.getNodeName());
        newNode.setNodeId(currentNodeId);
        newNode.setDepth(depth);
        newNode.setType(NodeEntity.TYPE.EMPTY_ELEMENT);
        ++currentNodeId;
        node.findTail(depth).addChild(newNode);
        parseAttributes(n, newNode);
    }

    private void parseText(com.yoclabo.reader.xml.NodeEntity n) {
        if (NodeEntity.TYPE.TEXT != n.getType()) {
            return;
        }
        node.findTail(depth).setNodeValue(n.getNodeValue());
    }

    private void parseEndElement(com.yoclabo.reader.xml.NodeEntity n) {
        if (NodeEntity.TYPE.END_ELEMENT != n.getType()) {
            return;
        }
        --depth;
    }

    private void parseComment(com.yoclabo.reader.xml.NodeEntity n) {
        if (NodeEntity.TYPE.COMMENT != n.getType()) {
            return;
        }
        com.yoclabo.wrapper.xml.NodeEntity newNode = new com.yoclabo.wrapper.xml.NodeEntity();
        newNode.setNodeName("COMMENT");
        newNode.setNodeId(currentNodeId);
        newNode.setDepth(depth);
        newNode.setNodeValue(n.getNodeValue());
        newNode.setType(NodeEntity.TYPE.COMMENT);
        ++currentNodeId;
        node.findTail(depth).addChild(newNode);
    }

    private void parseAttributes(com.yoclabo.reader.xml.NodeEntity nr, com.yoclabo.wrapper.xml.NodeEntity nw) {
        for (com.yoclabo.reader.xml.AttributeEntity a : nr.getAttributes()) {
            AttributeEntity newAttr = new AttributeEntity();
            newAttr.setAttrName(a.getAttrName());
            newAttr.setAttrValue(a.getAttrValue());
            nw.addAttr(newAttr);
        }
    }
}
