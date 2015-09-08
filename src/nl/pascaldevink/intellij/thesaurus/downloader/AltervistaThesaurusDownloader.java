package nl.pascaldevink.intellij.thesaurus.downloader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AltervistaThesaurusDownloader extends AbstractDownloader
{
    private static final String endpoint = "http://thesaurus.altervista.org/thesaurus/v1";

    public AltervistaThesaurusDownloader(String apiKey) {
        super(apiKey);
    }

    @Override
    protected String buildUrl(String originalWord) throws UnsupportedEncodingException
    {
        return endpoint + "?word="+ URLEncoder.encode(originalWord, "UTF-8")+"&language=en_US&key="+apiKey+"&output=json";
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

        for (String synonym : synonymList) {
            if (synonym.contains("(")) {
                int index = synonymList.indexOf(synonym);
                synonym = synonym.substring(0, synonym.indexOf("(") - 1).trim();
                synonymList.set(index, synonym);
            }
        }

        return synonymList;
    }
}
