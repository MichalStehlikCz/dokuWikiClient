package com.provys.dokuwiki;

import com.provys.xmlrpc.XmlRpcStruct;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Objects;

public class PageData {

    /**
     * Parse page data from strucxt, returned from wiki Xml-Rpc call
     * @param pageData is struct describing page data retrieved from wiki
     * @return new {@code PageData} object with data found in struct
     */
    static PageData parseResponse(XmlRpcStruct pageData) {
        return new PageData((String) pageData.get("id"), (int) pageData.get("perms"),
                (int) pageData.get("size"), (LocalDateTime) pageData.get("lastModified"));
    }


    /** id of the page */
    @Nonnull
     private final String id;
     /** integer denoting the permissions on the page */
     private final int perms;
     /** size in bytes */
     private final int size;
     /** dateTime object of last modification date */
     @Nonnull
     private final LocalDateTime lastModified;

    PageData(String id, int perms, int size, LocalDateTime lastModified) {
        this.id = Objects.requireNonNull(id);
        this.perms = perms;
        this.size = size;
        this.lastModified = Objects.requireNonNull(lastModified);
    }

    /**
     * @return integer denoting the permissions on the page
     */
    @Nonnull
    public String getId() {
        return id;
    }

    /**
     * @return integer denoting the permissions on the page
     */
    public int getPerms() {
        return perms;
    }

    /**
     * @return size in bytes
     */
    public int getSize() {
        return size;
    }

    /**
     * @return dateTime object of last modification date
     */
    @Nonnull
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageData)) return false;

        PageData pageData = (PageData) o;

        if (getPerms() != pageData.getPerms()) return false;
        if (getSize() != pageData.getSize()) return false;
        if (!getId().equals(pageData.getId())) return false;
        return getLastModified().equals(pageData.getLastModified());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getPerms();
        result = 31 * result + getSize();
        result = 31 * result + getLastModified().hashCode();
        return result;
    }

    @Override
    @Nonnull
    public String toString() {
        return "PageData{" +
                "id='" + id + '\'' +
                ", perms=" + perms +
                ", size=" + size +
                ", lastModified=" + lastModified +
                '}';
    }
}
