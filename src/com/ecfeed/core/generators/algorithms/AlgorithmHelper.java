package com.ecfeed.core.generators.algorithms;

import com.ecfeed.core.generators.DimensionedItem;
import com.google.common.collect.Maps;

import java.util.*;

public class AlgorithmHelper {
    public static <E> List<List<E>> AllSublists(List<E> inpList)
    {
        if(inpList.isEmpty())
        {
            List<List<E>> ret = new ArrayList<>();
            ret.add(new ArrayList<>());
            return ret;
        }

        E last = inpList.get(inpList.size()-1);

        List<E> shorter = new ArrayList<>(inpList.subList(0,inpList.size()-1));

        List<List<E>> ret = new ArrayList<>();
        for(List<E> x : AllSublists(shorter))
        {
            ret.add(x);
            List<E> y = new ArrayList<>(x);
            y.add(last);
            ret.add(y);
        }
        return ret;
    }


    public static <E> SortedMap<Integer,E> Compress(List<E> inpList)
    {
        SortedMap<Integer,E> ret = Maps.newTreeMap();
        for(int i=0;i<inpList.size();i++)
            if(inpList.get(i)!=null)
                ret.put(i, inpList.get(i));
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
