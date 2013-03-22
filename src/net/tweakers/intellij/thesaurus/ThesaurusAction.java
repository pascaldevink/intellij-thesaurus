package net.tweakers.intellij.thesaurus;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.util.ArrayList;

public class ThesaurusAction extends AnAction
{
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiElement psiElement = getPsiElement(e, editor);

        ArrayList synonyms = downloadSynonyms(psiElement.getText());

        ListPopup listPopup = JBPopupFactory.getInstance().createListPopup(
                new ThesaurusListPopupStep("Thesaurus", synonyms.toArray(), psiElement, editor)
        );
        listPopup.showInBestPositionFor(editor);
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiElement psiElement = getPsiElement(e, editor);
        e.getPresentation().setEnabled(psiElement != null);
    }

    private PsiElement getPsiElement(AnActionEvent e, Editor editor) {
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

    private ArrayList downloadSynonyms(String originalText)
    {
        ArrayList<String> synonyms = new ArrayList<String>();
        synonyms.add("fetch");
        synonyms.add("download");

        return synonyms;
    }
}
