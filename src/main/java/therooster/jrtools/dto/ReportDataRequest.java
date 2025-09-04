package therooster.jrtools.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ReportDataRequest {
    private Map<String, Object> parameters;
    private Map<String, List<Map<String, ?>>> dataSources;

    public ReportDataRequest() {
        parameters = new HashMap<>();
        dataSources = new HashMap<>();
    }


    // Méthode pour ajouter un paramètre simple (utilisée par la désérialisation JSON)
    @JsonAnySetter
    public void addParameter(String key, Object value) {
        // Si la valeur est une liste, c'est une datasource
        if (value instanceof List) {

            dataSources.put(key, (List<Map<String, ?>>) value);
            //parameters.put(key, value);
        } else {
            // Sinon, c'est un paramètre simple
            parameters.put(key, value);
        }
    }

    // Méthode pour récupérer tous les paramètres simples
    @JsonIgnore
    public Map<String, Object> getParameters() {
        return parameters;
    }

    // Méthode pour récupérer toutes les datasources
    @JsonIgnore
    public Map<String, List<Map<String, ?>>> getDataSources() {
        return dataSources;
    }

    // Méthode nécessaire pour la désérialisation JSON complète
    @JsonAnyGetter
    public Map<String, Object> getOtherFields() {
        Map<String, Object> allFields = new HashMap<>();
        allFields.putAll(parameters);
        allFields.putAll(dataSources);
        return allFields;
    }

    // Méthodes utilitaires pour accéder facilement aux données
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    public List<Map<String, ?>> getDataSource(String key) {
        return dataSources.get(key);
    }

    public boolean hasDataSource(String key) {
        return dataSources.containsKey(key);
    }

    public boolean hasParameter(String key) {
        return parameters.containsKey(key);
    }
}