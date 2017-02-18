package com.halfplatepoha.frnds.network;

import java.util.List;

import rx.Observable;

/**
 * Created by surajkumarsau on 15/09/16.
 */
public interface PaginationListener<ResItem> {
    Observable<List<ResItem>> onNextPage(int offset);
}
