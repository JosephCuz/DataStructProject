import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

public class Stage5TestPt1 {


    @Test
    public void basicTest() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        //setting memory to super high num, will need to fix this
        //documentStore.setMaxDocumentBytes(1000000000);
        //documentStore.setMaxDocumentCount(100000000);
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);
        assert (documentStore.get(uri) != null);
    }

    @Test
    public void put1andUndo() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);
        assert (documentStore.get(uri) != null);
        documentStore.undo();
        assert (documentStore.get(uri) == null);
    }

    @Test
    public void putManyAndUndo() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        InputStream inputStream2 = new ByteArrayInputStream(testText.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text 3";
        InputStream inputStream3 = new ByteArrayInputStream(testText.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "this is a test text 4";
        InputStream inputStream4 = new ByteArrayInputStream(testText.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //test undo() with uri4
        assert (documentStore.get(uri4) != null);
        documentStore.undo();
        assert (documentStore.get(uri4) == null);

        //test undo(uri) with uri2
        assert (documentStore.get(uri2) != null);
        documentStore.undo(uri2);
        assert (documentStore.get(uri2) == null);

    }

    @Test
    public void testDeleteViaPutAndUndoing() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text poopy 3";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "this is not a test text 4";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        System.out.println("uri 2: " + documentStore.search("2"));
        assert (documentStore.get(uri2) != null);
        //deleteing uri2 with a put
        documentStore.put(null, uri2, DocumentStore.DocumentFormat.TXT);
        System.out.println("uri 2 search after delete: " + documentStore.search("2"));
        assert (documentStore.get(uri2) == null);

        documentStore.undo();
        System.out.println("uri 2 after undo: " + documentStore.search("2"));
        assert (documentStore.get(uri2) != null);
    }

    @Test
    public void testPuttingThenUndoingDiamentEdition() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text 1";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        System.out.println("uri: " + documentStore.search("1"));
        assert (documentStore.get(uri) != null);
        documentStore.undo();
        System.out.println("uri: " + documentStore.search("1"));
        assert (documentStore.get(uri) == null);

        String testText2 = "this is a test text 2";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text poopy 3";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "this is not a test text 4";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);


        System.out.println("uri4: " + documentStore.search("4"));
        documentStore.undo();
        System.out.println("uri4: " + documentStore.search("4"));
        assert (documentStore.get(uri4) == null);

        System.out.println("uri3: " + documentStore.search("3"));
        documentStore.undo();
        System.out.println("uri3: " + documentStore.search("3"));
        assert (documentStore.get(uri3) == null);

        System.out.println("uri2: " + documentStore.search("2"));
        documentStore.undo();
        System.out.println("uri2: " + documentStore.search("2"));
        assert (documentStore.get(uri2) == null);

    }

    @Test
    public void testDeleteViaPutAndUndoingByURI() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text poopy 3";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        System.out.println("uri 2: " + documentStore.search("2"));
        assert (documentStore.get(uri2) != null);
        //deleteing uri2 with a put
        documentStore.put(null, uri2, DocumentStore.DocumentFormat.TXT);
        System.out.println("uri 2 search after delete: " + documentStore.search("2"));
        assert (documentStore.get(uri2) == null);

        String testText4 = "this is not a test text 4";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);


        documentStore.undo(uri2);
        System.out.println("uri 2 after undo: " + documentStore.search("2"));
        assert (documentStore.get(uri2) != null);
    }


    @Test
    public void testSearch() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text poopy 3";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "this is not a test text 4";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        System.out.println(documentStore.search("this"));
        System.out.println(documentStore.search("4"));
    }

    /**
     * need to confirm descending order still
     *
     * @throws IOException
     */
    @Test
    public void testSearchPrefix() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "yabadabadoo to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "mama dukes toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "all my homies teem";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no tECHY";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        System.out.println(documentStore.searchByPrefix("t"));

    }


    @Test
    public void testUndoException() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        URI uri = URI.create("http://test.com/document");
        //should throw illegl state
        documentStore.undo();

        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());

        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        documentStore.undo(uri);
        documentStore.undo(uri);
        //documentStore.undo(uri); //should be throwing exception but isnt
        //documentStore.undo();

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "too";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "to";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        String testText5 = "to";
        InputStream inputStream5 = new ByteArrayInputStream(testText5.getBytes());
        URI uri5 = URI.create("http://test.com/document5");
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.TXT);

        String testText6 = "too";
        InputStream inputStream6 = new ByteArrayInputStream(testText6.getBytes());
        URI uri6 = URI.create("http://test.com/document6");
        documentStore.put(inputStream6, uri6, DocumentStore.DocumentFormat.TXT);


    }


    @Test
    public void testDelete() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        System.out.println(documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        documentStore.delete(uri3);
        assert (documentStore.get(uri3) == null);
        System.out.println(documentStore.search("teem"));

    }

    @Test
    public void testUndoADelete() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //deleting uri 1
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        documentStore.delete(uri);
        assert (documentStore.get(uri) == null);
        System.out.println(documentStore.search("to"));

        //deleting uri3
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        documentStore.delete(uri3);
        assert (documentStore.get(uri3) == null);
        System.out.println(documentStore.search("teem"));

        //undoing delete on uri 1
        documentStore.undo(uri);
        assert (documentStore.get(uri) != null);
        System.out.println("uri 1: " + documentStore.search("to"));

        //undoingmost recent action, deleting uri 3
        documentStore.undo();
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
    }

    @Test
    public void testDeleteAllPrefix() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //list values before deleting
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        documentStore.deleteAllWithPrefix("t");

        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) == null);
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);
    }

    @Test
    public void testDeleteAllPrefixGeneralUndo() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //list values before deleting
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        documentStore.deleteAllWithPrefix("t");
        //confirm deletion
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) == null);
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        documentStore.undo();
        //confirm undid
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

    }

    @Test
    public void testDeleteAllWithPrefixSingleUndo() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //list values before deleting
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        documentStore.deleteAllWithPrefix("t");
        //confirm deletion
        System.out.println("uri 1: " + documentStore.search("to"));
        System.out.println(documentStore.get(uri));
        assert (documentStore.get(uri) == null);
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        //undo delete on only uri2
        documentStore.undo(uri2);
        //confirm delete was undid
        System.out.println("uri 2: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        //confirm others still deleted
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) == null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);

    }

    @Test
    public void testDeleteAll() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //confirm puts, toot should have 2 values
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2 and 3 b/c toot: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        //should delete uri2 and uri3
        documentStore.deleteAll("toot");
        //confirm deletion
        System.out.println("uri 2 and 3/toot search result: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("uri 3/teem search results: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);
        //confirm others exist still
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);
    }

    @Test
    public void testDeleteAllGeneralUndo() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //confirm puts, toot should have 2 values
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2 and 3 b/c toot: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        //should delete uri2 and uri3
        documentStore.deleteAll("toot");
        //confirm deletion
        System.out.println("uri 2 and 3/toot search result: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("uri 3/teem search results: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);

        //undo deleteAll
        documentStore.undo();
        //confirm undid
        System.out.println("uri 2 and 3 b/c toot: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
    }

    @Test
    public void testDeleteAllSingleUndo() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //confirm puts, toot should have 2 values
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2 and 3 b/c toot: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        //should delete uri2 and uri3
        documentStore.deleteAll("toot");
        //confirm deletion
        System.out.println("uri 2 and 3/toot search result: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("uri 3/teem search results: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);

        //undo just uri3, should at it to both teem search and toot search
        documentStore.undo(uri3);
        //confirm undid
        System.out.println("should just be uri 3 in toot: " + documentStore.search("toot"));
        System.out.println("uri 3 in teem: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        //confirm uri2 still deleted
        assert (documentStore.get(uri2) == null);
    }

    @Test
    public void testCommandSetRemovedAfterAllUriUndone() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //confirm puts, toot should have 2 values
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2 and 3 b/c toot: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        //delete uri 4, should be under the commandSet in stack
        documentStore.delete(uri4);
        //confirm deletion
        System.out.println("confirm delete uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) == null);

        //should delete uri2 and uri3
        documentStore.deleteAll("toot");
        //confirm deletion
        System.out.println("confirm delete, uri 2 and 3/toot search result: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("confirm delete, uri 3/teem search results: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);

        //undo just uri3, should at it to both teem search and toot search
        documentStore.undo(uri3);
        //confirm undid
        System.out.println("should just be uri 3 in toot: " + documentStore.search("toot"));
        System.out.println("uri 3 in teem: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        //confirm uri2 still deleted
        assert (documentStore.get(uri2) == null);
        //undo for uri 2
        documentStore.undo(uri2);
        System.out.println("should be uri 2 and 3 in toot: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);

        //general undo to confirm commandset gone and uri4 delete undid
        documentStore.undo();
        System.out.println("confirm undid uri 4 delete, 'no' search result: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);
    }

    @Test
    public void testDescendingOrderForSearch() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "poop toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot toot toot";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no toot toot";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        System.out.println("uri 1, has no toot: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2, has one toot: " + documentStore.search("poop"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3, has three toots: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4, has two toots: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        System.out.println("toot search results, should be uri3, uri4, uri2" + documentStore.search("toot"));
    }

    @Test
    public void testDescendingOrderForSearchPrefix() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "to";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "poop tot tok tooth toon";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot tool toll";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        String testText4 = "no tomb ton";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        System.out.println("uri 1, has one to: " + documentStore.search("to"));
        assert (documentStore.get(uri) != null);
        System.out.println("uri 2, has four to's: " + documentStore.search("poop"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3, has three to's: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4, has two to's: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        System.out.println("to prefix search results, should be uri2, uri3, uri4, uri1: " + documentStore.searchByPrefix("to"));
    }


    @Test
    public void testingNullInputs() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = null;
        InputStream inputStream2 = new ByteArrayInputStream(null);
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot tool toll";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);


    }
}
