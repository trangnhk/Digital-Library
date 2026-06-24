import { useContext, useEffect, useState } from "react";
import { MyUserContext } from "../configs/Context";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../configs/Apis";
import { Button, Container, Image, Nav, Navbar } from "react-bootstrap";
import NotificationBell from "./NotificationBell";

const LibrarianHeader = () => {
    const [user, dispatch] = useContext(MyUserContext);
    const [avatarUrl, setAvatarUrl] = useState("");
    const nav = useNavigate();

    useEffect(() => {
        const loadProfile = async () => {
            if (user === null) {
                setAvatarUrl("");
                return;
            }

            try {
                const res = await Apis.get(endpoints.profile);

                if (res.data && res.data.avatar) {
                    setAvatarUrl(res.data.avatar);
                } else {
                    setAvatarUrl(null);
                }

            } catch (err) {
                console.error("Load profile failed:", err);
                setAvatarUrl(null);
            }
        };

        loadProfile();
    }, [user]);

    const logout = async () => {
        try {
            await Apis.post(endpoints.logout);
        } catch (ex) {
            console.error("Logout failed:", ex);
        }

        // cookies.remove("user", { path: "/" });

        dispatch({ type: "LOGOUT" });

        nav("/login");
    };

    return (
        <Navbar expand="lg" bg="light" className="border-bottom shadow-sm">
            <Container>
                <Navbar.Brand as={Link} to="/librarian/dashboard" className="fw-bold text-primary">Digital Library (for Librarian)</Navbar.Brand>

                <Navbar.Toggle aria-controls="librarian-navbar" />

                <Navbar.Collapse id="librarian-navbar">
                    <Nav className="me-auto">
                        <Link to="/librarian/dashboard" className="nav-link">
                            Tài liệu của tôi
                        </Link>



                        <Link to="/home" className="nav-link">
                            Xem thư viện
                        </Link>
                    </Nav>

                    <Nav className="ms-auto align-items-lg-center">
                        <NotificationBell />
                        <Link to="/me" className="nav-link">
                            {avatarUrl && (
                                <Image src={avatarUrl} roundedCircle width={36} height={36} style={{ objectFit: "cover", border: "1px solid #dee2e6" }} />

                            )}
                            <strong>{user?.username}</strong> </Link>

                        <Button variant="outline-danger" size="sm" onClick={logout} > Đăng xuất </Button>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );



};

export default LibrarianHeader;