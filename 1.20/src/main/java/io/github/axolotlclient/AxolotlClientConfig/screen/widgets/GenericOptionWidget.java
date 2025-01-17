package io.github.axolotlclient.AxolotlClientConfig.screen.widgets;

import io.github.axolotlclient.AxolotlClientConfig.options.GenericOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class GenericOptionWidget extends ButtonWidget implements OptionWidget {

	private final GenericOption option;

	public GenericOptionWidget(int x, int y, int width, int height, GenericOption option) {
		super(x, y, width, height, Text.of(option.getLabel()), (widget) -> {
		}, DEFAULT_NARRATION);
		this.option = option;
	}


	@Override
	public void drawWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		hovered = isMouseOver(mouseX, mouseY) || isFocused();
		if (visible) {
			super.drawWidget(graphics, mouseX, mouseY, delta);
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.active || !this.visible) {
			return false;
		} else if (keyCode != 257 && keyCode != 32 && keyCode != 335) {
			return false;
		} else {
			this.playDownSound(MinecraftClient.getInstance().getSoundManager());
			onClick(getX() + 1, getY() + 1);
			return true;
		}
	}

	public void onClick(double mouseX, double mouseY) {
		option.get().onClick((int) mouseX, (int) mouseY);
	}

	@Override
	protected MutableText getNarrationMessage() {
		return Text.literal(option.getTranslatedName()).append(super.getNarrationMessage());
	}

	@Override
	public boolean isHoveredOrFocused() {
		return canHover() && super.isHoveredOrFocused();
	}
}
