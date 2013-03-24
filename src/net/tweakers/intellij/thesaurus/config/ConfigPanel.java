package net.tweakers.intellij.thesaurus.config;

import javax.swing.*;

public class ConfigPanel
{
    public JPanel panel;

    public JTextField apiKey;
    public JComboBox thesaurusApiClass;

    public ConfigPanel()
    {
        for(String label: ThesaurusApiClass.getLabels())
        {
            thesaurusApiClass.addItem(label);
        }
    }
}
