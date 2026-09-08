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
 * 住宿业务端到端测试（RANDOM_PORT + JWT）：入住→我的宿舍→记录→申请→撤销→再申请→审核通过→已退宿→清理；
 * 及直接退宿路径。测试数据已清理。
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccommodationControllerTest {

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

    private String createStudent(String suffix) throws Exception {
        String t = token();
        String sid = "T" + (System.nanoTime() % 100000000)
                + (suffix == null ? "" : suffix);
        Resp r = call("POST", "/students", Map.of(
                "studentId", sid, "name", "测试", "gender", "男",
                "college", "计算机学院", "major", "软件工程", "className", "软工2301",
                "academicStatus", "在校", "housingStatus", "未住"), t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
        return sid;
    }

    private void checkin(String sid, long roomId, int bedNo) throws Exception {
        String t = token();
        Resp r = call("POST", "/checkin", Map.of(
                "studentId", sid, "roomId", roomId, "bedNo", bedNo,
                "checkInDate", "2026-09-08", "remark", "测试入住"), t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);
    }

    @Test
    void applyCheckoutLifecycle() throws Exception {
        String t = token();
        String sid = createStudent(null);

        checkin(sid, 1, 4);

        Resp cur = call("GET", "/student/current-room?studentId=" + sid, null, t);
        assertThat(cur.body().path("data").path("dorm").path("roomId").asLong()).isEqualTo(1);
        assertThat(cur.body().path("data").path("student").path("className").asText()).isEqualTo("软工2301");

        Resp records = call("GET", "/checkin-records?studentId=" + sid, null, t);
        assertThat(records.body().path("data").path("list").get(0).path("status").asText()).isEqualTo("在住");

        // 提交申请
        Resp sub = call("POST", "/checkout-applications", Map.of(
                "studentId", sid, "reason", "毕业离校", "planDate", "2026-09-09", "description", "测试"), t);
        assertThat(sub.body().path("code").asInt()).isEqualTo(0);

        // 撤销
        Resp list = call("GET", "/checkout-applications?studentId=" + sid, null, t);
        long appId = list.body().path("data").path("list").get(0).path("id").asLong();
        Resp cancel = call("POST", "/checkout-applications/" + appId + "/cancel", Map.of("studentId", sid), t);
        assertThat(cancel.body().path("code").asInt()).isEqualTo(0);
        assertThat(call("GET", "/checkout-applications?studentId=" + sid, null, t)
                .body().path("data").path("total").asInt()).isZero();

        // 再申请 → 审核通过
        call("POST", "/checkout-applications", Map.of(
                "studentId", sid, "reason", "毕业离校", "planDate", "2026-09-09", "description", "测试"), t);
        long appId2 = call("GET", "/checkout-applications?studentId=" + sid, null, t)
                .body().path("data").path("list").get(0).path("id").asLong();
        Resp audit = call("POST", "/checkout-applications/" + appId2 + "/audit", Map.of("approve", true), t);
        assertThat(audit.body().path("code").asInt()).isEqualTo(0);

        // 校验已退宿
        Resp after = call("GET", "/checkin-records?studentId=" + sid, null, t);
        assertThat(after.body().path("data").path("list").get(0).path("status").asText()).isEqualTo("已退宿");
        assertThat(after.body().path("data").path("list").get(0).path("source").asText()).isEqualTo("apply");

        // 已退宿学生可删除（清理）
        assertThat(call("DELETE", "/students/" + sid, null, t).body().path("code").asInt()).isEqualTo(0);
    }

    @Test
    void directCheckout() throws Exception {
        String t = token();
        String sid = createStudent("D");

        checkin(sid, 6, 1);

        Resp r = call("POST", "/checkout/direct", Map.of(
                "studentId", sid, "checkoutDate", "2026-09-08", "reason", "休学", "remark", "直接退宿测试"), t);
        assertThat(r.body().path("code").asInt()).isEqualTo(0);

        Resp records = call("GET", "/checkin-records?studentId=" + sid, null, t);
        JsonNode rec = records.body().path("data").path("list").get(0);
        assertThat(rec.path("status").asText()).isEqualTo("已退宿");
        assertThat(rec.path("source").asText()).isEqualTo("direct");

        assertThat(call("DELETE", "/students/" + sid, null, t).body().path("code").asInt()).isEqualTo(0);
    }
}
