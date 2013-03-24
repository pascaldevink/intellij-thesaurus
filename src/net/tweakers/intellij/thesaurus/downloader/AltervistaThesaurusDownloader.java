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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AltervistaThesaurusDownloader implements ThesaurusDownloader
{
    final String endpoint = "http://thesaurus.altervista.org/thesaurus/v1";
    private String key = "";

    public AltervistaThesaurusDownloader(String apiKey)
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

    protected String download(String originalWord) throws IOException {
        URL serverAddress = new URL(endpoint + "?word="+ URLEncoder.encode(originalWord, "UTF-8")+"&language=en_US&key="+key+"&output=json");

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
        JSONObject obj = (JSONObject) JSONValue.parse(rawJson);
        JSONArray array = (JSONArray)obj.get("response");

        List<String> synonyms = new ArrayList<String>();

        for (int i=0; i < array.size(); i++) {
            JSONObject list = (JSONObject) ((JSONObject)array.get(i)).get("list");
            String partialSynonyms = (String)list.get("synonyms");

            synonyms.addAll(explodeSynonyms(partialSynonyms));
        }

        return synonyms;
    }

    protected List<String> explodeSynonyms(String partialSynonyms)
    {
        String[] synonyms = partialSynonyms.split("\\|");
        List<String> synonymList = new ArrayList<String>();
        Collections.addAll(synonymList, synonyms);

        for (Iterator<String> iterator = synonymList.iterator(); iterator.hasNext();)
        {
            String synonym = iterator.next();
            if (synonym.contains("("))
            {
                int index = synonymList.indexOf(synonym);
                synonym = synonym.substring(0, synonym.indexOf("(")-1).trim();
                synonymList.set(index, synonym);
            }
        }

        return synonymList;
    }
}
