package therooster.jrtools.config;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRCompiler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.sf.jasperreports.engine.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Map;

@Configuration
public class JasperReportConfig {

    @Bean
    public JasperReportsService jasperReportsService() {
        return new DefaultJasperReportsService();
    }

    public interface JasperReportsService {
        JasperReport compileReport(InputStream jrxmlInputStream) throws JRException;
        JasperPrint fillReport(JasperReport report, Map<String, Object> parameters) throws JRException;
        byte[] exportReportToPdf(JasperPrint jasperPrint) throws JRException;
    }

    public static class DefaultJasperReportsService implements JasperReportsService {
        @Override
        public JasperReport compileReport(InputStream jrxmlInputStream) throws JRException {
            return JasperCompileManager.compileReport(jrxmlInputStream);
        }

        @Override
        public JasperPrint fillReport(JasperReport report, Map<String, Object> parameters) throws JRException {
            return JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
        }

        @Override
        public byte[] exportReportToPdf(JasperPrint jasperPrint) throws JRException {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
}
