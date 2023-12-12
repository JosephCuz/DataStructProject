package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.stage5.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentImpl implements Document{

    byte[] binaryData;
    URI uri;
    String text;
    HashMap<String, Integer> wordMap = new HashMap<>();
    long lastUsedTime;

    public DocumentImpl(URI uri, String txt, Map<String, Integer> wordCountMap){
        if (uri == null || txt == null){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = txt;
        //this.binaryData = txt.getBytes();
        if (wordCountMap != null){
            setWordMap(wordCountMap);
        }
        else {
            String txtNoSpecChars = txt.replaceAll("[^a-zA-Z0-9\\s]", "");
            String[] words = txtNoSpecChars.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (wordMap.keySet().contains(words[i])) {
                    Integer plusOne = wordMap.get(words[i]) + 1;
                    wordMap.put(words[i], plusOne);
                } else {
                    wordMap.put(words[i], 1);
                }
            }
        }
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if (uri == null || binaryData == null){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }





    /**
     * @return content of text document
     */
    public String getDocumentTxt(){
        if (this.text != null) {
            if (this.text.startsWith("\"") && this.text.endsWith("\"")) {
                String noQuotes = this.text.substring(1, this.text.length() - 1);
                return noQuotes;
            }
        }
        return this.text;

    }

    /**
     * @return content of binary data document
     */
    public byte[] getDocumentBinaryData(){
        return this.binaryData;
    }

    /**
     * @return URI which uniquely identifies this document
     */
    public URI getKey(){
        return this.uri;
    }


    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0); result = 31 * result + Arrays.hashCode(binaryData);
        if (result < 0){//if negative
            result = -result;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentImpl document = (DocumentImpl) o;
        return this.hashCode() == document.hashCode();
    }

    /**
     //@Override
     public boolean equals(Document doc){
     if (doc.hashCode() == this.hashCode()){
     return true;
     }
     else{
     return false;
     }
     }
     /*

     /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document.
     * If it's a binary document, return 0.
     */
    public int wordCount(String word){
        if (text == null){
            return 0;
        }
        else{
            if(wordMap.get(word) == null){
                return 0;
            }
            else{
                return wordMap.get(word);
            }
        }
    }

    /**
     * @return all the words that appear in the document
     */
    public Set<String> getWords(){
        return wordMap.keySet();
    }

    @Override
    public int compareTo(Document otherDoc){
        //return positive if this.element has greater last use
        // time than other doc
        //could have the return mixed up
        return Long.compare(this.lastUsedTime, otherDoc.getLastUseTime());
    }



    /**
     * return the last time this document was used, via put/get or via a search result
     * (for stage 4 of project)
     */
    public long getLastUseTime(){
        return this.lastUsedTime;
    }


    public void setLastUseTime(long timeInNanoseconds){
        this.lastUsedTime = timeInNanoseconds;
    }

    /**
     * @return a copy of the word to count map so it can be serialized
     */
    public Map<String,Integer> getWordMap(){
        return this.wordMap;
    }

    /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
    public void setWordMap(Map<String,Integer> wordMap){
        this.wordMap = (HashMap<String, Integer>) wordMap;
    }

/**
 public static void main(String[] args) throws IOException {

 }

*/



}

