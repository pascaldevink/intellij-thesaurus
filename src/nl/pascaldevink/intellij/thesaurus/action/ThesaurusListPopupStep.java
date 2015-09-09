package nl.pascaldevink.intellij.thesaurus.action;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import org.jetbrains.annotations.Nullable;

public class ThesaurusListPopupStep extends BaseListPopupStep
{
    protected Editor editor;

    public ThesaurusListPopupStep(@Nullable String aTitle, Object[] aValues, Editor editor) {
        super(aTitle, aValues);

        this.editor = editor;
    }

    @Override
    public PopupStep onChosen(final Object selectedValue, boolean finalChoice) {
        if (!finalChoice) {
            return super.onChosen(selectedValue, finalChoice);
        }

        final Project project = editor.getProject();
        final Document document = editor.getDocument();
        final SelectionModel selectionModel = editor.getSelectionModel();

        if (!selectionModel.hasSelection()) {
            selectionModel.selectWordAtCaret(true);
        }

        final int start = selectionModel.getSelectionStart();
        final int end = selectionModel.getSelectionEnd();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                document.replaceString(start, end, (CharSequence) selectedValue);
            }
        };

        //Making the replacement
        WriteCommandAction.runWriteCommandAction(project, runnable);
        selectionModel.removeSelection();

        return super.onChosen(selectedValue, finalChoice);
    }
}
