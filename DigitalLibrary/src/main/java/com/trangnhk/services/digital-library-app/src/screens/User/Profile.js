import { useContext, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import cookies from "react-cookies";

import { MyUserContext } from "../../configs/Context";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";
import BorrowsList from "./BorrowsList";
import { Alert, Badge, Button, Card, Col, Form, Image, Modal, Row } from "react-bootstrap";

const Profile = () => {
    const emptyProfile = {
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        avatar: null,
        role: "",
        username: ""
    };

    const [profile, setProfile] = useState(emptyProfile);
    const [editProfile, setEditProfile] = useState(emptyProfile);

    const [avatarFile, setAvatarFile] = useState(null);
    const [avatarPreview, setAvatarPreview] = useState(null);

    const [showModal, setShowModal] = useState(false);

    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const [modalErr, setModalErr] = useState("");

    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);

    const [, dispatch] = useContext(MyUserContext);
    const nav = useNavigate();

    useEffect(() => {
        loadProfile();
    }, []);

    const loadProfile = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await Apis.get(endpoints.profile);
            const data = res.data;

            const loadedProfile = {
                firstName: data.firstName || "",
                lastName: data.lastName || "",
                email: data.email || "",
                phone: data.phone || "",
                avatar: data.avatar || null,
                role: data.role || "",
                username: data.username || ""
            };

            setProfile(loadedProfile);

        } catch (ex) {
            console.error(ex);

            if (ex.response && ex.response.status === 401) {
                setErr("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");

                cookies.remove("user", { path: "/" });

                dispatch({
                    type: "LOGOUT"
                });

                setTimeout(() => {
                    nav("/login");
                }, 800);

            } else {
                setErr("Không thể tải thông tin cá nhân.");
            }

        } finally {
            setLoading(false);
        }
    };

    const openUpdateModal = () => {
        setEditProfile({
            ...profile
        });

        setAvatarFile(null);
        setAvatarPreview(profile.avatar);
        setModalErr("");
        setShowModal(true);
    };

    const closeUpdateModal = () => {
        if (saving) {
            return;
        }

        setShowModal(false);
        setAvatarFile(null);
        setAvatarPreview(null);
        setModalErr("");
    };

    const updateEditField = (field, value) => {
        setEditProfile({...editProfile, [field]: value });
    };

    const handleAvatarChange = (e) => {
        const file = e.target.files[0];

        if (!file) {
            setAvatarFile(null);
            setAvatarPreview(editProfile.avatar || null);
            return;
        }

        setAvatarFile(file);
        setAvatarPreview(URL.createObjectURL(file));
    };

    const validate = () => {
        if (editProfile.firstName && editProfile.firstName.length > 100) {
            setModalErr("First name không được vượt quá 100 ký tự.");
            return false;
        }

        if (editProfile.lastName && editProfile.lastName.length > 100) {
            setModalErr("Last name không được vượt quá 100 ký tự.");
            return false;
        }

        if (editProfile.email) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if (!emailRegex.test(editProfile.email.trim())) {
                setModalErr("Email không hợp lệ.");
                return false;
            }
        }

        if (editProfile.phone) {
            const phoneRegex = /^[0-9]{10}$/;

            if (!phoneRegex.test(editProfile.phone.trim())) {
                setModalErr("Phone phải có đúng 10 chữ số.");
                return false;
            }
        }

        if (avatarFile !== null) {
            const allowedTypes = ["image/jpeg", "image/png", "image/webp"];

            if (!allowedTypes.includes(avatarFile.type)) {
                setModalErr("Avatar chỉ hỗ trợ JPG, PNG hoặc WEBP.");
                return false;
            }

            const maxSize = 2 * 1024 * 1024;

            if (avatarFile.size > maxSize) {
                setModalErr("Avatar không được vượt quá 2MB.");
                return false;
            }
        }

        return true;
    };

    const getErrorMessage = (ex) => {
        if (!ex.response) {
            return "Không thể kết nối đến server.";
        }

        const status = ex.response.status;
        const data = ex.response.data;

        if (status === 400) {
            if (Array.isArray(data)) {
                return data
                    .map(item => item.defaultMessage || item.message || "Dữ liệu không hợp lệ.")
                    .join(" ");
            }

            return "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.";
        }

        if (status === 409) {
            return "Email hoặc số điện thoại đã được sử dụng.";
        }

        if (status === 415) {
            return "Avatar không đúng định dạng. Chỉ hỗ trợ JPG, PNG hoặc WEBP.";
        }

        if (typeof data === "string") {
            return data;
        }

        if (data && data.message) {
            return data.message;
        }

        return "Cập nhật thất bại. Vui lòng thử lại.";
    };

    const saveProfile = async (e) => {
        e.preventDefault();

        setModalErr("");
        setSuccess("");

        if (!validate()) {
            return;
        }

        try {
            setSaving(true);

            const formData = new FormData();

            formData.append("firstName", editProfile.firstName);
            formData.append("lastName", editProfile.lastName);
            formData.append("email", editProfile.email);
            formData.append("phone", editProfile.phone);

            if (avatarFile !== null) {
                formData.append("avatar", avatarFile);
            }

            const res = await Apis.patch(endpoints.profile, formData);
            const updatedUser = res.data;

            const updatedProfile = {
                firstName: updatedUser.firstName || "",
                lastName: updatedUser.lastName || "",
                email: updatedUser.email || "",
                phone: updatedUser.phone || "",
                avatar: updatedUser.avatar || null,
                role: updatedUser.role || "",
                username: updatedUser.username || ""
            };

            setProfile(updatedProfile);

            const loginUser = {
                username: updatedUser.username,
                role: updatedUser.role,
                avatar: updatedUser.avatar
            };

            cookies.save("user", loginUser, {
                path: "/",
                maxAge: 24 * 60 * 60
            });

            dispatch({
                type: "LOGIN",
                payload: loginUser
            });

            setSuccess("Cập nhật thông tin cá nhân thành công.");
            setShowModal(false);
            setAvatarFile(null);
            setAvatarPreview(null);

        } catch (ex) {
            console.error(ex);
            setModalErr(getErrorMessage(ex));

        } finally {
            setSaving(false);
        }
    };

    const getFullName = () => {
        const fullName = `${profile.firstName} ${profile.lastName}`.trim();

        return fullName || "Chưa cập nhật họ tên";
    };

    if (loading) {
        return (
            <Row className="justify-content-center mt-5">
                <Col xs={12} md={8} lg={6}>
                    <Card className="shadow-sm border-0 rounded-4">
                        <Card.Body className="p-4 text-center">
                            <MySpinner />
                            <div className="text-muted mt-2">Đang tải thông tin cá nhân...</div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        );
    }

    return (
        <div className="mt-5 mb-5">
            <Row className="justify-content-center">
                <Col xs={12} lg={9}>

                    <Card className="shadow-sm border-0 rounded-4 mb-4">
                        <Card.Header className="bg-primary text-white rounded-top-4 d-flex justify-content-between align-items-center">
                            <h3 className="mb-0">Profile</h3>

                            <Badge bg="light" text="primary" className="px-3 py-2 rounded-pill">{profile.role}</Badge>
                        </Card.Header>

                        <Card.Body className="p-4">

                            {err && (<Alert variant="danger">{err}</Alert>)}

                            {success && (<Alert variant="success">{success}</Alert>)}

                            <div className="d-flex justify-content-between align-items-center mb-4">
                                <h5 className="fw-bold mb-0">Thông tin cá nhân</h5>
                                <div className="d-flex gap-2">
                                    <Button as={Link} to="/change-password" variant="outline-secondary" size="sm" >
                                        Đổi mật khẩu
                                    </Button>

                                    <Button variant="outline-primary" size="sm" onClick={openUpdateModal} >
                                        Thay đổi
                                    </Button>
                                </div>
                            </div>

                            <Row className="align-items-center g-4">

                                <Col xs={12} md={3} className="text-center">
                                    {profile.avatar && (
                                        <Image
                                            src={profile.avatar}
                                            width={150}
                                            height={180}
                                            style={{
                                                objectFit: "cover",
                                                border: "1px solid #dee2e6"
                                            }}
                                        />
                                    )}
                                </Col>

                                <Col xs={12} md={9}>
                                    <Row className="g-3">

                                        <Col md={6}>
                                            <div className="text-muted small">Username</div>

                                            <div className="fw-semibold">{profile.username}</div>
                                        </Col>

                                        <Col md={6}>
                                            <div className="text-muted small">Họ tên</div>

                                            <div className="fw-semibold">{getFullName()}</div>
                                        </Col>

                                        <Col md={6}>
                                            <div className="text-muted small">First name</div>

                                            <div className="fw-semibold">{profile.firstName || "N/A"}</div>
                                        </Col>

                                        <Col md={6}>
                                            <div className="text-muted small">Last name</div>

                                            <div className="fw-semibold">{profile.lastName || "N/A"}</div>
                                        </Col>

                                        <Col md={6}>
                                            <div className="text-muted small">Email</div>

                                            <div className="fw-semibold">{profile.email || "N/A"}</div>
                                        </Col>

                                        <Col md={6}>
                                            <div className="text-muted small">Phone</div>

                                            <div className="fw-semibold">{profile.phone || "N/A"}</div>
                                        </Col>

                                        <Col md={12}>
                                            <div className="text-muted small">Role</div>

                                            <div className="fw-semibold">{profile.role || "N/A"}</div>
                                        </Col>

                                    </Row>
                                </Col>

                            </Row>
                        </Card.Body>
                    </Card>

                    <BorrowsList />

                </Col>
            </Row>

            <Modal show={showModal} onHide={closeUpdateModal} centered backdrop="static" >
                <Form onSubmit={saveProfile}>
                    <Modal.Header closeButton={!saving}>
                        <Modal.Title>
                            Thay đổi thông tin cá nhân
                        </Modal.Title>
                    </Modal.Header>

                    <Modal.Body>

                        {modalErr && (<Alert variant="danger">{modalErr}</Alert>)}

                        <div className="d-flex align-items-center gap-3 mb-4">
                            {avatarPreview && (
                                <Image
                                    src={avatarPreview}
                                    roundedCircle
                                    width={96}
                                    height={96}
                                    style={{
                                        objectFit: "cover",
                                        border: "1px solid #dee2e6"
                                    }}
                                />
                            )}

                            <div className="flex-fill">
                                <Form.Label className="fw-semibold">Avatar</Form.Label>

                                <Form.Control
                                    type="file"
                                    accept="image/png,image/jpeg,image/webp"
                                    onChange={handleAvatarChange}
                                    disabled={saving}
                                />

                                <Form.Text className="text-muted">Chọn ảnh JPG, PNG hoặc WEBP</Form.Text>
                            </div>
                        </div>

                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3" controlId="firstName">
                                    <Form.Label>First name</Form.Label>

                                    <Form.Control
                                        type="text"
                                        value={editProfile.firstName}
                                        placeholder="Nhập first name"
                                        onChange={(e) => updateEditField("firstName", e.target.value)}
                                        disabled={saving}
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group className="mb-3" controlId="lastName">
                                    <Form.Label>Last name</Form.Label>

                                    <Form.Control
                                        type="text"
                                        value={editProfile.lastName}
                                        placeholder="Nhập last name"
                                        onChange={(e) => updateEditField("lastName", e.target.value)}
                                        disabled={saving}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Form.Group className="mb-3" controlId="email">
                            <Form.Label>Email</Form.Label>

                            <Form.Control
                                type="email"
                                value={editProfile.email}
                                placeholder="Nhập email"
                                onChange={(e) => updateEditField("email", e.target.value)}
                                disabled={saving}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="phone">
                            <Form.Label>Phone</Form.Label>

                            <Form.Control
                                type="text"
                                value={editProfile.phone}
                                placeholder="Nhập số điện thoại 10 chữ số"
                                onChange={(e) => updateEditField("phone", e.target.value)}
                                disabled={saving}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="role">
                            <Form.Label>Role</Form.Label>

                            <Form.Control type="text" value={editProfile.role} readOnly />

                        </Form.Group>

                    </Modal.Body>

                    <Modal.Footer>
                        <Button type="button" variant="outline-secondary" onClick={closeUpdateModal} disabled={saving} >
                            Hủy
                        </Button>

                        <Button type="submit" variant="primary" disabled={saving} >
                            {saving ? (
                                <>
                                    <MySpinner /> Đang lưu...
                                </>
                            ) : (
                                "Lưu thay đổi"
                            )}
                        </Button>
                    </Modal.Footer>
                </Form>
            </Modal>
        </div>
    );
};

export default Profile;