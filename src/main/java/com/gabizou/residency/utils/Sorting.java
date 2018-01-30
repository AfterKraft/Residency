package com.gabizou.residency.utils;

import com.gabizou.residency.protection.ClaimedResidence;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Sorting {

    public Map<String, Integer> sortByValueASC(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, Integer> sortByValueDESC(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, Integer> sortByKeyDESC(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return (o2.getKey()).compareTo(o1.getKey());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public List<ClaimedResidence> sortResidences(List<ClaimedResidence> residences) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (ClaimedResidence one : residences) {
            if (one == null) {
                continue;
            }
            if (one.getName() == null) {
                continue;
            }
            map.put(one.getName().toLowerCase(), one);
        }
        map = sortByKeyASC(map);
        residences.clear();
        for (Entry<String, Object> one : map.entrySet()) {
            residences.add((ClaimedResidence) one.getValue());
        }
        return residences;
    }

    public Map<String, Object> sortByKeyASC(Map<String, Object> unsortMap) {

        // Convert Map to List
        List<Entry<String, Object>> list = new LinkedList<Entry<String, Object>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<String, Object>>() {
            @Override
            public int compare(Entry<String, Object> o1, Entry<String, Object> o2) {
                return (o1.getKey()).compareTo(o2.getKey());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Object> sortedMap = new LinkedHashMap<String, Object>();
        for (Iterator<Entry<String, Object>> it = list.iterator(); it.hasNext(); ) {
            Entry<String, Object> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, String> sortStringByKeyASC(Map<String, String> unsortMap) {

        // Convert Map to List
        List<Entry<String, String>> list = new LinkedList<Entry<String, String>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
                return (o1.getKey()).compareTo(o2.getKey());
            }
        });

        // Convert sorted map back to a Map
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Iterator<Entry<String, String>> it = list.iterator(); it.hasNext(); ) {
            Entry<String, String> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, Double> sortDoubleDESC(Map<String, Double> unsortMap) {

        // Convert Map to List
        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<String, Double>>() {
            @Override
            public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Iterator<Entry<String, Double>> it = list.iterator(); it.hasNext(); ) {
            Entry<String, Double> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public Map<String, Integer> sortASC(Map<String, Integer> unsortMap) {

        // Convert Map to List
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
