package therooster.jrtools.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import net.sf.jasperreports.engine.*;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import therooster.jrtools.dto.ReportDataRequest;
import therooster.jrtools.entity.ReportTemplate;
import therooster.jrtools.exception.TemplateNotFoundException;
import therooster.jrtools.repository.TemplateRepository;
import therooster.jrtools.service.ReportService;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.rmi.RemoteException;


import java.util.*;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl    implements ReportService {
    private final TemplateRepository templateRepository;

    @Value("${app.template.dir}")
    private  String templateDir ;
    @Override
    public ReportTemplate updateTemplate(String tag, String description, MultipartFile newFile) throws IOException {

        ReportTemplate saveTemplate = this.templateRepository.findByTag(tag)
                .orElseThrow(() -> new TemplateNotFoundException("template with name " +tag + " not found"));

        String filePath = templateDir + tag + getExtension(Objects.requireNonNull(newFile.getOriginalFilename()));
       // TODO : Controller les informations fournis

        saveTemplate.setDescription(description);
        saveTemplate.setTag(tag);
        saveTemplate.setDirectory(filePath);

        return this.templateRepository.save(saveTemplate);
    }



    @Override
    public ReportTemplate uploadTemplate(String tag, String description, MultipartFile file) throws IOException {
        if (templateRepository.existsByTag(tag)){
            throw  new RemoteException("template with this name already exists");
        }

        // creer un dossier
        File dir = new File(templateDir);
        if (!dir.exists()){
            dir.mkdirs();
        }

        // sauvegarde du dossier
        String filePath = Paths.get(templateDir, tag + getExtension(Objects.requireNonNull(file.getOriginalFilename()))).toString();
        File destination = new File(filePath);

        // log clair
        System.out.println("Saving template to: " + destination.getAbsolutePath());

        file.transferTo(destination.getAbsoluteFile());

        //System.out.println(destination.getAbsolutePath());
       // file.transferTo(destination.getAbsoluteFile());

        // sauvegarde en BD

        ReportTemplate reportTemplate = new ReportTemplate();
        reportTemplate.setDescription(description);
        reportTemplate.setTag(tag);
        reportTemplate.setDirectory(filePath);

        return this.templateRepository.save(reportTemplate);
    }

    @Override
    public void deleteTemplate(String tag) {
        ReportTemplate saveTemplate = this.templateRepository.findByTag(tag)
                .orElseThrow(() -> new TemplateNotFoundException("template with name " +tag + " not found"));

        new File(saveTemplate.getDirectory()).delete();
        // TODO : Gestion d'ereeurs
        this.templateRepository.delete(saveTemplate);

    }


    private Map<String, Object> getParamsAndDataSourceFromJson(String jsonInput ) throws JsonProcessingException {
        // Parser le JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonInput);

        // Paramètres simples
        Map<String, Object> parameters = new HashMap<>();
        // les fieds qui sont des datasuorces
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

        return  parameters;
    }
    


    @Override
    public byte[] generateReportWithDto(ReportDataRequest reportData, String tag) throws JRException {
        ReportTemplate template = this.templateRepository.findByTag(tag)
                .orElseThrow(() -> new TemplateNotFoundException("template with name " +tag + " not found"));
        String templatePath = template.getDirectory();


        JasperReport jasperReport = null;
        if(getExtension(templatePath).contains("jrxml")){
            jasperReport = JasperCompileManager.compileReport(templatePath);
            System.out.println("Chargement du template" +  templatePath);
        }
        else {
            jasperReport = (JasperReport) JRLoader.loadObject(new File(templatePath));
            System.out.println("Chargement du template" +  templatePath);
        }

        // Récupération des paramètres simples pour le rapport
        Map<String, Object> reportParameters = reportData.getParameters();

        // Récupération des datasources pour les tableaux
        Map<String, List<Map<String, ?>>> dataSources = reportData.getDataSources();

        // Préparation des parameters Jasper
        Map<String, Object> jasperParameters = new HashMap<>();
        jasperParameters.putAll(reportParameters);

        // Ajout des datasources aux parameters Jasper
        for (Map.Entry<String, List<Map<String, ?>>> entry : dataSources.entrySet()) {
            String dataSourceName = entry.getKey();
            List<Map<String, ?>> data = entry.getValue();

            // Création du JRMapCollectionDataSource
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(data);
            jasperParameters.put(dataSourceName, dataSource);
        }


        // Remplir le rapport
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameters, new JREmptyDataSource());

        System.out.println("Exportation du rapport");
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        return  pdfBytes;

    }

    /*
    @Override
    public byte[] generateReport(String tag, String  params) throws IOException, JRException {
        ReportTemplate template = this.templateRepository.findByTag(tag)
                .orElseThrow(() -> new TemplateNotFoundException("template with name " +tag + " not found"));
        String templatePath = template.getDirectory();


        JasperReport jasperReport = null;
        if(getExtension(templatePath).contains("jrxml")){
            jasperReport = JasperCompileManager.compileReport(templatePath);
            System.out.println("Chargement du template" +  templatePath);
        }
        else {
            jasperReport = (JasperReport) JRLoader.loadObject(new File(templatePath));
            System.out.println("Chargement du template" +  templatePath);
        }

        // chargement des paramnètres simples:

        Map<String, Object> parameters = getParamsAndDataSourceFromJson(params);

        // Remplir le rapport
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        System.out.println("Exportation du rapport");
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
        return  pdfBytes;

    }
*/

}