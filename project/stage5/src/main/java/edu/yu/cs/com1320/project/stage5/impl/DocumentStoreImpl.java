package edu.yu.cs.com1320.project.stage5.impl;


import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
//import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.impl.StackImpl;

import java.io.File;
import java.util.HashMap;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {

    BTreeImpl<URI, Document> docBTree = new BTreeImpl<>();
    StackImpl<Undoable> commandStack = new StackImpl<>();
    TrieImpl<URI> trieDoc = new TrieImpl<>();
    MinHeapImpl<heapNode> docHeap = new MinHeapImpl<>();

    public DocumentStoreImpl(){
        docBTree.setPersistenceManager(new DocumentPersistenceManager(null));
    }
    public DocumentStoreImpl(File baseDir){
        docBTree.setPersistenceManager(new DocumentPersistenceManager(baseDir));
    }

    private class heapNode implements Comparable<heapNode>{
        URI uri;

        private heapNode(URI uri){
            this.uri = uri;
        }
        private URI getUri(){
            return this.uri;
        }
        @Override
        public int compareTo(heapNode other){
            if (other == null){
                throw new NullPointerException();
            }
            if (docBTree.get(this.getUri()).getLastUseTime() < docBTree.get(other.getUri()).getLastUseTime()){
                return -1;
            }
            if (docBTree.get(this.getUri()).getLastUseTime() > docBTree.get(other.getUri()).getLastUseTime()){
                return 1;
            }
            else {
                return 0;
            }

            //return (int) (docBTree.get(other.getUri()).getLastUseTime() - docBTree.get(this.getUri()).getLastUseTime());
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            heapNode other = (heapNode) obj;
            return this.getUri().equals(other.getUri());
        }
        @Override
        public int hashCode() {
            return Objects.hash(uri);
        }


    }


    /**
     * the two document formats supported by this document store.
     * Note that TXT means plain text, i.e. a String.
     */
    enum DocumentFormat{
        TXT,BINARY
    };
    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0.
     * If there is a previous doc, return the hashCode of the previous doc.
     * If InputStream is null, this is a delete, and thus return either
     * the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
    public int put(InputStream input, URI uri, DocumentStore.DocumentFormat format)
            throws IOException{
        if (input == null){//its a delete
            //below was here once upon a time
            if (docBTree.get(uri) != null){
                int theHash = docBTree.get(uri).hashCode();
                delete(uri);
                return theHash;
            } else{return 0;}}

        if (format == DocumentStore.DocumentFormat.TXT){
            String content = new String(input.readAllBytes());
            DocumentImpl newDoc = new DocumentImpl(uri, content, null);
            return puttingForPut(uri, newDoc, format);}
        if (format == DocumentStore.DocumentFormat.BINARY){
            byte[] content = input.readAllBytes();
            DocumentImpl newDoc = new DocumentImpl(uri, content);
            return puttingForPut(uri, newDoc, format);}
        if (uri == null){throw new IllegalArgumentException();}
        if (format == null){throw new IllegalArgumentException();}
        else{return 0;}}

    /**
     if(docTable.get(uri) != null){
     if (docTable.get(uri).getDocumentBinaryData() != null) {
     DocumentImpl newDoc = new DocumentImpl(uri, docTable.get(uri).getDocumentBinaryData());
     GenericCommand newDelete = new GenericCommand(uri, (URI) -> {
     docTable.put(uri, newDoc);return true;});
     commandStack.push(newDelete);
     } else{
     DocumentImpl newDoc = new DocumentImpl(uri, docTable.get(uri).getDocumentTxt());
     GenericCommand newDelete = new GenericCommand(uri, (URI) -> {
     docTable.put(uri, newDoc); puttingInTrie(newDoc); return true;});
     commandStack.push(newDelete);
     deleteDocFromTrie(newDoc); //delete from trie
     }
     */

    private int putForBigDoc(DocumentImpl newDoc, DocumentImpl oldDoc){//put for when doc too big for memory
        if (oldDoc != null){//its a replacement
            int theHash = oldDoc.hashCode();
            if(newDoc.getDocumentTxt() != null) {//its text doc
                GenericCommand<URI> replacingPut = new GenericCommand<>(newDoc.getKey(), (URI) -> {
                    //docBTree.put(uri, oldDoc);//should delete newDoc
                    //currBytes = currBytes - newDoc.getDocumentTxt().getBytes().length;
                    if (oldDoc.getDocumentTxt() != null) {
                        if (oldDoc.getDocumentTxt().getBytes().length < maxDocumentBytes) {
                            currBytes = currBytes + oldDoc.getDocumentTxt().getBytes().length;
                            deleteDocFromTrie(newDoc);
                            uriOnDisk.remove(newDoc.getKey());
                            puttingInTrie(oldDoc);
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap((DocumentImpl) oldDoc);
                        } else {//doc that was replaced too big to fit in mem coming back in
                            deleteDocFromTrie(newDoc);
                            puttingInTrie(oldDoc);
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            uriOnDisk.add(oldDoc.getKey());
                            try {
                                docBTree.moveToDisk(oldDoc.getKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else {
                        if (oldDoc.getDocumentBinaryData().length < maxDocumentBytes) {
                            currBytes = currBytes + oldDoc.getDocumentBinaryData().length;
                            deleteDocFromTrie(newDoc);
                            //puttingInTrie(oldDoc);
                            uriOnDisk.remove(newDoc.getKey());
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap((DocumentImpl) oldDoc);
                        } else {//doc that was replaced too big to fit in mem coming back in
                            deleteDocFromTrie(newDoc);
                            //puttingInTrie(oldDoc);
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            uriOnDisk.add(oldDoc.getKey());
                            try {
                                docBTree.moveToDisk(oldDoc.getKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    return true;
                });
                commandStack.push(replacingPut);
                if (!uriOnDisk.contains(oldDoc.getKey())){//check if in bytes
                    currBytes = currBytes - oldDoc.getDocumentTxt().getBytes().length;
                    deleteFromHeap((DocumentImpl) oldDoc);
                }
                else{
                    uriOnDisk.remove(oldDoc.getKey());
                }
                if (oldDoc.getDocumentTxt() != null){
                    deleteDocFromTrie(oldDoc);
                }
                puttingInTrie(newDoc);
                docBTree.put(newDoc.getKey(), newDoc);
                try {
                    docBTree.moveToDisk(newDoc.getKey());
                    uriOnDisk.add(newDoc.getKey());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else{//binary doc
                GenericCommand<URI> replacingPut = new GenericCommand<>(newDoc.getKey(), (URI) -> {
                    //docBTree.put(uri, oldDoc);//should delete newDoc
                    //currBytes = currBytes - newDoc.getDocumentTxt().getBytes().length;
                    if (oldDoc.getDocumentTxt() != null) {//old doc is text
                        if (oldDoc.getDocumentTxt().getBytes().length < maxDocumentBytes) {
                            currBytes = currBytes + oldDoc.getDocumentTxt().getBytes().length;
                            //deleteDocFromTrie(newDoc);
                            puttingInTrie(oldDoc);
                            uriOnDisk.remove(newDoc.getKey());
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap((DocumentImpl) oldDoc);
                        } else {//doc that was replaced too big to fit in mem coming back in
                            //deleteDocFromTrie(newDoc);
                            puttingInTrie(oldDoc);
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            uriOnDisk.add(oldDoc.getKey());
                            try {
                                docBTree.moveToDisk(oldDoc.getKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    else {//old doc is binary
                        if (oldDoc.getDocumentBinaryData().length < maxDocumentBytes) {
                            currBytes = currBytes + oldDoc.getDocumentBinaryData().length;
                            //deleteDocFromTrie(newDoc);
                            //puttingInTrie(oldDoc);
                            uriOnDisk.remove(newDoc.getKey());
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap((DocumentImpl) oldDoc);
                        } else {//doc that was replaced too big to fit in mem coming back in
                            //deleteDocFromTrie(newDoc);
                            //puttingInTrie(oldDoc);
                            docBTree.put(oldDoc.getKey(), oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            uriOnDisk.add(oldDoc.getKey());
                            try {
                                docBTree.moveToDisk(oldDoc.getKey());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    return true;
                });
                commandStack.push(replacingPut);
                if (!uriOnDisk.contains(oldDoc.getKey())) {//check if in bytes
                    if (oldDoc.getDocumentTxt() != null){
                        currBytes = currBytes - oldDoc.getDocumentTxt().getBytes().length;
                    }
                    else{
                        currBytes = currBytes - oldDoc.getDocumentBinaryData().length;
                    }
                    deleteFromHeap((DocumentImpl) oldDoc);
                }
                else{
                    uriOnDisk.remove(oldDoc.getKey());
                }
                deleteDocFromTrie(oldDoc);
                //puttingInTrie(newDoc);
                docBTree.put(newDoc.getKey(), newDoc);
                try {
                    docBTree.moveToDisk(newDoc.getKey());
                    uriOnDisk.add(newDoc.getKey());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return theHash;
        }
        else{//not a replacement
            docBTree.put(newDoc.getKey(), newDoc);
            try {
                docBTree.moveToDisk(newDoc.getKey());
                uriOnDisk.add(newDoc.getKey());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (newDoc.getDocumentTxt() != null){//text doc
                puttingInTrie(newDoc);
            }
            GenericCommand<URI> newPut =  new GenericCommand<>(newDoc.getKey(), (URI)-> {
                if (newDoc.getDocumentTxt() != null){
                    deleteDocFromTrie(newDoc);
                }
                uriOnDisk.remove(newDoc.getKey());
                docBTree.put(newDoc.getKey(), null);
                return true;
            });
            commandStack.push(newPut);
            return 0;
        }
    }


    private int puttingForPut(URI uri, DocumentImpl newDoc, DocumentStore.DocumentFormat format) {
        if (docBTree.get(uri) != null) { //means its a replacement
            int theHash = docBTree.get(uri).hashCode(); //hash to return for replaced doc
            if (format == DocumentStore.DocumentFormat.TXT){
                DocumentImpl oldDoc = new DocumentImpl(uri, docBTree.get(uri).getDocumentTxt(), docBTree.get(uri).getWordMap());
                //make sure replacement wont exceed memory
                //adjust byte count
                if(maxDocumentBytes != -1) {//need to change this
                    if (newDoc.getDocumentTxt().getBytes().length > maxDocumentBytes) {
                        return putForBigDoc(newDoc, oldDoc);
                    }
                }
                if (!uriOnDisk.contains(oldDoc.getKey())){//check if in bytes
                    currBytes = currBytes - oldDoc.getDocumentTxt().getBytes().length;
                }
                currBytes = currBytes + newDoc.getDocumentTxt().getBytes().length;
                GenericCommand<URI> replacingPut =  new GenericCommand<>(uri, (URI)->{
                    //docBTree.put(uri, oldDoc);//should delete newDoc
                    deleteDocFromTrie(newDoc);
                    if (!uriOnDisk.contains(newDoc.getKey())) {
                        currBytes = currBytes - newDoc.getDocumentTxt().getBytes().length;
                        deleteFromHeap(newDoc);
                    }
                    if (oldDoc.getDocumentTxt() != null){//old doc is text
                        puttingInTrie(oldDoc);
                        if (oldDoc.getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                            currBytes = currBytes + oldDoc.getDocumentTxt().getBytes().length;
                            docBTree.put(uri, oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap(oldDoc);
                        }
                    }
                    else{//old doc is binary
                        if (oldDoc.getDocumentBinaryData().length <= maxDocumentBytes || maxDocumentBytes == -1){
                            currBytes = currBytes + oldDoc.getDocumentBinaryData().length;
                            docBTree.put(uri, oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap(oldDoc);
                        }
                    }
                    //deleteFromHeap(newDoc);
                     return true;});
                commandStack.push(replacingPut);
                deleteDocFromTrie(oldDoc);
                puttingInTrie(newDoc);
                if (!uriOnDisk.contains(uri)){//delete from heap only if in heap
                    deleteFromHeap(oldDoc);
                }
                else{//if not in heap delete from list of uris not in heap
                    uriOnDisk.remove(uri);
                }
                //adjust for memory if needed
                if (currBytes > maxDocumentBytes){
                    int numberNeedDeleted = currBytes - maxDocumentBytes;
                    manageByteMemory(numberNeedDeleted);
                }
            }
            if (format == DocumentStore.DocumentFormat.BINARY){
                //    Document oldDoc = docTable.get(uri);
                DocumentImpl oldDoc = new DocumentImpl(uri, docBTree.get(uri).getDocumentBinaryData());
                //oldDoc.setLastUseTime(docBTree.get(uri).getLastUseTime());
                if(maxDocumentBytes != -1) {
                    if (newDoc.getDocumentBinaryData().length > maxDocumentBytes) {
                        return putForBigDoc(newDoc, oldDoc);
                    }
                }
                if (!uriOnDisk.contains(oldDoc.getKey())){//check if in bytes
                    currBytes = currBytes - oldDoc.getDocumentBinaryData().length;
                }
                currBytes = currBytes + newDoc.getDocumentBinaryData().length;
                GenericCommand<URI> replacingPut =  new GenericCommand<>(uri, (URI)->{
                    //docBTree.put(uri, oldDoc);//should delete newDoc
                    //deleteDocFromTrie(newDoc);
                    if (!uriOnDisk.contains(newDoc.getKey())) {
                        currBytes = currBytes - newDoc.getDocumentBinaryData().length;
                        deleteFromHeap(newDoc);
                    }
                    if (oldDoc.getDocumentTxt() != null){//old doc is text
                        puttingInTrie(oldDoc);
                        if (oldDoc.getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                            currBytes = currBytes + oldDoc.getDocumentTxt().getBytes().length;
                            docBTree.put(uri, oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap(oldDoc);
                        }
                    }
                    else{//old doc is binary
                        if (oldDoc.getDocumentBinaryData().length <= maxDocumentBytes || maxDocumentBytes == -1){
                            currBytes = currBytes + oldDoc.getDocumentBinaryData().length;
                            docBTree.put(uri, oldDoc);
                            oldDoc.setLastUseTime(System.nanoTime());
                            putInHeap(oldDoc);
                        }
                    }
                    //deleteFromHeap(newDoc);
                    return true;});
                commandStack.push(replacingPut);
                if (!uriOnDisk.contains(uri)){
                    deleteFromHeap(oldDoc);
                }
                else{
                    uriOnDisk.remove(uri);
                }
                //adjust for memory if need
                if (currBytes > maxDocumentBytes){
                    int numberNeedDeleted = currBytes - maxDocumentBytes;
                    manageByteMemory(numberNeedDeleted);
                }
            }
            //replacement methods that need to happen to both types
            docBTree.put(uri, newDoc);
            newDoc.setLastUseTime(System.nanoTime());
            putInHeap(newDoc);
            return theHash;
        }
        else {//its not a replacement
            if(maxDocumentBytes != -1) {
                if (format == DocumentStore.DocumentFormat.BINARY){
                    if (newDoc.getDocumentBinaryData().length > maxDocumentBytes) {
                        return putForBigDoc(newDoc, null);
                    }
                }
                if (format == DocumentStore.DocumentFormat.TXT){
                    if (newDoc.getDocumentTxt().getBytes().length > maxDocumentBytes) {
                        return putForBigDoc(newDoc, null);
                    }
                }
            }
            int potByteMemory = currBytes;
            if (format == DocumentStore.DocumentFormat.BINARY){
                potByteMemory = currBytes + newDoc.getDocumentBinaryData().length;
            }
            if (format == DocumentStore.DocumentFormat.TXT){
                potByteMemory = currBytes + newDoc.getDocumentTxt().getBytes().length;
            }
            //manage byte mem if needed
            if (potByteMemory > maxDocumentBytes){
                int numberNeedDeleted = potByteMemory - maxDocumentBytes;
                manageByteMemory(numberNeedDeleted);
            }
            if (currDocs + 1 > maxDocumentCount){
                manageDocMemory(1);
            }
            currDocs = currDocs + 1;
            newDoc.setLastUseTime(System.nanoTime());
            docBTree.put(uri, newDoc);
            putInHeap(newDoc);
            //putInHeap(newDoc);
            if (format == DocumentStore.DocumentFormat.TXT){
                puttingInTrie(newDoc);
                //add in new bytes of text length
                currBytes = currBytes + newDoc.getDocumentTxt().getBytes().length;
                GenericCommand<URI> newPut =  new GenericCommand<>(uri, (URI)-> {
                    deleteDocFromTrie(newDoc);
                    if (!uriOnDisk.contains(uri)){
                        currBytes = currBytes - newDoc.getDocumentTxt().getBytes().length;
                        deleteFromHeap(newDoc);
                    }
                    else{
                        uriOnDisk.remove(uri);
                    }
                    docBTree.put(uri, null);
                    //deleteFromHeap(newDoc);
                    return true;});
                commandStack.push(newPut);
            }
            else{
                //add binary data bytes to total
                currBytes = currBytes + newDoc.getDocumentBinaryData().length;
                GenericCommand<URI> newPut =  new GenericCommand<>(uri, (URI)-> {
                    if (!uriOnDisk.contains(uri)){
                        currBytes = currBytes - newDoc.getDocumentBinaryData().length;
                        deleteFromHeap(newDoc);
                    }
                    else{
                        uriOnDisk.remove(uri);
                    }
                    docBTree.put(uri, null);
                    //deleteFromHeap(newDoc);
                    return true;});
                commandStack.push(newPut);
            }
            //    GenericCommand newPut =  new GenericCommand(uri, (URI)-> {deleteDocFromTrie(newDoc); return delete(uri);});
            //commandStack.push(newPut);
            //docBTree.put(uri, newDoc);
            //putInHeap(newDoc);
            return 0;
        }
    }


    //need this to store doc in trie accordingly and also add to command
    //stack for individual puts
    //will be a looping delete off of string and doc where strings rotate
    private void puttingInTrie(DocumentImpl doc){
        if (doc.getDocumentTxt() == null){//not a text doc
            return;
        }
        Set<String> wordsInDoc = doc.wordMap.keySet();
        for (String i : wordsInDoc){
            trieDoc.put(i, doc.getKey());
        }
    }
    private void deleteDocFromTrie(DocumentImpl doc){
        if (doc.getDocumentTxt() == null){//not a text doc
            return;
        }
        Set<String> wordsInDoc = doc.wordMap.keySet();
        if (wordsInDoc.isEmpty()){return;}
        for (String i : wordsInDoc){
            trieDoc.delete(i, doc.getKey());
        }
    }


    HashMap<URI, heapNode> uriNodeMap = new HashMap<>();

    private void putInHeap(DocumentImpl doc){
        heapNode docHNode = new heapNode(doc.getKey());
        uriNodeMap.put(doc.getKey(), docHNode);
        docHeap.insert(docHNode);
    }
    //more recently used docs will have a greater last used time
    //move desired deletion element to top of heap, giving it the smallest possible
    //last use time, then remove then reheap
    private void deleteFromHeap(DocumentImpl doc){
        docBTree.get(doc.getKey()).setLastUseTime(-1000);
        doc.setLastUseTime(-10000);
        docHeap.reHeapify(uriNodeMap.get(doc.getKey()));
        docHeap.remove();
    }


    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document get(URI uri){
        if(docBTree.get(uri) == null){
            return docBTree.get(uri);
        }
        Document gotten = docBTree.get(uri);
        docBTree.get(uri).setLastUseTime(System.nanoTime());
        if (uriOnDisk.contains(uri)){
            if (docBTree.get(uri).getDocumentTxt() != null){
                if (docBTree.get(uri).getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                    currBytes = currBytes + docBTree.get(uri).getDocumentTxt().getBytes().length;
                    currDocs = currDocs + 1;
                    putInHeap((DocumentImpl) docBTree.get(uri));
                    uriOnDisk.remove(uri);
                }
                else{
                    try {
                        docBTree.moveToDisk(uri);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else{//it is binary
                if (docBTree.get(uri).getDocumentBinaryData().length <= maxDocumentBytes || maxDocumentBytes == -1){
                    currBytes = currBytes + docBTree.get(uri).getDocumentBinaryData().length;
                    currDocs = currDocs + 1;
                    putInHeap((DocumentImpl) docBTree.get(uri));
                    uriOnDisk.remove(uri);
                }
                else{
                    try {
                        docBTree.moveToDisk(uri);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (currBytes > maxDocumentBytes){
                int needDeleted = currBytes - maxDocumentBytes;
                manageByteMemory(needDeleted);
            }
            if (currDocs > maxDocumentCount){
                int needDeleted = currDocs - maxDocumentCount;
                manageDocMemory(needDeleted);
            }
        }
        if(!uriOnDisk.contains(uri)) {
            docHeap.reHeapify(uriNodeMap.get(uri));
        }
        return gotten;
    }

    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    public boolean delete(URI uri){
        if (docBTree.get(uri) != null){//will always be true usually cuz we checked in the put
            if (docBTree.get(uri).getDocumentBinaryData() != null) {//it is binary doc
                //newDoc is really the doc that is being deleted
                DocumentImpl newDoc = new DocumentImpl(uri, docBTree.get(uri).getDocumentBinaryData());
                if (!uriOnDisk.contains(uri)){
                    currBytes = currBytes - newDoc.getDocumentBinaryData().length;
                    currDocs = currDocs - 1;
                }
                GenericCommand<URI> newDelete = new GenericCommand<>(uri, (URI) -> {
                    docBTree.put(uri, newDoc);
                    if (newDoc.getDocumentBinaryData().length <= maxDocumentBytes || maxDocumentBytes == -1){
                        currDocs = currDocs + 1;
                        currBytes = currBytes + newDoc.getDocumentBinaryData().length;
                        newDoc.setLastUseTime(System.nanoTime());
                        putInHeap(newDoc);
                    }
                    else{
                        try {
                            docBTree.moveToDisk(uri);
                            uriOnDisk.add(uri);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;});//mem management handles in undo itself
                commandStack.push(newDelete);
            } else{//its text doc
                DocumentImpl newDoc = new DocumentImpl(uri, docBTree.get(uri).getDocumentTxt(), docBTree.get(uri).getWordMap());
                if (!uriOnDisk.contains(uri)){
                    currBytes = currBytes - newDoc.getDocumentTxt().getBytes().length;
                    currDocs = currDocs - 1;
                }
                GenericCommand<URI> newDelete = new GenericCommand<>(uri, (URI) -> {
                    docBTree.put(uri, newDoc);
                    puttingInTrie(newDoc);
                    if (newDoc.getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                        currDocs = currDocs + 1;
                        currBytes = currBytes + newDoc.getDocumentTxt().getBytes().length;
                        newDoc.setLastUseTime(System.nanoTime());
                        putInHeap(newDoc);
                    }
                    else{
                        try {
                            docBTree.moveToDisk(uri);
                            uriOnDisk.add(uri);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return true;});
                commandStack.push(newDelete);
                deleteDocFromTrie((DocumentImpl) docBTree.get(uri)); //delete from trie
            }
            //deleteDocFromTrie((DocumentImpl) docTable.get(uri));
            if(!uriOnDisk.contains(uri)){
                deleteFromHeap((DocumentImpl) docBTree.get(uri));
            }
            else{
                uriOnDisk.remove(uri);
            }
            docBTree.put(uri, null);
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to
     * be undone, i.e. the command stack is empty
     */
    public void undo() throws IllegalStateException{
        if (commandStack.peek()==null){
            throw new IllegalStateException();
        }
        if(commandStack.size() == 0){
            throw new IllegalStateException();
        }
        else{
            //need to check that this undo wont exceed memory, nvm just adjust after undone
            //Document undoneDoc = docTable.get((URI) ((GenericCommand<?>) commandStack.peek()).getTarget());
            commandStack.pop().undo();
            if (currBytes > maxDocumentBytes){
                int numberNeedDeleted = currBytes - maxDocumentBytes;
                manageByteMemory(numberNeedDeleted);
            }
            if (currDocs > maxDocumentCount){
                int numberNeedDeleted = currDocs - maxDocumentCount;
                manageDocMemory(numberNeedDeleted);
            }
        }
    }

    /**
     * undo the last put or delete that was done with the given
     * URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on
     * the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException{
        boolean uriFound = false;
        if(commandStack.size() == 0){
            throw new IllegalStateException();
        }
        StackImpl<Undoable> tempStack = new StackImpl<>();
        int originalStackSize = commandStack.size();
        for (int i=0; i< originalStackSize; i++){
            if(commandStack.peek() instanceof GenericCommand<?>) {
                GenericCommand<URI> uriTargetTemp = new GenericCommand<>(uri, (a) -> {
                    return true;
                });
                if (commandStack.peek().equals(uriTargetTemp)) {
                    commandStack.pop().undo();
                    //    System.out.println("Found and undid");
                    //manage memory if needed
                    if (currBytes > maxDocumentBytes){
                        int numberNeedDeleted = currBytes - maxDocumentBytes;
                        manageByteMemory(numberNeedDeleted);
                    }
                    if (currDocs > maxDocumentCount){
                        int numberNeedDeleted = currDocs - maxDocumentCount;
                        manageDocMemory(numberNeedDeleted);
                    }
                    uriFound = true;
                    break;
                } else {
                    tempStack.push(commandStack.pop());
                }
            }
            if(commandStack.peek() instanceof CommandSet<?>){
                CommandSet<URI> cmdSetReference = (CommandSet<URI>) commandStack.peek();
                if(cmdSetReference.containsTarget(uri)){
                    cmdSetReference.undo(uri);
                    uriFound = true;
                    //manage memory if needed
                    if (currBytes > maxDocumentBytes){
                        int numberNeedDeleted = currBytes - maxDocumentBytes;
                        manageByteMemory(numberNeedDeleted);
                    }
                    if (currDocs > maxDocumentCount){
                        int numberNeedDeleted = currDocs - maxDocumentCount;
                        manageDocMemory(numberNeedDeleted);
                    }
                    if (cmdSetReference.size() == 0){
                        commandStack.pop();
                    }
                    break;
                }
                else {
                    tempStack.push(commandStack.pop());
                }
            }
        }
        int startingTempSize = tempStack.size();
        for (int i=0; i< startingTempSize; i++){
            commandStack.push(tempStack.pop());
        }
        if (uriFound == false){
            throw new IllegalStateException();
        }
    }

    private class sorter implements Comparator<URI>{
        String key;
        private sorter(String key){
            this.key = key;
        }
        @Override
        public int compare(URI uriA, URI uriB){
            int docAMentions = docBTree.get(uriA).wordCount(key);
            int docBMentions = docBTree.get(uriB).wordCount(key);
            return docBMentions - docAMentions;
        }
    }

    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order,
     * sorted by the number of times the keyword appears in the document.
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword){
        if (keyword.contains("[^a-zA-Z0-9\\s]") || keyword == null){
            return Collections.emptyList();
        }
        long timeUsed = System.nanoTime();
        List<URI> result = trieDoc.getAllSorted(keyword, new sorter(keyword));
        ArrayList<Document> resultDocs = new ArrayList<>();
        for (int i = 0; i < result.size(); i++){//set use time and adjust heap
            docBTree.get(result.get(i)).setLastUseTime(timeUsed);
            resultDocs.add(docBTree.get(result.get(i)));
            if (uriOnDisk.contains(result.get(i))){//if doc was on disk and not in heap, put in heap
                if(docBTree.get(result.get(i)).getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                    uriOnDisk.remove(result.get(i));
                    putInHeap((DocumentImpl) docBTree.get(result.get(i)));
                    currBytes = currBytes + docBTree.get(result.get(i)).getDocumentTxt().getBytes().length;
                    currDocs++;
                }
                else{
                    try {
                        docBTree.moveToDisk(result.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (!uriOnDisk.contains(result.get(i))){
                docHeap.reHeapify(uriNodeMap.get(result.get(i)));
            }
        }
        if (currBytes > maxDocumentBytes){
            manageByteMemory(currBytes - maxDocumentBytes);
        }
        if (currDocs > maxDocumentCount){
            manageDocMemory(currDocs - maxDocumentCount);
        }
        return resultDocs;
    }



    private class sorterPrefix implements Comparator<URI>{
        String key;
        private sorterPrefix(String key){
            this.key = key;
        }
        @Override
        public int compare(URI docAUri, URI docBUri){
            //think this is creating errors
            HashSet<String> aKeySet = new HashSet<>(docBTree.get(docAUri).getWords());
            HashSet<String> bKeySet = new HashSet<>(docBTree.get(docBUri).getWords());
            int prefixMentionsA = 0;
            int prefixMentionsB = 0;
            for (String i : aKeySet){
                if(i.startsWith(this.key)){
                    prefixMentionsA = prefixMentionsA + docBTree.get(docAUri).wordCount(i);
                }
            }
            for (String i : bKeySet){
                if(i.startsWith(this.key)){
                    prefixMentionsB = prefixMentionsB + docBTree.get(docBUri).wordCount(i);
                }
            }
            return prefixMentionsB - prefixMentionsA;
        }
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order,
     * sorted by the number of times the prefix appears in the document.
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix){
        if (keywordPrefix == null || keywordPrefix.contains("[^a-zA-Z0-9\\s]")){
            return Collections.emptyList();
        }
        List<URI> result = trieDoc.getAllWithPrefixSorted(keywordPrefix, new sorterPrefix(keywordPrefix));
        long timeUsed = System.nanoTime();
        List<Document> resultDocs = new ArrayList<>();
        for (int i = 0; i < result.size(); i++){//adjust use time and heap
            docBTree.get((result.get(i))).setLastUseTime(timeUsed);
            resultDocs.add(docBTree.get(result.get(i)));
            if (uriOnDisk.contains(result.get(i))){//if doc was on disk and not in heap, put in heap
                if(docBTree.get(result.get(i)).getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                    putInHeap((DocumentImpl) docBTree.get(result.get(i)));
                    uriOnDisk.remove(result.get(i));
                    currBytes = currBytes + docBTree.get(result.get(i)).getDocumentTxt().getBytes().length;
                    currDocs++;
                }
                else{
                    try {
                        docBTree.moveToDisk(result.get(i));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (!uriOnDisk.contains(result.get(i))){
                docHeap.reHeapify(uriNodeMap.get(result.get(i)));
            }
        }
        if (currBytes > maxDocumentBytes){
            manageByteMemory(currBytes - maxDocumentBytes);
        }
        if (currDocs > maxDocumentCount){
            manageDocMemory(currDocs - maxDocumentCount);
        }
        return resultDocs;
    }

    /**
     * Completely remove any trace of any document which contains
     * the given keyword
     * Search is CASE SENSITIVE.
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    //deleteAll from trie then with that list of values/documents
    //delete from the Hashtable
    public Set<URI> deleteAll(String keyword){
        if (keyword.contains("[^a-zA-Z0-9\\s]") || keyword == null){
            return Collections.emptySet();
        }
        Set<URI> deletedDocsUri = trieDoc.deleteAll(keyword);
        Set<Document> deletedDocs = new HashSet<>();
        for (URI i : deletedDocsUri){
            deletedDocs.add(docBTree.get(i));
        }
        if (deletedDocs.isEmpty() == true){
            return Collections.emptySet();
        }
        CommandSet<URI> newDeleteAll = new CommandSet<>();
        //Set<URI> deletedURIs = new HashSet<>();
        for (Document i : deletedDocs){
            //deletedURIs.add(i.getKey());
            deleteDocFromTrie((DocumentImpl) i);
            //docBTree.put(i.getKey(), null);
            if (!uriOnDisk.contains(i.getKey())){
                deleteFromHeap((DocumentImpl) i);
                currDocs = currDocs - 1;
                currBytes = currBytes - i.getDocumentTxt().getBytes().length;
            }
            else{
                uriOnDisk.remove(i.getKey());
            }
            docBTree.put(i.getKey(), null);
            GenericCommand<URI> deleteInDeleteAll = new GenericCommand<>(i.getKey(), (URI)->{
                docBTree.put(i.getKey(), i);
                i.setLastUseTime(System.nanoTime());
                if (i.getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                    currDocs = currDocs + 1;
                    currBytes = currBytes + i.getDocumentTxt().getBytes().length;
                    //i.setLastUseTime(System.nanoTime());
                    putInHeap((DocumentImpl) i);
                }
                else{
                    try {
                        docBTree.moveToDisk(i.getKey());
                        uriOnDisk.add(i.getKey());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                puttingInTrie((DocumentImpl) i);
                return true;});
            newDeleteAll.addCommand(deleteInDeleteAll);
        }
        commandStack.push(newDeleteAll);
        return deletedDocsUri;
    }

    /**
     * Completely remove any trace of any document which
     * contains a word that has the given prefix
     * Search is CASE SENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
        if (keywordPrefix.contains("[^a-zA-Z0-9\\s]") || keywordPrefix == null){
            return Collections.emptySet();
        }
        Set<URI> deletedDocsUri = trieDoc.deleteAllWithPrefix(keywordPrefix);
        Set<Document> deletedDocs = new HashSet<>();
        for (URI i : deletedDocsUri){
            deletedDocs.add(docBTree.get(i));
        }
        CommandSet<URI> newDeleteAll = new CommandSet<>();
        //Set<URI> deletedURIs = new HashSet<>();
        for (Document i : deletedDocs){
            //deletedURIs.add(i.getKey());
            deleteDocFromTrie((DocumentImpl) i);
            //docBTree.put(i.getKey(), null);
            if (!uriOnDisk.contains(i.getKey())){//if it has been moved to disk dont bother deleteing from heap
                deleteFromHeap((DocumentImpl) i);
                currDocs = currDocs - 1;
                currBytes = currBytes - i.getDocumentTxt().getBytes().length;
            }
            else{
                uriOnDisk.remove(i.getKey());
            }
            docBTree.put(i.getKey(), null);
            GenericCommand<URI> deleteInDeleteAll = new GenericCommand<>(i.getKey(), (URI) -> {
                docBTree.put(i.getKey(), i);
                i.setLastUseTime(System.nanoTime());
                if (i.getDocumentTxt().getBytes().length <= maxDocumentBytes || maxDocumentBytes == -1){
                    currDocs = currDocs + 1;
                    currBytes = currBytes + i.getDocumentTxt().getBytes().length;
                    //i.setLastUseTime(System.nanoTime());
                    putInHeap((DocumentImpl) i);
                }
                else{
                    try {
                        docBTree.moveToDisk(i.getKey());
                        uriOnDisk.add(i.getKey());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                puttingInTrie((DocumentImpl) i);
                return true;});
            newDeleteAll.addCommand(deleteInDeleteAll);
        }
        commandStack.push(newDeleteAll);
        return deletedDocsUri;
    }

    int maxDocumentCount = -1;
    public void setMaxDocumentCount(int limit){
        if (limit < 0){
            throw new IllegalArgumentException();
        }
        this.maxDocumentCount = limit;
        if (currDocs > limit){
            int numberNeedDeleted = currDocs - limit;
            manageDocMemory(numberNeedDeleted);
        }
    }
    int currDocs;
    private int getDocumentCount(){
        return this.currDocs;
    }
    int maxDocumentBytes = -1;
    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit){
        if (limit < 0){
            throw new IllegalArgumentException();
        }
        this.maxDocumentBytes = limit;
        if (currBytes > limit){
            int numberNeedDeleted = currBytes - limit;
            manageByteMemory(numberNeedDeleted);
        }
    }
    int currBytes;
    private int getDocumentsBytes(){
        return this.currBytes;
    }

    Set<URI> uriOnDisk = new HashSet<>();

    private void manageByteMemory(int numberNeedDeleted){
        if (maxDocumentBytes < 0){
            return;
        }
        if (numberNeedDeleted <= 0){
            return;
        }
        Document removedDoc = docBTree.get(docHeap.remove().getUri());
        int bytesRemoved;
        if (removedDoc.getDocumentBinaryData() != null) {
            bytesRemoved = removedDoc.getDocumentBinaryData().length;
        }
        else{//it is a text doc
            bytesRemoved = removedDoc.getDocumentTxt().getBytes().length;
        }
        //currBytes = currBytes - bytesRemoved;
        ArrayList<Document> removedDocs = new ArrayList<>();
        removedDocs.add(removedDoc);
        while (bytesRemoved < numberNeedDeleted){//need to delete more
            Document nextDocRemoved = docBTree.get(docHeap.remove().getUri());
            removedDocs.add(nextDocRemoved);
            int bytesRemoved2;
            if (nextDocRemoved.getDocumentBinaryData() != null) {
                bytesRemoved2 = nextDocRemoved.getDocumentBinaryData().length;
            }
            else{//it is a text doc
                bytesRemoved2 = nextDocRemoved.getDocumentTxt().getBytes().length;
            }
            bytesRemoved = bytesRemoved + bytesRemoved2;
        }
        currBytes = currBytes - bytesRemoved;
        currDocs = currDocs - removedDocs.size();
        for (Document i : removedDocs){
            //deleteDocFromTrie((DocumentImpl) i);
            //deleteFromHeap((DocumentImpl) i);
            //removeFromStack(i);
            try {
                docBTree.moveToDisk(i.getKey());
                uriOnDisk.add(i.getKey());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void manageDocMemory(int numberNeedDeleted){
        if (maxDocumentCount <= 0){
            return;
        }
        if (numberNeedDeleted <= 0){
            return;
        }
//        Document removedDoc = docHeap.remove();
//        currDocs = currDocs - 1;
        ArrayList<Document> removedDocs = new ArrayList<>();
//        removedDocs.add(removedDoc);
        int count = 0;
        while (count < numberNeedDeleted){//need to delete more
            removedDocs.add(docBTree.get(docHeap.remove().getUri()));
//            Document nextDocRemoved = docHeap.remove();
//            removedDocs.add(nextDocRemoved);
//            currDocs = currDocs - 1;
            count++;
        }
        currDocs = currDocs - count;
        for (Document i : removedDocs){
            //deleteDocFromTrie((DocumentImpl) i);
            if (i.getDocumentTxt() != null){//it is a text doc
                currBytes = currBytes - i.getDocumentTxt().getBytes().length;
            }
            else {
                currBytes = currBytes - i.getDocumentBinaryData().length;
            }
            //deleteFromHeap((DocumentImpl) i); dont need to delete from heap cuz already did
            //removeFromStack(i);
            try {
                docBTree.moveToDisk(i.getKey());
                uriOnDisk.add(i.getKey());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void removeFromStack(Document doc){
        StackImpl<Undoable> tempStack = new StackImpl<>();
        int originalStackSize = commandStack.size();
        URI uri = doc.getKey();
        for (int i=0; i< originalStackSize; i++){
            if(commandStack.peek() instanceof GenericCommand<?>) {
                GenericCommand uriTargetTemp = new GenericCommand(uri, (a) -> {
                    return true;
                });
                if (commandStack.peek().equals(uriTargetTemp)) {
                    commandStack.pop();//removing command from stack
                    //    System.out.println("Found and undid");

                    break;
                } else {
                    tempStack.push(commandStack.pop());
                }
            }
            if(commandStack.peek() instanceof CommandSet){
                CommandSet<URI> cmdSetReference = (CommandSet<URI>) commandStack.peek();
                if(cmdSetReference.containsTarget(uri)){
                    GenericCommand uriTargetTemp = new GenericCommand(uri, (a) -> {
                        return true;
                    });
                    Iterator cmdSetIterator = cmdSetReference.iterator();
                    while (cmdSetIterator.hasNext()) {
                        GenericCommand cmdInSet = (GenericCommand) cmdSetIterator.next();
                        if (cmdInSet.equals(uriTargetTemp)){
                            cmdSetIterator.remove();
                        }
                    }
                    if (cmdSetReference.size() == 0){
                        commandStack.pop();
                    }
                    break;
                }
                else {
                    tempStack.push(commandStack.pop());
                }
            }
        }
        int startingTempSize = tempStack.size();
        for (int i=0; i< startingTempSize; i++){
            commandStack.push(tempStack.pop());
        }
    }


}