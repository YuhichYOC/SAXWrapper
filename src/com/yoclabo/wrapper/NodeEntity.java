/*
 *
 * NodeEntity.java
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

package com.yoclabo.wrapper;

import com.yoclabo.reader.NodeEntity.TYPE;

import java.util.ArrayList;
import java.util.List;

public class NodeEntity {

    private final List<AttributeEntity> attrList;
    private final List<NodeEntity> children;
    private String nodeName;
    private int nodeId;
    private int depth;
    private String nodeValue;
    private TYPE myType;
    private NodeEntity parent;

    public NodeEntity() {
        nodeName = "";
        nodeId = 0;
        depth = 0;
        nodeValue = "";
        attrList = new ArrayList<>();
        parent = null;
        children = new ArrayList<>();
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) {
        this.nodeValue = nodeValue;
    }

    public TYPE getType() {
        return myType;
    }

    public void setType(com.yoclabo.reader.NodeEntity.TYPE arg) {
        myType = arg;
    }

    public List<AttributeEntity> getAttrList() {
        return attrList;
    }

    private void setParent(NodeEntity n) {
        parent = n;
    }

    public List<NodeEntity> getChildren() {
        return children;
    }

    private boolean attrExists(String name) {
        return attrList.stream().anyMatch(a -> a.nameEquals(name));
    }

    private String attrByName(String name) {
        if (attrExists(name)) {
            return attrList.stream().filter(a -> a.nameEquals(name)).findFirst().get().getAttrValue();
        } else {
            return "";
        }
    }

    public void addAttr(AttributeEntity add) {
        attrList.add(add);
    }

    public void addChild(NodeEntity add) {
        add.setParent(this);
        children.add(add);
    }

    private boolean nameEquals(String name) {
        return nodeName.equals(name);
    }

    public NodeEntity clone() {
        NodeEntity ret = new NodeEntity();

        attrList.forEach(a -> ret.addAttr(a.clone()));
        children.forEach(c -> ret.addChild(c.clone()));

        ret.setNodeName(nodeName);
        ret.setNodeId(nodeId);
        ret.setDepth(depth);
        ret.setNodeValue(nodeValue);
        ret.setType(myType);
        return ret;
    }

    public NodeEntity cloneWithoutChildren() {
        NodeEntity ret = new NodeEntity();

        attrList.forEach(a -> ret.addAttr(a.clone()));

        ret.setNodeName(nodeName);
        ret.setNodeId(nodeId);
        ret.setDepth(depth);
        ret.setNodeValue(nodeValue);
        ret.setType(myType);
        return ret;
    }

    public NodeEntity find(String name) {
        for (NodeEntity n : children) {
            if (n.nameEquals(name)) {
                return n;
            }
        }
        return null;
    }

    private NodeEntity find(String tagName, String attrName, String attrValue) {
        for (NodeEntity n : children) {
            if (n.nameEquals(tagName) && n.attrExists(attrName) && n.attrByName(attrName).equals(attrValue)) {
                return n;
            }
        }
        return null;
    }

    private NodeEntity find(String tagName, String attr1Name, String attr1Value, String attr2Name, String attr2Value) {
        for (NodeEntity n : children) {
            if (n.nameEquals(tagName)
                    && n.attrExists(attr1Name)
                    && n.attrByName(attr1Name).equals(attr1Value)
                    && n.attrExists(attr2Name)
                    && n.attrByName(attr2Name).equals(attr2Value)) {
                return n;
            }
        }
        return null;
    }

    public NodeEntity dir(String name) {
        return find("Item", "type", "Dir", "name", name);
    }

    public NodeEntity file(String name) {
        return find("Item", "type", "File", "name", name);
    }

    public NodeEntity tag(String name) {
        return find("Item", "type", "Tag", "name", name);
    }

    public NodeEntity attr(String name) {
        return find("Item", "type", "Attr", "name", name);
    }

    public NodeEntity subCategory(String name) {
        return find("Category", "name", name);
    }

    public NodeEntity subCategory(String sub1name, String sub2name) {
        return find("Category", "name", sub1name).find("Category", "name", sub2name);
    }

    private NodeEntity command(String name) {
        return find("Command", "name", name);
    }

    private NodeEntity param(String name) {
        return find("Param", "name", name);
    }

    public NodeEntity param(String commandName, String paramName) {
        return command(commandName).param(paramName);
    }

    public NodeEntity findTail(int depth) {
        NodeEntity ret = this;
        if (1 == depth) {
            return ret;
        }
        --depth;
        return findTail(depth, ret.getChildren().get(ret.getChildren().size() - 1));
    }

    private NodeEntity findTail(int depth, NodeEntity n) {
        if (1 == depth) {
            return n;
        }
        --depth;
        return findTail(depth, n.getChildren().get(n.getChildren().size() - 1));
    }
}
