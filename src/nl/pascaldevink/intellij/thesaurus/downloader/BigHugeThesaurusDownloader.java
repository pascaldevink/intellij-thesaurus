package nl.pascaldevink.intellij.thesaurus.downloader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BigHugeThesaurusDownloader extends AbstractDownloader
{
    private static final String endpoint = "http://words.bighugelabs.com/api/2";

    public BigHugeThesaurusDownloader(String apiKey) {
        super(apiKey);
    }

    @Override
    protected String buildUrl(String originalWord) throws UnsupportedEncodingException
    {
        return endpoint + "/" + apiKey + "/" + URLEncoder.encode(originalWord, "UTF-8") + "/json";
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
}
