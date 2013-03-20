package net.tweakers.intellij.thesaurus;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.ArrayList;

public class ThesaurusAction extends AnAction {
    public void actionPerformed(AnActionEvent e) {
        PsiElement psiElement = getPsiElement(e);

        JBList synonyms = downloadSynonyms(psiElement.getText());

        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PopupChooserBuilder popupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(synonyms);
        popupBuilder.addListener(new JBPopupListener() {
            @Override
            public void beforeShown(LightweightWindowEvent lightweightWindowEvent) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onClosed(LightweightWindowEvent lightweightWindowEvent) {
                System.out.println("dsds");
            }
        });
        popupBuilder.setItemChoosenCallback(new Runnable() {
            @Override
            public void run() {
                System.out.println("dkjhfkfj");
            }
        });
        popupBuilder.createPopup().showInBestPositionFor(editor);
    }

    @Override
    public void update(AnActionEvent e) {
        PsiElement psiElement = getPsiElement(e);
        e.getPresentation().setEnabled(psiElement != null);
    }

    private PsiElement getPsiElement(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);

        if (psiFile == null || editor == null)
        {
            return null;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        if (elementAt.getNode().getElementType() != ElementType.IDENTIFIER)
        {
            return null;
        }

//        System.out.println("offset: " + offset);
//        System.out.println("psi element text: " + elementAt.getText());
//        System.out.println("psi element language: " + elementAt.getLanguage().getDisplayName());
//        System.out.println("psi element nodetype:  " + elementAt.getNode().getElementType());

        return elementAt;
    }

    private JBList downloadSynonyms(String originalText)
    {
        ArrayList<String> synonyms = new ArrayList<String>();
        synonyms.add("fetch");
        synonyms.add("download");

        JBList synonymList = new JBList(synonyms);

        return synonymList;
    }
}
