# Digital Library

Digital Library là hệ thống thư viện số hỗ trợ người dùng tìm kiếm, xem thông tin, mượn/truy cập tài liệu, đánh giá tài liệu, lưu bookmark và quản lý tài liệu theo vai trò. Project gồm 2 phần chính:

- **Backend**: Java Spring MVC/Spring Security/Hibernate, triển khai REST API và trang quản trị bằng Thymeleaf.
- **Frontend**: ReactJS, dùng để xây dựng giao diện cho người dùng như student, lecturer và librarian.

> Ghi chú: phần **admin** trong project này được xử lý bằng **Thymeleaf trong backend**, không triển khai bằng ReactJS.

---

## 1. Mục tiêu project

Project được xây dựng với mục tiêu mô phỏng một hệ thống thư viện số có các chức năng chính:

- Đăng ký, đăng nhập, xác thực người dùng bằng JWT.
- Quản lý thông tin cá nhân và đổi mật khẩu.
- Xem danh sách tài liệu, tìm kiếm, lọc, sắp xếp và phân trang.
- Xem chi tiết tài liệu, file tài liệu, lượt xem, lượt tải và đánh giá.
- Bookmark tài liệu yêu thích.
- Review/rating tài liệu.
- Mượn hoặc truy cập nội dung tài liệu.
- Librarian tạo, cập nhật, xóa mềm và upload tài liệu.
- Admin duyệt librarian, duyệt tài liệu, quản lý user/category/document.
- Thống kê tổng quan hệ thống.

---

## 2. Kiến trúc tổng quan

Project được chia theo mô hình nhiều tầng:

```text
Client ReactJS
    |
    | HTTP/JSON
    v
Spring MVC Controller / REST Controller
    |
    v
Service Layer
    |
    v
Repository Layer
    |
    v
Hibernate ORM
    |
    v
MySQL Database
```

### Vai trò từng tầng

| Tầng | Công dụng |
|---|---|
| Controller | Nhận request từ client, lấy `@RequestParam`, `@PathVariable`, `@RequestBody`, gọi service và trả response. |
| Service | Xử lý nghiệp vụ chính, kiểm tra bảo mật, kiểm tra quyền, validate dữ liệu. |
| Repository | Truy vấn database bằng Hibernate/HQL/Criteria. |
| DTO | Đóng gói dữ liệu trả về frontend, tránh trả trực tiếp entity và tránh lộ dữ liệu nhạy cảm. |
| Entity/POJO | Đại diện cho các bảng trong database. |
| Config | Cấu hình Spring MVC, Hibernate, Security, CORS, Thymeleaf, upload file. |

---

## 3. Công nghệ sử dụng

### 3.1. Backend

| Công nghệ | Công dụng trong project |
|---|---|
| Java | Ngôn ngữ lập trình backend. |
| Spring Framework / Spring MVC | Xây dựng ứng dụng web theo mô hình MVC, xử lý request/response. |
| Spring Security | Xác thực, phân quyền, bảo vệ API theo role. |
| JWT | Xác thực API `/api/secure/**`. |
| Hibernate ORM | Mapping entity Java với bảng trong MySQL, truy vấn dữ liệu. |
| MySQL | Hệ quản trị cơ sở dữ liệu. |
| Maven | Quản lý dependency và build project. |
| Apache Tomcat | Server chạy ứng dụng Java Web dạng WAR. |
| Thymeleaf | Xây dựng giao diện admin phía backend. |
| Jackson | Chuyển đổi Java object sang JSON và ngược lại. |
| Cloudinary | Lưu trữ ảnh như avatar/thumbnail. |
| Cloudflare R2 hoặc dịch vụ object storage khác | Có thể dùng để lưu file tài liệu nếu project đã tích hợp hoặc muốn thay thế Cloudinary cho file. |
| Postman | Test API backend. |

### 3.2. Frontend

| Công nghệ | Công dụng trong project |
|---|---|
| ReactJS | Xây dựng giao diện người dùng. |
| Yarn | Quản lý thư viện frontend. |
| React Router DOM | Điều hướng giữa các trang. |
| Axios | Gọi REST API từ backend. |
| Bootstrap / React Bootstrap | Xây dựng UI nhanh, responsive. |
| React Bootstrap Icons | Hiển thị icon như mũi tên tăng/giảm, bookmark, user, document. |
| Context API / Local Storage / Cookie | Lưu trạng thái đăng nhập, token hoặc thông tin user nếu project có dùng. |

