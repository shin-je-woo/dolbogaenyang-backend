package com.whatpl.global.util;

import com.whatpl.global.pagination.SliceElement;

import java.util.List;

public class PaginationUtils {

    private PaginationUtils() {
        throw new IllegalStateException("This is Utility class!");
    }

    public static boolean hasNext(List<? extends SliceElement> list, int pageSize) {
        boolean hasNext = false;
        if (list.size() > pageSize) {
            list.remove(pageSize);
            hasNext = true;
        }
        return hasNext;
    }
}
