package com.ecfeed.core.generators.algorithms;

import java.util.*;

public class AlgorithmHelper {

    public static <E> List<List<E>> getAllSublists(List<E> inpList)
    {
        if (inpList.isEmpty()) {
            List<List<E>> ret = new ArrayList<>();
            ret.add(new ArrayList<>());
            return ret;
        }

        E last = inpList.get(inpList.size()-1);

        List<E> shorter = new ArrayList<>(inpList.subList(0,inpList.size()-1));

        List<List<E>> ret = new ArrayList<>();

        for (List<E> list : getAllSublists(shorter)) {
            ret.add(list);
            List<E> y = new ArrayList<>(list);
            y.add(last);
            ret.add(y);
        }

        return ret;
    }


    public static <E> List<E> Uncompress(SortedMap<Integer,E> inpList,int dimension)
    {
        ArrayList<E> ret = new ArrayList<>(Collections.nCopies(dimension, null));

        for(Map.Entry<Integer,E> entry : inpList.entrySet())
            ret.set(entry.getKey(), entry.getValue());

        return ret;
    }
}
