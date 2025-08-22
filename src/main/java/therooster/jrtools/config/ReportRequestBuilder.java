package therooster.jrtools.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestBuilder {
    private byte[] templateContent;
    private Map<String, Object> parameters = new HashMap<>();
    private Map<String, List<Map<String, ?>>> datasources = new HashMap<>();

    public ReportRequestBuilder templateContent(byte[] templateContent) {
        this.templateContent = templateContent;
        return this;
    }

    public ReportRequestBuilder parameter(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }

    public ReportRequestBuilder datasource(String name, List<Map<String, ?>> data) {
        this.datasources.put(name, data);
        return this;
    }

    public RaportRequest build() {
        RaportRequest request = new RaportRequest();
        request.templateContent = this.templateContent;
        request.parameters = this.parameters;
        request.datasources = this.datasources;
        return request;
    }
}
