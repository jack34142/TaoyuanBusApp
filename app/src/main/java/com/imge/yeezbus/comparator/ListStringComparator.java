package com.imge.yeezbus.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListStringComparator implements Comparator<Integer> {
    private List<String> list;

    public ListStringComparator(List<String> list) {
        this.list = list;
    }

    public List<Integer> createIndexArray(){
        List<Integer> indexes = new ArrayList<>();

        int len_list = list.size();
        for (int i = 0; i < len_list; i++){
            indexes.add(i);
        }
        return indexes;
    }

    public int compare(Integer o1, Integer o2) {
        return list.get(o1).compareTo(list.get(o2));
    }
}
