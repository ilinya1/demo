# K8s 部署 — 学生宿舍管理系统（Ingress 对外 + 阿里云 ACR 镜像）

本目录提供一套可直接 `kubectl apply` 的清单，把系统以 **前端(nginx) + 后端(jar) + MySQL** 三个工作负载部署到 Kubernetes，通过 **Ingress + 域名** 对外供所有人访问。

**架构**：Ingress → frontend(:80) →(/api、/uploads 反代)→ backend(:8080) → mysql(:3306, PVC 持久化)

**目录/文件**：`namespace.yaml`、`mysql.yaml`、`backend.yaml`、`frontend.yaml`、`ingress.yaml`。

---

## 0. 前置

- 已有可用 K8s 集群与 `kubectl`（已配置 kubeconfig 指向集群）。
- 集群已安装 **Ingress Controller**（阿里云 ACK 自带 nginx-ingress；自建集群先装 ingress-nginx）。
- 已有 **阿里云 ACR** 并创建好独立命名空间（如 `dorm`）。
- 已把仓库代码放在本地某目录（用于构建镜像）。

> 变量约定（以下统一替换）：
> - `REGISTRY` = 你的 ACR 地址，如 `registry.cn-hangzhou.aliyuncs.com/<你的命名空间>`
> - `YOUR_DOMAIN` = 你的真实域名，如 `dorm.example.com`

## 1. 构建前端 + 后端镜像并推送到 ACR

### 1.1 登录 ACR
```bash
docker login registry.cn-hangzhou.aliyuncs.com
# 用户名/密码：阿里云容器镜像服务控制台提供的固定密码
```

### 1.2 后端镜像（在项目根目录）
```bash
docker build -f docker/backend.Dockerfile -t $REGISTRY/dorm-backend:latest .
docker push $REGISTRY/dorm-backend:latest
```

### 1.3 前端镜像（在 frontend 目录）
```bash
cd frontend
docker build -f ../docker/frontend.Dockerfile -t $REGISTRY/dorm-frontend:latest .
docker push $REGISTRY/dorm-frontend:latest
```

> 前端镜像内置 nginx 及反代配置（`frontend/nginx/default.conf`），生产构建自动使用 `frontend/.env.production`（`VITE_USE_MOCK=false`）。

## 2. 准备 Secret / ConfigMap / Namespace

```bash
# 命名空间
kubectl apply -f k8s/namespace.yaml

# 密钥（生产务必替换强值；以下仅示例）
kubectl -n dorm create secret generic dorm-secret \
  --from-literal=MYSQL_ROOT_PASSWORD='改成一个强密码' \
  --from-literal=JWT_SECRET='用 openssl rand -hex 32 生成并粘贴'

# init.sql 作为 ConfigMap，挂到 MySQL 首次初始化目录
kubectl create configmap dorm-init-sql \
  --namespace dorm \
  --from-file=01-init.sql=docs/sql/init.sql

# 私有 ACR：建镜像拉取凭据（若你的 ACR 仓库为私 有）
kubectl -n dorm create secret docker-registry regcred \
  --docker-server=registry.cn-hangzhou.aliyuncs.com \
  --docker-username=<ACR账号> \
  --docker-password=<ACR固定密码>
#   并在 backend.yaml / frontend.yaml 里取消注释 imagePullSecrets: - name: regcred
```

## 3. 应用工作负载（按依赖顺序）

```bash
kubectl apply -f k8s/mysql.yaml      # 等 mysql Pod Running 且 init 完成
kubectl apply -f k8s/backend.yaml    # 等 backend Ready（会自动连 mysql）
kubectl apply -f k8s/frontend.yaml
```

把 `backend.yaml`、`frontend.yaml` 里的镜像地址 `registry.cn-hangzhou.aliyuncs.com/your-ns/dorm-*` **替换为你的 `$REGISTRY/dorm-*`** 后再 apply（可用 `sed` 或直接改文件）。

查看进度：
```bash
kubectl -n dorm get pods
kubectl -n dorm get svc
```

## 4. 对外发布（Ingress + 域名）

```bash
# 改 ingress.yaml 里的 host 为你的真实域名，然后应用
kubectl apply -f k8s/ingress.yaml
```

- 在阿里云 DNS 控制台把域名 **A 记录解析到 Ingress Controller 的对外 IP**（ACK 中即绑定的 SLB 公网 IP，可用 `kubectl -n kube-system get svc ingress-nginx-controller -o jsonpath='{.status.loadBalancer.ingress[0].ip}'` 查得）。
- 解析生效后，所有人访问 `http://YOUR_DOMAIN` 即可（前端），后端 API 仍走 `/api`（由 Ingress→frontend→backend 转发）。

## 5. 验证

```bash
# 从公网（或本机）访问：
curl -I http://YOUR_DOMAIN
curl -X POST http://YOUR_DOMAIN/api/auth/login -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"123456"}'   # 期望 code:0 + token
```
浏览器登录：`admin/123456`。

## 6. 启用 HTTPS（推荐）

1. 申请证书（阿里云免费 DV 证书 或 cert-manager）。
2. 建 TLS Secret：
   ```bash
   kubectl -n dorm create secret tls dorm-tls --key=tls.key --cert=tls.crt
   ```
3. 在 `ingress.yaml` 取消注释 `tls:` 段（hosts 填你的域名、secretName: dorm-tls），并给 Ingress 加注释 `kubernetes.io/ingress.ssl-redirect: "true"` 后 `kubectl apply -f k8s/ingress.yaml`。
4. 用 `https://YOUR_DOMAIN` 访问。

## 7. 日常运维

```bash
kubectl -n dorm get pods | grep -E 'mysql|backend|frontend'
kubectl -n dorm logs deployment/backend -f
kubectl -n dorm rollout restart deployment/backend   # 改配置后滚动重启
# 备份 MySQL（PVC 持久化为 /var/lib/mysql）：可用 mysqldump / 或 PVC 快照
kubectl -n dorm exec deploy/mysql -- mysqldump -uroot -p$MYSQL_ROOT_PASSWORD dorm_manager > dorm_backup.sql
```

## 8. 常见注意

- **大图上传**：卫生/报修图片为 base64 存入 `LONGTEXT`；已在该 Ingress 加 `nginx.ingress.kubernetes.io/proxy-body-size: "50m"` 以防 413。
- **PRIVATE ACR**：私有仓库需 `imagePullSecrets: regcred`，否则 Pod 拉取镜像失败（ImagePullBackOff）。
- **MySQL 首次初始化**只在你 ConfigMap 挂载后、且 PVC 为空时执行；数据库已生成后想重建可删除 PVC（会清库）。
- 副本数：backend/frontend 现为 2；如只需单副本改 `replicas`。
- 数据库连接：backend 通过环境变量 `SPRING_DATASOURCE_URL` 连同命名空间 Service `mysql:3306`；`JWT_SECRET` 从 Secret 注入。