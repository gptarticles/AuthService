package me.zedaster.authservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Utilities for testing with MockMvc.
 */
@Component
public class MvcTestUtils {
    /**
     * Extracts the JSON to a map from the result.
     * @param result Result of the mock MVC request.
     * @return JSON as a map.
     */
    public Map<String, Object> jsonResultToMap(MvcResult result) throws UnsupportedEncodingException, JsonProcessingException {
        String loginResponse = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        return (Map<String, Object>) mapper.readValue(loginResponse, Map.class);
    }
}
