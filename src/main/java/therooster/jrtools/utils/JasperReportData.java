package therooster.jrtools.utils;

import lombok.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JasperReportData {

    private Map<String, Object> parameters = new HashMap<>();
    private  Map<String, JRMapCollectionDataSource> dataSources = new HashMap<>();




    ///  Methode qui permet d'ajouter des paramètres
    public  void addParameter(String name, Object value) {
        parameters.put(name, value);
    }


    /// Methode pour ajouter les datasources
    public  void addParameters(Map<String, Object> parameters) {
        this.parameters.putAll(parameters);
    }

    ///  Methode pour ajouter des datasources
    public  void addDataSource(String key, JRMapCollectionDataSource dataSource) {
        dataSources.put(key, dataSource);
    }

    public  void addDatSource(String key, List<Map<String, ?>> data){
        dataSources.put(key, new JRMapCollectionDataSource(data));
    }

    ///  methode pour recuperer les paramètres combinees
    public Map<String, Object> getCombineParameters() {
        Map<String, Object> combined = new HashMap<>(parameters);
        combined.putAll(dataSources);
        return combined;
    }





}
