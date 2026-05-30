import { useContext, useEffect, useState } from "react";
import { Button, Container, Image, Nav, Navbar } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import cookies from "react-cookies";

import { MyUserContext } from "../configs/Context";
import Apis, { endpoints } from "../configs/Apis";

const Header = () => {
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
        } catch (err) {
            console.error("Logout failed:", err);
        }

        dispatch({ type: "LOGOUT" });

        nav("/login");
    };



    return (
        <Navbar expand="lg" bg="light" className="border-bottom shadow-sm">
            <Container>
                <Navbar.Brand as={Link} to="/home" className="fw-bold text-primary">
                    Digital Library
                </Navbar.Brand>

                <Navbar.Toggle aria-controls="main-navbar" />

                <Navbar.Collapse id="main-navbar">
                    <Nav className="me-auto">
                        {user && (user.role === "ROLE_LIBRARIAN") ? (
                            <>
                                <Link to="/home" className="nav-link">Thư viện</Link>
                                <Link to="/librarian/dashboard" className="nav-link">Dashboard</Link>
                                <Link to="/librarian/documents" className="nav-link">Tài liệu của tôi</Link>
                            </>

                        ) :
                            <>
                                <Link to="/home" className="nav-link">Tài liệu</Link>

                                <Link to="/bookmarks" className="nav-link">Yêu thích</Link>
                            </>

                        }

                    </Nav>

                    <Nav className="ms-auto align-items-lg-center">
                        {user === null ? (
                            <Link to="/login" className="nav-link">Đăng nhập</Link>
                        ) : (
                            <>
                                <Link to="/me" className="nav-link">
                                    {avatarUrl && (
                                        <Image src={avatarUrl} roundedCircle width={36} height={36} style={{ objectFit: "cover", border: "1px solid #dee2e6" }} />

                                    )}
                                    <strong>{user.username}</strong>
                                </Link>

                                <Button variant="outline-danger" size="sm" onClick={logout} >Đăng xuất</Button>

                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
};

export default Header;