package com.ecfeed.core.generators.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
