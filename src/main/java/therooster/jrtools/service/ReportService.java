package therooster.jrtools.service;


import net.sf.jasperreports.engine.JRException;
import org.springframework.web.multipart.MultipartFile;
import therooster.jrtools.dto.ReportDataRequest;
import therooster.jrtools.entity.ReportTemplate;

import java.io.IOException;

public interface ReportService {

    public ReportTemplate uploadTemplate(String tag, String description, MultipartFile file) throws IOException;

    public ReportTemplate updateTemplate(String tag, String description, MultipartFile file) throws IOException;

    public void deleteTemplate(String tag);


    public default String getExtension(String originlaFilename) {
        if (!originlaFilename.contains(".")) {
            throw new RuntimeException("Invalid filename format");
        }
        return originlaFilename.substring(originlaFilename.lastIndexOf("."));
    }

    boolean generateReportWithDto(ReportDataRequest reportData, String tag) throws JRException;


    // byte[] generateReport(String tag, JsonNode params) throws IOException, JRException;
}
