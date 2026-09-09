# 后端多阶段构建镜像（context = 项目根目录）
# 构建：maven 打包为可执行 jar；运行：jre 镜像 java -jar
# 阶段一：构建 jar
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# 先 COPY pom 以利用依赖缓存
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline || true
COPY src ./src
RUN mvn -B -DskipTests clean package

# 阶段二：运行时镜像
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# 卫生照片等静态样例图（后端 addResourceHandlers 映射 file:uploads/）
RUN mkdir -p /app/uploads/hygiene
COPY uploads ./uploads
EXPOSE 8080
# 生产数据库地址/账号等由 docker-compose 通过环境变量注入
ENTRYPOINT ["java", "-jar", "/app/app.jar"]