---

## 4. Chức năng theo vai trò

### 4.1. Guest

Người dùng chưa đăng nhập có thể:

- Xem danh sách tài liệu public.
- Tìm kiếm tài liệu.
- Lọc tài liệu theo category.
- Xem chi tiết tài liệu đã được duyệt.
- Xem review public của tài liệu.
- Đăng ký tài khoản.
- Đăng nhập.

### 4.2. Student / Lecturer

Sau khi đăng nhập, người dùng có thể:

- Xem profile cá nhân.
- Cập nhật thông tin cá nhân.
- Đổi mật khẩu.
- Bookmark tài liệu.
- Xem danh sách bookmark của mình.
- Review/rating tài liệu.
- Mượn hoặc truy cập tài liệu.
- Xem lịch sử mượn tài liệu.
- Truy cập nội dung tài liệu nếu thỏa điều kiện quyền truy cập.

### 4.3. Librarian

Librarian có thể:

- Quản lý tài liệu do mình upload.
- Tạo tài liệu mới.
- Upload thumbnail và file tài liệu.
- Cập nhật metadata tài liệu.
- Xóa mềm tài liệu.
- Xem danh sách người đã mượn/truy cập tài liệu.

> Lưu ý: librarian cần được admin duyệt trước khi được tạo/sửa/xóa tài liệu.

### 4.4. Admin

Admin quản lý hệ thống thông qua giao diện Thymeleaf trong backend:

- Đăng nhập trang admin.
- Xem dashboard.
- Quản lý user.
- Khóa/mở tài khoản user.
- Duyệt hoặc từ chối librarian.
- Quản lý category.
- Duyệt hoặc từ chối tài liệu.
- Xem thống kê tổng quan, thống kê lượt truy cập và lượt mượn.

---

## 5. Cấu trúc thư mục đề xuất

Cấu trúc thực tế có thể khác một chút tùy branch, nhưng project nên được tổ chức tương tự:

```text
DigitalLibrary/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/trangnhk/
│   │   │       ├── configs/          # Cấu hình Spring MVC, Security, Hibernate, CORS
│   │   │       ├── controllers/      # Controller REST API và Controller Thymeleaf
│   │   │       ├── dto/              # DTO request/response
│   │   │       ├── filters/          # JWT filter nếu có
│   │   │       ├── pojo/             # Entity mapping database
│   │   │       ├── repositories/     # Tầng truy vấn database
│   │   │       ├── services/         # Tầng xử lý nghiệp vụ
│   │   │       └── utils/            # JWTUtils, helper upload file...
│   │   ├── resources/
│   │   │   ├── database.properties   # Cấu hình database
│   │   │   ├── templates/            # File Thymeleaf admin
│   │   │   └── static/               # CSS, JS, image cho admin nếu có
│   │   └── webapp/
│   └── test/
└── target/
```

Frontend ReactJS có thể đặt riêng:

```text
frontend/
├── package.json
├── yarn.lock
├── public/
└── src/
    ├── components/       # Component dùng lại
    ├── pages/            # Các màn hình chính
    ├── configs/          # Cấu hình Axios/API endpoint
    ├── contexts/         # Auth context nếu có
    ├── reducers/         # Reducer nếu có
    ├── App.js
    └── index.js
```

---

## 6. Yêu cầu môi trường

Trước khi chạy project, cần cài đặt:

### Backend

- JDK 17 trở lên.
- Apache Maven.
- Apache Tomcat 10 trở lên.
- MySQL 8.x.
- NetBeans hoặc IntelliJ IDEA hoặc Eclipse.
- Postman để test API.

### Frontend

- Node.js LTS.
- Yarn.

Kiểm tra phiên bản:

```bash
java -version
mvn -version
node -v
yarn -v
```

---

## 7. Cài đặt backend

### 7.1. Clone project

```bash
git clone <repository-url>
cd DigitalLibrary
```

### 7.2. Tạo database MySQL

Đăng nhập MySQL và tạo database:

