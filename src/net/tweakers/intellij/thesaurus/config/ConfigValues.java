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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disposeComponent() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
