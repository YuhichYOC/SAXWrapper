/*
 *
 * XWriter.java
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class XWriter {

    private NodeEntity writerSetting;
    private boolean newLineAfterOpeningBracket;
    private boolean newLineAfterClosingBracket;
    private boolean newLineAfterAttributes;
    private boolean newLineAfterNodeValue;
    private int indentSize;

    public XWriter() {
        writerSetting = null;
        newLineAfterOpeningBracket = true;
        newLineAfterClosingBracket = true;
        newLineAfterAttributes = true;
        newLineAfterNodeValue = true;
        indentSize = 2;
    }

    public void setWriterSetting(NodeEntity n) {
        writerSetting = n;
        if (null == writerSetting.find("Writer")
                || null == writerSetting.find("Writer").find("Setting")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine").find("OpeningBracket")
        ) {
            newLineAfterOpeningBracket = true;
        }
        else {
            newLineAfterOpeningBracket = writerSetting.find("Writer").find("Setting").find("NewLine").find("OpeningBracket").getNodeValue().equals("YES");
        }
        if (null == writerSetting.find("Writer")
                || null == writerSetting.find("Writer").find("Setting")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine").find("ClosingBracket")
        ) {
            newLineAfterClosingBracket = true;
        }
        else {
            newLineAfterClosingBracket = writerSetting.find("Writer").find("Setting").find("NewLine").find("ClosingBracket").getNodeValue().equals("YES");
        }
        if (null == writerSetting.find("Writer")
                || null == writerSetting.find("Writer").find("Setting")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine").find("AfterAttrElements")
        ) {
            newLineAfterAttributes = true;
        }
        else {
            newLineAfterAttributes = writerSetting.find("Writer").find("Setting").find("NewLine").find("AfterAttrElements").getNodeValue().equals("YES");
        }
        if (null == writerSetting.find("Writer")
                || null == writerSetting.find("Writer").find("Setting")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine")
                || null == writerSetting.find("Writer").find("Setting").find("NewLine").find("AfterNodeValue")
        ) {
            newLineAfterNodeValue = true;
        }
        else {
            newLineAfterNodeValue = writerSetting.find("Writer").find("Setting").find("NewLine").find("AfterNodeValue").getNodeValue().equals("YES");
        }
        if (null == writerSetting.find("Writer")
                || null == writerSetting.find("Writer").find("Setting")
                || null == writerSetting.find("Writer").find("Setting").find("IndentSize")
        ) {
            indentSize = 2;
        }
        else {
            indentSize = Integer.parseInt(writerSetting.find("Writer").find("Setting").find("IndentSize").getNodeValue());
        }
    }

    public void write(NodeEntity n, String directory, String fileName) throws IOException {
        String value = nodeToString(n);
        try (PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter(directory + "/" + fileName)))) {
            w.write(value);
        }
    }

    private String nodeToString(NodeEntity n) {
        String ret = "";
        switch (n.getType()) {
            case ELEMENT:
                ret += toStringElement(n);
                break;
            case EMPTY_ELEMENT:
                ret += toStringEmpty(n);
                break;
            default:
                ret += toStringComment(n);
                break;
        }
        ret = newLine(ret, true);
        if (0 < n.getChildren().size()) {
            for (NodeEntity c : n.getChildren()) {
                ret += nodeToString(c);
            }
        }
        ret = newLine(ret, true);
        if (com.yoclabo.reader.xml.NodeEntity.TYPE.ELEMENT == n.getType()) {
            ret += toStringEndElement(n);
        }
        return ret;
    }

    private String toStringElement(NodeEntity n) {
        String ret = indent(n, 0) + "<" + n.getNodeName();
        if (0 < n.getAttrList().size()) {
            ret = newLine(ret, newLineAfterOpeningBracket);
            for (AttributeEntity a : n.getAttrList()) {
                ret += indent(n, 1) + toStringAttribute(a);
                ret = newLine(ret, newLineAfterAttributes);
            }
            ret += indent(n, 1) + ">";
        }
        else {
            ret += ">";
        }
        ret = newLine(ret, newLineAfterClosingBracket);
        if (0 < n.getNodeValue().length()) {
            ret += n.getNodeValue();
            ret = newLine(ret, newLineAfterNodeValue);
        }
        return ret;
    }

    private String toStringEndElement(NodeEntity n) {
        return indent(n, 0) + "</" + n.getNodeName() + ">";
    }

    private String toStringEmpty(NodeEntity n) {
        String ret = indent(n, 0) + "<" + n.getNodeName();
        if (0 < n.getAttrList().size()) {
            ret = newLine(ret, newLineAfterOpeningBracket);
            for (AttributeEntity a : n.getAttrList()) {
                ret += indent(n, 1) + toStringAttribute(a);
                ret = newLine(ret, newLineAfterAttributes);
            }
            ret += indent(n, 1) + "/>";
        }
        else {
            ret += "/>";
        }
        return ret;
    }

    private String toStringAttribute(AttributeEntity a) {
        return a.getAttrName() + "=\"" + a.getAttrValue() + "\"";
    }

    private String toStringComment(NodeEntity n) {
        return indent(n, 0) + "<!-- " + n.getNodeValue() + " -->";
    }

    private String indent(NodeEntity n, int plus) {
        StringBuilder ret = new StringBuilder();
        ret.append(" ".toCharArray(), 0, (n.getDepth() + plus) * indentSize);
        return ret.toString();
    }

    private String newLine(String arg, boolean newLine) {
        if (newLine) {
            return arg + "\n";
        }
        else {
            return arg;
        }
    }
}
