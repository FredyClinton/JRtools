package therooster.jrtools.dto;

import java.util.Map;


public record ReportRequest(String tag, Map<String, Object> parameters, Map<String, Object> datasource) {
}
