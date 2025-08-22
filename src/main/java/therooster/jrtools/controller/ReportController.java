package therooster.jrtools.controller;

import lombok.RequiredArgsConstructor;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import therooster.jrtools.dto.ReportDataRequest;
import therooster.jrtools.dto.ReportRequest;
import therooster.jrtools.entity.ReportTemplate;
import therooster.jrtools.service.impl.ReportServiceImpl;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class ReportController {
    private final ReportServiceImpl reportService;


    @Value("${app.template.dir}")
    private  String templateDir ;

    @PostMapping("/upload")
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
        return  ResponseEntity.created(templateUri)
                .body(body);

    }


    @PutMapping("/{tag}")
    public ResponseEntity<?> updateTemplate(
            @PathVariable String tag,
            @RequestParam String description,
            @RequestParam MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok(this.reportService.updateTemplate(tag,description, file));
    }


    @DeleteMapping("/{tag}")
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




    @GetMapping("/download/{tag}")
    public ResponseEntity<Resource> downloadTemplate(@PathVariable String tag) throws IOException {

           Path filePath = Paths.get(templateDir, tag + ".jrxml"); // ou .jrxml
            if (!Files.exists(filePath.toAbsolutePath())) {


                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(filePath.toAbsolutePath().toFile());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

    }

    @PostMapping("/generate/{tag}")
    public ResponseEntity<?> test(@RequestBody ReportDataRequest params, @PathVariable String tag) throws JRException {

        byte[] pdf = this.reportService.generateReportWithDto(params, tag);
        System.out.println(Arrays.toString(pdf));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachement; filename=" + tag + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }





}
