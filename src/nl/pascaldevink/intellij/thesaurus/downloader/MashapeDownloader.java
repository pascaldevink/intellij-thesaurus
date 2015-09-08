package nl.pascaldevink.intellij.thesaurus.downloader;

import org.apache.commons.httpclient.HttpException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MashapeDownloader extends AbstractDownloader
{
    private static final String endpoint = "https://wikisynonyms.p.mashape.com/";

    public MashapeDownloader(String apiKey)
    {
        super(apiKey);
    }

    protected String download(String originalWord) throws IOException
    {
        URL serverAddress = new URL(buildUrl(originalWord));

        HttpURLConnection connection = (HttpURLConnection)serverAddress.openConnection();
        connection.setRequestProperty("X-Mashape-Key", apiKey);
        connection.setRequestProperty("Accept", "application/json");

        connection.connect();

        int rc = connection.getResponseCode();

        String line = null;
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();

        while ((line = br.readLine()) != null)
            sb.append(line + '\n');

        connection.disconnect();

        if (rc != 200) {
            throw new HttpException("No result found. Response body: " + sb.toString());
        }

        return sb.toString();
    }

    @Override
    protected String buildUrl(String originalWord) throws UnsupportedEncodingException
    {
        return endpoint + originalWord;
    }

    @Override
    protected List<String> parseRawJsonToSynonymsList(String rawJson)
    {
        List<String> synonyms = new ArrayList<String>();

        JSONObject fullJSON = (JSONObject) JSONValue.parse(rawJson);
        JSONArray arr = (JSONArray) fullJSON.get("terms");

        for (int i = 0; i <arr.toArray().length; i++) {
            JSONObject term = (JSONObject) arr.get(i);
            synonyms.add(term.get("term").toString());
        }

        return synonyms;
    }
}
