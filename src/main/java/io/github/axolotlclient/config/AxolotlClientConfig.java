package io.github.axolotlclient.config;

import io.github.axolotlclient.config.ConfigManager;
import io.github.axolotlclient.config.DefaultConfigManager;
import io.github.axolotlclient.config.options.OptionCategory;
import io.github.axolotlclient.config.screen.OptionsScreenBuilder;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class AxolotlClientConfig {
    private static final HashMap<String, ConfigHolder> configs = new HashMap<>();
    private static final HashMap<String, ConfigManager> managers = new HashMap<>();

    public static Logger LOGGER = LogManager.getLogger("AxolotlClient Config");

    public AxolotlClientConfig(){

    }

    public static void registerConfig(String modid, ConfigHolder config, ConfigManager manager){
        configs.put(modid, config);
        managers.put(modid, manager);
    }

    public static void registerConfig(String modid, ConfigHolder config){
        registerConfig(modid, config, new DefaultConfigManager(modid));
    }

    public static void openConfigScreen(String modid){
       MinecraftClient.getInstance().openScreen(new OptionsScreenBuilder(MinecraftClient.getInstance().currentScreen, new OptionCategory(modid+"Config", false).addSubCategories(configs.get(modid).getCategories()), modid));
    }

    public static ConfigHolder getModConfig(String modid){
        return configs.get(modid);
    }

    public static void save(String modid) {
        managers.get(modid).save();
    }

    public static void load(String modid) {
        managers.get(modid).load();
    }

    public static void saveCurrentConfig(){
        if(MinecraftClient.getInstance().currentScreen instanceof OptionsScreenBuilder){
            save(((OptionsScreenBuilder) MinecraftClient.getInstance().currentScreen).modid);
        }
    }
}
