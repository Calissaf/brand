package org.qrush.brand.integration.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@EnableConfigurationProperties
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractIntegrationTest extends TestFactory {

    @Autowired
    protected MockMvc mockMvc;
    protected final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules().configure(
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    protected <T> T perfomPostRequest(String path, Object object, Class<T> responseType, ResultMatcher expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(object)))
                .andExpect(expectedStatus)
                .andReturn();

        return convertStringToClass(result.getResponse().getContentAsString(), responseType);
    }

    protected <T> T performPostRequestExpectedSuccess(String path, Object object, Class<T> responseType) throws Exception {
        return perfomPostRequest(path, object, responseType, status().is2xxSuccessful());
    }

    protected <T> T performPostRequestExpectClientError(String path, Object object, Class<T> responseType) throws Exception {
        return perfomPostRequest(path, object, responseType, status().is4xxClientError());
    }

    protected <T> T performPostRequestExpectedServerFailure(String path, Object object, Class<T> responseType) throws Exception {
        return perfomPostRequest(path, object, responseType, status().is5xxServerError());
    }

    protected <T> T perfomGetRequest(String path, Class<T> responseType, ResultMatcher expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(path)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andReturn();

        return convertStringToClass(result.getResponse().getContentAsString(), responseType);
    }

    protected <T> T performGetRequestExpectedSuccess(String path, Class<T> responseType) throws Exception {
        return perfomGetRequest(path, responseType, status().is2xxSuccessful());
    }

    protected <T> T performGetRequestExpectClientError(String path, Class<T> responseType) throws Exception {
        return perfomGetRequest(path, responseType, status().is4xxClientError());
    }

    protected <T> T performGetRequestExpectedServerFailure(String path, Class<T> responseType) throws Exception {
        return perfomGetRequest(path, responseType, status().is5xxServerError());
    }

    protected MockHttpServletResponse performDeleteRequest(String path, ResultMatcher expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete(path))
                .andExpect(expectedStatus)
                .andReturn();

        return result.getResponse();
    }

    protected MockHttpServletResponse performDeleteRequestExpectedSuccess(String path) throws Exception {
        return performDeleteRequest(path, status().is2xxSuccessful());
    }

    protected MockHttpServletResponse performDeleteRequestExpectClientError(String path) throws Exception {
        return performDeleteRequest(path, status().is4xxClientError());
    }

    protected MockHttpServletResponse performDeleteRequestExpectedServerFailure(String path) throws Exception {
        return performDeleteRequest(path, status().is5xxServerError());
    }

    private <T> T convertStringToClass(String json, Class<T> responseType) throws Exception {
        return mapper.readValue(json, responseType);
    }

}
