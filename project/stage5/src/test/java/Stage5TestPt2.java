import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import org.junit.Test;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.impl.StackImpl;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

public class Stage5TestPt2 {



    @Test
    public void ReplacingWithABigDocEasy() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        documentStore.setMaxDocumentBytes(1000);
        documentStore.setMaxDocumentCount(10);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.BINARY);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        //call get on uri one to reset use time
        documentStore.get(uri1);


        String testText4 = "this is a test text 4";
        byte[] byteArray4 = new byte[20];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.BINARY);

        //documentStore.undo(uri3);

        String testText7 = "this is a test text 4";
        byte[] byteArray7 = new byte[10000000];
        InputStream inputStream7 = new ByteArrayInputStream(byteArray7);
        //URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream7, uri4, DocumentStore.DocumentFormat.BINARY);


        documentStore.setMaxDocumentBytes(40);
        //should push 2 to mem
        //documentStore.get(uri3);
        //documentStore.get(uri1);
        documentStore.undo(uri4);

        //documentStore.setMaxDocumentBytes(20);
//y wont push work
    }

    @Test
    public void ReplacingWithABigDocLookingAtDocLimit() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        //documentStore.setMaxDocumentBytes(1000);
        //documentStore.setMaxDocumentCount(10);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.BINARY);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        //call get on uri one to reset use time
        //documentStore.get(uri1);


        String testText4 = "this is a test text 4";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.BINARY);

        String testText5 = "this is a test text 5";
        byte[] byteArray5 = new byte[10];
        InputStream inputStream5 = new ByteArrayInputStream(byteArray5);
        URI uri5 = URI.create("http://test.com/almost/there/document5");
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.BINARY);



        String testText6 = "this is a test text 6";
        byte[] byteArray6 = new byte[10];
        InputStream inputStream6 = new ByteArrayInputStream(byteArray6);
        URI uri6 = URI.create("http://test.com/almost/there/document6");
        documentStore.put(inputStream6, uri6, DocumentStore.DocumentFormat.BINARY);

        //documentStore.undo(uri3);

        String testText7 = "this is a test text 4";
        byte[] byteArrayX = new byte[10];
        InputStream inputStreamX = new ByteArrayInputStream(byteArrayX);
        URI uriX = URI.create("http://test.com/almost/there/documentX");
        documentStore.put(inputStreamX, uriX, DocumentStore.DocumentFormat.BINARY);

        //documentStore.get(uri3);
        //should push 1 to mem
        String testText7a = "this is a test text 6";
        byte[] byteArray7 = new byte[10];
        InputStream inputStream7 = new ByteArrayInputStream(byteArray7);
        URI uri7 = URI.create("http://test.com/almost/there/document7");
        documentStore.put(inputStream7, uri7, DocumentStore.DocumentFormat.BINARY);

        //documentStore.setMaxDocumentBytes(20);

        //should push 5 to mem
        String testText8 = "this is a test text 6";
        byte[] byteArray8 = new byte[10];
        InputStream inputStream8 = new ByteArrayInputStream(byteArray8);
        URI uri8 = URI.create("http://test.com/almost/there/document8");
        documentStore.put(inputStream8, uri8, DocumentStore.DocumentFormat.BINARY);

        documentStore.get(uri4);
        //should push 1 2 3 4
        documentStore.setMaxDocumentCount(5);
        //should push all except 4
        documentStore.setMaxDocumentBytes(10);


    }

    @Test
    public void ReplacingWithABigDoc() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        documentStore.setMaxDocumentBytes(1000);
        //documentStore.setMaxDocumentCount(10);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.BINARY);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        //call get on uri one to reset use time
        documentStore.get(uri1);


        String testText4 = "this is a test text 4";
        byte[] byteArray4 = new byte[20];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.BINARY);

        String testText5 = "this is a test text 5";
        byte[] byteArray5 = new byte[10];
        InputStream inputStream5 = new ByteArrayInputStream(byteArray5);
        URI uri5 = URI.create("http://test.com/almost/there/document5");
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.BINARY);



        String testText6 = "this is a test text 6";
        byte[] byteArray6 = new byte[10];
        InputStream inputStream6 = new ByteArrayInputStream(byteArray6);
        URI uri6 = URI.create("http://test.com/almost/there/document6");
        documentStore.put(inputStream6, uri6, DocumentStore.DocumentFormat.BINARY);

        //documentStore.undo(uri3);

        String testText7 = "this is a test text 4";
        byte[] byteArray4b = new byte[10000000];
        InputStream inputStream4b = new ByteArrayInputStream(byteArray4b);
        //URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream4b, uri4, DocumentStore.DocumentFormat.BINARY);


        documentStore.setMaxDocumentBytes(60);
        //should push 2 to mem
        documentStore.undo(uri4);


        documentStore.get(uri3);
        //should push 1 to mem
        String testText7a = "this is a test text 6";
        byte[] byteArray7 = new byte[10];
        InputStream inputStream7 = new ByteArrayInputStream(byteArray7);
        URI uri7 = URI.create("http://test.com/almost/there/document7");
        documentStore.put(inputStream7, uri7, DocumentStore.DocumentFormat.BINARY);

        //documentStore.setMaxDocumentBytes(20);

        //should push 5 to mem
        String testText8 = "this is a test text 6";
        byte[] byteArray8 = new byte[10];
        InputStream inputStream8 = new ByteArrayInputStream(byteArray8);
        URI uri8 = URI.create("http://test.com/almost/there/document8");
        documentStore.put(inputStream8, uri8, DocumentStore.DocumentFormat.BINARY);

        documentStore.get(uri4);
        //should push all except 4
        documentStore.setMaxDocumentCount(1);

    }

    @Test
    public void BasicPutThenLimit() throws IOException {
        DocumentStoreImpl documentStore = new DocumentStoreImpl(new File("/Users/josephcouzens/Desktop/testTrash"));

        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri1 = URI.create("https://example.com:8080/test1/test2/65378");
        //setting memory
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.BINARY);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("https://example.com:8080/test1/test2/65379");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        URI uri3 = URI.create("https://example.com:8080/test1/test2/65374");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        //call get on uri one to reset use time
        //documentStore.get(uri1);

        //now pass memory
        String testText4 = "this is a test text 4";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("https://example.com:8080/test1/test2/65371");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.BINARY);

        documentStore.setMaxDocumentCount(3);

        String testText5 = "this is a test text 5";
        byte[] byteArray5 = new byte[10];
        InputStream inputStream5 = new ByteArrayInputStream(byteArray5);
        URI uri5 = URI.create("https://example.com:8080/test1/test2/653456");
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.BINARY);

        documentStore.get(uri1);
        documentStore.get(uri2);
        //documentStore.setMaxDocumentBytes(50);
        //documentStore.setMaxDocumentCount(3);

    }

    @Test
    public void AllTheBigDocs() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        documentStore.setMaxDocumentBytes(1000);
        documentStore.setMaxDocumentCount(10);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.BINARY);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        //call get on uri one to reset use time
        documentStore.get(uri1);

        //now pass memory
        String testText4 = "this is a test text 4";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.BINARY);

        String testText5 = "this is a test text 5";
        byte[] byteArray5 = new byte[40];
        InputStream inputStream5 = new ByteArrayInputStream(byteArray5);
        URI uri5 = URI.create("http://test.com/almost/there/document5");
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.BINARY);

        documentStore.get(uri5);
        documentStore.delete(uri5);
        documentStore.undo();

        documentStore.get(uri1);
        documentStore.delete(uri3);


        String testText6 = "this is a test text 6";
        byte[] byteArray6 = new byte[20];
        InputStream inputStream6 = new ByteArrayInputStream(byteArray6);
        URI uri6 = URI.create("http://test.com/almost/there/document6");
        documentStore.put(inputStream6, uri6, DocumentStore.DocumentFormat.BINARY);

        documentStore.undo(uri3);

        String testText7 = "this is a test text 4";
        byte[] byteArray7 = new byte[10000000];
        InputStream inputStream7 = new ByteArrayInputStream(byteArray7);
        //URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream7, uri4, DocumentStore.DocumentFormat.BINARY);

        //documentStore.undo();

        //documentStore.setMaxDocumentBytes(20);

    }


    @Test
    public void TestGeneralStuffWithMemory() throws IOException {

        DocumentStore documentStore = new DocumentStoreImpl();

        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        documentStore.setMaxDocumentBytes(30);
        documentStore.setMaxDocumentCount(10);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        //call get on uri one to reset use time
        documentStore.get(uri1);

        //now pass memory
        String testText4 = "this is a test text 4";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.BINARY);

        //should send second doc to disk
        //now should bring it back off pushing 3 to disk
        documentStore.get(uri2);
        //should delete doc 3 from everywhere
        documentStore.undo(uri3);
    }

    @Test
    public void SearchWithManyDocsOnDisk() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        String testText1 = "this is a test text 1 poop";
        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(testText1.getBytes());
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        //documentStore.setMaxDocumentBytes(30);
        documentStore.setMaxDocumentCount(3);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2 poop";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text 3 too";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        //call get on uri one to reset use time
        //documentStore.get(uri1);

        //now pass memory
        String testText4 = "this is a test text 4 too";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        //kicks one to disk
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        String testText5 = "this is a test text 5 too";
        byte[] byteArray5 = new byte[10];
        InputStream inputStream5 = new ByteArrayInputStream(testText5.getBytes());
        URI uri5 = URI.create("http://test.com/almost/there/document5");
        //kicks two to disk
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.TXT);

        System.out.println("docs with poop: ");
        //should kick 3 and 4
        for (int i = 0; i < documentStore.search("poop").size(); i++){
            System.out.println(documentStore.search("poop").get(i).getKey());
        }

        //should kick 1 and 2
        System.out.println("docs with too: ");
        for (int i = 0; i < documentStore.searchByPrefix("too").size(); i++){
            System.out.println(documentStore.searchByPrefix("too").get(i).getKey());
        }

        documentStore.undo(uri2);

    }

    @Test
    public void testDeletes() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        String testText1 = "this is a test text 1 poop";
        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(testText1.getBytes());
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        //documentStore.setMaxDocumentBytes(30);
        documentStore.setMaxDocumentCount(3);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2 poop";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text 3 too";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        //call get on uri one to reset use time
        //documentStore.get(uri1);

        //now pass memory
        String testText4 = "this is a test text 4 too";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        //kicks one to disk
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        String testText5 = "this is a test text 5 too";
        byte[] byteArray5 = new byte[10];
        InputStream inputStream5 = new ByteArrayInputStream(testText5.getBytes());
        URI uri5 = URI.create("http://test.com/almost/there/document5");
        //kicks two to disk
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.TXT);

        documentStore.delete(uri5);
        documentStore.undo();

        //documentStore.deleteAll("poop");

    }

    @Test
    public void testDeletes2() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        String testText1 = "this is a test text 1 poop";
        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(testText1.getBytes());
        URI uri1 = URI.create("http://test.com/almost/there/document1");
        //setting memory
        //documentStore.setMaxDocumentBytes(30);
        documentStore.setMaxDocumentCount(3);
        documentStore.put(inputStream, uri1, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2 poop";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text 3 too";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        //call get on uri one to reset use time
        //documentStore.get(uri1);

        //now pass memory
        String testText4 = "this is a test text 4 too";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        //kicks one to disk
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        String testText5 = "this is a test text 5 too";
        byte[] byteArray5 = new byte[10];
        InputStream inputStream5 = new ByteArrayInputStream(testText5.getBytes());
        URI uri5 = URI.create("http://test.com/almost/there/document5");
        //kicks two to disk
        documentStore.put(inputStream5, uri5, DocumentStore.DocumentFormat.TXT);

        documentStore.delete(uri5);
        documentStore.undo();

        documentStore.deleteAllWithPrefix("p");

        //kicks 4 and 5
        documentStore.undo();
        documentStore.get(uri5);

    }

    @Test
    public void puttingTooBigDoc() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();

        docStore.setMaxDocumentBytes(19);
        String testText1 = "this is a test text 1 poop";
        byte[] byteArray = new byte[20];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri1 = URI.create("http://test.com/almost/there/document1");

        docStore.put(inputStream, uri1, DocumentStore.DocumentFormat.BINARY);
        //docStore.undo();

        String testText2 = "this is a test text 2 poop";
        byte[] byteArray2 = new byte[1];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/almost/there/document2");

        docStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3 poop";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        URI uri3 = URI.create("http://test.com/almost/there/document3");

        docStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        String testText4 = "this is a test text 4 poop";
        byte[] byteArray4 = new byte[9];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        //pushes 2 to disk
        docStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.BINARY);



        //docStore.undo(uri1);
    }

    @Test
    public void puttingTooBigDocMore() throws IOException {
        DocumentStoreImpl docStore = new DocumentStoreImpl();

        docStore.setMaxDocumentBytes(23);
        String testText1 = "this is a test text 1 poop";
        byte[] byteArray = new byte[200000000];
        InputStream inputStream = new ByteArrayInputStream(testText1.getBytes());
        URI uri1 = URI.create("http://test.com/almost/there/document1");

        docStore.put(inputStream, uri1, DocumentStore.DocumentFormat.BINARY);
        //docStore.undo();

        String testText2 = "this is a test text 2 poop";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/almost/there/document2");

        docStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.BINARY);

        String testText3 = "this is a test text 3 poop";
        byte[] byteArray3 = new byte[10];
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/almost/there/document3");

        docStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        String testText4 = "this is a test text 4 poop";
        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/almost/there/document4");

        docStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        docStore.get(uri1);
        //pushes all except 1
        docStore.setMaxDocumentBytes(8);
        //docStore.undo(uri1);
    }

    @Test
    public void findDir(){
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
    }

    @Test
    public void testingHeap(){
        MinHeapImpl<Document> minHeap = new MinHeapImpl<>();
        ArrayList<DocumentImpl> allDoc = new ArrayList<>();
        URI uri1 = URI.create("http://test.com/document");
        DocumentImpl doc1 =  new DocumentImpl(uri1, "yabadabadoo", null);
        allDoc.add(doc1);

        URI uri2 = URI.create("http://test.com/document2");
        DocumentImpl doc2 =  new DocumentImpl(uri2, "yabadabadoo2", null);
        allDoc.add(doc2);

        URI uri3 = URI.create("http://test.com/document3");
        DocumentImpl doc3 =  new DocumentImpl(uri3, "yabadabadoo3", null);
        allDoc.add(doc3);

        URI uri4 = URI.create("http://test.com/document4");
        DocumentImpl doc4 =  new DocumentImpl(uri4, "yabadabadoo4", null);
        allDoc.add(doc4);

        URI uri5 = URI.create("http://test.com/document5");
        DocumentImpl doc5 =  new DocumentImpl(uri5, "yabadabadoo5", null);
        allDoc.add(doc5);

        URI uri6 = URI.create("http://test.com/document6");
        DocumentImpl doc6 =  new DocumentImpl(uri6, "yabadabadoo6", null);
        allDoc.add(doc6);

        URI uri7 = URI.create("http://test.com/document7");
        DocumentImpl doc7 =  new DocumentImpl(uri7, "yabadabadoo7", null);
        allDoc.add(doc7);

        URI uri8 = URI.create("http://test.com/document8");
        DocumentImpl doc8 =  new DocumentImpl(uri8, "yabadabadoo8", null);
        allDoc.add(doc8);

        URI uri9 = URI.create("http://test.com/document9");
        DocumentImpl doc9 =  new DocumentImpl(uri9, "yabadabadoo9", null);
        allDoc.add(doc9);

        URI uri10 = URI.create("http://test.com/document10");
        DocumentImpl doc10 =  new DocumentImpl(uri10, "yabadabadoo10", null);
        allDoc.add(doc10);

        URI uri11 = URI.create("http://test.com/document11");
        DocumentImpl doc11 =  new DocumentImpl(uri11, "yabadabadoo11", null);
        allDoc.add(doc11);

        for(DocumentImpl doc : allDoc){
            doc.setLastUseTime(System.nanoTime());
        }

        minHeap.insert(doc1);
        minHeap.insert(doc2);
        minHeap.insert(doc3);
        minHeap.insert(doc4);
        minHeap.insert(doc5);
        minHeap.insert(doc6);
        minHeap.insert(doc7);
        minHeap.insert(doc8);
        minHeap.insert(doc9);
        minHeap.insert(doc10);
        minHeap.insert(doc11);

        doc1.setLastUseTime(System.nanoTime());
        minHeap.reHeapify(doc1);

        System.out.println();
        //System.out.println(minHeap.getArrayIndex);
    }

