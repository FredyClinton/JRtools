package therooster.jrtools.service.impl;


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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {
    private final TemplateRepository templateRepository;

    @Value("${app.template.dir}")
    private String templateDir;

    @Value("${app.report.dir}")
    private String reportDir;

    private static Map<String, Object> getJasperParameters(ReportDataRequest reportData) {
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
        return jasperParameters;
    }

    @Override
    public ReportTemplate updateTemplate(String tag, String description, MultipartFile newFile) {

        ReportTemplate saveTemplate = this.templateRepository.findByTag(tag)
                .orElseThrow(() -> new TemplateNotFoundException("template with name " + tag + " not found"));

        String filePath = templateDir + tag + getExtension(Objects.requireNonNull(newFile.getOriginalFilename()));
        // TODO : Controller les informations fournis

        saveTemplate.setDescription(description);
        saveTemplate.setTag(tag);
        saveTemplate.setDirectory(filePath);

        return this.templateRepository.save(saveTemplate);
    }

    @Override
    public ReportTemplate uploadTemplate(String tag, String description, MultipartFile file) throws IOException {
        if (templateRepository.existsByTag(tag)) {
            throw new RemoteException("template with this name already exists");
        }

        // creer un dossier
        File dir = new File(templateDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // sauvegarde du dossier
        String filePath = Paths.get(templateDir, tag + getExtension(Objects.requireNonNull(file.getOriginalFilename()))).toString();
        File destination = new File(filePath);

        // log clair
        System.out.println("Saving template to: " + destination.getAbsolutePath());

        file.transferTo(destination.getAbsoluteFile());


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
                .orElseThrow(() -> new TemplateNotFoundException("template with name " + tag + " not found"));

        new File(saveTemplate.getDirectory()).delete();
        // TODO : Gestion d'erreurs
        this.templateRepository.delete(saveTemplate);

    }

    @Override
    public boolean generateReportWithDto(ReportDataRequest reportData, String tag) throws JRException {
        ReportTemplate template = this.templateRepository.findByTag(tag)
                .orElseThrow(() -> new TemplateNotFoundException("template with name " + tag + " not found"));
        String templatePath = template.getDirectory();


        JasperReport jasperReport;
        if (getExtension(templatePath).contains("jrxml")) {
            jasperReport = JasperCompileManager.compileReport(templatePath);
            System.out.println("Chargement du template" + templatePath);
        } else {
            jasperReport = (JasperReport) JRLoader.loadObject(new File(templatePath));
            System.out.println("Chargement du template" + templatePath);
        }

        // Récupération des paramètres simples pour le rapport
        Map<String, Object> jasperParameters = getJasperParameters(reportData);


        // Remplir le rapport
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, jasperParameters, new JREmptyDataSource());

        System.out.println("Exportation du rapport");
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        // Save

        try {

            // Créer le dossier s'il n'existe pas
            File directory = new File(reportDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Chemin complet du fichier
            String filePath = reportDir + tag + ".pdf";

            // Écrire le fichier
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(pdfBytes);
            }

            System.out.println("Rapport sauvegardé : " + filePath);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du rapport", e);
        }


    }


}