package com.kman.data;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import com.coreyd97.BurpExtenderUtilities.DefaultGsonProvider;
import com.coreyd97.BurpExtenderUtilities.Preferences;
import com.kman.objects.Event;
import com.kman.objects.SavedResponse;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Data {
    public MontoyaApi api;
    public Logging logging;
    public Preferences preferences;
    public List<SavedResponse> saved = new ArrayList<>();
    public List<String> recentlyCopied = new ArrayList<>();
    public List<Event> events = new ArrayList<>();
    public List<String> tagNames = new ArrayList<>();
    public List<String> eventNames = new ArrayList<>();
    public ImageIcon chromeIcon = new ImageIcon(getClass().getClassLoader().getResource("chrome.png"));
    public ImageIcon firefoxIcon = new ImageIcon(getClass().getClassLoader().getResource("firefox.png"));
    public ImageIcon safariIcon = new ImageIcon(getClass().getClassLoader().getResource("safari.png"));
    public ImageIcon copyIcon = new ImageIcon(getClass().getClassLoader().getResource("copy.png"));

    public Data(MontoyaApi api, Logging logging) {
        this.api = api;
        this.logging = logging;
        this.preferences = new Preferences(api, new DefaultGsonProvider());
        saved.add(new SavedResponse("Event handlers", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/events.json", ""));
        saved.add(new SavedResponse("Consuming tags", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/consuming_tags.json", ""));
        saved.add(new SavedResponse("File upload attacks", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/file_uploads.json", ""));
        saved.add(new SavedResponse("Restricted characters", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/restricted_characters.json", ""));
        saved.add(new SavedResponse("Frameworks", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/frameworks.json", ""));
        saved.add(new SavedResponse("Protocols", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/protocols.json", ""));
        saved.add(new SavedResponse("Other useful attributes", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/useful_tags.json", ""));
        saved.add(new SavedResponse("Special tags", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/special_tags.json", ""));
        saved.add(new SavedResponse("Encoding", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/encodings.json", ""));
        saved.add(new SavedResponse("Obfuscation", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/obfuscation.json", ""));
        saved.add(new SavedResponse("Client-side template injection (VueJS)", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/vuejs.json", ""));
        saved.add(new SavedResponse("Client-side template injection (AngularJS)", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/angularjs.json", ""));
        saved.add(new SavedResponse("Scriptless attacks", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/dangling_markup.json", ""));
        saved.add(new SavedResponse("Polyglots", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/polyglot.json", ""));
        saved.add(new SavedResponse("WAF bypass global objects", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/waf_bypass_global_obj.json", ""));
        saved.add(new SavedResponse("Content types", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/content-types.json", ""));
        saved.add(new SavedResponse("Response content types", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/response-content-types.json", ""));
        saved.add(new SavedResponse("Prototype pollution", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/prototype-pollution.json", ""));
        saved.add(new SavedResponse("Classic vectors (XSS crypt)", "https://raw.githubusercontent.com/PortSwigger/xss-cheatsheet-data/master/json/classic.json", ""));
    }
}
