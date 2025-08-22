package therooster.jrtools;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalReportGenerator {

    public static void main(String[] args) {
        try {
            // Charger le fichier JRXML
            JasperReport jasperReport = JasperCompileManager.compileReport("/home/therooster/IdeaProjects/JRTools/src/main/resources/templates/MedvaultResumeReport.jrxml");

            // Préparer les paramètres
            Map<String, Object> parameters = new HashMap<>();

            // Paramètres simples
            parameters.put("IDCONSULTATION", "CONS-2023-001");
            parameters.put("IDPATIENT", "PAT-12345");
            parameters.put("prochain_rendez_vous", "15/12/2023 à 10h00");
            parameters.put("PATIENT_NAME", "DUPONT Jean");
            parameters.put("SEX", "Masculin");
            parameters.put("DATE_OF_BIRTH", "15/05/1980");
            parameters.put("NATIONALITY", "Française");
            parameters.put("REGION", "Île-de-France");
            parameters.put("RELIGION", "Catholicisme");
            parameters.put("PROFESSION", "Ingénieur");
            parameters.put("MARITAL_STATUS", "Marié");
            parameters.put("STUDY_LEVEL", "Bac+5");
            parameters.put("REFERENT_NAME", "Dr. Martin");
           // parameters.put("ImagePath", "/chemin/vers/logo.png");
            parameters.put("CARACTERISATION_SYMPTOME", "Douleur constrictive rétrosternale...");
            parameters.put("TRAITEMENTS_RECUS", "Aspirine 100mg/jour");
            parameters.put("ANTECEDENT_MEDICAL", "Hypertension artérielle");
            parameters.put("ANTECEDENT_CHIRURGICAL", "Appendicectomie en 2005");
            parameters.put("ALCOOL", "10");
            parameters.put("tabac", "5");
            parameters.put("autre_drogue", "Aucune");
            parameters.put("groupe_sanguin", "A");
            parameters.put("rhesus", "+");
            parameters.put("electrophorese", "Normale");
            parameters.put("hepatiteB", "Vacciné");
            parameters.put("autre_vaccin", "COVID-19, Tétanos");
            parameters.put("antecedent_ascendant", "Père: infarctus à 65 ans");
            parameters.put("antecedent_collateral", "Frère: hypertension");
            parameters.put("antecedent_descendant", "Aucun");
            parameters.put("oms", "1");
            parameters.put("paleur", "Non");
            parameters.put("ictere", "Non");
            parameters.put("deshydratation", "Non");
            parameters.put("poid_actuel", "75");
            parameters.put("poid_habituel", "78");
            parameters.put("taille", "1.75");
            parameters.put("imc", "24.5");
            parameters.put("ta", "130/80");
            parameters.put("pouls", "72");
            parameters.put("spO2", "98");
            parameters.put("bilan_biologique", "NFS, CRP, Troponine");
            parameters.put("bilan_morphologique", "ECG, Echocardiographie");
            parameters.put("bilan_fonctionnel", "Test d'effort");
            parameters.put("mesure_non_pharmacologique", "Régime hyposodé...");
            parameters.put("mesure_pharmacologique", "Aspirine 100mg, IEC");
            parameters.put("autre_traitement", "Suivi cardiologique mensuel");

             //DataSources pour les tableaux
           // parameters.put("motif_apparition_params", new JRBeanCollectionDataSource(getMotifApparitionList()));
            //parameters.put("motif_apparition_system_params", new JRBeanCollectionDataSource(getMotifApparitionSystemList()));
            //parameters.put("etat_propriete_physique", new JRBeanCollectionDataSource(getEtatProprietePhysiqueList()));
            //parameters.put("etat_propriete_diag_travail", new JRBeanCollectionDataSource(getEtatProprieteDiagTravailList()));
            //  parameters.put("etat_propriete_diag_associe", new JRBeanCollectionDataSource(getEtatProprieteDiagAssocieList()));
          //  parameters.put("etat_propriete_diag_terrain", new JRBeanCollectionDataSource(getEtatProprieteDiagTerrainList()));

            // Remplir le rapport avec une source de données vide (car le rapport utilise des paramètres)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // Afficher le rapport
            JasperViewer.viewReport(jasperPrint);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    // Méthodes pour créer les listes d'objets pour les DataSources
    private static List<MotifApparition> getMotifApparitionList() {
        List<MotifApparition> list = new ArrayList<>();
        list.add(new MotifApparition("Douleur thoracique", "Depuis 3 jours"));
        return list;
    }

    private static List<MotifApparition> getMotifApparitionSystemList() {
        List<MotifApparition> list = new ArrayList<>();
        list.add(new MotifApparition("Système cardiovasculaire", "Examen normal"));
        return list;
    }

    private static List<EtatPropriete> getEtatProprietePhysiqueList() {
        List<EtatPropriete> list = new ArrayList<>();
        list.add(new EtatPropriete("Coeur", "Rythme régulier, sans souffle"));
        return list;
    }

    // ... autres méthodes similaires pour les autres DataSources
}

// Classes POJO pour les DataSources
class MotifApparition {
    private String motif;
    private String temp_apparition;

    public MotifApparition(String motif, String temp_apparition) {
        this.motif = motif;
        this.temp_apparition = temp_apparition;
    }

    // Getters et setters
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getTemp_apparition() { return temp_apparition; }
    public void setTemp_apparition(String temp_apparition) { this.temp_apparition = temp_apparition; }
}

class EtatPropriete {
    private String etat;
    private String propriete;

    public EtatPropriete(String etat, String propriete) {
        this.etat = etat;
        this.propriete = propriete;
    }

    // Getters et setters
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public String getPropriete() { return propriete; }
    public void setPropriete(String propriete) { this.propriete = propriete; }
}