package net.tweakers.intellij.thesaurus.config;

import com.intellij.openapi.components.*;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@State(name = "ThesaurusPlugin", storages = {@com.intellij.openapi.components.Storage(id = "config", file = StoragePathMacros.APP_CONFIG+"/thesaurus.xml")})
public class PluginConfiguration implements Configurable, PersistentStateComponent<ConfigValues>, ApplicationComponent
{
    private ConfigPanel configPanel;
    private ConfigValues configValues;

    public PluginConfiguration(ConfigValues configValues)
    {
        this.configValues = configValues;
    }

    @Nls
    @Override
    public String getDisplayName()
    {
        return "Thesaurus";
    }

    @Nullable
    @Override
    public ConfigValues getState()
    {
        return configValues;
    }

    @Override
    public void loadState(ConfigValues configValues)
    {
        configValues.loadFromInstance(configValues);
    }

    @Nullable
    @Override
    public JComponent createComponent()
    {
        configPanel = new ConfigPanel();

        return configPanel.panel;
    }

    @Override
    public boolean isModified()
    {
        String selectedItem = (String)configPanel.thesaurusApiClass.getSelectedItem();
        boolean isModified = !configPanel.apiKey.getText().equals(configValues.apiKey) ||
                !selectedItem.equals(configValues.thesaurusApiClass.getLabel());

        return isModified;
    }

    @Override
    public void apply() throws ConfigurationException
    {
        configValues.apiKey = configPanel.apiKey.getText();
        configValues.thesaurusApiClass = ThesaurusApiClass.getThesaurusApiClassByLabel((String) configPanel.thesaurusApiClass.getSelectedItem());
    }

    @Override
    public void reset()
    {
        configPanel.apiKey.setText(configValues.apiKey);
        configPanel.thesaurusApiClass.setSelectedItem(configValues.thesaurusApiClass.getLabel());
    }

    @Override
    public void disposeUIResources()
    {
    }

    @Nullable
    @Override
    public String getHelpTopic()
    {
        return null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ThesaurusConfiguration";
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }
}
