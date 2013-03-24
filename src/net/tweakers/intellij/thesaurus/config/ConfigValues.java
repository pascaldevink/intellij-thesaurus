package net.tweakers.intellij.thesaurus.config;

import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class ConfigValues implements ApplicationComponent {
    public ThesaurusApiClass thesaurusApiClass = ThesaurusApiClass.AlterVista;
    public String apiKey = "";

    public void loadFromInstance(ConfigValues values)
    {
        this.thesaurusApiClass = values.thesaurusApiClass;
        this.apiKey = values.apiKey;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ConfigValues";
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }
}
