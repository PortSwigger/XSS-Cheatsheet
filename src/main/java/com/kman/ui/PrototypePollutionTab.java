package com.kman.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kman.data.Data;
import com.kman.functions.Functions;
import com.kman.objects.SavedResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PrototypePollutionTab extends GenericTab{
    private DefaultTableModel model = new DefaultTableModel();
    public PrototypePollutionTab(LayoutManager layout, Functions functions, Data data, List<SavedResponse> saved, ArrayList<String> COLUMN_NAMES) {
        super(layout, functions, data, saved, COLUMN_NAMES);
        for (String name : COLUMN_NAMES){
            model.addColumn(name);
        }
    }

    @Override
    public JComponent getUiComponent() {
        // Create scrollpane
        JScrollPane sp = new JScrollPane();
        // Create table
        JTable tbl = new JTable(model){
            // Stupid workaround for row highlighting issue
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(row%2==0 ? UIManager.getColor("Table.rowColor") : UIManager.getColor("Table.alternateRowColor"));
                if (isRowSelected(row)){
                    c.setBackground(UIManager.getColor("Table.selectionBackground"));
                }
                return c;
            }
        };
        // Table config
        tbl.setAutoCreateRowSorter(true);
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getActionMap().put("copy", functions.multipleCopyAction(tbl, 1));
        // Popup menu
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItemCopy = new JMenuItem("Copy");
        menuItemCopy.setIcon(data.copyIcon);
        JMenuItem menuItemCopyURL = new JMenuItem("Copy (URL)");
        menuItemCopyURL.setIcon(data.copyIcon);
        // Add listeners
        menuItemCopy.addActionListener(functions.createActionListener(tbl, 1, false));
        menuItemCopyURL.addActionListener(functions.createActionListener(tbl, 1, true));
        menu.addPopupMenuListener(functions.createPopUpMenuListener(tbl, menu));
        // Add items
        menu.add(menuItemCopy);
        menu.add(menuItemCopyURL);
        tbl.setComponentPopupMenu(menu);
        // Column config
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tbl.getColumnModel().getColumn(0).setMinWidth(90);
        tbl.getColumnModel().getColumn(0).setMaxWidth(200);
        tbl.getColumnModel().getColumn(0).setPreferredWidth(200);
        tbl.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());// Word wrap payload column
        tbl.getColumnModel().getColumn(2).setMinWidth(90);
        tbl.getColumnModel().getColumn(2).setMaxWidth(150);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(150);
        tbl.getColumnModel().getColumn(3).setMinWidth(90);
        tbl.getColumnModel().getColumn(3).setMaxWidth(150);
        tbl.getColumnModel().getColumn(3).setPreferredWidth(150);

        // Add table to scrollpane
        sp.setViewportView(tbl);
        // Add scrollpane to panel
        this.add(sp);
        return this;
    }
    // Load JSON from SavedResponses
    @Override
    public void loadData() {
        // Clear model
        model.setRowCount(0);
        // Iterate Saved Responses
        for (SavedResponse response : this.saved){
            // If blank then update
            if (response.response.equals("")){
                return;
            }
            // Parse response
            JsonArray jsonArray = new JsonParser().parse(response.response).getAsJsonArray();
            // Iterate over json and get details
            for (JsonElement e : jsonArray){
                JsonObject jsonObject = e.getAsJsonObject();
                String library = jsonObject.get("library").getAsString();
                String payload = jsonObject.get("payload").getAsString();
                String author = new String(jsonObject.get("author").getAsString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                String version = jsonObject.get("version").getAsString();
                String fingerprint = jsonObject.get("fingerprint").getAsString();
                // Add row to model
                model.addRow(new Object[]{library, payload, author, version, fingerprint});
            }
        }
    }
}