```sql
CREATE DATABASE digital_library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Nếu project có file SQL mẫu, import database:

```bash
mysql -u root -p digital_library < database.sql
```

### 7.3. Cấu hình database

Mở file:

```text
src/main/resources/database.properties
```

Cấu hình mẫu:

```properties
hibernate.dialect=org.hibernate.dialect.MySQLDialect
hibernate.showSql=true
hibernate.connection.driverClass=com.mysql.cj.jdbc.Driver
hibernate.connection.url=jdbc:mysql://localhost:3306/digital_library?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
hibernate.connection.username=root
hibernate.connection.password=your_password
```

Thay `your_password` bằng mật khẩu MySQL trên máy của bạn.

### 7.4. Cấu hình Cloudinary nếu project có upload ảnh

Nếu project dùng Cloudinary để upload avatar/thumbnail, tạo file hoặc biến môi trường theo cách project đang cấu hình.

Ví dụ cấu hình dạng properties:

```properties
cloudinary.cloud_name=your_cloud_name
cloudinary.api_key=your_api_key
cloudinary.api_secret=your_api_secret
```

Không commit thông tin thật của Cloudinary lên GitHub.

### 7.5. Cấu hình JWT secret nếu có

Ví dụ:

```properties
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000
```

`jwt.secret` nên đủ dài và không đưa lên repository public.

### 7.6. Cài thư viện backend

Với Maven, không cần cài từng thư viện bằng tay. Chỉ cần chạy:

```bash
mvn clean install
```

Hoặc nếu chỉ muốn build file WAR:

```bash
mvn clean package
```

File WAR sau khi build thường nằm trong:

```text
target/DigitalLibrary-1.0-SNAPSHOT.war
```

### 7.7. Các dependency backend quan trọng trong `pom.xml`

Nếu bị thiếu thư viện, kiểm tra `pom.xml` có các nhóm dependency chính sau:

```xml
<!-- Spring MVC / Web -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>6.1.14</version>
</dependency>

<!-- Spring ORM -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>6.1.14</version>
</dependency>

<!-- Hibernate -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.6.1.Final</version>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.4.0</version>
</dependency>

<!-- Jackson JSON -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.18.1</version>
</dependency>

<!-- Thymeleaf -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <version>3.3.5</version>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-web</artifactId>
    <version>6.3.4</version>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-config</artifactId>
    <version>6.3.4</version>
</dependency>

<!-- Thymeleaf Spring Security Extras -->
<dependency>
    <groupId>org.thymeleaf.extras</groupId>
    <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    <version>3.1.2.RELEASE</version>
</dependency>
```

Nếu project dùng JWT hoặc Cloudinary, kiểm tra thêm dependency tương ứng theo thư viện đang dùng trong source code.

### 7.8. Chạy backend bằng Tomcat

Có 2 cách phổ biến.

#### Cách 1: Chạy bằng IDE

1. Mở project bằng NetBeans/IntelliJ/Eclipse.
2. Add server Apache Tomcat.
3. Kiểm tra JDK và Maven.
4. Clean and Build project.
5. Run project trên Tomcat.

#### Cách 2: Deploy file WAR thủ công

Copy file WAR vào thư mục Tomcat:

```bash
cp target/DigitalLibrary-1.0-SNAPSHOT.war <TOMCAT_HOME>/webapps/
```

Khởi động Tomcat:

```bash
<TOMCAT_HOME>/bin/startup.sh
```

Trên Windows:

```bash
<TOMCAT_HOME>\bin\startup.bat
```

Backend thường chạy tại:

```text
http://localhost:8080/DigitalLibrary
```

API base URL:

```text
http://localhost:8080/DigitalLibrary/api
```

Trang admin Thymeleaf:

```text
http://localhost:8080/DigitalLibrary/admin/login
```

---

## 8. Cài đặt frontend

### 8.1. Di chuyển vào thư mục frontend

```bash
cd frontend
```

### 8.2. Cài thư viện có sẵn trong `package.json`

```bash
yarn install
```

### 8.3. Cài các thư viện frontend thường dùng

Nếu project chưa có các thư viện này, cài thêm:

```bash
yarn add axios react-router-dom bootstrap react-bootstrap react-bootstrap-icons
```

Ý nghĩa:

| Thư viện | Công dụng |
|---|---|
| axios | Gọi API backend. |
| react-router-dom | Điều hướng route frontend. |
| bootstrap | CSS framework. |
| react-bootstrap | Component Bootstrap cho React. |
| react-bootstrap-icons | Icon dùng trong UI. |

### 8.4. Cấu hình API base URL

Tạo file `.env` trong thư mục frontend:

```env
REACT_APP_API_BASE_URL=http://localhost:8080/DigitalLibrary/api
```

Nếu project dùng Vite thay vì Create React App, dùng:

```env
VITE_API_BASE_URL=http://localhost:8080/DigitalLibrary/api
```

### 8.5. Cấu hình Axios mẫu

Ví dụ file:

```text
src/configs/Apis.js
```

```javascript
import axios from "axios";

const BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/DigitalLibrary/api";

export const endpoints = {
    login: "/auth/login",
    register: "/auth/register",
    profile: "/secure/profile",
    categories: "/categories",
    documents: "/documents",
    bookmarks: "/secure/bookmarks/me",
    borrows: "/secure/borrows/me"
};

export default axios.create({
    baseURL: BASE_URL,
    withCredentials: true
});
```

Nếu backend yêu cầu `Authorization: Bearer <token>`, có thể tạo axios private:

```javascript
export const authApis = (token) => {
    return axios.create({
        baseURL: BASE_URL,
        headers: {
            Authorization: `Bearer ${token}`
        },
        withCredentials: true
    });
};
```

### 8.6. Chạy frontend

Nếu project dùng Create React App:

```bash
yarn start
```

Frontend chạy tại:

```text
http://localhost:3000
```

Nếu project dùng Vite:

```bash
yarn dev
```

Frontend thường chạy tại:

```text
http://localhost:5173
```

---

## 9. CORS và xác thực giữa frontend/backend

Vì frontend và backend chạy khác port, backend cần bật CORS.

Ví dụ:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}
```

Nếu frontend dùng cookie để lưu JWT, cần lưu ý:

- Axios phải có `withCredentials: true`.
- Backend CORS phải có `allowCredentials(true)`.
- Không nên để `allowedOrigins("*")` khi dùng credentials.
- JWT cookie nên có cấu hình `HttpOnly`, `Secure` và `SameSite` phù hợp môi trường.

Nếu test bằng Postman, có thể dùng một trong hai cách:

```text
Authorization: Bearer <jwt_token>
```

Hoặc dùng cookie nếu API login đã set cookie:

```text
jwt_token=<jwt_token>
```

---

## 10. Các nhóm API chính

### 10.1. Auth API

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/auth/register` | Đăng ký tài khoản. |
| POST | `/api/auth/login` | Đăng nhập, trả JWT hoặc set cookie JWT. |
| GET | `/api/secure/profile` | Lấy thông tin user đang đăng nhập. |
| PATCH | `/api/secure/profile` | Cập nhật profile. |
| PATCH | `/api/secure/change-password` | Đổi mật khẩu. |

### 10.2. Category API

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/categories` | Lấy danh sách category public. |
| GET | `/api/categories/{categoryId}` | Lấy chi tiết category. |
| GET | `/api/secure/admin/categories` | Admin xem danh sách category. |
| POST | `/api/secure/admin/categories` | Admin tạo category. |
| PATCH | `/api/secure/admin/categories/{categoryId}` | Admin cập nhật category. |
| DELETE | `/api/secure/admin/categories/{categoryId}` | Admin xóa category nếu được phép. |

### 10.3. Document API

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/documents` | Xem danh sách tài liệu, hỗ trợ search/filter/sort/page. |
| GET | `/api/documents/{documentId}` | Xem chi tiết tài liệu. |
| GET | `/api/documents/{documentId}/files` | Xem metadata file của tài liệu. |
| GET | `/api/documents/{documentId}/reviews` | Xem review public của tài liệu. |
| GET | `/api/documents/{documentId}/compare` | So sánh tài liệu nếu đã triển khai. |

### 10.4. Secure Document API

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/secure/documents/{documentId}/access` | Ghi nhận lượt truy cập tài liệu. |
| GET | `/api/secure/documents/{documentId}/content` | Truy cập nội dung/file tài liệu. |
| POST | `/api/secure/documents/{documentId}/borrow` | Mượn tài liệu. |
| GET | `/api/secure/borrows/me` | Xem lịch sử mượn của user hiện tại. |

