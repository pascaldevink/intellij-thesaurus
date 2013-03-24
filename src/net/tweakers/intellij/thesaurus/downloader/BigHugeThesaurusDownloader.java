package net.tweakers.intellij.thesaurus.downloader;

import org.apache.commons.httpclient.HttpException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BigHugeThesaurusDownloader implements ThesaurusDownloader
{
    final String endpoint = "http://words.bighugelabs.com/api/2";
    private String key = "";

    public BigHugeThesaurusDownloader(String apiKey)
    {
        this.key = apiKey;
    }

    @Override
    public List<String> downloadThesaurusList(String originalWord) throws IOException
    {
        String serverResponse = this.download(originalWord);
        List<String> synonyms = this.parseRawJsonToSynonymsList(serverResponse);

        return synonyms;
    }

    protected String download(String originalWord) throws IOException
    {
        URL serverAddress = new URL(endpoint + "/" + key + "/" + URLEncoder.encode(originalWord, "UTF-8") + "/json");

        HttpURLConnection connection = (HttpURLConnection)serverAddress.openConnection();
        connection.connect();

        int rc = connection.getResponseCode();
        if (rc != 200) {
            throw new HttpException("No result found");
        }

        String line = null;
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null)
            sb.append(line + '\n');

        connection.disconnect();

        return sb.toString();
    }

    protected List<String> parseRawJsonToSynonymsList(String rawJson)
    {
        List<String> synonyms = new ArrayList<String>();

        JSONObject fullJSON = (JSONObject) JSONValue.parse(rawJson);

        // Get nouns
        synonyms.addAll(getSynonymsFromJSON(fullJSON, "noun"));

        // Get verbs
        synonyms.addAll(getSynonymsFromJSON(fullJSON, "verb"));

        return synonyms;
    }

    protected List<String> getSynonymsFromJSON(JSONObject fullJSON, String type)
    {
        List<String> synonyms = new ArrayList<String>();

        JSONObject wordType = (JSONObject)fullJSON.get(type);
        if (wordType == null)
            return synonyms;

        JSONArray syns = (JSONArray)wordType.get("syn");
        for(Object synonym: syns)
        {
            synonyms.add(camelCaseSynonym((String) synonym));
        }

        return synonyms;
    }

    protected String camelCaseSynonym(String originalSynonym)
    {
        if (!originalSynonym.contains(" "))
            return originalSynonym;

        String[] words = originalSynonym.split(" ");
        StringBuilder camelCaseBuilder = new StringBuilder();

        camelCaseBuilder.append(words[0].trim());
        for (int i = 1; i < words.length; i++)
        {
            String word = words[i].trim();
            char[] chars = word.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            camelCaseBuilder.append(new String(chars));
        }

        return camelCaseBuilder.toString();
    }
}
