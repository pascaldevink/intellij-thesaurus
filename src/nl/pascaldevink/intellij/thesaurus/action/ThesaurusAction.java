package nl.pascaldevink.intellij.thesaurus.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
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
import nl.pascaldevink.intellij.thesaurus.config.Configuration;
import nl.pascaldevink.intellij.thesaurus.downloader.MashapeDownloader;
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
        if (editor == null)
            return;

        if (false == isRenameAllowed(e, editor))
            return;

        SelectionModel selectionModel = editor.getSelectionModel();
        selectionModel.selectWordAtCaret(true);
        String originalWord = selectionModel.getSelectedText();

        try
        {
            List<String> synonyms = downloadSynonyms(originalWord);

            ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(
                new ThesaurusListPopupStep("Thesaurus", synonyms.toArray(), editor)
            );
            listPopup.showInBestPositionFor(editor);
        }
        catch (IOException ioe)
        {
            System.out.println("Exception! " + ioe.getMessage());
            Notification notification = new Notification("Thesaurus", "Thesaurus", "No synonyms available for '"+originalWord+"'", NotificationType.ERROR);
            Notifications.Bus.register("Thesaurus", NotificationDisplayType.BALLOON);
            Notifications.Bus.notify(notification, editor.getProject());
        }
    }

    @Override
    public void update(AnActionEvent e)
    {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        e.getPresentation().setEnabled(isRenameAllowed(e, editor));
    }

    private boolean isRenameAllowed(AnActionEvent e, Editor editor)
    {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);

        if (psiFile == null || editor == null)
        {
            return false;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        if (elementAt == null || elementAt.getNode() == null)
            return false;

//        System.out.println(elementAt.getNode().getElementType().toString());

        IElementType elementType = elementAt.getNode().getElementType();
        if (elementType != ElementType.STRING_LITERAL)
        {
            return false;
        }

        return true;
    }

    private List<String> downloadSynonyms(String originalWord) throws IOException {
        ThesaurusDownloader downloader = new MashapeDownloader(Configuration.MASHAPE_API_KEY);
        List<String> synonyms = downloader.downloadThesaurusList(originalWord);

        return synonyms;
    }
}
