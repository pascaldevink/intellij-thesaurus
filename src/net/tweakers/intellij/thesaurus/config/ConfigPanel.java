package net.tweakers.intellij.thesaurus.config;

import javax.swing.*;

public class ConfigPanel
{
    public JPanel panel;

    public JTextField apiKey;
    public JComboBox thesaurusApiClass;

    public ConfigPanel()
    {
        thesaurusApiClass.addItem("AlterVista");
        thesaurusApiClass.addItem("Big Huge Thesaurus");
    }
}
