package therooster.jrtools.service;


import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import net.sf.jasperreports.engine.JRException;
import org.springframework.web.multipart.MultipartFile;
import therooster.jrtools.dto.ReportDataRequest;
import therooster.jrtools.entity.ReportTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public interface ReportService {

    public ReportTemplate uploadTemplate(String tag, String description, MultipartFile file) throws IOException;

    public ReportTemplate updateTemplate(String tag,String description, MultipartFile file) throws IOException;

    public  void deleteTemplate(String tag);

   // public  byte[] generateReport(String tag, String params) throws IOException, JRException;

    public default String getExtension(String originlaFilename) {
        if(!originlaFilename.contains(".")) {
         throw  new RuntimeException("Invalid filename format");
        }
        return originlaFilename.substring(originlaFilename.lastIndexOf(".") );
    }

    byte[] generateReportWithDto(ReportDataRequest reportData, String tag) throws JRException;


    // byte[] generateReport(String tag, JsonNode params) throws IOException, JRException;
}
