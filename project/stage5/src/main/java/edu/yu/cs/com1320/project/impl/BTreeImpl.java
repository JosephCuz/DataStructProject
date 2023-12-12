package edu.yu.cs.com1320.project.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;

public class BTreeImpl<Key extends Comparable<Key>, Value> implements BTree<Key, Value>{

    DocumentPersistenceManager dpm;
    private static final int MAX = 4;
    private BTreeImpl.Node root; //root of the B-tree
    private BTreeImpl.Node leftMostExternalNode;
    private int height; //height of the B-tree
    private int n; //number of key-value pairs in the B-tree

    private static final class Node
    {
        private int entryCount; // number of entries
        private BTreeImpl.Entry[] entries = new BTreeImpl.Entry[BTreeImpl.MAX]; // the array of children
        private BTreeImpl.Node next;
        private BTreeImpl.Node previous;

        // create a node with k entries
        private Node(int k)
        {
            this.entryCount = k;
        }

        private void setNext(BTreeImpl.Node next)
        {
            this.next = next;
        }
        private BTreeImpl.Node getNext()
        {
            return this.next;
        }
        private void setPrevious(BTreeImpl.Node previous)
        {
            this.previous = previous;
        }
        private BTreeImpl.Node getPrevious()
        {
            return this.previous;
        }

        private BTreeImpl.Entry[] getEntries()
        {
            return Arrays.copyOf(this.entries, this.entryCount);
        }

    }

    //internal nodes: only use key and child
    //external nodes: only use key and value
    public static class Entry
    {
        private Comparable key;
        private Object val;
        //private BTreeImpl.Node child;

        public Entry(Comparable key, Object val)
        {
            this.key = key;
            this.val = val;
            //this.child = child;
        }
        public Object getValue()
        {
            return this.val;
        }
        public Comparable getKey()
        {
            return this.key;
        }
    }

    public BTreeImpl()
    {
        this.root = new BTreeImpl.Node(0);
        this.leftMostExternalNode = this.root;
    }
    /**
     * Returns true if this symbol table is empty.
     *
     * @return {@code true} if this symbol table is empty; {@code false}
     *         otherwise
     */
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    /**
     * @return the number of key-value pairs in this symbol table
     */
    public int size()
    {
        return this.n;
    }

    /**
     * @return the height of this B-tree
     */
    public int height()
    {
        return this.height;
    }