### 10.5. Review API

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/secure/reviews` | Tạo review cho tài liệu. |
| PATCH | `/api/secure/reviews/{reviewId}` | Cập nhật review. |
| DELETE | `/api/secure/reviews/{reviewId}` | Xóa review. |

### 10.6. Bookmark API

| Method | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/secure/bookmarks/{documentId}` | Bookmark tài liệu. |
| DELETE | `/api/secure/bookmarks/{documentId}` | Xóa bookmark. |
| GET | `/api/secure/bookmarks/me` | Xem danh sách bookmark của user hiện tại. |

### 10.7. Librarian API

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/secure/librarian/documents` | Librarian xem tài liệu của mình. |
| POST | `/api/secure/librarian/documents` | Librarian tạo tài liệu mới. |
| GET | `/api/secure/librarian/documents/{documentId}` | Xem chi tiết tài liệu của librarian. |
| PATCH | `/api/secure/librarian/documents/{documentId}` | Cập nhật tài liệu. |
| DELETE | `/api/secure/librarian/documents/{documentId}` | Xóa mềm tài liệu. |
| POST | `/api/secure/librarian/documents/{documentId}/files` | Upload file bổ sung cho tài liệu. |
| DELETE | `/api/secure/librarian/documents/{documentId}/files/{fileId}` | Xóa file tài liệu. |
| GET | `/api/secure/librarian/documents/{documentId}/borrowers` | Xem người mượn/truy cập tài liệu. |

### 10.8. Admin API

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/secure/admin/users` | Admin xem danh sách user. |
| GET | `/api/secure/admin/users/{userId}` | Admin xem chi tiết user. |
| PATCH | `/api/secure/admin/users/{userId}/active` | Khóa/mở tài khoản user. |
| PATCH | `/api/secure/admin/librarians/{userId}/approve` | Duyệt librarian. |
| PATCH | `/api/secure/admin/librarians/{userId}/reject` | Từ chối librarian. |
| GET | `/api/secure/admin/documents` | Admin xem danh sách tài liệu. |
| GET | `/api/secure/admin/documents/pending` | Admin xem tài liệu chờ duyệt. |
| PATCH | `/api/secure/admin/documents/{documentId}/approve` | Duyệt tài liệu. |
| PATCH | `/api/secure/admin/documents/{documentId}/reject` | Từ chối tài liệu. |
| GET | `/api/secure/admin/statistics/overview` | Thống kê tổng quan. |
| GET | `/api/secure/admin/statistics/access` | Thống kê lượt truy cập. |
| GET | `/api/secure/admin/statistics/borrows` | Thống kê lượt mượn. |

