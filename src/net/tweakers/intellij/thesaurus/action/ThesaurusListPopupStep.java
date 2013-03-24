package net.tweakers.intellij.thesaurus.action;

import com.intellij.codeInsight.CodeInsightUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class ThesaurusListPopupStep extends BaseListPopupStep
{
    protected PsiElement psiElement;
    protected Editor editor;

    public ThesaurusListPopupStep(@Nullable String aTitle, Object[] aValues, PsiElement psiElement, Editor editor) {
        super(aTitle, aValues);

        this.psiElement = psiElement;
        this.editor = editor;
    }

    @Override
    public PopupStep onChosen(Object selectedValue, boolean finalChoice) {
        if (!finalChoice)
            return super.onChosen(selectedValue, finalChoice);

        if (!CodeInsightUtil.preparePsiElementsForWrite(psiElement)) {
            return super.onChosen(selectedValue, finalChoice);
        }

        editor.getSelectionModel().selectWordAtCaret(true);
        EditorModificationUtil.deleteSelectedText(editor);
        EditorModificationUtil.insertStringAtCaret(editor, (String) selectedValue, true, true);

        return super.onChosen(selectedValue, finalChoice);
    }
}
