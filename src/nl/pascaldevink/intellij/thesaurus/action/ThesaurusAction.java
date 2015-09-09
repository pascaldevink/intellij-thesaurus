package nl.pascaldevink.intellij.thesaurus.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.PhpElementType;
import nl.pascaldevink.intellij.thesaurus.config.Configuration;
import nl.pascaldevink.intellij.thesaurus.downloader.BigHugeThesaurusDownloader;
import nl.pascaldevink.intellij.thesaurus.downloader.ThesaurusDownloader;

import java.io.IOException;
import java.util.List;

public class ThesaurusAction extends AnAction
{
    public ThesaurusAction() {
        super("Thesaurus");
    }

    public void actionPerformed(AnActionEvent e)
    {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (psiFile == null || editor == null)
        {
            return;
        }

        if (false == isRenameAllowed(e, editor))
            return;

        String originalWord = getOriginalWord(editor);

        try
        {
            List<String> synonyms = downloadSynonyms(originalWord);

            ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(
                new ThesaurusListPopupStep("Thesaurus", synonyms.toArray(), editor, psiFile)
            );
            listPopup.showInBestPositionFor(editor);
        }
        catch (IOException ioe)
        {
            System.out.println("Exception! " + ioe.getMessage());
            Notification notification = new Notification("Thesaurus", "Thesaurus", "No synonyms available for '"+originalWord+"'", NotificationType.ERROR);
            Notifications.Bus.notify(notification, editor.getProject());
        }
    }

    @Override
    public void update(AnActionEvent e)
    {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        e.getPresentation().setEnabled(isRenameAllowed(e, editor));
    }

    private String getOriginalWord(Editor editor)
    {
        SelectionModel selectionModel = editor.getSelectionModel();

        if (selectionModel.hasSelection()) {
            return selectionModel.getSelectedText();
        }

        selectionModel.selectWordAtCaret(true);
        return selectionModel.getSelectedText();
    }

    private boolean isRenameAllowed(AnActionEvent e, Editor editor)
    {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (psiFile == null || editor == null)
        {
            return false;
        }

        if (editor.getSelectionModel().hasSelection()) {
            return true;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        if (elementAt == null || elementAt.getNode() == null)
            return false;

        if (elementAt.getNode().getElementType() instanceof PhpElementType) {
            IElementType elementType = elementAt.getNode().getElementType();
            if (elementType != PhpTokenTypes.VARIABLE_NAME &&
                elementType != PhpTokenTypes.STRING_LITERAL &&
                elementType != PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE)
            {
                return false;
            }
        } else {
            IElementType elementType = elementAt.getNode().getElementType();
            if (elementType != ElementType.STRING_LITERAL)
            {
                return false;
            }
        }

        return true;
    }

    private List<String> downloadSynonyms(String originalWord) throws IOException {
        ThesaurusDownloader downloader = new BigHugeThesaurusDownloader(Configuration.BIG_HUGE_THESAURUS_API_KEY);
        List<String> synonyms = downloader.downloadThesaurusList(originalWord);

        return synonyms;
    }
}