/**
    @Test
    public void docMemoryOverload() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        //setting memory
        documentStore.setMaxDocumentBytes(1000000000);
        documentStore.setMaxDocumentCount(3);
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

        assert(documentStore.get(uri) == null);
        assert(documentStore.get(uri4) != null);

    }
    */

/**
    @Test
    public void docMemoryOverloadAdjustedUsage() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "this is a test text";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        //setting memory
        documentStore.setMaxDocumentBytes(1000000000);
        documentStore.setMaxDocumentCount(3);
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text poopy 3";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        documentStore.get(uri);

        String testText4 = "this is not a test text 4";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        assert(documentStore.get(uri2) == null);
        assert(documentStore.get(uri) != null);
        assert(documentStore.get(uri4) != null);

    }
    */

    @Test
    public void byteMemoryOverloadAdjustedUsage() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri = URI.create("http://test.com/almost/there/document1");
        //setting memory
        documentStore.setMaxDocumentBytes(20);
        documentStore.setMaxDocumentCount(10);
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/almost/there/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text poopy 3";
        byte[] byteArray3 = new byte[10]; // create byte array of length 10
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        //InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/almost/there/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        System.out.println(documentStore.get(uri).getKey());

        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/almost/there/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);




        //assert(documentStore.get(uri2) == null);
        //assert(documentStore.get(uri) != null);
        //assert(documentStore.get(uri4) != null);

    }

    /**
    @Test
    public void testRemovedFromCommandSet() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        String testText = "toot poo";
        InputStream inputStream = new ByteArrayInputStream(testText.getBytes());
        URI uri = URI.create("http://test.com/document");
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "toot toot";
        InputStream inputStream2 = new ByteArrayInputStream(testText2.getBytes());
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "teem toot";
        InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.TXT);

        documentStore.setMaxDocumentCount(3);

        String testText4 = "no";
        InputStream inputStream4 = new ByteArrayInputStream(testText4.getBytes());
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        //confirm puts, toot should have 2 values
        //one shouldnt be there
        System.out.println("uri 1: " + documentStore.search("to"));
        assert (documentStore.get(uri) == null);
        System.out.println("uri 2 and 3 b/c toot: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) != null);
        System.out.println("uri 3: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        System.out.println("uri 4: " + documentStore.search("no"));
        assert (documentStore.get(uri4) != null);

        //should delete uri2 and uri3 and uri but uri should already be gone
        documentStore.deleteAll("toot");
        //confirm deletion
        System.out.println("uri 2 and 3/toot search result: " + documentStore.search("toot"));
        assert (documentStore.get(uri2) == null);
        System.out.println("uri 3/teem search results: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) == null);



        //undo general should bring back 2 and 3 but not 1
        documentStore.undo();
        //confirm undid
        System.out.println("should just be uri 3 and 2 in toot: " + documentStore.search("toot"));
        System.out.println("uri 3 in teem: " + documentStore.search("teem"));
        assert (documentStore.get(uri3) != null);
        //confirm uri2 still deleted
        assert (documentStore.get(uri) == null);
    }
    */

    /**
    @Test
    public void testRemovedFromStack() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        byte[] byteArray = new byte[10];
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        URI uri = URI.create("http://test.com/document");
        //setting memory
        documentStore.setMaxDocumentBytes(30);
        documentStore.setMaxDocumentCount(10);
        documentStore.put(inputStream, uri, DocumentStore.DocumentFormat.TXT);

        String testText2 = "this is a test text 2";
        byte[] byteArray2 = new byte[10];
        InputStream inputStream2 = new ByteArrayInputStream(byteArray2);
        URI uri2 = URI.create("http://test.com/document2");
        documentStore.put(inputStream2, uri2, DocumentStore.DocumentFormat.TXT);

        String testText3 = "this is a test text poopy 3";
        byte[] byteArray3 = new byte[10]; // create byte array of length 10
        InputStream inputStream3 = new ByteArrayInputStream(byteArray3);
        //InputStream inputStream3 = new ByteArrayInputStream(testText3.getBytes());
        URI uri3 = URI.create("http://test.com/document3");
        documentStore.put(inputStream3, uri3, DocumentStore.DocumentFormat.BINARY);

        documentStore.get(uri);

        byte[] byteArray4 = new byte[10];
        InputStream inputStream4 = new ByteArrayInputStream(byteArray4);
        URI uri4 = URI.create("http://test.com/document4");
        documentStore.put(inputStream4, uri4, DocumentStore.DocumentFormat.TXT);

        assert(documentStore.get(uri2) == null);
        assert(documentStore.get(uri) != null);
        assert(documentStore.get(uri4) != null);
        URI uri9 = URI.create("poop");

        //should get illegal state exception here
        documentStore.undo(uri2);
    }
*/

}

