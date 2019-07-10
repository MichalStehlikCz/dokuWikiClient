package com.provys.dokuwiki;

import javax.annotation.Nonnull;

public class PageIdParser {

    /**
     * @return namespace of page (without name)
     */
    @Nonnull
    public String getNamespace(String id) {
        if (id.lastIndexOf(':') > 0) {
            return id.substring(0, id.lastIndexOf(':') - 1);
        }
        return "";
    }

    /**
     * Get depth of specified namespace.
     *
     * @param namespace is string representing namespace. It should be absolute path (cannot evaluate relative namespace
     *                  without context). If tehre is leading or trailing :, it is ignored.
     * @return depth of namespace (as used in dokuWiki search functions); root level documents (root namespace) has
     * depth 1.
     */
    public int getDepth(String namespace) {
        int depth;
        if (namespace.isEmpty()) {
            // root level has depth 1
            depth = 1;
        } else {
            // root level has depth 1 and last level does not have : and thus is excluded from calculation
            depth = 2;
            for (int pos = 1; pos < namespace.length() - 1; pos++) {
                if (namespace.charAt(pos) == ':') {
                    depth++;
                }
            }
        }
        return depth;
    }

    /**
     * @return name of page (without namespace)
     */
    @Nonnull
    public String getName(String id) {
        if (id.lastIndexOf(':') > 0) {
            return id.substring(id.lastIndexOf(':') + 1);
        }
        return id;
    }
}
