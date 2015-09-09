package nl.pascaldevink.intellij.thesaurus.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.RefactoringFactory;
import com.intellij.refactoring.RenameRefactoring;
import com.intellij.refactoring.openapi.impl.RefactoringFactoryImpl;
import com.intellij.usageView.UsageInfo;
import nl.pascaldevink.intellij.thesaurus.helper.ElementTypeHelper;
import org.jetbrains.annotations.Nullable;

public class ThesaurusListPopupStep extends BaseListPopupStep
{
    protected Editor editor;
    private PsiFile psiFile;

    public ThesaurusListPopupStep(@Nullable String aTitle, Object[] aValues, Editor editor, PsiFile psiFile) {
        super(aTitle, aValues);

        this.editor = editor;
        this.psiFile = psiFile;
    }

    @Override
    public PopupStep onChosen(final Object selectedValue, boolean finalChoice) {
        if (!finalChoice) {
            return super.onChosen(selectedValue, finalChoice);
        }

        if (ElementTypeHelper.canUseRefactoring(editor, psiFile)) {
            replaceWordRefactoring((String) selectedValue);
        } else {
            replaceWordSimple((CharSequence) selectedValue);
        }

        return super.onChosen(selectedValue, finalChoice);
    }

    private void replaceWordSimple(final CharSequence selectedValue)
    {
        final Project project = editor.getProject();
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();

        if (!selectionModel.hasSelection()) {
            selectionModel.selectWordAtCaret(true);
        }

        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                document.replaceString(start, end, selectedValue);
            }
        };

        //Making the replacement
        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();
    }

    private void replaceWordRefactoring(String selectedValue)
    {
        final Project project = editor.getProject();

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        RefactoringFactory refactoringFactory = RefactoringFactoryImpl.getInstance(project);
        RenameRefactoring rename = refactoringFactory.createRename(findNamedElement(elementAt), selectedValue);

        UsageInfo[] usages = rename.findUsages();
        rename.doRefactoring(usages);
    }

    private PsiElement findNamedElement(PsiElement element)
    {
        if (element instanceof PsiNamedElement) {
            return element;
        }

        return findNamedElement(element.getParent());
    }
}
