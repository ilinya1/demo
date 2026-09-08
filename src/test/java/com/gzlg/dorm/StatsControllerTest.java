package com.gzlg.dorm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 统计与仪表盘接口端到端测试（真实 HTTP + JWT）。仅断言 code==0 且 data 非空。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatsControllerTest {

    @Value("${local.server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    private static String token;

    private record Resp(int status, JsonNode body) {
    }

    private Resp call(String method, String path, Map<String, Object> body, String authToken) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api" + path));
        if (body != null) {
            b.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        } else {
            b.method(method, HttpRequest.BodyPublishers.noBody());
        }
        if (authToken != null) {
            b.header("Authorization", "Bearer " + authToken);
        }
        HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        JsonNode node = res.body() == null || res.body().isBlank() ? null : objectMapper.readTree(res.body());
        return new Resp(res.statusCode(), node);
    }

    private String loginAdmin() throws Exception {
        if (token != null) {
            return token;
        }
        Resp r = call("POST", "/auth/login", Map.of("username", "admin", "password", "123456"), null);
        token = r.body().path("data").path("token").asText();
        return token;
    }

    @Test
    void statsOccupancyOk() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/stats/occupancy", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().has("data")).isTrue();
        assertThat(r.body().path("data").isNull()).isFalse();
    }

    @Test
    void statsHygieneOk() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/stats/hygiene", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().has("data")).isTrue();
        assertThat(r.body().path("data").isNull()).isFalse();
    }

    @Test
    void statsRepairOk() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/stats/repair", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().has("data")).isTrue();
        assertThat(r.body().path("data").isNull()).isFalse();
    }

    @Test
    void dashboardStatsOk() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/dashboard/stats", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().has("data")).isTrue();
        assertThat(r.body().path("data").isNull()).isFalse();
    }

    @Test
    void dashboardBuildingOccupancyOk() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/dashboard/building-occupancy", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().has("data")).isTrue();
        assertThat(r.body().path("data").isNull()).isFalse();
    }

    @Test
    void dashboardHygieneTrendOk() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/dashboard/hygiene-trend", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().has("data")).isTrue();
        assertThat(r.body().path("data").isNull()).isFalse();
    }

    @Test
    void dashboardWorkbenchOk() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/dashboard/workbench", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().has("data")).isTrue();
        assertThat(r.body().path("data").isNull()).isFalse();
    }
}