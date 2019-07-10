package com.provys.dokuwiki;

import com.provys.xmlrpc.XmlRpcArray;
import com.provys.xmlrpc.XmlRpcClient;
import com.provys.xmlrpc.XmlRpcFaultException;
import com.provys.xmlrpc.XmlRpcStruct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Client class that represents connection to DokuWiki via Xml-Rpc
 */
@SuppressWarnings("WeakerAccess") // DokuWikiClient is published library class
public class DokuWikiClient {
    private final XmlRpcClient xmlRpcClient;
    private final PageIdParser pageIdParser = new PageIdParser();

    /**
     * Create new DokuWiki client instance.
     *
     * @param url is url used to access xml-rpc endpoint of DokuWiki
     * @param userName used to login to wiki
     * @param password used to login to wiki
     */
    public DokuWikiClient(String url, String userName, String password) {
        try {
            this.xmlRpcClient = new XmlRpcClient(new URL(Objects.requireNonNull(url)), Objects.requireNonNull(userName),
                    Objects.requireNonNull(password), true, StandardCharsets.UTF_8);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Malformed URL " + url, e);
        }
    }

    /**
     * @return version of wiki
     */
    @Nonnull
    public String getVersion() {
        return (String) xmlRpcClient.invoke("dokuwiki.getVersion");
    }

    /**
     * Get list of names of namespaces right bellow specified one. Method assumes that namespaces are non-empty -
     * technically, there might be empty child with topics in its children, but it does not happen on our wiki so we do
     * not care
     *
     * @param namespace is namespace search is done in
     * @return list of names of namespaces one level bellow our namespace
     */
    public List<String> getNamespaceNames(String namespace) {
        return (getPagesInt(namespace, pageIdParser.getDepth(namespace) + 1))
                .stream()
                .map(page -> (String) ((XmlRpcStruct) page).get("id"))
                .map(id -> (id.lastIndexOf(':') >= 0) ? id.substring(0, id.lastIndexOf(':')) : "")// remove page name
                .filter(ns -> !ns.equals(namespace)) // filter out pages directly in namespace
                .map(ns -> (ns.lastIndexOf(':') >= 0) ? ns.substring(ns.lastIndexOf(':') + 1) : ns) // leave only trailing name
                .distinct() // and leave each namespace only once
                .collect(Collectors.toList());
    }

    /**
     * Remove namespace - tries to remove all pages and attachments under given namespace
     *
     * @param namespace is namespace to be removed
     * @throws XmlRpcFaultException if attachments are referenced from existing topics
     */
    public void deleteNamespace(String namespace) {
        deletePages(namespace);
        deleteAttachments(namespace);
    }

    /**
     * Envelope for Xml-Rpc call to retrieve pages in namespace
     *
     * @param namespace is namespace query is executed for
     * @param depth is depth to which search is performed (from root, not relative to given namespace)
     * @return wiki response as array
     */
    private XmlRpcArray getPagesInt(String namespace, int depth) {
        return (XmlRpcArray) xmlRpcClient.invoke("dokuwiki.getPagelist", namespace, Map.of("depth", depth));
    }

    /**
     * Get pages in namespace, conforming to specified criteria
     *
     * @param namespace is namespace in which search is done
     * @param depth is depth of search, 0 means unlimited
     */
    @Nonnull
    public List<PageListResult> getPages(String namespace, int depth) {
        return (getPagesInt(namespace, depth))
                .stream()
                .map(page -> PageListResult.parseResponse((XmlRpcStruct) page))
                .collect(Collectors.toList());
    }

    /**
     * Get names of pages directly in namespace, conforming to specified criteria
     *
     * @param namespace is namespace in which search is done
     */
    @Nonnull
    public List<String> getPageNames(String namespace) {
        return (getPagesInt(namespace, pageIdParser.getDepth(namespace)))
                .stream()
                .map(page -> (String) ((XmlRpcStruct) page).get("id"))
                .map(pageIdParser::getName)
                .collect(Collectors.toList());
    }

    /**
     * Get all pages from wiki
     *
     * @return list of all pages in wiki
     */
    public List<PageData> getAllPages() {
        return ((XmlRpcArray) xmlRpcClient.invoke("wiki.getAllPages"))
                .stream()
                .map(page -> PageData.parseResponse((XmlRpcStruct) page))
                .collect(Collectors.toList());
    }

    /**
     * Internal method - Xml-Rpc call to DokuWiki. Public methods translate result to more reasonable types
     *
     * @param query is query executed against wiki
     * @return response from wiki as array
     */
    private XmlRpcArray searchPagesInt(String query) {
        return (XmlRpcArray) xmlRpcClient.invoke("dokuwiki.search", query);
    }

    /**
     * Find pages in wiki matching search term.
     *
     * @param query is search query (using wiki syntax)
     * @return list of matching pages
     */
    public List<SearchResult> searchPages(String query) {
        return searchPagesInt(query)
                .stream()
                .map(searchResult -> SearchResult.parseResponse((XmlRpcStruct) searchResult))
                .collect(Collectors.toList());
    }

