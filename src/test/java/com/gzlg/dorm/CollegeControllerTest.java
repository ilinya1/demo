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
 * 学院管理接口端到端测试（真实 HTTP + JWT）。
 * 覆盖：返回数组、新增 / 重名拦截、改名级联 class/student、删除前引用拦截。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CollegeControllerTest {

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

    private int code(JsonNode body) {
        return body.path("code").asInt();
    }

    @Test
    void collegeCrudAndCascade() throws Exception {
        String t = loginAdmin();
        String suffix = Long.toString(System.currentTimeMillis() % 100000);
        String college = "试点学院" + suffix;
        String college2 = "试点学院改" + suffix;
        String clazz = "试点班级" + suffix;
        String stuId = "9999" + suffix;

        // 1) list 返回数组并含种子学院
        JsonNode list = call("GET", "/colleges", null, t).body();
        assertThat(code(list)).isEqualTo(0);
        assertThat(list.path("data").isArray()).isTrue();
        boolean hasSeed = false;
        for (JsonNode n : list.path("data")) {
            if ("计算机学院".equals(n.path("name").asText())) {
                hasSeed = true;
            }
        }
        assertThat(hasSeed).isTrue();

        // 2) 新增学院，并从列表回查其 id（create 返回无 data）
        Resp create = call("POST", "/colleges", Map.of("name", college), t);
        assertThat(code(create.body())).isEqualTo(0);
        JsonNode afterCreate = call("GET", "/colleges", null, t).body();
        long collegeId = 0;
        for (JsonNode n : afterCreate.path("data")) {
            if (college.equals(n.path("name").asText())) {
                collegeId = n.path("id").asLong();
            }
        }
        assertThat(collegeId).isGreaterThan(0);

        // 3) 重名拦截
        assertThat(code(call("POST", "/colleges", Map.of("name", college), t).body())).isNotEqualTo(0);

        // 4) 建班级与学生引用该学院
        assertThat(code(call("POST", "/classes", Map.of("name", clazz, "college", college), t).body())).isEqualTo(0);
        assertThat(code(call("POST", "/students", Map.of("studentId", stuId, "name", "测试生", "gender", "男",
                "college", college, "className", clazz), t).body())).isEqualTo(0);

        // 5) 有引用时删除被拦截
        assertThat(code(call("DELETE", "/colleges/" + collegeId, null, t).body())).isNotEqualTo(0);

        // 6) 改名级联 class.college 与 student.college
        assertThat(code(call("PUT", "/colleges/" + collegeId, Map.of("name", college2), t).body())).isEqualTo(0);
        JsonNode classList = call("GET", "/classes?name=" + clazz, null, t).body();
        assertThat(classList.path("data").path("list").get(0).path("college").asText()).isEqualTo(college2);
        JsonNode stuList = call("GET", "/students?studentId=" + stuId, null, t).body();
        assertThat(stuList.path("data").path("list").get(0).path("college").asText()).isEqualTo(college2);

        // 7) 清理：先删学生（其引用班级）、再删班级、最后删学院
        long clazzId = classList.path("data").path("list").get(0).path("id").asLong();
        assertThat(code(call("DELETE", "/students/" + stuId, null, t).body())).isEqualTo(0);
        assertThat(code(call("DELETE", "/classes/" + clazzId, null, t).body())).isEqualTo(0);
        assertThat(code(call("DELETE", "/colleges/" + collegeId, null, t).body())).isEqualTo(0);

        // 8) 清理后列表中不再含该学院
        JsonNode after = call("GET", "/colleges", null, t).body();
        boolean gone = true;
        for (JsonNode n : after.path("data")) {
            if (college2.equals(n.path("name").asText())) {
                gone = false;
            }
        }
        assertThat(gone).isTrue();
    }
}