package com.provys.dokuwiki;

import com.provys.xmlrpc.XmlRpcFaultException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DokuWikiClientTest {

    private final static DokuWikiClient dokuWikiClient = new DokuWikiClient("http://provys-wiki.dcit.cz/lib/exe/xmlrpc.php", "stehlik", "stehlik");

    @Test
    void getVersionTest() {
        assertThat(dokuWikiClient.getVersion()).startsWith("Release ");
    }

    @Test
    void getNamespaceNames() {
        dokuWikiClient.putPage("playground:java:getns:test1", "Test text");
        dokuWikiClient.putPage("playground:java:getns:test2", "Test text 2");
        dokuWikiClient.putPage("playground:java:getns:sub:test3", "Test text");
        dokuWikiClient.putPage("playground:java:getns:sub2:test4", "Test text");
        dokuWikiClient.putPage("playground:java:getns:sub2:test5", "Test text");
        assertThat(dokuWikiClient.getNamespaceNames("playground:java:getns"))
                .containsExactlyInAnyOrder("sub", "sub2");
    }

    @Test
    void deleteNamespaceTest() {
        dokuWikiClient.putPage("playground:java:deletens:test1", "Test text");
        dokuWikiClient.putPage("playground:java:deletens:test2", "Test text 2");
        dokuWikiClient.putPage("playground:java:deletens:sub:test3", "Test text");
        dokuWikiClient.putPage("playground:java:deletens:sub2:test4", "Test text");
        dokuWikiClient.putPage("playground:java:deletens:sub2:test5", "Test text");
        dokuWikiClient.putAttachment("playground:java:deletens:testtext.svg", "Test attachment".getBytes(),
                true);
        dokuWikiClient.putAttachment("playground:java:deletens:sub3:testtext.svg", "Test attachment 2".getBytes(),
                true);
        assertThatCode(() -> dokuWikiClient.deleteNamespace("playground:java:deletens")).doesNotThrowAnyException();
        assertThat(dokuWikiClient.getPageNames("playground:java:deletens")).isEmpty();
        assertThat(dokuWikiClient.getNamespaceNames("playground:java:deletens")).isEmpty();
        assertThat(dokuWikiClient.getAttachments("playground:java:deletens", 0)).isEmpty();
    }

    @Test
    void getPageNamesTest() {
        dokuWikiClient.putPage("playground:java:getpages:test1", "Test text");
        dokuWikiClient.putPage("playground:java:getpages:test2", "Test text 2");
        dokuWikiClient.putPage("playground:java:getpages:sub:test3", "Test text");
        assertThat(dokuWikiClient.getPageNames("playground:java:getpages"))
                .containsExactlyInAnyOrder("test1", "test2");
    }

    @Test
    void getAllPages() {
        assertThat(dokuWikiClient.getAllPages().size()).isGreaterThan(100);
    }

    @Test
    void searchPageIds() {
        dokuWikiClient.putPage("playground:java:searchpages:test1", "Test text");
        dokuWikiClient.putPage("playground:java:searchpages:test2", "Test text 2");
        dokuWikiClient.putPage("playground:java:searchpages:sub:test2", "Test text 2");
        assertThat(dokuWikiClient.searchPageIds("ns:playground:java:searchpages"))
                .containsExactlyInAnyOrder("playground:java:searchpages:test1", "playground:java:searchpages:test2",
                        "playground:java:searchpages:sub:test2");
    }

    @Test
    void getPageNonExistentTest() {
        assertThat(dokuWikiClient.getPage("playground:java:non_existent_page")).isEqualTo("");
    }

    @Test
    void getPageTest() {
        assertThat(dokuWikiClient.getPage("playground:java:test")).isEqualTo("Content of test page");
    }

    @Test
    void putPageTest() {
        dokuWikiClient.putPage("playground:java:puttest", "Test text");
        assertThat(dokuWikiClient.getPage("playground:java:puttest")).isEqualTo("Test text");
        dokuWikiClient.putPage("playground:java:puttest", "Different test text");
        assertThat(dokuWikiClient.getPage("playground:java:puttest")).isEqualTo("Different test text");
    }

    @Test
    void deletePageTest() {
        dokuWikiClient.putPage("playground:java:deletetest", "Delete test");
        assertThat(dokuWikiClient.getPage("playground:java:deletetest")).isEqualTo("Delete test");
        dokuWikiClient.deletePage("playground:java:deletetest");
        assertThat(dokuWikiClient.getPage("playground:java:deletetest")).isEqualTo("");
    }

    @Test
    void deletePagesTest() {
        dokuWikiClient.putPage("playground:java:deletepages:test1", "Test text");
        dokuWikiClient.putPage("playground:java:deletepages:test2", "Test text 2");
        dokuWikiClient.putPage("playground:java:deletepages:sub:test3", "Test text");
        assertThat(dokuWikiClient.getPages("playground:java:deletepages", 0).size()).isEqualTo(3);
        dokuWikiClient.deletePages("playground:java:deletepages");
        assertThat(dokuWikiClient.getPages("playground:java:deletepages", 0)).isEmpty();
    }

    @Test
    void getAttachmentFileNamesTest() {
        dokuWikiClient.putAttachment("playground:java:getfilenames:testtext.svg", "Test attachment".getBytes(),
                true);
        assertThat(dokuWikiClient.getAttachmentFileNames("playground:java:getfilenames"))
                .containsExactly("testtext.svg");
    }

    @Test
    void getAttachmentTest() {
        assertThatThrownBy(() -> dokuWikiClient.getAttachment("playground:java:non_existent_file.svg"))
                .isInstanceOf(XmlRpcFaultException.class)
                .hasMessage("The requested file does not exist");
    }

    @Test
    void putAttachmentTest() {
        assertThatCode(() -> dokuWikiClient.putAttachment(
                "playground:java:putattachment:testtext.svg", "Test attachment".getBytes(), true))
                .doesNotThrowAnyException();
        assertThatCode(() -> dokuWikiClient.putAttachment(
                "playground:java:putattachment:testtext.svg", "Test attachment 2".getBytes(), true))
                .doesNotThrowAnyException();
        assertThatCode(() -> dokuWikiClient.putAttachment(
                "playground:java:putattachment:testtext.svg", "Test attachment 3".getBytes(), false))
                .isInstanceOf(XmlRpcFaultException.class)
                .hasMessage("File already exists. Nothing done.");
        assertThat(dokuWikiClient.getAttachment("playground:java:putattachment:testtext.svg"))
                .containsExactly("Test attachment 2".getBytes());
    }

    @SuppressWarnings("squid:S2925") // we can only verify if data has been changed with second granularity on wiki
    @Test
    void putAttachment2Test() {
        assertThatCode(() -> dokuWikiClient.putAttachment(
                "playground:java:putattachment:testtext.svg", "Test attachment".getBytes(), true,
                true)).doesNotThrowAnyException();
        assertThat(dokuWikiClient.getAttachment("playground:java:putattachment:testtext.svg"))
                .containsExactly("Test attachment".getBytes());
        var attachments1 = dokuWikiClient.getAttachments("playground:java:putattachment");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException("", e);
        }
        assertThatCode(() -> dokuWikiClient.putAttachment(
                "playground:java:putattachment:testtext.svg", "Test attachment".getBytes(), true,
                true)).doesNotThrowAnyException();
        assertThat(dokuWikiClient.getAttachments("playground:java:putattachment")).isEqualTo(attachments1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException("", e);
        }
        assertThatCode(() -> dokuWikiClient.putAttachment(
                "playground:java:putattachment:testtext.svg", "Test attachment 2".getBytes(), true,
                true)).doesNotThrowAnyException();
        assertThat(dokuWikiClient.getAttachments("playground:java:putattachment")).isNotEqualTo(attachments1);
        assertThatCode(() -> dokuWikiClient.putAttachment(
                "playground:java:putattachment:testtext.svg", "Test attachment 3".getBytes(), false,
                true)).isInstanceOf(XmlRpcFaultException.class)
                .hasMessage("File already exists. Nothing done.");
        assertThat(dokuWikiClient.getAttachment("playground:java:putattachment:testtext.svg"))
                .containsExactly("Test attachment 2".getBytes());
    }

    @Test
    void deleteAttachmentTest() {
        dokuWikiClient.putAttachment("playground:java:deleteattachment:testtext.svg", "Test attachment".getBytes(), true);
        assertThatCode(() -> dokuWikiClient.deleteAttachment("playground:java:deleteattachment:testtext.svg"))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> dokuWikiClient.getAttachment("playground:java:deleteattachment:testtext.svg"))
                .isInstanceOf(XmlRpcFaultException.class)
                .hasMessage("The requested file does not exist");
        assertThatCode(() -> dokuWikiClient.deleteAttachment("playground:java:deleteattachment:testtext.svg"))
                .isInstanceOf(XmlRpcFaultException.class)
                .hasMessage("Could not delete file");
    }
}