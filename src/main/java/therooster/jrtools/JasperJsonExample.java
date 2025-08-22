package therooster.jrtools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class JasperJsonExample {

    public static void main(String[] args) throws Exception {
        // Exemple de JSON (normalement reçu via une API ou un fichier)
        String jsonInput = """
        {
          "parameters": {
            "reportTitle": "Rapport des ventes",
            "author": "Frédy"
          },
          "datasources": {
            "mainData": [
              {"product": "Laptop", "price": 800, "quantity": 2},
              {"product": "Phone", "price": 500, "quantity": 5},
              {"product": "Tablet", "price": 300, "quantity": 3}
            ]
          }
        }
        """;

        // Parser le JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonInput);

        // Récupérer les paramètres
        Map<String, Object> parameters = new HashMap<>();
        root.get("parameters").fields().forEachRemaining(entry -> {
            parameters.put(entry.getKey(), entry.getValue().asText());
        });

        // Récupérer les datasources (on suppose qu'il peut y en avoir plusieurs)
        Map<String, JRMapCollectionDataSource> dataSources = new HashMap<>();
        JsonNode datasourcesNode = root.get("datasources");
        datasourcesNode.fieldNames().forEachRemaining(dsName -> {
            JsonNode arrayNode = datasourcesNode.get(dsName);
            List<Map<String, ?>> list = new ArrayList<>();

            arrayNode.forEach(objNode -> {
                Map<String, Object> map = new HashMap<>();
                objNode.fieldNames().forEachRemaining(field -> {
                    map.put(field, objNode.get(field).asText()); // tout en String pour simplifier
                });
                list.add(map);
            });

            dataSources.put(dsName, new JRMapCollectionDataSource(list));
        });

        // Compiler le template JRXML
        JasperReport jasperReport = JasperCompileManager.compileReport("/home/therooster/IdeaProjects/JRTools/src/main/java/therooster/jrtools/rapport_consultation.jrxml");

        // ⚡ Ici on suppose que le rapport principal utilise une datasource nommée "mainData"
        JRMapCollectionDataSource mainDataSource = dataSources.get("mainData");

        // Remplir le rapport
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, mainDataSource);

        // Exporter en PDF
        JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(new File("rapport.pdf")));

        System.out.println("✅ Rapport généré : rapport.pdf");
    }
}
