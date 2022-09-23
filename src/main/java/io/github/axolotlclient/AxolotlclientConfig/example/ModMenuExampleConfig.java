package io.github.axolotlclient.AxolotlclientConfig.example;

import io.github.axolotlclient.AxolotlclientConfig.Color;
import io.github.axolotlclient.AxolotlclientConfig.options.*;
import io.github.axolotlclient.AxolotlclientConfig.screen.OptionsScreenBuilder;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;

import java.util.function.Function;

public class ModMenuExampleConfig implements ModMenuApi {

    private static final OptionCategory example = new OptionCategory("Example Config");

    public ModMenuExampleConfig(){
        if(example.getOptions().isEmpty()){
            example.add(new BooleanOption("Example Toggle", false),
                    new DoubleOption("Example Slider", 5D, 1, 10),
                    new EnumOption("Example Enum Option", new String[]{"Option 1", "Option 2", "Option 3"}, "Option 1"),
                    new ColorOption("Example Color Option", -162555),
                    new StringOption("Example String Option", "Example §2String"),
                    new GenericOption("Example Generic Option", "Open Minecraft Options", (mouseX, mouseY)->{
                        MinecraftClient.getInstance().openScreen(new SettingsScreen(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance().options));
                    }));
            OptionCategory sub = new OptionCategory("Example Sub Category");
            sub.add(new BooleanOption("Example Toggle", true),
                    new ColorOption("Example Color Option", Color.parse("#FF550055")),
                    new StringOption("Example String Option", "Example §bString"));
            example.addSubCategory(sub);
        }
    }

    @Override
    public String getModId() {
        return "axolotlclientconfig";
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory() {
        return (parent) -> new OptionsScreenBuilder(parent, example, "axolotlclientconfig");
    }
}