    /**
     * Get Ids (full names) of pages conforming to search criteria
     *
     * @param query is search query (using wiki syntax)
     * @return list of matching pages
     */
    @Nonnull
    public List<String> searchPageIds(String query) {
        return searchPagesInt(query)
                .stream()
                .map(searchResult -> (String) ((XmlRpcStruct) searchResult).get("id"))
                .collect(Collectors.toList());
    }

    /**
     * Get content of page from wiki
     *
     * @param id is page name to be retrieved
     * @return content of given page, empty string if page does not exist
     */
    @Nonnull
    public String getPage(String id) {
        return (String) xmlRpcClient.invoke("wiki.getPage", id);
    }

    /**
     * Put page (update its content) on wiki. Shortened version without summary and minor change marker
     *
     * @param id is name of page to be created / updated
     * @param text is new text of page
     */
    public void putPage(String id, String text) {
        putPage(id, text, null, null);
    }

    /**
     * Put page (update its content) on wiki.
     *
     * @param id is name of page to be created / updated
     * @param text is new text of page
     * @param summary is change summary to be stored in history for given version of page
     * @param minor indicates that modification should be marked minor
     */
    public void putPage(String id, String text, @Nullable String summary, @Nullable Boolean minor) {
        var attrs=new XmlRpcStruct();
        if (summary != null) {
            attrs.put("sum", summary);
        }
        if (minor != null) {
            attrs.put("minor", minor);
        }
        xmlRpcClient.invoke("wiki.putPage", id, text, attrs);
    }

    /**
     * Delete page; wiki does not implement delete as such, call translates to changing content of page to empty
     *
     * @param id is name of page to be removed
     */
    public void deletePage(String id) {
        putPage(id, "");
    }

    /**
     * Delete all pages in given namespace. We do not have movement detection, thus if package is renamed, it will be
     * removed in wiki and created on new place. This is not ideal, but there is no way around that... and it is why we
     * need this function
     *
     * @param namespace delete all pages in given namespace
     */
    public void deletePages(String namespace) {
        getPagesInt(namespace, 0)
                .stream()
                .map(page -> (String) ((XmlRpcStruct) page).get("id"))
                .forEach(this::deletePage);
    }

    /**
     * Get list of attachments in given namespace. Depth is set to namespace depth to return just attachments directly
     * in given namespace
     */
    public List<AttachmentInfo> getAttachments(String namespace) {
        return getAttachments(namespace, pageIdParser.getDepth(namespace));
    }

    /**
     * Get list of names of attachments in given namespace. Depth is set to namespace depth to return just attachments
     * directly in given namespace, returns plain filenames
     */
    public List<String> getAttachmentFileNames(String namespace) {
        return getAttachmentsInt(namespace, pageIdParser.getDepth(namespace))
                .map(AttachmentInfo::getFile)
                .collect(Collectors.toList());
    }

    private Stream<AttachmentInfo> getAttachmentsInt(String namespace, int depth) {
        return ((XmlRpcArray) xmlRpcClient.invoke("wiki.getAttachments", namespace, Map.of("depth", depth)))
                .stream()
                .map(item -> AttachmentInfo.parseResponse((XmlRpcStruct) item));
    }

    /**
     * Get list of attachments in given namespace.
     *
     * @param namespace is namespace that should be searched
     * @param depth is depth of sub-spaces to be searched through (absolute from root, not from given namespace);
     *             0 means unlimited
     */
    public List<AttachmentInfo> getAttachments(String namespace, int depth) {
        return getAttachmentsInt(namespace, depth)
                .collect(Collectors.toList());
    }

    /**
     * Get content of attachment (file) from wiki
     *
     * @param id is name of attachment we want to retrieve
     * @return content of attachment
     * @throws XmlRpcFaultException if file does not exist
     */
    public byte[] getAttachment(String id) {
        return (byte[]) xmlRpcClient.invoke("wiki.getAttachment", id);
    }

    /**
     * Put attachment (file) to wiki
     *
     * @param id is location where attachment should be put to
     * @param file is byte array with file content
     * @param overwrite indicates if potential existing content should be overwritten
     */
    public void putAttachment(String id, byte[] file, boolean overwrite) {
        xmlRpcClient.invoke("wiki.putAttachment", id, file, Map.of("ow", overwrite));
    }

    /**
     * Delete attachment from wiki
     *
     * @param id is name of attachment wwe want to remove
     * @throws XmlRpcFaultException if attachment does not exist or it is referenced from existing topic
     */
    public void deleteAttachment(String id) {
        xmlRpcClient.invoke("wiki.deleteAttachment", id);
    }

    /**
     * Delete all attachments in given namespace from wiki
     *
     * @param namespace is namespace from which we want to remove all attachments
     * @throws XmlRpcFaultException if attachments are referenced from existing topics
     */
    public void deleteAttachments(String namespace) {
        getAttachmentsInt(namespace, 0)
                .map(AttachmentInfo::getId)
                . forEach(this::deleteAttachment);
    }
}
