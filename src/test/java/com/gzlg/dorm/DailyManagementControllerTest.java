package com.gzlg.dorm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 日常管理（卫生检查 + 报修 + 报修类型字典）端到端测试（RANDOM_PORT + JWT）。
 * 覆盖类型增删、卫生登记与必插图校验、报修登记与处理；测试数据已清理。
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DailyManagementControllerTest {

    @Value("${local.server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newHttpClient();

    private static String token;

    private record Resp(int status, JsonNode body) {
    }

    private Resp call(String method, String path, Map<String, Object> body, String auth) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api" + path));
        if (body != null) {
            b.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        } else {
            b.method(method, HttpRequest.BodyPublishers.noBody());
        }
        if (auth != null) {
            b.header("Authorization", "Bearer " + auth);
        }
        HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        return new Resp(res.statusCode(), res.body().isBlank() ? null : objectMapper.readTree(res.body()));
    }

    private String token() throws Exception {
        if (token == null) {
            Resp r = call("POST", "/auth/login", Map.of("username", "admin", "password", "123456"), null);
            token = r.body().path("data").path("token").asText();
        }
        return token;
    }

    @Test
    void repairTypeCrud() throws Exception {
        String t = token();

        Resp list = call("GET", "/daily/repair-types", null, t);
        assertThat(list.body().path("code").asInt()).isEqualTo(0);
        assertThat(list.body().path("data").size()).isGreaterThan(0);

        String name = "测试类型" + System.currentTimeMillis();
        Resp created = call("POST", "/daily/repair-types", Map.of("name", name, "sort", 99), t);
        assertThat(created.body().path("code").asInt()).isEqualTo(0);

        Resp after = call("GET", "/daily/repair-types", null, t);
        JsonNode type = null;
        for (JsonNode n : after.body().path("data")) {
            if (name.equals(n.path("name").asText())) {
                type = n;
                break;
            }
        }
        assertThat(type).isNotNull();
        long typeId = type.path("id").asLong();

        Resp del = call("DELETE", "/daily/repair-types/" + typeId, null, t);
        assertThat(del.body().path("code").asInt()).isEqualTo(0);
    }

    @Test
    void hygieneRegisterValidate() throws Exception {
        String t = token();

        Resp list = call("GET", "/daily/hygiene", null, t);
        assertThat(list.body().path("code").asInt()).isEqualTo(0);

        String today = LocalDate.now().toString();
        Resp ok = call("POST", "/daily/hygiene", Map.of(
                "checkDate", today, "checker", "测试", "buildingId", 1, "roomId", 1,
                "score", 95, "deductItems", List.of(), "photos", List.of()), t);
        assertThat(ok.body().path("code").asInt()).isEqualTo(0);

        Resp bad = call("POST", "/daily/hygiene", Map.of(
                "checkDate", today, "checker", "测试", "buildingId", 1, "roomId", 1,
                "score", 55, "deductItems", List.of(), "photos", List.of()), t);
        assertThat(bad.body().path("code").asInt()).isEqualTo(400);
        assertThat(bad.body().path("msg").asText()).contains("必须上传");
    }

    @Test
    void repairLifecycle() throws Exception {
        String t = token();

        Resp list = call("GET", "/daily/repairs", null, t);
        assertThat(list.body().path("code").asInt()).isEqualTo(0);

        Resp created = call("POST", "/daily/repairs", Map.of(
                "studentId", "2023010101", "roomId", 2, "typeId", 1,
                "description", "灯管不亮，请求更换", "contactPhone", "13800001234",
                "images", List.of()), t);
        assertThat(created.body().path("code").asInt()).isEqualTo(0);

        Resp mine = call("GET", "/daily/repairs?studentId=2023010101", null, t);
        assertThat(mine.body().path("code").asInt()).isEqualTo(0);
        JsonNode first = mine.body().path("data").path("list").get(0);
        assertThat(first).isNotNull();
        long id = first.path("id").asLong();

        Resp handled = call("PUT", "/daily/repair/" + id, Map.of(
                "handlerName", "张师傅", "handlerPhone", "13800000000",
                "status", "已完成", "handleDesc", "已更换并处理"), t);
        assertThat(handled.body().path("code").asInt()).isEqualTo(0);

        Resp detail = call("GET", "/daily/repair/" + id, null, t);
        assertThat(detail.body().path("data").path("status").asText()).isEqualTo("已完成");
        assertThat(detail.body().path("data").path("handleDesc").asText()).isEqualTo("已更换并处理");
    }
}