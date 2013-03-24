package net.tweakers.intellij.thesaurus.action;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.ElementType;
import net.tweakers.intellij.thesaurus.ApiKeyException;
import net.tweakers.intellij.thesaurus.config.ConfigValues;
import net.tweakers.intellij.thesaurus.config.ThesaurusApiClass;
import net.tweakers.intellij.thesaurus.downloader.AltervistaThesaurusDownloader;
import net.tweakers.intellij.thesaurus.downloader.BigHugeThesaurusDownloader;
import net.tweakers.intellij.thesaurus.downloader.ThesaurusDownloader;

import java.io.IOException;
import java.util.List;

public class ThesaurusAction extends AnAction
{
    public void actionPerformed(AnActionEvent e)
    {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiElement psiElement = getPsiElement(e, editor);

        if (editor == null)
            return;

        String originalWord = psiElement.getText();

        try
        {
            List<String> synonyms = downloadSynonyms(originalWord);

            ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(
                    new ThesaurusListPopupStep("Thesaurus", synonyms.toArray(), psiElement, editor)
            );
            listPopup.showInBestPositionFor(editor);
        }
        catch (IOException ioe)
        {
            System.out.println("Exception! " + ioe.getMessage());
            Notification notification = new Notification("Thesaurus", "Thesaurus", "No synonyms available for '"+originalWord+"'", NotificationType.ERROR);
            Notifications.Bus.register("Thesaurus", NotificationDisplayType.BALLOON);
            Notifications.Bus.notify(notification, psiElement.getProject());
        } catch (ClassNotFoundException e1) {
            System.out.println("Selected ThesaurusDownloader class not found");
            Notification notification = new Notification("Thesaurus", "Thesaurus", "Error in provider configuration", NotificationType.ERROR);
            Notifications.Bus.register("Thesaurus", NotificationDisplayType.BALLOON);
            Notifications.Bus.notify(notification, psiElement.getProject());
        } catch (ApiKeyException e1) {
            System.out.println("API key is missing");
            Notification notification = new Notification("Thesaurus", "Thesaurus", "API key is missing", NotificationType.ERROR);
            Notifications.Bus.register("Thesaurus", NotificationDisplayType.BALLOON);
            Notifications.Bus.notify(notification, psiElement.getProject());
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

    private List<String> downloadSynonyms(String originalWord) throws IOException, ClassNotFoundException, ApiKeyException {
        ConfigValues configValues = ServiceManager.getService(ConfigValues.class);

        ThesaurusDownloader downloader = getDownloaderFromConfig(configValues);
        List<String> synonyms = downloader.downloadThesaurusList(originalWord);

        return synonyms;
    }

    private ThesaurusDownloader getDownloaderFromConfig(ConfigValues configValues) throws ClassNotFoundException, ApiKeyException {
        if (configValues.apiKey == null || configValues.apiKey.isEmpty())
            throw new ApiKeyException();

        if (configValues.thesaurusApiClass.equals(ThesaurusApiClass.AlterVista))
            return new AltervistaThesaurusDownloader(configValues.apiKey);

        if (configValues.thesaurusApiClass.equals(ThesaurusApiClass.BigHugeThesaurus))
            return new BigHugeThesaurusDownloader(configValues.apiKey);

        throw new ClassNotFoundException(configValues.thesaurusApiClass.toString());
    }
}
