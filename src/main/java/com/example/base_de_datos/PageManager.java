package com.example.base_de_datos;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.util.HashMap;

public class PageManager {

    private static final HashMap<String, Pane> cache = new HashMap<>();

    public static Pane get(String path) {
        try {
            if (cache.containsKey(path)) {
                return cache.get(path);
            }

            FXMLLoader loader = new FXMLLoader(PageManager.class.getResource(path));
            Pane view = loader.load();

            cache.put(path, view);

            return view;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
