package nl.pascaldevink.intellij.thesaurus.downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ThesaurusDownloader
{
    List<String> downloadThesaurusList(String originalWord) throws IOException;
}
