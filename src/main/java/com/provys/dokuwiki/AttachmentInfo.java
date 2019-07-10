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
    public Integer getMtime() {
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
    public Integer getPerms() {
        return perms;
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
