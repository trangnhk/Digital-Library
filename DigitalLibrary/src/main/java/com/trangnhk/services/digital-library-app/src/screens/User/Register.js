import { useState } from "react";
import { Alert, Button, Card, Col, Form, Row } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";

import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";

const Register = () => {
    const [user, setUser] = useState({
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        username: "",
        password: "",
        registerType: "STUDENT",
        avatar: null
    });

    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const [loading, setLoading] = useState(false);

    const nav = useNavigate();

    const updateField = (field, value) => {
        setUser({
            ...user,
            [field]: value
        });
    };

    const validate = () => {
        if (!user.email.trim()) {
            setErr("Vui lòng nhập email.");
            return false;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailRegex.test(user.email.trim())) {
            setErr("Email không hợp lệ.");
            return false;
        }

        if (!user.phone.trim()) {
            setErr("Vui lòng nhập số điện thoại.");
            return false;
        }

        const phoneRegex = /^[0-9]{10}$/;

        if (!phoneRegex.test(user.phone.trim())) {
            setErr("Số điện thoại phải có đúng 10 chữ số.");
            return false;
        }

        if (!user.username.trim()) {
            setErr("Vui lòng nhập username.");
            return false;
        }

        if (user.username.trim().length < 3 || user.username.trim().length > 50) {
            setErr("Username phải từ 3 đến 50 ký tự.");
            return false;
        }

        if (!user.password.trim()) {
            setErr("Vui lòng nhập password.");
            return false;
        }

        if (user.password.length < 6) {
            setErr("Password phải có ít nhất 6 ký tự.");
            return false;
        }

        if (!user.registerType) {
            setErr("Vui lòng chọn loại tài khoản.");
            return false;
        }

        return true;
    };

    const getErrorMessage = (ex) => {
        if (!ex.response) {
            return "Không thể kết nối đến server.";
        }

        const data = ex.response.data;

        if (typeof data === "string") {
            return data;
        }

        if (Array.isArray(data)) {
            return data
                .map(item => item.defaultMessage || item.message || "Dữ liệu không hợp lệ.")
                .join(" ");
        }

        if (data && data.message) {
            return data.message;
        }

        return "Đăng ký thất bại. Vui lòng kiểm tra lại thông tin.";
    };

    const register = async (e) => {
        e.preventDefault();

        setErr("");
        setSuccess("");

        if (!validate()) {
            return;
        }

        try {
            setLoading(true);

            const formData = new FormData();

            formData.append("firstName", user.firstName);
            formData.append("lastName", user.lastName);
            formData.append("email", user.email);
            formData.append("phone", user.phone);
            formData.append("username", user.username);
            formData.append("password", user.password);
            formData.append("registerType", user.registerType);

            if (user.avatar !== null) {
                formData.append("avatar", user.avatar);
            }

            await Apis.post(endpoints.register, formData);

            setSuccess("Đăng ký thành công. Vui lòng đăng nhập.");

            setTimeout(() => {
                nav("/login");
            }, 1000);

        } catch (ex) {
            console.error(ex);
            setErr(getErrorMessage(ex));

        } finally {
            setLoading(false);
        }
    };

    return (
        <Row className="justify-content-center mt-5">
            <Col xs={12} md={8} lg={6}>
                <Card className="shadow-sm border-0 rounded-4">
                    <Card.Header className="bg-primary text-white rounded-top-4">
                        <h3 className="mb-0">
                            Digital Library
                        </h3>
                    </Card.Header>

                    <Card.Body className="p-4">
                        <h5 className="fw-bold mb-3">
                            Đăng ký tài khoản
                        </h5>

                        {err && (
                            <Alert variant="danger">
                                {err}
                            </Alert>
                        )}

                        {success && (
                            <Alert variant="success">
                                {success}
                            </Alert>
                        )}

                        <Form onSubmit={register}>

                            <Row>
                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="firstName">
                                        <Form.Label>
                                            First Name
                                        </Form.Label>

                                        <Form.Control
                                            type="text"
                                            placeholder="Nhập first name"
                                            value={user.firstName}
                                            onChange={(e) => updateField("firstName", e.target.value)}
                                            disabled={loading}
                                        />
                                    </Form.Group>
                                </Col>

                                <Col md={6}>
                                    <Form.Group className="mb-3" controlId="lastName">
                                        <Form.Label>
                                            Last Name
                                        </Form.Label>

                                        <Form.Control
                                            type="text"
                                            placeholder="Nhập last name"
                                            value={user.lastName}
                                            onChange={(e) => updateField("lastName", e.target.value)}
                                            disabled={loading}
                                        />
                                    </Form.Group>
                                </Col>
                            </Row>

                            <Form.Group className="mb-3" controlId="email">
                                <Form.Label>
                                    Email
                                </Form.Label>

                                <Form.Control
                                    type="email"
                                    placeholder="Nhập email"
                                    value={user.email}
                                    onChange={(e) => updateField("email", e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="phone">
                                <Form.Label>
                                    Phone
                                </Form.Label>

                                <Form.Control
                                    type="text"
                                    placeholder="Nhập số điện thoại 10 chữ số"
                                    value={user.phone}
                                    onChange={(e) => updateField("phone", e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="username">
                                <Form.Label>
                                    Username
                                </Form.Label>

                                <Form.Control
                                    type="text"
                                    placeholder="Nhập username"
                                    value={user.username}
                                    onChange={(e) => updateField("username", e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="password">
                                <Form.Label>
                                    Password
                                </Form.Label>

                                <Form.Control
                                    type="password"
                                    placeholder="Nhập password"
                                    value={user.password}
                                    onChange={(e) => updateField("password", e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="registerType">
                                <Form.Label>
                                    Role
                                </Form.Label>

                                <Form.Select
                                    value={user.registerType}
                                    onChange={(e) => updateField("registerType", e.target.value)}
                                    disabled={loading}
                                    required
                                >
                                    <option value="STUDENT">
                                        Student
                                    </option>

                                    <option value="LECTURER">
                                        Lecturer
                                    </option>

                                    <option value="LIBRARIAN">
                                        Librarian
                                    </option>
                                </Form.Select>

                                <Form.Text className="text-muted">
                                    Nếu đăng ký Librarian, tài khoản có thể cần admin duyệt trước khi dùng đầy đủ chức năng.
                                </Form.Text>
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="avatar">
                                <Form.Label>
                                    Avatar
                                </Form.Label>

                                <Form.Control
                                    type="file"
                                    accept="image/png,image/jpeg,image/webp"
                                    onChange={(e) => updateField("avatar", e.target.files[0])}
                                    disabled={loading}
                                />

                                <Form.Text className="text-muted">
                                    Không bắt buộc. Hỗ trợ PNG, JPG, WEBP.
                                </Form.Text>
                            </Form.Group>

                            <div className="d-grid mb-3">
                                <Button
                                    variant="primary"
                                    type="submit"
                                    disabled={loading}
                                >
                                    {loading ? (
                                        <>
                                            <MySpinner />
                                            Đang đăng ký...
                                        </>
                                    ) : (
                                        "Đăng ký"
                                    )}
                                </Button>
                            </div>

                            <div className="text-center">
                                <span className="text-muted">
                                    Đã có tài khoản?
                                </span>

                                <Link to="/login" className="ms-2 fw-semibold">
                                    Đăng nhập
                                </Link>
                            </div>

                        </Form>
                    </Card.Body>
                </Card>
            </Col>
        </Row>
    );
};

export default Register;