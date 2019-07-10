package com.provys.dokuwiki;

import com.provys.xmlrpc.XmlRpcStruct;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Represents item in result of getPagelist call
 */
public class PageListResult {

    static PageListResult parseResponse(XmlRpcStruct pageListResult) {
        return new PageListResult((int) pageListResult.get("rev"), (int) pageListResult.get("size"),
                (String) pageListResult.get("id"), (int) pageListResult.get("mtime"));
    }

    /** page revision */
    private final int rev;
    /** page size in bytes */
    private final int size;
    /** page id (namespace + name) */
    @Nonnull
    private final String id;
    /** last change timestamp */
    private final int mtime;

    PageListResult(int rev, int size, String id, int mtime) {
        this.rev = rev;
        this.size = size;
        this.id = Objects.requireNonNull(id);
        this.mtime = mtime;
    }

    /**
     * @return page revision
     */
    public int getRev() {
        return rev;
    }

    /**
     * @return page size in bytes
     */
    public int getSize() {
        return size;
    }

    /**
     * @return page id (namespace + name)
     */
    @Nonnull
    public String getId() {
        return id;
    }

    /**
     * @return last change timestamp
     */
    public int getMtime() {
        return mtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageListResult)) return false;

        PageListResult that = (PageListResult) o;

        if (getRev() != that.getRev()) return false;
        if (getSize() != that.getSize()) return false;
        if (getMtime() != that.getMtime()) return false;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        int result = getRev();
        result = 31 * result + getSize();
        result = 31 * result + getId().hashCode();
        result = 31 * result + getMtime();
        return result;
    }

    @Override
    public String toString() {
        return "PageListResult{" +
                "rev=" + rev +
                ", size=" + size +
                ", id='" + id + '\'' +
                ", mtime=" + mtime +
                '}';
    }
}
