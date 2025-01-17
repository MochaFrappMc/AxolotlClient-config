package io.github.axolotlclient.AxolotlClientConfig.screen;

import com.mojang.blaze3d.platform.InputUtil;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigConfig;
import io.github.axolotlclient.AxolotlClientConfig.AxolotlClientConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.common.types.Tooltippable;
import io.github.axolotlclient.AxolotlClientConfig.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.screen.overlay.Overlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OptionsScreenBuilder extends Screen {

	private static boolean pickerWasOpened = false;
	private final Screen parent;
	public String modid;
	public ButtonWidget backButton;
	protected OptionCategory cat;
	protected Overlay overlay;
	protected TextFieldWidget searchWidget;
	private ButtonWidgetList list;

	public OptionsScreenBuilder(Screen parent, OptionCategory category, String modid) {
		super(Text.of(category.getTranslatedName()));
		this.parent = parent;
		this.cat = category;
		this.modid = modid;
	}

	public OptionCategory getCategory() {
		return cat;
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.render(graphics, mouseX, mouseY, delta);
	}

	@Override
	public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		super.renderBackground(graphics, mouseX, mouseY, delta);
		this.list.renderList(graphics, mouseX, mouseY, delta);
		graphics.drawCenteredShadowedText(textRenderer, cat.getTranslatedName(), width / 2, 25, -1);
		if (overlay == null) {
			list.renderTooltips(graphics, mouseX, mouseY);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void init() {
		if (!pickerWasOpened) {
			createWidgetList(cat);
		} else {
			pickerWasOpened = false;
		}
		this.addSelectableElement(list);

		this.addDrawableSelectableElement(backButton = ButtonWidget.builder(CommonTexts.BACK, buttonWidget -> {
			if (isPickerOpen()) {
				closeOverlay();
			} else {
				MinecraftClient.getInstance().setScreen(parent);
			}

			AxolotlClientConfigManager.getInstance().saveCurrentConfig();
		}).positionAndSize(this.width / 2 - 100, this.height - 40, 200, 20).build());

		this.addDrawableSelectableElement(searchWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, width - 120, 20, 100, 20, Text.translatable("search")) {

			@Override
			public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
				if ((keyCode == InputUtil.KEY_SPACE_CODE || keyCode == InputUtil.KEY_ENTER_CODE) && cat.toString().toLowerCase(Locale.ROOT).contains(modid.toLowerCase(Locale.ROOT))) {
					MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(OptionsScreenBuilder.this, getAllOptions(), modid));
				}

				return super.keyPressed(keyCode, scanCode, modifiers);
			}

			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (isMouseOver(mouseX, mouseY)) {
					if (!isFocused() && cat.toString().toLowerCase(Locale.ROOT).contains(modid.toLowerCase(Locale.ROOT))) {
						MinecraftClient.getInstance().setScreen(new OptionsScreenBuilder(OptionsScreenBuilder.this, getAllOptions(), modid));
						return true;
					}
					setSuggestion("");
					return super.mouseClicked(mouseX, mouseY, button);
				}
				setFocused(false);
				setSuggestion(Formatting.byName("ITALIC") + Text.translatable("search").append("...").getString());
				return false;
			}

			@Override
			public void updateNarration(NarrationMessageBuilder builder) {
				super.updateNarration(builder);
				builder.put(NarrationPart.USAGE, Text.translatable("narration.type_to_search"));
			}

			@Override
			public void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
				super.drawWidget(graphics, mouseX, mouseY, delta);

				graphics.fill(getX() - 5, getY() + 11, getX() + width, getY() + 12, -1);
			}
		});

		searchWidget.setDrawsBackground(false);
		searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
		searchWidget.setChangedListener(s -> {
			if (s.isEmpty()) {
				searchWidget.setSuggestion(Formatting.ITALIC + Text.translatable("search").append("...").getString());
			} else {
				searchWidget.setSuggestion("");
			}
			list.filter(s);
		});
		setFocusedChild(list);
	}

	@Override
	public void tick() {
		this.list.tick();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		AxolotlClientConfigManager.getInstance().saveCurrentConfig();
		boolean pickerOpened = pickerWasOpened;
		pickerWasOpened = false;
		super.resize(client, width, height);
		pickerWasOpened = pickerOpened;
	}

	public void setOverlay(Overlay overlay) {
		MinecraftClient.getInstance().setScreen(this.overlay = overlay);
	}

	public void closeOverlay() {
		AxolotlClientConfigManager.getInstance().saveCurrentConfig();
		pickerWasOpened = true;
		MinecraftClient.getInstance().setScreen(this);
		overlay = null;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		searchWidget.mouseClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button) || this.list.mouseClicked(mouseX, mouseY, button);

	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		boolean li;
		if (list != null) {
			li = list.mouseReleased(mouseX, mouseY, button);
		} else {
			li = false;
		}
		return li || super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isPickerOpen()) {
			return false;
		}
		return list.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	public boolean isPickerOpen() {
		return overlay != null;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
		if (list.isMouseOver(mouseX, mouseY)) {
			return list.mouseScrolled(mouseX, mouseY, amountX, amountY);
		}
		return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (isPickerOpen()) {
			return true;
		}
		return super.charTyped(chr, modifiers);
	}

	public void renderTooltip(GuiGraphics graphics, Tooltippable option, int x, int y) {
		List<Text> text = new ArrayList<>();
		String[] tooltip = Objects.requireNonNull(option.getTooltip()).split("<br>");
		for (String s : tooltip) text.add(Text.literal(s));
		graphics.drawTooltip(client.textRenderer, text, x, y);
	}

	protected void createWidgetList(OptionCategory category) {
		this.list = new ButtonWidgetList(client, this.width, height, 50, height - 50, 25, category);
	}

	protected OptionCategory getAllOptions() {
		OptionCategory temp = new OptionCategory("", false);

		for (io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory cat : AxolotlClientConfigManager.getInstance().getModConfig(modid).getCategories()) {
			setupOptionsList(temp, cat);
		}

		List<io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory> list = temp.getSubCategories();

		if (AxolotlClientConfigConfig.searchSort.get()) {
			if (AxolotlClientConfigConfig.searchSortOrder.get().equals("ASCENDING")) {
				list.sort(new Tooltippable.AlphabeticalComparator());
			} else {
				list.sort(new Tooltippable.AlphabeticalComparator().reversed());
			}
		}

		return (OptionCategory) new OptionCategory("searchOptions", false)
			.addSubCategories(list);
	}

	protected void setupOptionsList(OptionCategory target, io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory cat) {
		target.addSubCategory(cat);
		if (!cat.getSubCategories().isEmpty()) {
			for (io.github.axolotlclient.AxolotlClientConfig.common.options.OptionCategory sub : cat.getSubCategories()) {
				setupOptionsList(target, sub);
			}
		}
	}
}