    public Value get(Key k){
        if (k == null)
        {
            throw new IllegalArgumentException("argument to get() is null");
        }
        Entry entry = this.get(this.root, k, this.height);
        if(entry != null)
        {
            if (entry.val instanceof Function<?,?>){
                ((Function<?, ?>) entry.val).apply(null);
                //((Document) this.get(k)).setLastUseTime(System.nanoTime());
                return this.get(k);
            }
            return (Value)entry.val;
        }
        return null;
    }
    private Entry get(Node currentNode, Key key, int height)
    {
        Entry[] entries = currentNode.entries;

        //current node is external (i.e. height == 0)
        if (height == 0)
        {
            for (int j = 0; j < currentNode.entryCount; j++)
            {
                if(isEqual(key, entries[j].key))
                {
                    //found desired key. Return its value
                    return entries[j];
                }
            }
            //didn't find the key
            return null;
        }

        //current node is internal (height > 0)
        else
        {
            for (int j = 0; j < currentNode.entryCount; j++)
            {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be in the subtree below the current entry),
                //then recurse into the current entry’s child
                if (j + 1 == currentNode.entryCount || less(key, entries[j + 1].key))
                {
                    //cast to node as if its an internal node, its value is reference to another node
                    return this.get((Node) entries[j].val, key, height - 1);
                }
            }
            //didn't find the key
            return null;
        }
    }
    private static boolean less(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) < 0;
    }

    private static boolean isEqual(Comparable k1, Comparable k2)
    {
        return k1.compareTo(k2) == 0;
    }


    public Value put(Key k, Value v){
        if (k == null)
        {
            throw new IllegalArgumentException("argument key to put() is null");
        }
        //if the key already exists in the b-tree, simply replace the value
        Entry alreadyThere = this.get(this.root, k, this.height);
        if(alreadyThere != null)
        {
            Value temp = (Value) alreadyThere.val;
            //if a delete, delete doc from memory
            if (temp instanceof Function<?,?>) {
                ((Function<?, ?>) temp).apply(null);
            }
            alreadyThere.val = v;
            return temp;
        }

        Node newNode = this.put(this.root, k, v, this.height);
        this.n++;
        if (newNode == null)
        {   //it got put
            return v;
        }

        //split the root:
        //Create a new node to be the root.
        //Set the old root to be new root's first entry.
        //Set the node returned from the call to put to be new root's second entry
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, newNode);
        this.root = newRoot;
        //a split at the root always increases the tree height by 1
        this.height++;
        //node was put and split handles i think so return v
        return v;
    }
    private Node put(Node currentNode, Key key, Value val, int height)
    {
        int j;
        Entry newEntry = new Entry(key, val);

        //external node
        if (height == 0)
        {
            //find index in currentNode’s entry[] to insert new entry
            //we look for key < entry.key since we want to leave j
            //pointing to the slot to insert the new entry, hence we want to find
            //the first entry in the current node that key is LESS THAN
            for (j = 0; j < currentNode.entryCount; j++)
            {
                if (less(key, currentNode.entries[j].key))
                {
                    break;
                }
            }
        }

        // internal node
        else
        {
            //find index in node entry array to insert the new entry
            for (j = 0; j < currentNode.entryCount; j++)
            {
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be added to the subtree below the current entry),
                //then do a recursive call to put on the current entry’s child
                if ((j + 1 == currentNode.entryCount) || less(key, currentNode.entries[j + 1].key))
                {
                    //increment j (j++) after the call so that a new entry created by a split
                    //will be inserted in the next slot
                    //cast because if internal node, val will be node
                    Node newNode = this.put((Node) currentNode.entries[j++].val, key, val, height - 1);
                    if (newNode == null)
                    {
                        return null;
                    }
                    //if the call to put returned a node, it means I need
                    // to add a new entry to
                    //the current node
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = newNode;
                    //newEntry.child = newNode;
                    break;
                }
            }
        }
        //shift entries over one place to make room for new entry
        for (int i = currentNode.entryCount; i > j; i--)
        {
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        //add new entry
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < BTreeImpl.MAX)
        {
            //no structural changes needed in the tree
            //so just return null
            return null;
        }
        else
        {
            //will have to create new entry in the parent due
            //to the split, so return the new node, which is
            //the node for which the new entry will be created
            return this.split(currentNode, height);
        }
    }
    private Node split(Node currentNode, int height)
    {
        Node newNode = new Node(BTreeImpl.MAX / 2);
        //by changing currentNode.entryCount, we will treat any value
        //at index higher than the new currentNode.entryCount as if
        //it doesn't exist
        currentNode.entryCount = BTreeImpl.MAX / 2;
        //copy top half of h into t
        for (int j = 0; j < BTreeImpl.MAX / 2; j++)
        {
            newNode.entries[j] = currentNode.entries[BTreeImpl.MAX / 2 + j];
        }
        //external node
        if (height == 0)
        {
            newNode.setNext(currentNode.getNext());
            newNode.setPrevious(currentNode);
            currentNode.setNext(newNode);
        }
        return newNode;
    }
    public void moveToDisk(Key k) throws Exception{
        Value doc = get(k);
        this.dpm.serialize((URI) k, (Document) doc);

        Function<String, Boolean> docFactory = (String s) -> {
            Document backAg = null;
            try {//not sure why need to try catch I think cuz
                // deserial MAY throw IOException as described in signature
                backAg = this.dpm.deserialize((URI) k);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //put(k, (Value) backAg);
            Entry theEntry = this.get(this.root, k, this.height);
            theEntry.val = backAg;
            this.dpm.delete((URI) k);
            return true;
        };
        //Document backAgain = () -> (Document) this.dpm.deserialize((URI) k);
        //idk if cast will work, assuming yes well need to add in .apply
        put(k, (Value) docFactory);
    }
    public void setPersistenceManager(PersistenceManager<Key,Value> pm){
        this.dpm = (DocumentPersistenceManager) pm;
    }

    /**
    public static void main(String[] args) throws Exception {
        BTreeImpl<URI, Document> test = new BTreeImpl<>();

        URI uri1 = new URI("http://www.poop.com/i/kinda/hate/this1");
        String txt1 = "a b c d d d";
        byte[] bytes1 = txt1.getBytes();
        DocumentImpl doc1 = new DocumentImpl(uri1, bytes1);

        URI uri2 = new URI("http://www.poop.com/i/kinda/hate/this2");
        String txt2 = "a b c yaba";
        byte[] bytes2 = txt2.getBytes();
        DocumentImpl doc2 = new DocumentImpl(uri2, bytes2);

        test.put(uri1, doc1);
        test.put(uri2, doc2);

        System.out.println(Arrays.toString(test.get(uri1).getDocumentBinaryData()));
        System.out.println(Arrays.toString(test.get(uri2).getDocumentBinaryData()));

        test.moveToDisk(uri1);
        test.get(uri1);
        System.out.println(Arrays.toString(test.get(uri1).getDocumentBinaryData()));

        test.put(uri1, null);
        System.out.println(test.get(uri1));


    }
    */
}
