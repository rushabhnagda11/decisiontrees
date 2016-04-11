package com.rushabh.decisiontrees;

import org.json.JSONObject;

/**
 * Created by rushabhnagda on 4/7/16.
 */
public class DecisionTree<F,C> {

    public Node[] nodesArray;

    public DecisionTree(Node[] nodesArray) {
        this.nodesArray = nodesArray;
    }

    public Node scoreTree(C featureSet) {
        Node n = nodesArray[0];
        while(!n.isLeafNode()) {
            n = nodesArray[n.nextNode(featureSet)];
        }
        return n;
    }
}