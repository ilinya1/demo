package com.gzlg.dorm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 个人中心 + 系统设置 + 改密 端到端测试（真实 HTTP + JWT）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProfileSettingsTest {

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

    private String login(String username, String password) throws Exception {
        Resp r = call("POST", "/auth/login",
                Map.of("username", username, "password", password), null);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        return r.body().path("data").path("token").asText();
    }

    private String loginAdmin() throws Exception {
        if (token != null) {
            return token;
        }
        token = login("admin", "123456");
        return token;
    }

    @Test
    @Order(1)
    void systemParamsCrudAndReset() throws Exception {
        String t = loginAdmin();
        Resp init = call("GET", "/settings/params", null, t);
        assertThat(init.body().path("code").asInt()).isEqualTo(0);
        assertThat(init.body().toString()).contains("systemName");

        JsonNode sysName = findParam(init.body().path("data"), "systemName");
        assertThat(sysName.path("value").asText()).isEqualTo("学生宿舍管理系统");

        Resp upd = call("PUT", "/settings/params",
                Map.of("list", java.util.List.of(
                        Map.of("key", "systemName", "name", "系统名称", "value", "测试系统"))), t);
        assertThat(upd.body().path("code").asInt()).isEqualTo(0);

        Resp after = call("GET", "/settings/params", null, t);
        assertThat(findParam(after.body().path("data"), "systemName").path("value").asText())
                .isEqualTo("测试系统");

        Resp reset = call("POST", "/settings/params/reset", null, t);
        assertThat(reset.body().path("code").asInt()).isEqualTo(0);

        Resp back = call("GET", "/settings/params", null, t);
        assertThat(findParam(back.body().path("data"), "systemName").path("value").asText())
                .isEqualTo("学生宿舍管理系统");
    }

    @Test
    @Order(2)
    void adminProfile() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/profile?role=ADMIN&username=admin", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().path("data").path("role").asText()).isEqualTo("ADMIN");
        assertThat(r.body().path("data").path("username").asText()).isEqualTo("admin");
    }

    @Test
    @Order(3)
    void studentProfile() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/profile?role=STUDENT&username=2023010101", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().path("data").path("phone").asText()).isNotBlank();
        assertThat(r.body().path("data").path("role").asText()).isEqualTo("STUDENT");
    }

    @Test
    @Order(4)
    void changePasswordAndRestore() throws Exception {
        String t = loginAdmin();
        Resp chg = call("POST", "/auth/change-password",
                Map.of("username", "2023010101", "oldPassword", "123456", "newPassword", "12345678"), t);
        assertThat(chg.body().path("code").asInt()).isEqualTo(0);

        // 新密码登录成功
        String newToken = login("2023010101", "12345678");
        assertThat(newToken).isNotBlank();

        // 改回默认 123456，避免污染后续测试数据
        Resp revert = call("POST", "/auth/change-password",
                Map.of("username", "2023010101", "oldPassword", "12345678", "newPassword", "123456"), t);
        assertThat(revert.body().path("code").asInt()).isEqualTo(0);
    }

    @Test
    @Order(5)
    void checkoutReasons() throws Exception {
        String t = loginAdmin();
        Resp r = call("GET", "/daily/checkout-reasons", null, t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().path("data").size()).isGreaterThanOrEqualTo(5);
    }

    private JsonNode findParam(JsonNode data, String key) {
        for (JsonNode n : data) {
            if (key.equals(n.path("key").asText())) {
                return n;
            }
        }
        throw new AssertionError("param not found: " + key);
    }
}