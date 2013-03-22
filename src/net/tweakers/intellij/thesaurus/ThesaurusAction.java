package net.tweakers.intellij.thesaurus;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.ElementType;

import java.io.IOException;
import java.util.List;

public class ThesaurusAction extends AnAction
{
    public void actionPerformed(AnActionEvent e)
    {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiElement psiElement = getPsiElement(e, editor);

        try
        {
            List<String> synonyms = downloadSynonyms(psiElement.getText());

            ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(
                    new ThesaurusListPopupStep("Thesaurus", synonyms.toArray(), psiElement, editor)
            );
            listPopup.showInBestPositionFor(editor);
        }
        catch (IOException ioe)
        {
            System.out.println("Exception! " + ioe.getMessage());
        }
    }

    @Override
    public void update(AnActionEvent e)
    {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiElement psiElement = getPsiElement(e, editor);
        e.getPresentation().setEnabled(psiElement != null);
    }

    private PsiElement getPsiElement(AnActionEvent e, Editor editor)
    {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (psiFile == null || editor == null)
        {
            return null;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        if (elementAt == null || elementAt.getNode() == null || elementAt.getNode().getElementType() != ElementType.IDENTIFIER)
        {
            return null;
        }

//        System.out.println("offset: " + offset);
//        System.out.println("psi element text: " + elementAt.getText());
//        System.out.println("psi element language: " + elementAt.getLanguage().getDisplayName());
//        System.out.println("psi element nodetype:  " + elementAt.getNode().getElementType());

        return elementAt;
    }

    private List<String> downloadSynonyms(String originalWord) throws IOException
    {
        ThesaurusDownloader downloader = new AltervistaThesaurusDownloader();
        List<String> synonyms = downloader.downloadThesaurusList(originalWord);

        return synonyms;
    }
}