### 10.9. Admin Thymeleaf pages

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/admin/login` | Trang đăng nhập admin. |
| POST | `/process-login` | Xử lý đăng nhập admin. |
| GET | `/admin` | Dashboard admin. |
| GET | `/admin/users` | Quản lý user. |
| GET | `/admin/librarians/pending` | Duyệt librarian. |
| GET | `/admin/categories` | Quản lý category. |
| GET | `/admin/documents` | Quản lý tài liệu. |
| GET | `/admin/documents/pending` | Duyệt tài liệu. |
| GET | `/admin/statistics` | Xem thống kê. |

---

## 11. Quy tắc bảo mật quan trọng

Project cần đảm bảo các nguyên tắc sau:

- API `/api/secure/**` bắt buộc phải đăng nhập.
- API admin chỉ cho phép `ROLE_ADMIN`.
- API librarian chỉ cho phép `ROLE_LIBRARIAN` đã được duyệt.
- Không trả password trong response.
- Không cho client tự set role admin khi đăng ký/cập nhật profile.
- User chỉ được sửa/xóa dữ liệu của chính mình, trừ admin.
- Librarian chỉ được sửa/xóa tài liệu do chính mình upload, trừ admin.
- Upload file cần kiểm tra định dạng và dung lượng.
- Nên trả lỗi rõ ràng: `400`, `401`, `403`, `404`, `409`, `415`, `422`, `500`.

Bảng test nhanh:

| Trường hợp | Kết quả mong muốn |
|---|---|
| Không token gọi `/api/secure/profile` | `401 Unauthorized` |
| Student gọi API admin | `403 Forbidden` |
| Librarian chưa duyệt tạo document | `403 Forbidden` |
| User gửi `ROLE_ADMIN` khi register | Bị bỏ qua hoặc trả `400 Bad Request` |
| Response user chứa password | Không được phép xảy ra |
| Upload sai loại file | `415 Unsupported Media Type` |
| Username/email/phone trùng | `409 Conflict` |
| Bookmark trùng | `409 Conflict` |
| Review trùng cùng document | `409 Conflict` |
| Borrow trùng đang mở | `409 Conflict` hoặc `422 Unprocessable Entity` |

---

## 12. Luồng chạy project đề xuất

### Bước 1: Chạy MySQL

Đảm bảo MySQL đang chạy và database `digital_library` đã được tạo.

### Bước 2: Chạy backend

```bash
mvn clean package
```

Deploy WAR lên Tomcat hoặc chạy trực tiếp bằng IDE.

Kiểm tra backend:

```text
http://localhost:8080/DigitalLibrary/api/categories
```

### Bước 3: Chạy frontend

```bash
cd frontend
yarn install
yarn start
```

Kiểm tra frontend:

```text
http://localhost:3000
```

### Bước 4: Test đăng nhập

Dùng Postman gọi:

```http
POST http://localhost:8080/DigitalLibrary/api/auth/login
Content-Type: application/json
```

Body mẫu:

```json
{
  "username": "admin",
  "password": "123456"
}
```

Sau khi đăng nhập, dùng token để gọi API secure.

---

## 13. Một số lỗi thường gặp

### 13.1. Lỗi 401 Unauthorized

Nguyên nhân thường gặp:

- Chưa đăng nhập.
- Thiếu token.
- Token sai định dạng.
- Token hết hạn.
- Postman chưa gửi `Authorization: Bearer <token>` hoặc chưa gửi cookie `jwt_token`.

### 13.2. Lỗi 403 Forbidden

Nguyên nhân thường gặp:

- Đã đăng nhập nhưng role không đủ quyền.
- Student gọi API admin.
- Librarian chưa được duyệt nhưng gọi API tạo tài liệu.

### 13.3. Lỗi CORS

Nguyên nhân thường gặp:

- Frontend chạy `localhost:3000`, backend chạy `localhost:8080`, nhưng backend chưa cấu hình CORS.
- Dùng cookie nhưng backend chưa bật `allowCredentials(true)`.
- Dùng `allowedOrigins("*")` cùng credentials.

### 13.4. Lỗi database connection

Nguyên nhân thường gặp:

- Sai username/password MySQL.
- Database chưa được tạo.
- Sai port MySQL.
- Thiếu MySQL driver.

### 13.5. Lỗi upload file

Nguyên nhân thường gặp:

- Thiếu cấu hình Cloudinary hoặc object storage.
- File vượt quá dung lượng cho phép.
- Sai định dạng file.
- FormData frontend gửi sai key so với backend yêu cầu.

---

## 14. Gợi ý script trong frontend `package.json`

Nếu dùng Create React App:

```json
{
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  }
}
```

Nếu dùng Vite:

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  }
}
```

---

## 15. Gợi ý tài khoản demo

> Thay thông tin bên dưới theo dữ liệu seed thực tế của project.

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `123456` |
| Librarian | `librarian` | `123456` |
| Student | `student` | `123456` |
| Lecturer | `lecturer` | `123456` |

---

## 16. Ghi chú khi làm việc nhóm

- Không commit file chứa secret thật như mật khẩu database, JWT secret, Cloudinary API secret.
- Trước khi merge code, cần test lại các API quan trọng bằng Postman.
- Ưu tiên giữ logic kiểm tra quyền trong service để tránh controller bị quá nhiều code.
- Frontend nên gọi API thông qua một file cấu hình chung để dễ đổi base URL.
- DTO response không nên chứa password hoặc dữ liệu nhạy cảm.
- Với API upload file, frontend cần dùng `FormData`, không gửi JSON thường.

---

## 17. Tóm tắt cách chạy nhanh

Backend:

```bash
cd DigitalLibrary
mvn clean package
# deploy file WAR trong target/ lên Tomcat
```

Frontend:

```bash
cd frontend
yarn install
yarn start
```

Truy cập:

```text
Backend:  http://localhost:8080/DigitalLibrary
API:      http://localhost:8080/DigitalLibrary/api
Admin:    http://localhost:8080/DigitalLibrary/admin/login
Frontend: http://localhost:3000
```
