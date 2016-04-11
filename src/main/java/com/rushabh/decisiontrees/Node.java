package com.rushabh.decisiontrees;

import org.json.JSONObject;

/**
 * Created by rushabhnagda on 4/11/16.
 */
public class Node<F,C> {
    private boolean isMaxBidFlag = false;
    private boolean isLeafNode = false;
    private final F feature;
    private final JSONObject ext;
    private final Condition<F,C> condition;
    private final String action;
    private final String nodeId;

    public boolean isMaxBidFlag() {
        return isMaxBidFlag;
    }

    public boolean isLeafNode() {
        return isLeafNode;
    }

    public JSONObject getExt() {
        return ext;
    }

    public String getAction() {
        return action;
    }

    public String getNodeId() {
        return nodeId;
    }

    public Node(F feature,JSONObject ext, String action, String nodeId, Condition condition, boolean isLeafNode, boolean isMaxBidFlag) {
        this.feature = feature;
        this.ext = ext;
        this.action = action;
        this.nodeId = nodeId;
        this.condition = condition;
        this.isMaxBidFlag = isMaxBidFlag;
        this.isLeafNode = isLeafNode;
    }

    public int nextNode(C features) {
        return condition.nextNodeIndex(feature, features);
    }


}