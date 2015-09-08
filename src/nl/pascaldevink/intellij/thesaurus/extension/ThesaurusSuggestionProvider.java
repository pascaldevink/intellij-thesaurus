package nl.pascaldevink.intellij.thesaurus.extension;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.refactoring.rename.NameSuggestionProvider;
import com.intellij.util.containers.ContainerUtil;
import nl.pascaldevink.intellij.thesaurus.config.Configuration;
import nl.pascaldevink.intellij.thesaurus.downloader.MashapeDownloader;
import nl.pascaldevink.intellij.thesaurus.downloader.ThesaurusDownloader;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.util.Set;

public class ThesaurusSuggestionProvider implements NameSuggestionProvider
{
    @Nullable
    public SuggestedNameInfo getSuggestedNames(PsiElement element, @Nullable PsiElement nameSuggestionContext, Set<String> results)
    {
        if (null == nameSuggestionContext) {
            return null;
        }

        String text = nameSuggestionContext.getText();
        if(nameSuggestionContext instanceof PsiNamedElement) {
            text = ((PsiNamedElement)element).getName();
        }

        if(text == null) {
            return null;
        }

        try {
            ThesaurusDownloader downloader = new MashapeDownloader(Configuration.MASHAPE_API_KEY);
            ContainerUtil.addAllNotNull(results, downloader.downloadThesaurusList(text));

            return SuggestedNameInfo.NULL_INFO;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
