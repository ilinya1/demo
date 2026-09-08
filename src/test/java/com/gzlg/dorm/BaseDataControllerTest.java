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
 * 基础数据 CRUD 端到端测试（真实 HTTP + JWT）。覆盖班级/学生/房间列表与增删、校验拦截；测试数据已清理。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BaseDataControllerTest {

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
    @Order(1)
    void listBaseData() throws Exception {
        String t = loginAdmin();
        Resp classes = call("GET", "/classes", null, t);
        assertThat(classes.body().path("code").asInt()).isEqualTo(0);
        assertThat(classes.body().path("data").path("list").size()).isGreaterThan(0);

        Resp students = call("GET", "/students?pageSize=5", null, t);
        assertThat(students.body().path("code").asInt()).isEqualTo(0);
        JsonNode first = students.body().path("data").path("list").get(0);
        assertThat(first.path("className").asText()).isNotBlank();
    }

    @Test
    @Order(2)
    void classCrudAndDeleteBlock() throws Exception {
        String t = loginAdmin();
        String name = "测试班级" + System.currentTimeMillis();
        Resp created = call("POST", "/classes",
                Map.of("name", name, "college", "测试学院", "major", "测试专业", "grade", "2025", "headTeacher", "张三"), t);
        assertThat(created.body().path("code").asInt()).isEqualTo(0);

        Resp dup = call("POST", "/classes",
                Map.of("name", name, "college", "测试学院"), t);
        assertThat(dup.body().path("code").asInt()).isEqualTo(400);
        assertThat(dup.body().path("msg").asText()).isEqualTo("班级名称已存在");

        Resp page = call("GET", "/classes?name=" + name, null, t);
        JsonNode item = page.body().path("data").path("list").get(0);
        long id = item.path("id").asLong();

        // 软工2301 有学生，删除应被拦截
        Resp classes = call("GET", "/classes?name=软工2301", null, t);
        long softId = classes.body().path("data").path("list").get(0).path("id").asLong();
        Resp block = call("DELETE", "/classes/" + softId, null, t);
        assertThat(block.body().path("code").asInt()).isEqualTo(400);

        Resp del = call("DELETE", "/classes/" + id, null, t);
        assertThat(del.body().path("code").asInt()).isEqualTo(0);
    }

    @Test
    @Order(3)
    void roomCrudAndBeds() throws Exception {
        String t = loginAdmin();
        String roomNo = "66" + (System.nanoTime() % 1000);
        Resp created = call("POST", "/rooms",
                Map.of("buildingId", 1, "floor", 1, "roomNo", roomNo, "capacity", 4), t);
        assertThat(created.body().path("code").asInt()).isEqualTo(0);

        Resp dup = call("POST", "/rooms",
                Map.of("buildingId", 1, "floor", 1, "roomNo", roomNo, "capacity", 4), t);
        assertThat(dup.body().path("code").asInt()).isEqualTo(400);
        assertThat(dup.body().path("msg").asText()).isEqualTo("该楼栋房间号已存在");

        Resp page = call("GET", "/rooms?roomNo=" + roomNo, null, t);
        JsonNode room = page.body().path("data").path("list").get(0);
        long id = room.path("id").asLong();
        assertThat(room.path("roomType").asText()).isEqualTo("四人间");

        Resp beds = call("GET", "/rooms/" + id + "/beds", null, t);
        assertThat(beds.body().path("data").size()).isEqualTo(4);

        Resp del = call("DELETE", "/rooms/" + id, null, t);
        assertThat(del.body().path("code").asInt()).isEqualTo(0);
    }
}