package com.kman.ui;

import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import com.kman.data.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ContextMenu implements ContextMenuItemsProvider {
    Data data;
    public ContextMenu(Data data) {
        this.data = data;
    }
    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        List<Component> extentionMenu = new ArrayList<>();
        // Quick insert menu
        JMenu quickInsertMenu = new JMenu("Quick Insert");
        // Iterate over recently copied strings
        for (String code : data.recentlyCopied){
            // Create menu item
            JMenuItem menuItem = new JMenuItem(code);
            // Add action listener
            menuItem.addActionListener(createActionListener(code, false));
            quickInsertMenu.add(menuItem);
        }
        // Quick insert (URL) menu
        JMenu quickInsertURL = new JMenu("Quick Insert (URL)");
        // Iterate over recently copied strings
        for (String code : data.recentlyCopied){
            // Create menu item
            JMenuItem menuItem = new JMenuItem(code);
            // Add action listener
            menuItem.addActionListener(createActionListener(code, true));
            quickInsertURL.add(menuItem);
        }
        // Add items
        extentionMenu.add(quickInsertMenu);
        extentionMenu.add(quickInsertURL);
        return extentionMenu;
    }
    private ActionListener createActionListener(String code, boolean urlEncode){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Copy code to clipboard
                StringSelection selection;
                if (urlEncode){
                    selection = new StringSelection(URLEncoder.encode(code, StandardCharsets.UTF_8));
                }
                else{
                    selection = new StringSelection(code);
                }
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                robotType();
            }
        };
    }
    // Robot paste clipboard contents
    private void robotType(){
        Robot robot = null;
        try {
            robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        } catch (AWTException ex) {
            throw new RuntimeException(ex);
        }
    }
}