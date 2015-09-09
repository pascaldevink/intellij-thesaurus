package nl.pascaldevink.intellij.thesaurus.helper;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.source.tree.ElementType;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;

public class ElementTypeHelper
{
    public static boolean canUseThesaurus(Editor editor, PsiFile psiFile)
    {
        if (editor.getSelectionModel().hasSelection()) {
            return true;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        if (elementAt == null || elementAt.getNode() == null) {
            return false;
        }

        if (elementAt.getNode().getElementType().getLanguage().getID().equals("PHP")) {
            IElementType elementType = elementAt.getNode().getElementType();
            if (elementType != PhpTokenTypes.VARIABLE_NAME &&
                    elementType != PhpTokenTypes.STRING_LITERAL &&
                    elementType != PhpTokenTypes.STRING_LITERAL_SINGLE_QUOTE)
            {
                return false;
            }

            return true;
        } else if (elementAt.getNode().getElementType().getLanguage().getID().equals("JAVA")) {
            IElementType elementType = elementAt.getNode().getElementType();
            if (elementType != ElementType.STRING_LITERAL) {
                return false;
            }

            return true;
        }

        return false;
    }

    public static boolean canUseRefactoring(Editor editor, PsiFile psiFile)
    {
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);

        return elementAt instanceof PsiNamedElement || elementAt.getParent() instanceof PsiNamedElement;
    }
}
