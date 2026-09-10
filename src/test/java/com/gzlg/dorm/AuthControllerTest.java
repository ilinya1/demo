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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 登录鉴权端到端测试（RANDOM_PORT 真实 HTTP，含 /api context-path 与 JWT 拦截器）。
 * 依赖本机 MySQL 与种子账号 admin/123456。
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Value("${local.server.port}")
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient http = HttpClient.newHttpClient();

    private record Resp(int status, JsonNode body) {
    }

    private Resp call(String method, String path, String token, Map<String, Object> body) throws Exception {
        HttpRequest.Builder b =
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api" + path));
        if (body != null) {
            b.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        } else {
            b.method(method, HttpRequest.BodyPublishers.noBody());
        }
        if (token != null) {
            b.header("Authorization", "Bearer " + token);
        }
        HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        JsonNode node = res.body() == null || res.body().isBlank()
                ? null : objectMapper.readTree(res.body());
        return new Resp(res.statusCode(), node);
    }

    @Test
    void loginAdminOk() throws Exception {
        Resp r = call("POST", "/auth/login", null, Map.of("username", "admin", "password", "123456"));
        assertThat(r.status()).isEqualTo(200);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        JsonNode data = r.body().path("data");
        assertThat(data.path("token").asText()).isNotBlank();
        assertThat(data.path("user").path("role").asText()).isEqualTo("ADMIN");
        assertThat(data.path("user").path("name").asText()).isEqualTo("系统管理员");
    }

    @Test
    void loginStudentOk() throws Exception {
        Resp r = call("POST", "/auth/login", null, Map.of("username", "2023010101", "password", "123456"));
        assertThat(r.status()).isEqualTo(200);
        JsonNode user = r.body().path("data").path("user");
        assertThat(user.path("role").asText()).isEqualTo("STUDENT");
        assertThat(user.path("studentId").asText()).isEqualTo("2023010101");
        assertThat(user.path("name").asText()).isEqualTo("王小明");
    }

    @Test
    void loginWrongPassword() throws Exception {
        Resp r = call("POST", "/auth/login", null, Map.of("username", "admin", "password", "wrong"));
        assertThat(r.body().path("code").asInt()).isEqualTo(400);
        assertThat(r.body().path("msg").asText()).isEqualTo("账号或密码错误");
    }

    @Test
    void meWithoutTokenRejected() throws Exception {
        Resp r = call("GET", "/auth/me", null, null);
        assertThat(r.status()).isEqualTo(401);
        assertThat(r.body().path("code").asInt()).isEqualTo(401);
    }

    @Test
    void meWithTokenOk() throws Exception {
        Resp login = call("POST", "/auth/login", null, Map.of("username", "admin", "password", "123456"));
        String token = login.body().path("data").path("token").asText();

        Resp r = call("GET", "/auth/me", token, null);
        assertThat(r.status()).isEqualTo(200);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        assertThat(r.body().path("data").path("username").asText()).isEqualTo("admin");
    }

    @Test
    void studentCannotAccessAdminApi() throws Exception {
        Resp login = call("POST", "/auth/login", null, Map.of("username", "2023010101", "password", "123456"));
        String st = login.body().path("data").path("token").asText();

        // 学生不能访问管理端接口（学生管理 / 退宿审核）
        Resp list = call("GET", "/students", st, null);
        assertThat(list.body().path("code").asInt()).isEqualTo(403);
        Resp audit = call("POST", "/checkout-applications/1/audit", st, Map.of("approve", true));
        assertThat(audit.body().path("code").asInt()).isEqualTo(403);

        // 学生不能撤销他人的待审核申请（归属以服务端登录态为准）
        Resp notOwn = call("POST", "/checkout-applications/1/cancel", st, null);
        assertThat(notOwn.body().path("code").asInt()).isNotEqualTo(0);

        // 学生不能重置他人/管理员密码（重置属管理功能）
        Resp resetPwd = call("POST", "/auth/reset-password", st,
                Map.of("username", "admin", "name", "系统管理员"));
        assertThat(resetPwd.body().path("code").asInt()).isNotEqualTo(0);

        // 学生不能越权查看他人的宿舍信息（IDOR）
        Resp otherRoom = call("GET", "/student/current-room?studentId=999999", st, null);
        assertThat(otherRoom.body().path("code").asInt()).isNotEqualTo(0);

        // 学生仍可访问自己的退宿申请（提交/撤销）与卫生只读
        Resp own = call("GET", "/checkout-applications?studentId=2023010101", st, null);
        assertThat(own.body().path("code").asInt()).isEqualTo(0);
    }
}
