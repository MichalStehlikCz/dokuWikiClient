package com.provys.dokuwiki;

import com.provys.xmlrpc.XmlRpcStruct;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SearchResult extends PageListResult {

    @Nonnull
    static SearchResult parseResponse(XmlRpcStruct searchResult) {
        return new SearchResult((String) searchResult.get("snippet"), (int) searchResult.get("score"),
                (int) searchResult.get("rev"), (int) searchResult.get("size"),
                (String) searchResult.get("id"), (int) searchResult.get("mtime"), (String) searchResult.get("title"));
    }

    /** text snippet with hit highlighting */
    @Nonnull
    private final String snippet;
    /** score achieved in fulltext search */
    private final int score;
    /** page title */
    @Nonnull
    private final String title;

    SearchResult(String snippet, int score, int rev, int size, String id, int mtime, String title) {
        super(rev, size, id, mtime);
        this.snippet = Objects.requireNonNull(snippet);
        this.score = score;
        this.title = Objects.requireNonNull(title);
    }

    /**
     * @return text snippet with hit highlighting
     */
    @Nonnull
    public String getSnippet() {
        return snippet;
    }

    /**
     * @return score achieved in fulltext search
     */
    public int getScore() {
        return score;
    }

    /**
     * @return page title
     */
    @Nonnull
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchResult)) return false;

        SearchResult that = (SearchResult) o;

        if (getScore() != that.getScore()) return false;
        if (getRev() != that.getRev()) return false;
        if (getSize() != that.getSize()) return false;
        if (getMtime() != that.getMtime()) return false;
        if (!getSnippet().equals(that.getSnippet())) return false;
        if (!getId().equals(that.getId())) return false;
        return getTitle().equals(that.getTitle());
    }

    @Override
    public int hashCode() {
        int result = getSnippet().hashCode();
        result = 31 * result + getScore();
        result = 31 * result + getRev();
        result = 31 * result + getSize();
        result = 31 * result + getId().hashCode();
        result = 31 * result + getMtime();
        result = 31 * result + getTitle().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "snippet='" + snippet + '\'' +
                ", score=" + score +
                ", title='" + title + '\'' +
                ", " + super.toString() +
                '}';
    }
}
