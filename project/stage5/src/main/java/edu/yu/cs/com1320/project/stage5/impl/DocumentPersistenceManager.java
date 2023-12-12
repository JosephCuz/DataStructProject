package edu.yu.cs.com1320.project.stage5.impl;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Base64;

import com.google.gson.reflect.TypeToken;
import jakarta.xml.bind.DatatypeConverter;
import com.google.gson.*;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {

    File nonBaseDir;
    public DocumentPersistenceManager(File nonBaseDir){
        this.nonBaseDir = nonBaseDir;
    }

    private class DocSerializer implements JsonSerializer<Document> {
        @Override
        public JsonObject serialize(Document doc, Type type, JsonSerializationContext context){
            if (doc.getDocumentTxt() != null) {
                JsonObject jsonDoc = new JsonObject();
                //add content
                jsonDoc.addProperty("text", doc.getDocumentTxt());
                //add uri
                jsonDoc.addProperty("URI", doc.getKey().toString());
                //add word map
                Gson gson = new Gson();
                String jsonMap = gson.toJson(doc.getWordMap());
                jsonDoc.addProperty("wordMap", jsonMap);
                return jsonDoc;
                //for (String word : doc.getWordMap().keySet()){
                //    jsonDoc.addProperty(word, doc.getWordMap().get(word));
                //}
            }
            else{
                JsonObject jsonDoc = new JsonObject();
                //Gson gson = new Gson();
                byte[] docBytes = doc.getDocumentBinaryData();
                String jsonBytes = DatatypeConverter.printBase64Binary(docBytes);
                jsonDoc.addProperty("bytes", jsonBytes);
                jsonDoc.addProperty("URI", doc.getKey().toString());
                return jsonDoc;
            }
        }
    }
    private class DocDeserializer implements JsonDeserializer<Document>{
        @Override
        public Document deserialize(JsonElement jsonElement, Type type1, JsonDeserializationContext context){
            JsonObject jsonDoc = (JsonObject) jsonElement;
            URI docUri = URI.create(jsonDoc.get("URI").getAsString());
            if (jsonDoc.get("text") != null){//its a text doc
                String docText = jsonDoc.get("text").toString();
                Gson gson = new Gson();
                //TypeToken<Map<String, Integer>> type = new TypeToken<Map<String, Integer>>(){};
                Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();

                JsonElement jsonDocwmElement = jsonDoc.get("wordMap");
                JsonParser parser = new JsonParser();
                JsonObject jsonDocwm = parser.parse(jsonDocwmElement.getAsString()).getAsJsonObject();

                //JsonObject jsonDocwm = jsonDoc.get("wordMap").getAsJsonObject();
                Map<String, Integer> docWordMap = gson.fromJson(jsonDocwm, type);
                Document unSerializedDoc = new DocumentImpl(docUri, docText, docWordMap);
                return unSerializedDoc;
            }
            else{//it is a byte doc
                byte[] docBytes = DatatypeConverter.parseBase64Binary(jsonDoc.get("bytes").getAsString());
                Document unSerializedDoc = new DocumentImpl(docUri, docBytes);
                return unSerializedDoc;
            }
        }
    }

    @Override
    public void serialize(URI uri, Document val) throws IOException{
        DocSerializer ds = new DocSerializer();
        JsonObject serializedDoc = ds.serialize(val, null, null);

        //might still need to to add another get for things after the path
        String dirUriString = uri.getAuthority() + uri.getPath();

        dirUriString = dirUriString.substring(0, dirUriString.lastIndexOf(File.separator));

        String fullUriString = uri.getAuthority() + uri.getPath() +".json";

        if (this.nonBaseDir == null){//using baseDir
            //File fileDir = new File(System.getProperty("user.dir"), dirUriString);
            String fullFileDir = System.getProperty("user.dir") + File.separator + dirUriString;
            File fileDir = new File(fullFileDir);
            fileDir.mkdirs();
            //File file = new File(System.getProperty("user.dir"), fullUriString);
            String fullFile = System.getProperty("user.dir") + File.separator + fullUriString;
            File file = new File(fullFile);
            file.createNewFile();
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                fileWriter.write(serializedDoc.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            //File fileDir = new File(this.nonBaseDir, dirUriString);
            String fullFileDir = this.nonBaseDir + File.separator + dirUriString;
            File fileDir = new File(fullFileDir);
            fileDir.mkdirs();
            //File file = new File(this.nonBaseDir, fullUriString);
            String fullFile = this.nonBaseDir + File.separator + fullUriString;
            File file = new File(fullFile);
            file.createNewFile();
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                fileWriter.write(serializedDoc.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Document deserialize(URI uri) throws IOException{
        String uriString = uri.getAuthority() + uri.getPath() +".json";
        File file;
        if (this.nonBaseDir == null){//use base dir
            String fullFile = System.getProperty("user.dir") + File.separator + uriString;
            file = new File(fullFile);
        }
        else{
            String fullFile = this.nonBaseDir + File.separator + uriString;
            file = new File(fullFile);
        }
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        JsonParser parser = new JsonParser();
        JsonElement jsonDoc = parser.parse(reader);
        DocDeserializer dds = new DocDeserializer();
        Document deserializedDoc = dds.deserialize(jsonDoc, null, null);
        reader.close();
        return deserializedDoc;
    }

    @Override
    public boolean delete(URI uri){
        if (uri == null){
            return false;
        }
        String uriString = uri.getAuthority() + uri.getPath() +".json";
        File file;
        if (this.nonBaseDir == null){
            String fullFile = System.getProperty("user.dir") + File.separator + uriString;
            file = new File(fullFile);
        }
            //file = new File(System.getProperty("user.dir"), uriString);
        else{
            String fullFile = this.nonBaseDir + File.separator + uriString;
            file = new File(fullFile);
            //file = new File(this.nonBaseDir, uriString);
        }
        if (file.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
    public static void main(String[] args) throws IOException, URISyntaxException {
        DocumentPersistenceManager dpmTest = new DocumentPersistenceManager(null);
        URI testUri = new URI("http://www.poop.com/i/kinda/hate/this");
        DocumentImpl test = new DocumentImpl(testUri,
                "What you call an icon livin? Start a record label, MSFTS just did it  Nylon cover, five minutes, whoa We up too hot in the business Bout to make a movie independent Need new tracks, Independent I need you to listen to the vision All your verses sound like dirty dishes (Gross)",
                null);
        dpmTest.serialize(testUri, test);
        Document dsDoc = dpmTest.deserialize(testUri);
        System.out.println(dsDoc.getDocumentTxt());
        System.out.println(dsDoc.getKey());

        for(String i : dsDoc.getWords()){
            System.out.println(i);
        }
        System.out.println(dpmTest.delete(testUri));





    }
    */

}
