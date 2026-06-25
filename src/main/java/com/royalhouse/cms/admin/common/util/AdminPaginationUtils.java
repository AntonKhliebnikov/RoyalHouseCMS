package com.royalhouse.cms.admin.common.util;

public final class AdminPaginationUtils {
    private AdminPaginationUtils() {
    }

    public static int lastPageIndex(long totalElements, int pageSize) {
        if (totalElements <= 0) {
            return 0;
        }

        return (int) ((totalElements - 1) / pageSize);
    }
}
