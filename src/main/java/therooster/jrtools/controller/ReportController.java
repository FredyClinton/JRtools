package therooster.jrtools.controller;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import therooster.jrtools.dto.ReportDataRequest;
import therooster.jrtools.service.impl.ReportServiceImpl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReportController {
    private final ReportServiceImpl reportService;


    @Value("${app.template.dir}")
    private String templateDir;
    @Value("${app.report.dir}")
    private String reportsDirectory;

    @PostMapping("/templates/upload")
    public ResponseEntity<?> upload(
            @RequestParam String tag,
            @RequestParam String description,
            @RequestParam("file") MultipartFile file,
            UriComponentsBuilder ucb)
            throws IOException {


        var body = this.reportService.uploadTemplate(tag, description, file);
        URI templateUri = ucb
                .path("api/templates/download/{tag}")
                .buildAndExpand(tag)
                .toUri();
        return ResponseEntity.created(templateUri)
                .body(body);

    }


    @PutMapping("/templates/{tag}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable String tag,
            @RequestParam String description,
            @RequestParam MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(this.reportService.updateTemplate(tag, description, file));
    }


    @DeleteMapping("/templates/{tag}")
    public ResponseEntity<?> deleteTemplate(@PathVariable String tag) {
        this.reportService.deleteTemplate(tag);
        return ResponseEntity.ok("Template supprimé avec succès");
    }


    /*
    @PostMapping("/generate/{tag}")
    public ResponseEntity<?> generateReport(
            @PathVariable String tag,
            @RequestBody String params
    ) throws Exception {
        byte[] pdf = this.reportService.generateReport(tag, params);
        System.out.println(Arrays.toString(pdf));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=" + tag + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    */


    @GetMapping("/templates/download/{tag}")
    public ResponseEntity<Resource> downloadTemplate(@PathVariable String tag) throws IOException {

        Path filePath = Paths.get(templateDir, tag + ".jrxml"); // ou .jrxml
        Resource resource = getResourceResponseEntity(filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

    }

    private Resource getResourceResponseEntity(Path filePath) {
        if (!Files.exists(filePath.toAbsolutePath())) {


            return null;
        }

        return new FileSystemResource(filePath.toAbsolutePath().toFile());


    }

    @PostMapping("/report/generate/{tag}")
    public ResponseEntity<String> generateReport(@RequestBody ReportDataRequest params, @PathVariable String tag) throws JRException {

        boolean pdfCreatedAndSaved = this.reportService.generateReportWithDto(params, tag);

        if (!pdfCreatedAndSaved) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body("rapport generé avec succes");
    }


    @GetMapping("/report/download/{tag}")
    public ResponseEntity<Resource> downloadReport(@PathVariable String tag) throws IOException {

        Path filePath = Paths.get(reportsDirectory, tag + ".pdf"); // ou .jrxml


        Resource resource = getResourceResponseEntity(filePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);

    }


}
