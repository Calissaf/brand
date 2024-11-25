package org.qrush.brand.integration.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ProblemDetail;

import java.util.Map;


public class ExtendedProblemDetails extends ProblemDetail {
    @JsonProperty("invalid-params")
    public Map<String, String> invalidParams;
}
