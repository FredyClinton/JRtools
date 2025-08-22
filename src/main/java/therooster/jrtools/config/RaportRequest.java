package therooster.jrtools.config;

import java.util.Collections;

import java.util.List;
import java.util.Map;

public class RaportRequest {
    byte[] templateContent;  // Contenu binaire du template JRXML ou JASPER
    Map<String, Object> parameters;
    Map<String, List<Map<String, ?>>> datasources;

    // Getters et Setters
    public byte[] templateContent() {
        return templateContent;
    }

    public Map<String, Object> parameters() {
        return parameters != null ? parameters : Collections.emptyMap();
    }

    public Map<String, List<Map<String, ?>>> datasources() {
        return datasources != null ? datasources : Collections.emptyMap();
    }




}
