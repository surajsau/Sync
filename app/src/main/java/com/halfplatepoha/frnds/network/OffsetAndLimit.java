package com.halfplatepoha.frnds.network;

/**
 * Created by surajkumarsau on 15/09/16.
 */
public class OffsetAndLimit {

    private int offset;
    private int limit;

    public OffsetAndLimit(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
