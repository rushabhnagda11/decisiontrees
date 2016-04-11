package com.rushabh.decisiontrees;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import it.unimi.dsi.fastutil.objects.*;

import com.rushabh.decisiontrees.DecisionTree;
import com.rushabh.decisiontrees.Condition;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rushabhnagda on 4/11/16.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        String value = "";
        JSONObject j = new JSONObject();
        Map<String,String> featureSet = new HashMap<>();
        for(String s : Files.readAllLines(Paths.get("/tmp/","featureSet"), Charset.defaultCharset())) {
            j = new JSONObject(s);
        }
        Iterator<String> itr = j.keys();
        while(itr.hasNext()) {
            String key = itr.next();
            String val = j.getString(key);
            featureSet.put(key,val);
        }
        
        DecisionTree dtree = null;
        for(String s : Files.readAllLines(Paths.get("/tmp/","testTree"), Charset.defaultCharset())) {
            dtree = createDecisionTree(s);
        }
        System.out.println(dtree.scoreTree(featureSet).getNodeId());
    }

    public static DecisionTree<String, Map<String, String>> createDecisionTree(String value) {

        List<Noode<String, Map<String, String>>> nodeList = new ArrayList<>();

        try {
            JSONObject a = new JSONObject(value);
            for(int i = 0; i < a.length(); i++) {

                JSONObject nodeInfo = a.getJSONObject(String.valueOf(i));

                Noode<String, Map<String, String>> node = null;

                JSONObject def_json = new JSONObject();
                def_json.put("bin_selection","0.0");
                def_json.put("range_selection","1.0-1.5");

                if(!nodeInfo.getBoolean("isLeafNode")) {

                    if("Categorical".equals(nodeInfo.getString("nodeType"))) {

                        node = new Noode<>(
                                nodeInfo.has("variableName") ? nodeInfo.getString("variableName") : null,
                                null,
                                "DO_NOTHING",
                                String.valueOf(i),
                                new Categorical(nodeInfo.getString("variableDef"),nodeInfo.getString("childNodes")),
                                false,
                                false);

                    }
                    if("Regression".equals(nodeInfo.getString("nodeType"))) {
                        node = new Noode<>(
                                nodeInfo.has("variableName") ? nodeInfo.getString("variableName") : null,
                                null,
                                "DO_NOTHING",
                                String.valueOf(i),
                                new Numeric(nodeInfo.getString("variableDef"),nodeInfo.getString("childNodes")),
                                false,
                                false);
                    }
                } else {
                    node = new Noode<>(
                            nodeInfo.has("variableName") ? nodeInfo.getString("variableName") : null,
                            nodeInfo.has("ext") ? nodeInfo.getJSONObject("ext") : def_json,
                            nodeInfo.has("valueType") ? nodeInfo.getString("valueType") : "DO_NOTHING",
                            String.valueOf(i),
                            null,
                            true,
                            nodeInfo.has("maxBidFlag") ? nodeInfo.getBoolean("maxBidFlag") : true);
                }
                nodeList.add(node);
            }

        } catch (JSONException e) {
        }

        return new DecisionTree<>(nodeList.toArray(nodeList.toArray(new Noode[nodeList.size()])));


    }


    private static class Numeric implements Condition<String, Map<String, String>> {

        private final TreeMap<Double,Integer> valueToChildNodeMap = new TreeMap<>();
        private int defaultChildNode = 0;

        private void createNumericTreeMap(String variableDefCSV,String childNodeCSV) {
            String[] arr1 = variableDefCSV.split(",");
            String[] arr2 = childNodeCSV.split(",");
            for(int i = 0; i < arr1.length; i++) {
                valueToChildNodeMap.put(Double.parseDouble(arr1[i]),Integer.parseInt(arr2[i]));
            }
            defaultChildNode = Integer.parseInt(arr2[arr2.length - 1]);
        }

        public Numeric(String variableDefCSV, String childNodeCSV) {
            createNumericTreeMap(variableDefCSV,childNodeCSV);
        }

        @Override
        public int nextNodeIndex(final String feature, final Map<String, String> features) {

            String valueStr = features.get(feature);
            if(valueStr == null) {
                return defaultChildNode;
            }
            Double value = Double.parseDouble(valueStr);
            return valueToChildNodeMap.floorEntry(value).getValue();

        }
    }

    private static class Categorical implements Condition<String, Map<String, String>> {

        private final Map<String,Integer> valueToChildNodeMap = new HashMap<>();


        private void createCategoicalMap(String variableDefCSV, String childNodeCSV) {
            String[] arr1 = variableDefCSV.split(",");
            String[] arr2 = childNodeCSV.split(",");
            for(int i = 0;i < arr1.length ; i++) {
                valueToChildNodeMap.put(arr1[i], Integer.parseInt(arr2[i]));
            }
            valueToChildNodeMap.put("var_not_found_or_not_set",Integer.parseInt(arr2[arr2.length - 1]));
        }

        public Categorical(String variableDefCSV, String childNodeCSV) {
            createCategoicalMap(variableDefCSV, childNodeCSV);
        }

        @Override
        public int nextNodeIndex(final String feature, final Map<String, String> features) {
            try {
                String featureValue = features.get(feature);
                //if the variable value is null or not present in the variable def map, then return default go to node
                if (featureValue == null || !valueToChildNodeMap.containsKey(featureValue)) {
                    return valueToChildNodeMap.get("var_not_found_or_not_set");
                } else {
                    return valueToChildNodeMap.get(featureValue);
                }
            } catch(Exception e) {
                return 0;
            }
        }
    }
}
