package com.imge.yeezbus.comparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListIntegerComparator implements Comparator<Integer> {
    /**
     * 用途：index 排序
     *
     * 用法：
     *      List<Integer> list = new ArrayList<>();
     *      list.add(87);
     *      list.add(88);
     *      list.add(69);
     *      list.add(77);
     *      list.add(66);
     *
     *      ListIntegerComparator comparator = new ListIntegerComparator(list);
     *      List<Integer> indexes = comparator.createIndexArray();
     *      Collections.sort(indexes,comparator);
     *
     *      執行後，indexes 會按照 list 的大小被排序
     * */

    private List<Integer> list;

    public ListIntegerComparator(List<Integer> list) {
        super();
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

    @Override
    public int compare(Integer o1, Integer o2) {
        return list.get(o1).compareTo(list.get(o2));
    }
}
