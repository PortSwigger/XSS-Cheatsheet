package com.kman.functions;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kman.data.Data;
import com.kman.models.EventsTableModel;
import com.kman.objects.Event;
import com.kman.objects.SavedResponse;
import com.kman.objects.Tag;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Functions {
    MontoyaApi api;
    Logging logging;
    Data data;
    ReentrantLock mutex = new ReentrantLock();
    public Functions(MontoyaApi api, Logging logging, Data data) {
        this.api = api;
        this.logging = logging;
        this.data = data;
    }
    // Get SavedResponse by name
    public SavedResponse loadData(String name){
        for (SavedResponse savedResponse : data.saved){
            if (name.equals(savedResponse.name)){
                return savedResponse;
            }
        }
        return null;
    }
    // Save JSON responses to global preferences
    public void saveResponses(){
        mutex.lock();
        List<JsonObject> toSave = new ArrayList<>();
        for (SavedResponse resp : data.saved){
            toSave.add(resp.toJSON());
        }
        try{
            data.preferences.setSetting("saved", toSave);
        }
        catch (Exception e){
            data.logging.logToOutput(e.toString());
        }
        mutex.unlock();
    }
    // Load JSON responses from global preferences or JSON files
    public void loadReponses() {
        // Get persisted data
        List<JsonObject> json = data.preferences.getSetting("saved");
        // If no persisted data
        if (json.size() == 0){
            logging.logToOutput("Persisted data not found, falling back to local copy!");
            // Load from json files
            mutex.lock();
            for (SavedResponse resp : data.saved){
                String[] url = resp.url.split("/");
                String fileName = "json/" + url[url.length - 1];
                try {
                    resp.response = new String(getClass().getClassLoader().getResourceAsStream(fileName).readAllBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            mutex.unlock();
        }
        // If persisted data
        else {
            mutex.lock();
            for (JsonObject jsonObj : json){
                for (SavedResponse resp : data.saved){
                    if (resp.name.equals(jsonObj.get("name").getAsString())){
                        resp.response = jsonObj.get("response").getAsString();
                    }
                }
            }
            mutex.unlock();
            logging.logToOutput("Loaded persisted data!");
        }
    }
    // Get all event names to populate Events table
    public void getEventNames(DefaultTableModel eventNamesModel){
        data.eventNames.clear();
        eventNamesModel.setRowCount(0);
        eventNamesModel.addRow(new Object[] { "All events" });
        for (Event event : data.events){
            data.eventNames.add(event.getName());
        }
        Collections.sort(data.eventNames);
        for (String event : data.eventNames){
            eventNamesModel.addRow(new Object[] { event });
        }
    }
    // Get all tag names to populate Tags table
    public void getTagNames(JsonObject jsonObject, DefaultTableModel tagModel){
        tagModel.addRow(new Object[] { "All tags" });
        tagModel.addRow(new Object[] { "custom tags" });
        // Parse JSON
        for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
            String name = e.getKey();
            JsonObject element = jsonObject.getAsJsonObject(name);
            JsonArray tags = element.get("tags").getAsJsonArray();
            for (JsonElement j : tags) {
                JsonObject obj = j.getAsJsonObject();
                String tagName = obj.get("tag").getAsString();
                if (!data.tagNames.contains(tagName) && !data.tagNames.contains(tagName.replaceAll("[0-9]", "")) && !tagName.equals("custom tags")){
                    data.tagNames.add(tagName);
                }
            }
        }
        // Sort tag names
        Collections.sort(data.tagNames);
        // Add rows to model
        for (String tag : data.tagNames){
            if (!tag.equals("*")){
                tagModel.addRow(new Object[] { tag });
            }
        }
        tagModel.fireTableDataChanged();
    }
    // Search all events by any combination of tag, event and browser
    public List<Event> searchByElement(String tag, String eventName, String browser){
        List<Event> newEvents = new ArrayList<>();

        for (Event e : data.events){
            // Default model reset
            if (eventName.equals("All events") && tag.equals("All tags") && browser.equals("All browsers")){
                e.setSelectedTag(e.getTags().get(0).getName());
                newEvents.add(e);
            }
            // Tag only search
            else if (eventName.equals("All events") && !tag.equals("All tags") && browser.equals("All browsers")){
                for (Tag t : e.getTags()){
                    if (t.getName().equals(tag)){
                        e.setSelectedTag(tag);
                        newEvents.add(e);
                        break;
                    }
                }
            }
            // Event only search
            else if (!eventName.equals("All events") && tag.equals("All tags") && browser.equals("All browsers")){
                if (e.getName().equals(eventName)){
                    e.setSelectedTag(e.getTags().get(0).getName());
                    newEvents.add(e);
                    break;
                }
            }
            // Browser only search
            else if (eventName.equals("All events") && tag.equals("All tags") && !browser.equals("All browsers")){
                for (Tag t : e.getTags()){
                    if (t.hasBrowser(browser)){
                        e.setSelectedTag(e.getTags().get(0).getName());
                        newEvents.add(e);
                        break;
                    }
                }
            }
            // Tag and Event search
            else if (!eventName.equals("All events") && !tag.equals("All tags") && browser.equals("All browsers")){
                if (e.getName().equals(eventName)){
                    for (Tag t : e.getTags()){
                        if (t.getName().equals(tag)){
                            e.setSelectedTag(tag);
                            newEvents.add(e);
                            break;
                        }
                    }
                }
            }
            // Tag and Browser search
            else if (eventName.equals("All events") && !tag.equals("All tags") && !browser.equals("All browsers")){
                for (Tag t : e.getTags()){
                    if (t.getName().equals(tag) && t.hasBrowser(browser)){
                        e.setSelectedTag(tag);
                        newEvents.add(e);
                        break;
                    }
                }
            }
            // Event and Browser search
            else if (!eventName.equals("All events") && tag.equals("All tags") && !browser.equals("All browsers")){
                if (e.getName().equals(eventName)){
                    for (Tag t : e.getTags()){
                        if (t.hasBrowser(browser)){
                            e.setSelectedTag(tag);
                            newEvents.add(e);
                            break;
                        }
                    }
                }
            }
            // All search
            else if (!eventName.equals("All events") && !tag.equals("All tags") && !browser.equals("All browsers")){
                if (e.getName().equals(eventName)){
                    for (Tag t : e.getTags()){
                        if (t.getName().equals(tag) && t.hasBrowser(browser)){
                            e.setSelectedTag(tag);
                            newEvents.add(e);
                            break;
                        }
                    }
                }
            }
        }
        return newEvents;
    }
    // Search all events by type and search term
    public List<Event> searchByTerm(String type, String term){
        term = term.toLowerCase();
        List<Event> newEvents = new ArrayList<>();

        for (Event e : data.events){
            switch (type){
                case "tag":
                    for (Tag t : e.getTags()){
                        if (t.getName().contains(term)){
                            e.setSelectedTag(e.getTags().get(0).getName());
                            newEvents.add(e);
                            break;
                        }
                    }
                    break;
                case "event":
                    if (e.getName().contains(term)){
                        e.setSelectedTag(e.getTags().get(0).getName());
                        newEvents.add(e);
                        break;
                    }
                    break;
                case "code":
                    for (Tag t : e.getTags()){
                        if (t.getCode().contains(term)){
                            e.setSelectedTag(e.getTags().get(0).getName());
                            newEvents.add(e);
                            break;
                        }
                    }
            }
        }

        return newEvents;
    }
    // Return action listener for copying payloads
    public ActionListener createActionListener(JTable table, int column, boolean urlEncode){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Copy code to clipboard
                String s = (String) table.getModel().getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), column);
                StringSelection selection;
                if (urlEncode){
                    selection = new StringSelection(URLEncoder.encode(s, StandardCharsets.UTF_8));
                }
                else{
                    selection = new StringSelection(s);
                }
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                updateRecentlyCopied(s);
            }
        };
    }
    // Return Abstract Action to enable copying multiple items
    public AbstractAction multipleCopyAction(JTable table, Integer column){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = table.getSelectedRows();
                String payloads = "";
                for (int row : rows){
                    payloads += table.getValueAt(table.convertRowIndexToModel(row), column) + "\n";
                    updateRecentlyCopied(table.getValueAt(table.convertRowIndexToModel(row), column).toString());
                }
                StringSelection selection = new StringSelection(payloads);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        };
    }
    // Return action listener for Event Handlers tab
    public ActionListener eventHandlersActionListener(EventsTableModel eventHandlerModel, JTable tblPayloads, boolean urlEncode){
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Copy code to clipboard
                Event event = eventHandlerModel.getEvent(tblPayloads.convertRowIndexToModel(tblPayloads.getSelectedRow()));
                StringSelection selection;
                if (urlEncode){
                    selection = new StringSelection(URLEncoder.encode(event.getSelectedTag().getCode(), StandardCharsets.UTF_8));
                }
                else{
                    selection = new StringSelection(event.getSelectedTag().getCode());
                }
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                updateRecentlyCopied(event.getSelectedTag().getCode());
            }
        };
    }
    // Return popup menu listener that automatically selects row
    public PopupMenuListener createPopUpMenuListener(JTable table, JPopupMenu menu){
        return new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(menu, new Point(0, 0), table));
                        if (rowAtPoint > -1) {
                            table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        };
    }
    // Load five most recently copied payloads
    public void loadRecentlyCopied(){
        // Get persisted setting
        List<String> recent = data.preferences.getSetting("recentlyCopied");
        // If empty list
        if (recent.size() == 0){
            // Populate with five payloads
            List<Event> events = searchByTerm("event", "ona").subList(0,5);
            for (Event e : events){
                data.recentlyCopied.add(e.getSelectedTag().getCode());
            }
        }
        else{
            data.recentlyCopied = recent;
        }
        saveRecentlyCopied();
    }
    // Save recently copied payloads to persisted setting
    public void saveRecentlyCopied(){
        List<String> recent = data.recentlyCopied;
        data.preferences.setSetting("recentlyCopied", recent);
    }
    // Update recently copied payloads
    public void updateRecentlyCopied(String code){
        // If code already exists
        if (data.recentlyCopied.contains(code)){
            // Remove code
            data.recentlyCopied.remove(code);
        }
        else{
            // Remove last item
            data.recentlyCopied.remove(data.recentlyCopied.size()-1);
        }
        // Add
        data.recentlyCopied.add(0, code);
        saveRecentlyCopied();
    }
}
