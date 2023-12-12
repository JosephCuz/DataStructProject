package edu.yu.cs.com1320.project.impl;


import edu.yu.cs.com1320.project.Trie;
//for testing
import java.util.Comparator;
import java.util.*;

public class TrieImpl<Value> implements Trie<Value>{


    private static final int alphabetSize = 128; // standard ASCII
    private Node root; // root of trie

    private static class Node<Value>{
        protected List<Value> values = new ArrayList<>();
        protected Node[] links = new Node[TrieImpl.alphabetSize];
    }

    public TrieImpl() {
        this.root = new Node<>();
    }


    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val){

        //deleteAll the value from this key
        if (val == null)
        {
            this.deleteAll(key);
        }
        else
        {
            this.root = put(this.root, key, val, 0);
        }
    }
    /**
     *
     * @param x
     * @param key
     * @param val
     * @param d
     * @return
     */
    private Node put(Node x, String key, Value val, int d)
    {
        //create a new node
        if (x == null)
        {
            x = new Node();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if (d == key.length())
        {
            x.values.add(val);
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        x.links[c] = this.put(x.links[c], key, val, d + 1);
        return x;
    }

    //basic get that return arrayList of values at a node
    private List<Value> get(Node x, String key, int d)
    {
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x.values;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.get(x.links[c], key, d + 1);
    }
    private Node getNode(Node x, String key, int d)
    {
        //link was null - return null, indicating a miss
        if (x == null)
        {
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if (d == key.length())
        {
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        return this.getNode(x.links[c], key, d + 1);
    }


    /**
     * get all exact matches for the given key, sorted in
     * descending order.
     * Search is CASE SENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */

    //comparable will need to be able to figure out which docs/values
    //hav the most mentions
    public List<Value> getAllSorted(String key, Comparator<Value> comparator){
        if (key == null || comparator == null){
            throw new IllegalArgumentException();
        }
        if (key.matches(".*[^a-zA-Z0-9\\s].*")){
            return Collections.emptyList();
        }
        List<Value> preSortVals = this.get(this.root, key, 0);
        if (preSortVals == null){
            return Collections.emptyList();
        }
        else{
            Collections.sort(preSortVals, comparator);
            return preSortVals;
        }
    }

    //recursive method that adds all children values to given array
    //adds until it reaches a child with only null links
    private List<Value> bigListGet(Node current, List<Value> ourList){
        for(Node i : current.links){
            if(i != null) {
                for(int j=0; j< i.values.size(); j++){
                    if (!ourList.contains(i.values.get(j))){
                        ourList.add((Value) i.values.get(j));
                    }
                }
                //    ourList.addAll(i.values);
                bigListGet(i, ourList);
            }
        }
        return ourList;
    }


    /**
     * get all matches which contain a String with the given prefix,
     * sorted in descending order.
     * For example, if the key is "Too", you would return any
     * value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given
     * prefix, in descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        //    Node currNode = this.getNode(this.root, prefix, 0);
        if (prefix == null || comparator == null){
            throw new IllegalArgumentException();
        }
        if (prefix.matches(".*[^a-zA-Z0-9\\s].*")){
            return Collections.emptyList();
        }
        List<Value> preSortPrefixVals = new ArrayList<>();//gets values at the prefix
        if (this.get(this.root, prefix, 0) == null){
            return Collections.emptyList();
        }
        preSortPrefixVals.addAll(this.get(this.root, prefix, 0));
        List<Value> toBeSortedPrefixVals = bigListGet(this.getNode(this.root, prefix, 0), preSortPrefixVals);
        Collections.sort(toBeSortedPrefixVals, comparator);
        return toBeSortedPrefixVals;
    }

    private List<Value> recursiveDelete(Node current, List<Value> ourList){
        for (Node i : current.links) {
            if (i == null) continue; // skip null links
            ourList.addAll(i.values);
            i.values.clear();
            recursiveDelete(i, ourList);
        }
        return ourList;
    }

/**
 private List<Value> recursiveDelete(Node current, List<Value> ourList){
 for(Node i : current.links){
 if(i != null) {
 ourList.addAll(i.values);
 i.values.clear();
 recursiveDelete(i, ourList);
 }
 }
 return ourList;
 }
 */

    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE SENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */


    public Set<Value> deleteAllWithPrefix(String prefix){
        if (prefix == null){
            throw new IllegalArgumentException();
        }
        if (prefix.matches(".*[^a-zA-Z0-9\\s].*")){
            return Collections.emptySet();
        }
        List<Value> listOfDels = new ArrayList<>();
        if (this.get(this.root, prefix, 0) == null){
            return new HashSet<Value>();
        }
        listOfDels.addAll(this.get(this.root, prefix, 0));
        this.get(this.root, prefix, 0).clear();
        //    Node currNode = this.getNode(this.root, prefix, 0);
        List<Value> allDels = recursiveDelete(this.getNode(this.root, prefix, 0), listOfDels);
        if (this.get(this.root, prefix, 0) == null){
            return Collections.emptySet();
        }
        Set<Value> setDels = new HashSet<>(allDels);
        this.getNode(this.root, prefix, 0).links = new Node[TrieImpl.alphabetSize];
        return setDels;
    }


    /**
     * Delete all values from the node of the given key
     * (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key){
        if (key == null){
            throw new IllegalArgumentException();
        }
        if (key.matches(".*[^a-zA-Z0-9\\s].*")){
            throw new IllegalArgumentException();
        }
        Node theNode = getNode(this.root, key, 0);
        if (theNode == null){
            return new HashSet<Value>();
        }

        Set<Value> setDels = new HashSet<>(theNode.values);
        theNode.values.clear();
        deleteEmptyNodes(key);
        return setDels;
    }

    /**
     * Remove the given value from the node of the given key
     * (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not
     * contain the given value, return null.
     */
    public Value delete(String key, Value val){
        if (key == null){
            throw new IllegalArgumentException();
        }
        if (key.matches(".*[^a-zA-Z0-9\\s].*")){
            throw new IllegalArgumentException();
        }
        Node theNode = getNode(this.root, key, 0);
        if (theNode == null){ //case where node doesnt exist as had no values so was already deleted
            return null;
        }
        if (theNode.values.contains(val)) {
            for (int i = 0; i < theNode.values.size(); i++) {
                if (theNode.values.get(i) != null) {
                    if (theNode.values.get(i).equals(val)) {
                        Value delVal = (Value) theNode.values.get(i);
                        theNode.values.remove(i);
                        deleteEmptyNodes(key);
                        return delVal;
                    }
                }
            }
            return null;
        }
        else {
            return null;
        }
    }

    private void deleteEmptyNodes(String key){
        //deletes nodes with no values or kids up from this key
        Node theNode = getNode(this.root, key, 0);
        while (theNode.values.isEmpty() && hasNoKids(theNode) == true && key.length() != 0){
            char c = key.charAt(key.length()-1);
            theNode = getNode(this.root, removeLastCharacter(key), 0);
            key = removeLastCharacter(key);
            theNode.links[c] = null;
        }
    }

    private boolean hasNoKids(Node node){
        boolean hasNoChild = true;
        for (int i=0; i < node.links.length; i++){
            if(node.links[i] != null){
                hasNoChild = false;
                break;
            }
        }
        return hasNoChild;
    }

    private String removeLastCharacter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        int length = input.length();
        String shortenedString = input.substring(0, length - 1);
        return shortenedString;
    }

    /**
     public static void main(String[] args) {
     TrieImpl<String> trie = new TrieImpl<>();

     // Test put() method
     //    trie.put("a", "aVal");
     //    trie.put("hell", "hellVal");
     trie.put("ha", "haVal");
     trie.put("hb", "hbVal");
     trie.put("ha", "haVal2");
     trie.put("hb", "hbVal2");
     trie.put("hc", "hcVal");
     //    trie.put("help", "helpVal");
     //    trie.put("helloThere", "helloThereVal");
     //    trie.put("hellothere", "hellothereVal");

     //    System.out.println(trie.getAllSorted("hi", Comparator.naturalOrder()));
     //    System.out.println(trie.getAllSorted("hell", Comparator.naturalOrder()));
     //    System.out.println(trie.getAllSorted("hello", Comparator.naturalOrder()));
     //    System.out.println(trie.getAllSorted("helloThere", Comparator.naturalOrder()));
     //    System.out.println(trie.getAllSorted("hellothere", Comparator.naturalOrder()));

     System.out.println(trie.getAllWithPrefixSorted("h", Comparator.naturalOrder()));
     System.out.println(trie.getAllWithPrefixSorted("h", Comparator.naturalOrder()));


     System.out.println(trie.getAllSorted("ha", Comparator.naturalOrder()));
     trie.deleteAll("ha");
     System.out.println("post delete all on ha: " + trie.getAllSorted("ha", Comparator.naturalOrder()));
     System.out.println(trie.getAllWithPrefixSorted("h", Comparator.naturalOrder()));


     System.out.println(trie.getAllSorted("hb", Comparator.naturalOrder()));
     trie.delete("hb", "hbVal");
     System.out.println(trie.getAllSorted("hb", Comparator.naturalOrder()));
     System.out.println(trie.getAllWithPrefixSorted("h", Comparator.naturalOrder()));



     System.out.println(trie.deleteAllWithPrefix("h"));
     System.out.println(trie.getAllWithPrefixSorted("h", Comparator.naturalOrder()));



     }
     */


}

