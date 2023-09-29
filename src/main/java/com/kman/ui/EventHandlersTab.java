package com.kman.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kman.data.Data;
import com.kman.functions.Functions;
import com.kman.models.EventsTableModel;
import com.kman.objects.SavedResponse;
import com.kman.objects.Event;
import com.kman.objects.Tag;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

public class EventHandlersTab extends GenericTab{
    Functions functions;
    Data data;
    private List<SavedResponse> saved =  new ArrayList<>();
    public List<Event> events = new ArrayList<>();
    public DefaultTableModel tagModel = new DefaultTableModel(new Object[]{"Tags"}, 0);
    public DefaultTableModel eventModel = new DefaultTableModel(new Object[]{"Events"}, 0);
    public DefaultTableModel browserModel = new DefaultTableModel(new Object[]{"Browsers"}, 0);
    public EventsTableModel eventHandlerModel;

    public EventHandlersTab(LayoutManager layout, Functions functions, Data data, List<SavedResponse> saved) {
        super(layout, functions, data, saved, null);
        this.functions = functions;
        this.data = data;
        this.saved = saved;
        this.events = data.events;
        this.eventHandlerModel = new EventsTableModel(this.events);
        browserModel.addRow(new Object[] {"All browsers"});
        browserModel.addRow(new Object[] {"Chrome"});
        browserModel.addRow(new Object[] {"Firefox"});
        browserModel.addRow(new Object[] {"Safari"});
    }
    public JComponent getUiComponent(){
        // Create split pane
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setAutoscrolls(true);
        splitPane.setDividerLocation(300);
        this.add(splitPane);

        // Top panel
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Tag table
        JScrollPane tagScrollPane = new JScrollPane();
        JTable tblTags = new JTable();
        tblTags.setModel(tagModel);
        tagScrollPane.setViewportView(tblTags);

        // Event table
        JScrollPane eventScrollPane = new JScrollPane();
        JTable tblEvents = new JTable();
        tblEvents.setModel(eventModel);
        eventScrollPane.setViewportView(tblEvents);

        // Browser table
        JScrollPane browserScrollPane = new JScrollPane();
        JTable tblBrowsers = new JTable();
        tblBrowsers.setModel(browserModel);
        browserScrollPane.setViewportView(tblBrowsers);

        // Add tables with constraints
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.95;
        c.gridx = 1;
        c.gridy = 0;
        panelTop.add(tagScrollPane, c);
        c.gridx = 2;
        panelTop.add(eventScrollPane, c);
        c.gridx = 3;
        panelTop.add(browserScrollPane, c);

        // Search panel
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 3;
        c.weighty = 0.0;
        panelTop.add(pnlSearch, c);

        JLabel lblSearch = new JLabel("Search Type:");
        pnlSearch.add(lblSearch);

        // Drop down menu
        JComboBox<String> cmbSearch = new JComboBox();
        cmbSearch.addItem("tag");
        cmbSearch.addItem("event");
        cmbSearch.addItem("code");
        pnlSearch.add(cmbSearch);

        JLabel lblSearchTerm = new JLabel("Search Term:");
        pnlSearch.add(lblSearchTerm);

        // Search text field
        JTextField txtSearch = new JTextField(20);
        pnlSearch.add(txtSearch);
        txtSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Search on enter
                eventHandlerModel.setEvents(functions.searchByTerm(cmbSearch.getSelectedItem().toString(), txtSearch.getText()));
            }
        });

        // Search button
        JButton btnSearch = new JButton("Search");
        pnlSearch.add(btnSearch);
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Search on click
                eventHandlerModel.setEvents(functions.searchByTerm(cmbSearch.getSelectedItem().toString(), txtSearch.getText()));
            }
        });

        // Credit & hyperlink
        JLabel credit = new JLabel("All credit to PortSwigger Research:");
        pnlSearch.add(credit);

        JLabel hyperlink = new JLabel("https://portswigger.net/web-security/cross-site-scripting/cheat-sheet");
        hyperlink.setForeground(Color.CYAN.darker());
        hyperlink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hyperlink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://portswigger.net/web-security/cross-site-scripting/cheat-sheet"));

                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                // the mouse has entered the label
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // the mouse has exited the label
            }
        });
        pnlSearch.add(hyperlink);

        // Add top panel to split pane
        splitPane.setTopComponent(panelTop);

        // Top tables listener
        ListSelectionListener listener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (event.getValueIsAdjusting()){
                    return;
                }
                if (tblTags.getSelectedRow() == -1){
                    tblTags.setRowSelectionInterval(0, 0);
                    return;
                }
                if (tblEvents.getSelectedRow() == -1){
                    tblEvents.setRowSelectionInterval(0, 0);
                    return;
                }
                if (tblBrowsers.getSelectedRow() == -1){
                    tblBrowsers.setRowSelectionInterval(0, 0);
                    return;
                }

                // Get currently selected rows
                String tag = (String) tagModel.getValueAt(tblTags.getSelectedRow(),0);
                String evt = (String) eventModel.getValueAt(tblEvents.getSelectedRow(), 0);
                String browser = (String) browserModel.getValueAt(tblBrowsers.getSelectedRow(), 0);

                // Update model based on selected rows
                eventHandlerModel.setEvents(functions.searchByElement(tag, evt, browser));
            }
        };

        // Add listener to all tables
        tblTags.getSelectionModel().addListSelectionListener(listener);
        tblEvents.getSelectionModel().addListSelectionListener(listener);
        tblBrowsers.getSelectionModel().addListSelectionListener(listener);

        // Create bottom panel
        JScrollPane scrollPane = new JScrollPane();
        splitPane.setBottomComponent(scrollPane);

        // Create main table
        JTable tblPayloads = new JTable(){
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

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                // Set tags column combobox
                if (column == 3){
                    Event event = (Event) eventHandlerModel.getEvent(convertRowIndexToModel(row));
                    JComboBox<String> cmbBox = new JComboBox<String>();
                    for (Tag t: event.getTags()){
                        cmbBox.addItem(t.getName());
                    }
                    return new DefaultCellEditor(cmbBox);
                }
                return super.getCellEditor(row, column);
            }
        };

        // Disallow reordering
        tblPayloads.getTableHeader().setReorderingAllowed(false);

        // Set table model
        tblPayloads.setModel(eventHandlerModel);

        // Add table to pane
        scrollPane.setViewportView(tblPayloads);

        // Column config
        // Center interaction column text
        DefaultTableCellRenderer interactionRenderer = new DefaultTableCellRenderer();
        interactionRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblPayloads.getColumnModel().getColumn(5).setCellRenderer(interactionRenderer);

        // Sort table by interaction column, then event column
        TableRowSorter<EventsTableModel> sorter = new TableRowSorter<EventsTableModel>(eventHandlerModel);
        tblPayloads.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(5, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        // Column sizes
        tblPayloads.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tblPayloads.getColumnModel().getColumn(0).setMinWidth(90);
        tblPayloads.getColumnModel().getColumn(0).setMaxWidth(90);
        tblPayloads.getColumnModel().getColumn(1).setMinWidth(120);
        tblPayloads.getColumnModel().getColumn(1).setMaxWidth(200);
        tblPayloads.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPayloads.getColumnModel().getColumn(2).setMinWidth(200);
        tblPayloads.getColumnModel().getColumn(2).setMaxWidth(800);
        tblPayloads.getColumnModel().getColumn(2).setPreferredWidth(250);
        tblPayloads.getColumnModel().getColumn(3).setMinWidth(90);
        tblPayloads.getColumnModel().getColumn(3).setMaxWidth(150);
        tblPayloads.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblPayloads.getColumnModel().getColumn(4).setCellRenderer(new WordWrapCellRenderer());// Wrap text in code column
        tblPayloads.getColumnModel().getColumn(5).setMinWidth(90);
        tblPayloads.getColumnModel().getColumn(5).setMaxWidth(90);

        // Create popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copy = new JMenuItem("Copy");
        copy.setIcon(data.copyIcon);
        JMenuItem copyURL = new JMenuItem("Copy (URL)");
        copyURL.setIcon(data.copyIcon);
        // Add action listeners
        copy.addActionListener(functions.eventHandlersActionListener(eventHandlerModel, tblPayloads, false));
        copyURL.addActionListener(functions.eventHandlersActionListener(eventHandlerModel, tblPayloads, true));
        // Add items
        popupMenu.add(copy);
        popupMenu.add(copyURL);

        // Select row on right click
        popupMenu.addPopupMenuListener(functions.createPopUpMenuListener(tblPayloads, popupMenu));

        // Add menu to table
        tblPayloads.setComponentPopupMenu(popupMenu);

        // Enable multiple copy on ctrl+c
        tblPayloads.getActionMap().put("copy", new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int[] rows = tblPayloads.getSelectedRows();
                List<Event> events = new ArrayList<>();
                for (int row : rows){
                    events.add(eventHandlerModel.getEvent(tblPayloads.convertRowIndexToModel(row)));
                }
                String codes = "";
                for (Event event : events){
                    codes += event.getSelectedTag().getCode() + "\n";
                    functions.updateRecentlyCopied(event.getSelectedTag().getCode());
                }
                StringSelection selection = new StringSelection(codes);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });
        return this;
    }
    // Load JSON from SavedResponses
    public void loadData() {
        // Iterate SavedResponses
        for (SavedResponse response : saved){
            // If blank then update
            if (response.response.equals("")){
                return;
            }
            // Parse JSON
            data.events.clear();
            // Parse response
            JsonObject jsonObject = new JsonParser().parse(response.response).getAsJsonObject();
            // Populate tags model
            this.tagModel.setRowCount(0);
            functions.getTagNames(jsonObject, this.tagModel);
            // Iterate over json
            for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                // Get event name
                String name = e.getKey();
                // Get description and tags
                JsonObject element = jsonObject.getAsJsonObject(name);
                String description = element.get("description").getAsString();
                JsonArray tags = element.get("tags").getAsJsonArray();
                // Add new event
                data.events.add(new Event(data, name, description, tags));
            }
            // Populate events table
            functions.getEventNames(eventModel);
            // Fire data changed
            eventHandlerModel.fireTableDataChanged();
        }
    }
}
