package com.provys.dokuwiki;

import com.provys.xmlrpc.XmlRpcStruct;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Provides description of file, uploaded to wiki as attachment. Retrieved via getAttachments.
 */
@SuppressWarnings("WeakerAccess")
public class AttachmentInfo {

    /**
     * Create attachment info based on information contained in struct, retrieved from wiki
     */
    @Nonnull
    static AttachmentInfo parseResponse(XmlRpcStruct attachmentInfo) {
        return new AttachmentInfo((String) attachmentInfo.get("id"), (String) attachmentInfo.get("file"),
                (int) attachmentInfo.get("size"), (int) attachmentInfo.get("mtime"),
                (LocalDateTime) attachmentInfo.get("lastModified"), (boolean) attachmentInfo.get("isimg"),
                (boolean) attachmentInfo.get("writable"), (int) attachmentInfo.get("perms"));
    }

    /** Media id (namespace + name) */
    private final String id;
    /** Name of the file */
    private final String file;
    /** Size in bytes */
    private final int size;
    /** Upload date as a timestamp */
    private final int mtime;
    /** Modification date as Date object */
    private final LocalDateTime lastModified;
     /** true if file is an image, false otherwise */
    private final boolean isImg;
    /** true if file is writable, false otherwise */
    private final boolean writable;
    /** permissions of file */
    private final int perms;

    private AttachmentInfo(String id, String file, int size, int mtime, LocalDateTime lastModified, boolean isImg,
                           boolean writable, int perms) {
        this.id = Objects.requireNonNull(id);
        this.file = Objects.requireNonNull(file);
        this.size = size;
        this.mtime = mtime;
        this.lastModified = Objects.requireNonNull(lastModified);
        this.isImg = isImg;
        this.writable = writable;
        this.perms = perms;
    }

    /**
     * @return media id (namespace + name)
     */
    public String getId() {
        return id;
    }

    /**
     * @return name of the file
     */
    public String getFile() {
        return file;
    }

    /**
     * @return size in bytes
     */
    public int getSize() {
        return size;
    }

    /**
     * @return upload date as a timestamp
     */
    public int getMtime() {
        return mtime;
    }

    /**
     * @return modification date as Date object
     */
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    /**
     * @return true if file is an image, false otherwise
     */
    public Boolean getImg() {
        return isImg;
    }

    /**
     * @return true if file is writable, false otherwise
     */
    public Boolean getWritable() {
        return writable;
    }

    /**
     * @return permissions of file
     */
    public int getPerms() {
        return perms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttachmentInfo)) return false;

        AttachmentInfo that = (AttachmentInfo) o;

        if (getSize() != that.getSize()) return false;
        if (getMtime() != that.getMtime()) return false;
        if (getImg() != that.getImg()) return false;
        if (getWritable() != that.getWritable()) return false;
        if (getPerms() != that.getPerms()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getFile() != null ? !getFile().equals(that.getFile()) : that.getFile() != null) return false;
        return getLastModified() != null ? getLastModified().equals(that.getLastModified()) : that.getLastModified() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getFile() != null ? getFile().hashCode() : 0);
        result = 31 * result + getSize();
        result = 31 * result + getMtime();
        result = 31 * result + (getLastModified() != null ? getLastModified().hashCode() : 0);
        result = 31 * result + (getImg() ? 1 : 0);
        result = 31 * result + (getWritable() ? 1 : 0);
        result = 31 * result + getPerms();
        return result;
    }

    @Override
    public String toString() {
        return "AttachmentInfo{" +
                "id='" + id + '\'' +
                ", file='" + file + '\'' +
                ", size=" + size +
                ", mtime=" + mtime +
                ", lastModified=" + lastModified +
                ", isImg=" + isImg +
                ", writable=" + writable +
                ", perms=" + perms +
                '}';
    }
}
