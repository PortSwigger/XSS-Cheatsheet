package com.kman.ui;

import burp.api.montoya.http.HttpMode;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kman.data.Data;
import com.kman.functions.Functions;
import com.kman.models.GenericModel;
import com.kman.objects.GenericRow;
import com.kman.objects.SavedResponse;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GenericTab extends JPanel {
    Functions functions;
    Data data;
    public List<SavedResponse> saved;
    private GenericModel model;
    private List<GenericRow> rows = new ArrayList<>();
    public GenericTab(LayoutManager layout, Functions functions, Data data, List<SavedResponse> saved, ArrayList<String> COLUMN_NAMES) {
        super(layout);
        this.functions = functions;
        this.data = data;
        this.saved = saved;
        this.model = new GenericModel(data, COLUMN_NAMES, rows);
    }
    public JComponent getUiComponent(){
        // Create scroll pane
        JScrollPane sp = new JScrollPane();
        // Create table
        JTable tbl = new JTable(this.model){
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
        tbl.setAutoCreateRowSorter(true);
        tbl.getActionMap().put("copy", functions.multipleCopyAction(tbl, 2));
        // Popup menu
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItemCopy = new JMenuItem("Copy");
        menuItemCopy.setIcon(data.copyIcon);
        JMenuItem menuItemCopyURL = new JMenuItem("Copy (URL)");
        menuItemCopyURL.setIcon(data.copyIcon);
        // Add menu items
        menu.add(menuItemCopy);
        menu.add(menuItemCopyURL);
        tbl.setComponentPopupMenu(menu);
        // Column config
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tbl.getColumnModel().getColumn(0).setMinWidth(90);
        tbl.getColumnModel().getColumn(0).setMaxWidth(90);
        tbl.getColumnModel().getColumn(1).setMinWidth(120);
        tbl.getColumnModel().getColumn(1).setMaxWidth(800);
        tbl.getColumnModel().getColumn(1).setPreferredWidth(200);
        tbl.getColumnModel().getColumn(2).setCellRenderer(new WordWrapCellRenderer());// Word wrap payload column
        // Add listeners
        menuItemCopy.addActionListener(functions.createActionListener(tbl, 2, false));
        menuItemCopyURL.addActionListener(functions.createActionListener(tbl, 2, true));
        menu.addPopupMenuListener(functions.createPopUpMenuListener(tbl, menu));
        // Add table to scrollpane
        sp.setViewportView(tbl);
        // Add scrollpane to panel
        this.add(sp);
        return this;
    }
    // Load JSON from SavedResponses
    public void loadData(){
        // Clear model
        rows.clear();
        // Iterate SavedResponses
        for (SavedResponse response : this.saved){
            // If blank then update
            if (response.response.equals("")){
                return;
            }
            // Parse response
            JsonArray jsonArray = new JsonParser().parse(response.response).getAsJsonArray();
            // Create BasicRows and add to BasicModel
            for (JsonElement e : jsonArray){
                JsonObject jsonObject = e.getAsJsonObject();
                String description = jsonObject.get("description").getAsString();
                if (response.name.equals("Response content types")){
                    description = jsonObject.get("content-type").getAsString();
                }
                String code = jsonObject.get("code").getAsString();
                JsonArray browsers = jsonObject.get("browsers").getAsJsonArray();
                rows.add(new GenericRow(data, description, code, browsers));
            }
        }
    }
    // Update SavedResponses via Github
    public void update(){
        List<SavedResponse> saved = this.saved;
        Thread thread = new Thread(){
            @Override
            public void run() {
                // Iterate SavedResponses
                for (SavedResponse response : saved){
                    try{
                        // Make request
                        HttpRequest r = HttpRequest.httpRequestFromUrl(response.url);
                        HttpRequestResponse rr = data.api.http().sendRequest(r, HttpMode.HTTP_2);
                        String json = rr.response().bodyToString();
                        // If response not empty
                        if (!json.isEmpty()){
                            // Update
                            response.response = json;
                            data.logging.logToOutput("Updated " + response.name);
                        }
                    }
                    catch (Exception e){
                        data.logging.logToOutput("Failed to update " + response.name);
                    }
                }
                // Reload data
                loadData();
                // Save all responses
                functions.saveResponses();
            }
        };
        thread.start();
    }
    public List<SavedResponse> getSaved(){
        return this.saved;
    }
}
