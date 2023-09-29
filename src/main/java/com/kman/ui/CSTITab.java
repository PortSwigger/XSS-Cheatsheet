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

// Client-side template injection Tab
public class CSTITab extends GenericTab{
    private DefaultTableModel model = new DefaultTableModel();
    public CSTITab(LayoutManager layout, Functions functions, Data data, List<SavedResponse> saved, ArrayList<String> COLUMN_NAMES) {
        super(layout, functions, data, saved, COLUMN_NAMES);
        for (String name : COLUMN_NAMES){
            model.addColumn(name);
        }
    }
    @Override
    public JComponent getUiComponent() {
        // Scroll pane
        JScrollPane sp = new JScrollPane();
        // Table
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
        tbl.getActionMap().put("copy", functions.multipleCopyAction(tbl, 4));
        // Popup menu
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItemCopy = new JMenuItem("Copy");
        menuItemCopy.setIcon(data.copyIcon);
        JMenuItem menuItemCopyURL = new JMenuItem("Copy (URL)");
        menuItemCopyURL.setIcon(data.copyIcon);
        // Add action listeners
        menuItemCopy.addActionListener(functions.createActionListener(tbl, 4, false));
        menuItemCopyURL.addActionListener(functions.createActionListener(tbl, 4, true));
        menu.addPopupMenuListener(functions.createPopUpMenuListener(tbl, menu));
        // Add items
        menu.add(menuItemCopy);
        menu.add(menuItemCopyURL);
        tbl.setComponentPopupMenu(menu);
        // Column config
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tbl.getColumnModel().getColumn(0).setMinWidth(90);
        tbl.getColumnModel().getColumn(0).setMaxWidth(90);
        tbl.getColumnModel().getColumn(1).setMinWidth(90);
        tbl.getColumnModel().getColumn(1).setMaxWidth(90);
        tbl.getColumnModel().getColumn(2).setMinWidth(120);
        tbl.getColumnModel().getColumn(2).setMaxWidth(800);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(200);
        tbl.getColumnModel().getColumn(3).setMinWidth(50);
        tbl.getColumnModel().getColumn(3).setMaxWidth(50);
        tbl.getColumnModel().getColumn(4).setCellRenderer(new WordWrapCellRenderer());
        tbl.getColumnModel().getColumn(5).setMinWidth(90);
        tbl.getColumnModel().getColumn(5).setMaxWidth(90);
        // Add table to scrollpane
        sp.setViewportView(tbl);
        // Add scrollpane to panel
        this.add(sp);
        return this;
    }

    @Override
    public void loadData() {
        // Clear model
        model.setRowCount(0);
        // Iterate SavedResponses
        for (SavedResponse response : this.saved){
            // If blank then update
            if (response.response.equals("")){
                return;
            }

            // Parse response
            JsonArray jsonArray = new JsonParser().parse(response.response).getAsJsonArray();

            // Iterate over array
            for (JsonElement jsonElement : jsonArray){

                JsonObject obj = jsonElement.getAsJsonObject();

                // Get authors
                String authors = "";
                for (JsonElement author : obj.getAsJsonArray("authors")){
                    JsonObject o = author.getAsJsonObject();
                    // If no company
                    if (o.get("company").getAsString().equals("")){
                        authors += new String(o.get("name").getAsString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) +  " & ";
                    }
                    else{
                        authors += new String(o.get("name").getAsString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8) + " (" + o.get("company").getAsString() + ") & ";
                    }
                }
                // Trim trailing &
                if (authors.endsWith(" ")){
                    authors = authors.substring(0, authors.length()-3);
                }
                // Get software
                String software = "VueJS";
                if (response.name.contains("AngularJS")){
                    software = "AngularJS";
                }

                // Get details
                String vector = obj.get("vector").getAsString();
                String version = obj.get("version").getAsString();
                String csp = obj.get("csp").getAsString();

                // Add new row to model
                this.model.addRow(new Object[]{software, version, authors, Integer.toString(vector.length()), vector, csp});
            }
        }
    }
}