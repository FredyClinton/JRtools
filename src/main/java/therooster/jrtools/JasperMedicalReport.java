package therooster.jrtools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class JasperMedicalReport {

    public static void main(String[] args) throws Exception {
        // JSON médical
        String jsonInput = """
        {
          "IDCONSULTATION": "CONS-2023-001",
          "IDPATIENT": "PAT-12345",
          "PATIENT_NAME": "DUPONT Jean",
          "SEX": "Masculin",
          "DATE_OF_BIRTH": "15/05/1980",
          "NATIONALITY": "Française",
          "REGION": "Île-de-France",
          "RELIGION": "Catholicisme",
          "PROFESSION": "Ingénieur",
          "MARITAL_STATUS": "Marié",
          "STUDY_LEVEL": "Bac+5",
          "REFERENT_NAME": "Dr. Martin",
          "CARACTERISATION_SYMPTOME": "Douleur constrictive rétrosternale, irradiant au bras gauche",
          "TRAITEMENTS_RECUS": "Aspirine 100mg/jour",
          "ANTECEDENT_MEDICAL": "Hypertension artérielle",
          "ANTECEDENT_CHIRURGICAL": "Appendicectomie en 2005",
          "ALCOOL": "10",
          "tabac": "5",
          "autre_drogue": "Aucune",
          "groupe_sanguin": "A",
          "rhesus": "+",
          "electrophorese": "Normale",
          "hepatiteB": "Vacciné",
          "autre_vaccin": "COVID-19, Tétanos",
          "antecedent_ascendant": "Père: infarctus à 65 ans",
          "antecedent_collateral": "Frère: hypertension",
          "antecedent_descendant": "Aucun",
          "oms": "1",
          "paleur": "Non",
          "ictere": "Non",
          "deshydratation": "Non",
          "poid_actuel": "75",
          "poid_habituel": "78",
          "taille": "1.75",
          "imc": "24.5",
          "ta": "130/80",
          "pouls": "72",
          "spO2": "98",
          "bilan_biologique": "NFS, CRP, Troponine",
          "bilan_morphologique": "ECG, Echocardiographie",
          "bilan_fonctionnel": "Test d'effort",
          "mesure_non_pharmacologique": "Régime hyposodé, activité physique modérée",
          "mesure_pharmacologique": "Aspirine 100mg, IEC",
          "autre_traitement": "Suivi cardiologique mensuel",
          "motif_apparition_params": [
            {"motif": "Douleur thoracique", "temp_apparition": "Depuis 3 jours"},
            {"motif": "Douleur à la tete", "temp_apparition": "Depuis 1 jours"},
            {"motif": "Diahrée", "temp_apparition": "Depuis 5 jours"}
          ],
          "etat_propriete_physique": [
            {"etat": "Coeur", "propriete": "Rythme régulier, sans souffle"},
            {"etat": "Tete", "propriete": "Gros"}
          ],
          "etat_propriete_diag_travail": [
            {"etat": "Suspicion", "propriete": "Angor stable"}
          ],
          "etat_propriete_diag_associe": [
            {"etat": "Confirmé", "propriete": "Hypertension artérielle"}
          ],
          "etat_propriete_diag_terrain": [
            {"etat": "Facteur de risque", "propriete": "Antécédents familiaux"}
          ]
        }
        """;

        // Parser le JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonInput);

        // Paramètres simples
        Map<String, Object> parameters = new HashMap<>();
        List<String>  champsDatasources = new ArrayList<>();
        root.fieldNames().forEachRemaining(field -> {
            JsonNode node = root.get(field);
            if (!node.isArray()) { // si ce n'est pas une collection
                parameters.put(field, node.asText());
            }

            if(node.isArray()){
                champsDatasources.add(field);
            }
        });

        // Collections JRMapCollectionDataSource
        Map<String, JRMapCollectionDataSource> dataSources = new HashMap<>();


        for (String collName : champsDatasources) {
            JsonNode arrayNode = root.get(collName);
            if (arrayNode != null) {
                List<Map<String, ?>> rows = new ArrayList<>();

                arrayNode.forEach(objNode -> {
                    Map<String, Object> item = new HashMap<>();

                    objNode.fieldNames().forEachRemaining(field -> item.put(field, objNode.get(field).asText()));
                    rows.add(item);
                });


                // cree le DataSource
                dataSources.put(collName, new JRMapCollectionDataSource(rows));
            }
        }
        // Ajouter les datasources aux paramètres
        parameters.putAll(dataSources);

        // Compiler le template
        JasperReport jasperReport = JasperCompileManager.compileReport("/home/therooster/IdeaProjects/JRTools/src/main/resources/templates/MedvaultResumeReport.jrxml");

        // ⚡ Ici, exemple : utiliser "motif_apparition_params" comme datasource principale
        //JRMapCollectionDataSource mainDataSource = dataSources.get("motif_apparition_params");

        // Remplir le rapport
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        // Export PDF
        JasperExportManager.exportReportToPdfStream(jasperPrint, new FileOutputStream(new File("rapport_medical_2.pdf")));

        System.out.println("✅ Rapport généré : rapport_medical.pdf");
    }
}

