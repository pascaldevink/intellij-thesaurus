package nl.pascaldevink.intellij.thesaurus.downloader;

import org.apache.commons.httpclient.HttpException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public abstract class AbstractDownloader implements ThesaurusDownloader
{
    protected String apiKey = "";

    public AbstractDownloader(String apiKey)
    {
        this.apiKey = apiKey;
    }

    @Override
    public List<String> downloadThesaurusList(String originalWord) throws IOException {
        String serverResponse = this.download(originalWord);
        List<String> synonyms = this.parseRawJsonToSynonymsList(serverResponse);

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

    protected String download(String originalWord) throws IOException
    {
        URL serverAddress = new URL(buildUrl(originalWord));

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

    protected abstract String buildUrl(String originalWord) throws UnsupportedEncodingException;
    protected abstract List<String> parseRawJsonToSynonymsList(String rawJson);
}
