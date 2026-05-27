import { useContext, useState } from "react";
import {Alert, Button, Card, Col, Form, Row} from "react-bootstrap";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import cookies from "react-cookies";

import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Context";
import MySpinner from "../../components/MySpinner";

const Login = () => {
    const [user, setUser] = useState({
        username: "",
        password: ""
    });

    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);

    const [, dispatch] = useContext(MyUserContext);

    const [q] = useSearchParams();
    const nav = useNavigate();

    const updateField = (field, value) => {
        setUser({
            ...user,
            [field]: value
        });
    };

    const validate = () => {
        if (!user.username.trim()) {
            setErr("Vui lòng nhập username.");
            return false;
        }

        if (!user.password.trim()) {
            setErr("Vui lòng nhập password.");
            return false;
        }

        return true;
    };

    const login = async (e) => {
        e.preventDefault();

        setErr("");

        if (!validate()) {
            return;
        }

        try {
            setLoading(true);

            const res = await Apis.post(endpoints.login, {
                username: user.username,
                password: user.password
            });

            const loginUser = {
                username: res.data.username,
                role: res.data.role
            };

            cookies.save("user", loginUser, {
                path: "/",
                maxAge: 24 * 60 * 60
            });

            dispatch({
                type: "LOGIN",
                payload: loginUser
            });

            const next = q.get("next");

            if (next) {
                nav(next);
            } else {
                nav("/home");
            }

        } catch (ex) {
            console.error(ex);

            if (ex.response && ex.response.status === 401) {
                setErr("Sai tài khoản, sai mật khẩu hoặc tài khoản chưa được duyệt.");
            } else if (ex.response && ex.response.data) {
                setErr("Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
            } else {
                setErr("Không thể kết nối đến server.");
            }

        } finally {
            setLoading(false);
        }
    };

    return (
        <Row className="justify-content-center mt-5">
            <Col xs={12} md={7} lg={5}>
                <Card className="shadow-sm border-0 rounded-4">
                    <Card.Header className="bg-primary text-white rounded-top-4">
                        <h3 className="mb-0">Digital Library</h3>
                    </Card.Header>

                    <Card.Body className="p-4">
                        <h5 className="fw-bold mb-3">Đăng nhập người dùng</h5>

                        {err && (
                            <Alert variant="danger">{err}</Alert>
                        )}

                        <Form onSubmit={login}>
                            <Form.Group className="mb-3" controlId="username">
                                <Form.Label>Username</Form.Label>

                                <Form.Control type="text"
                                    placeholder="Nhập username"
                                    value={user.username}
                                    onChange={(e) => updateField("username", e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3" controlId="password">
                                <Form.Label>Password</Form.Label>

                                <Form.Control type="password"
                                    placeholder="Nhập password"
                                    value={user.password}
                                    onChange={(e) => updateField("password", e.target.value)}
                                    disabled={loading}
                                    required
                                />
                            </Form.Group>

                            <div className="d-grid mb-3">
                                <Button variant="primary" type="submit" disabled={loading}>
                                    {loading ? (
                                        <><MySpinner />Đang đăng nhập...</>
                                    ) : (
                                        "Đăng nhập"
                                    )}
                                </Button>
                            </div>

                            <div className="text-center">
                                <span className="text-muted">Chưa có tài khoản?</span>

                                <Link to="/register" className="ms-2 fw-semibold">Đăng ký</Link>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            </Col>
        </Row>
    );
};

export default Login;