package com.kman.ui;

import com.kman.data.Data;
import com.kman.functions.Functions;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTab extends JPanel {
    Functions functions;
    Data data;
    public MainTab(Functions functions, Data data) {
        this.functions = functions;
        this.data = data;
    }
    public JComponent getUiComponent(){

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        // Create list of tabs
        List<GenericTab> tabs = new ArrayList<>();

        // Add tabs
        EventHandlersTab eventHandlersTab = new EventHandlersTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Event handlers")));
        tabs.add(eventHandlersTab);

        GenericTab consumingTagsTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Consuming tags")),
                null);
        tabs.add(consumingTagsTab);

        GenericTab fileUploadsTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("File upload attacks")),
                null);
        tabs.add(fileUploadsTab);

        GenericTab restrictedCharactersTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Restricted characters")),
                null);
        tabs.add(restrictedCharactersTab);

        GenericTab frameworksTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Frameworks")),
                null);
        tabs.add(frameworksTab);

        GenericTab protocolsTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Protocols")),
                null);
        tabs.add(protocolsTab);

        GenericTab usefulAttributesTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Other useful attributes")),
                null);
        tabs.add(usefulAttributesTab);

        GenericTab specialTagsTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Special tags")),
                null);
        tabs.add(specialTagsTab);

        GenericTab encodingTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Encoding")),
                null);
        tabs.add(encodingTab);

        GenericTab obfuscationTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Obfuscation")),
                null);
        tabs.add(obfuscationTab);

        CSTITab CSTITab = new CSTITab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Client-side template injection (VueJS)"), functions.loadData("Client-side template injection (AngularJS)")),
                new ArrayList<>(Arrays.asList("Software", "Version", "Author", "Length", "Vector", "CSP Bypass")));
        tabs.add(CSTITab);

        GenericTab scriptlessAttacksTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Scriptless attacks")),
                null);
        tabs.add(scriptlessAttacksTab);

        GenericTab polyglotsTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Polyglots")),
                null);
        tabs.add(polyglotsTab);

        GenericTab wafBypassTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("WAF bypass global objects")),
                null);
        tabs.add(wafBypassTab);

        GenericTab contentTypesTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Content types")),
                new ArrayList<>(Arrays.asList("Browsers", "Content-Type", "PoC")));
        tabs.add(contentTypesTab);

        GenericTab responseContentTypesTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Response content types")),
                new ArrayList<>(Arrays.asList("Browsers", "Content-Type", "PoC")));
        tabs.add(responseContentTypesTab);

        PrototypePollutionTab prototypePollutionTab = new PrototypePollutionTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Prototype pollution")),
                new ArrayList<>(Arrays.asList("Library", "Payload", "Author", "Version", "Fingerprint")));
        tabs.add(prototypePollutionTab);

        GenericTab classicVectorsTab = new GenericTab(new GridLayout(0,1),
                functions,
                data,
                Arrays.asList(functions.loadData("Classic vectors (XSS crypt)")),
                null);
        tabs.add(classicVectorsTab);

        // Add tabs to tabbed pane
        tabbedPane.add("Event Handlers", eventHandlersTab.getUiComponent());
        tabbedPane.add("Consuming tags", consumingTagsTab.getUiComponent());
        tabbedPane.add("File upload attacks", fileUploadsTab.getUiComponent());
        tabbedPane.add("Restricted characters", restrictedCharactersTab.getUiComponent());
        tabbedPane.add("Frameworks", frameworksTab.getUiComponent());
        tabbedPane.add("Protocols", protocolsTab.getUiComponent());
        tabbedPane.add("Other useful attributes", usefulAttributesTab.getUiComponent());
        tabbedPane.add("Special tags", specialTagsTab.getUiComponent());
        tabbedPane.add("Encoding", encodingTab.getUiComponent());
        tabbedPane.add("Obfuscation", obfuscationTab.getUiComponent());
        tabbedPane.add("Client-side template injection", CSTITab.getUiComponent());
        tabbedPane.add("Scriptless attacks", scriptlessAttacksTab.getUiComponent());
        tabbedPane.add("Polyglots", polyglotsTab.getUiComponent());
        tabbedPane.add("WAF bypass global objects", wafBypassTab.getUiComponent());
        tabbedPane.add("Content types", contentTypesTab.getUiComponent());
        tabbedPane.add("Response content types", responseContentTypesTab.getUiComponent());
        tabbedPane.add("Prototype pollution", prototypePollutionTab.getUiComponent());
        tabbedPane.add("Classic vectors (XSS crypt)", classicVectorsTab.getUiComponent());

        // Load and update tab data
        data.logging.logToOutput("Attempting to update...");
        for (GenericTab tab : tabs){
            tab.loadData();
            tab.update();
        }
        functions.loadRecentlyCopied();
        return tabbedPane;
    }
}
