package com.ecfeed.core.utils;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.ArrayList;
import java.util.List;

public class ListOfStrings {

    List<String> fStrings;

    public ListOfStrings() {

        fStrings = new ArrayList<>();
    }

    public void add(String string) {

        fStrings.add(string);
    }

    public boolean isEmpty() {

        if (fStrings.size() == 0) {
            return true;
        }

        return false;
    }
}
