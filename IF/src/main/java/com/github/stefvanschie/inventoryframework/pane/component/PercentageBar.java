package com.github.stefvanschie.inventoryframework.pane.component;

import com.github.stefvanschie.inventoryframework.gui.InventoryComponent;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.exception.XMLLoadException;
import com.github.stefvanschie.inventoryframework.pane.Flippable;
import com.github.stefvanschie.inventoryframework.pane.Orientable;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.component.util.VariableBar;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;

/**
 * A percentage bar for a graphical interface into what amount of a whole is set.
 *
 * @since 0.5.0
 */
public class PercentageBar extends VariableBar {

    /**
     * Creates a new percentage bar
     *
     * @param x the x coordinate of the bar
     * @param y the y coordinate of the bar
     * @param length the length of the bar
     * @param height the height of the bar
     * @param priority the priority of the bar
     * @param plugin the plugin that will be the owner for this percentage bar's items
     * @since 0.10.8
     */
    public PercentageBar(int x, int y, int length, int height, @NotNull Priority priority, @NotNull Plugin plugin) {
        super(x, y, length, height, priority, plugin);
    }

    /**
     * Creates a new percentage bar
     *
     * @param x the x coordinate of the bar
     * @param y the y coordinate of the bar
     * @param length the length of the bar
     * @param height the height of the bar
     * @param plugin the plugin that will be the owner for this percentage bar's items
     * @since 0.10.8
     */
    public PercentageBar(int x, int y, int length, int height, @NotNull Plugin plugin) {
        super(x, y, length, height, plugin);
    }

    /**
     * Creates a new percentage bar
     *
     * @param length the length of the bar
     * @param height the height of the bar
     * @param plugin the plugin that will be the owner for this percentage bar's items
     * @since 0.10.8
     */
    public PercentageBar(int length, int height, @NotNull Plugin plugin) {
        super(length, height, plugin);
    }

    public PercentageBar(int x, int y, int length, int height, @NotNull Priority priority) {
        super(x, y, length, height, priority);
    }

    public PercentageBar(int x, int y, int length, int height) {
        super(x, y, length, height);
    }

    public PercentageBar(int length, int height) {
        super(length, height);
    }

    @Override
    public boolean click(@NotNull Gui gui, @NotNull InventoryComponent inventoryComponent,
                         @NotNull InventoryClickEvent event, int slot, int paneOffsetX, int paneOffsetY, int maxLength,
                         int maxHeight) {
        int length = Math.min(this.length, maxLength);
        int height = Math.min(this.height, maxHeight);

        int adjustedSlot = slot - (getX() + paneOffsetX) - inventoryComponent.getLength() * (getY() + paneOffsetY);

        int x = adjustedSlot % inventoryComponent.getLength();
        int y = adjustedSlot / inventoryComponent.getLength();

        if (x < 0 || x >= length || y < 0 || y >= height) {
            return false;
        }

        callOnClick(event);

        event.setCancelled(true);

        int newPaneOffsetX = paneOffsetX + getX();
        int newPaneOffsetY = paneOffsetY + getY();


        return this.fillPane.click(
            gui, inventoryComponent, event, slot, newPaneOffsetX, newPaneOffsetY, length, height
        ) || this.backgroundPane.click(
            gui, inventoryComponent, event, slot, newPaneOffsetX, newPaneOffsetY, length, height
        );
    }

    /**
     * Sets the percentage of this bar. The percentage has to be in (0,1). If not, this method will throw an
     * {@link IllegalArgumentException}.
     *
     * @param percentage the new percentage.
     * @throws IllegalArgumentException when the percentage is out of range
     * @since 0.5.0
     * @see VariableBar#setValue(float) the implementation
     */
    public void setPercentage(float percentage) {
        super.setValue(percentage);
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public PercentageBar copy() {
        PercentageBar percentageBar = new PercentageBar(x, y, length, height, getPriority());

        applyContents(percentageBar);

        return percentageBar;
    }

    /**
     * Gets the percentage as a float in between (0,1) this bar is currently set at.
     *
     * @return the percentage
     * @since 0.5.0
     */
    public float getPercentage() {
        return value;
    }

    /**
     * Loads a percentage bar from a given element
     *
     * @param instance the instance class
     * @param element the element
     * @param plugin the plugin that will be the owner of the underlying items
     * @return the percentage bar
     * @since 0.10.8
     */
    @NotNull
    @Contract(pure = true)
    public static PercentageBar load(@NotNull Object instance, @NotNull Element element, @NotNull Plugin plugin) {
        int length;
        int height;

        try {
            length = Integer.parseInt(element.getAttribute("length"));
            height = Integer.parseInt(element.getAttribute("height"));
        } catch (NumberFormatException exception) {
            throw new XMLLoadException(exception);
        }

        PercentageBar percentageBar = new PercentageBar(length, height, plugin);

        Pane.load(percentageBar, instance, element);
        Orientable.load(percentageBar, element);
        Flippable.load(percentageBar, element);

        if (element.hasAttribute("populate")) {
            return percentageBar;
        }

        if (element.hasAttribute("percentage")) {
            try {
                percentageBar.setPercentage(Float.parseFloat(element.getAttribute("percentage")));
            } catch (IllegalArgumentException exception) {
                throw new XMLLoadException(exception);
            }
        }

        return percentageBar;
    }

    /**
     * Loads a percentage bar from a given element
     *
     * @param instance the instance class
     * @param element the element
     * @return the percentage bar
     * @deprecated this method is no longer used internally and has been superseded by
     *             {@link #load(Object, Element, Plugin)}
     */
    @NotNull
    @Contract(pure = true)
    @Deprecated
    public static PercentageBar load(@NotNull Object instance, @NotNull Element element) {
        return load(instance, element, JavaPlugin.getProvidingPlugin(PercentageBar.class));
    }
}
