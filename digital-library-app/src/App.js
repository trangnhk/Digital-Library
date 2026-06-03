import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import Header from "./components/Header";
import Footer from "./components/Footer";
import "bootstrap/dist/css/bootstrap.min.css";
import { Container } from "react-bootstrap";
import Register from "./screens/User/Register";
import Login from "./screens/User/Login";
import Home from "./screens/Home/Home";
import { MyUserContext } from "./configs/Context";
import { useReducer } from "react";
import MyUserReducer from "./reducers/MyUserReducer";
import cookies from "react-cookies";
import Profile from "./screens/User/Profile";
import ChangePassword from "./screens/User/ChangePassword";
import LibrarianDashboard from "./screens/Librarian/Dashboard";
import LibrarianDocumentDetail from "./screens/Librarian/DocumentDetail";
import NewDocument from "./screens/Librarian/DocumentForm";
import DocumentDetail from "./screens/Home/DocumentDetail";
import Bookmark from "./screens/User/Bookmark";
import PaymentCancel from "./screens/Home/PaymentCancel";
import PaymentSuccess from "./screens/Home/PaymentSuccess";
import Borrowers from "./screens/Librarian/Borrowers";
import CompareDocument from "./screens/User/CompareDocument";

const App = () => {
    const [user, dispatch] = useReducer(
        MyUserReducer,
        cookies.load("user") || null
    );

    return (
        <MyUserContext.Provider value={[user, dispatch]}>
            <BrowserRouter>
                <Header />

                <Container>
                    <Routes>
                        <Route path="/" element={<Navigate to="/home" />} />
                        <Route path="/home" element={<Home />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/me" element={<Profile />} />
                        <Route path="/change-password" element={<ChangePassword />} />
                        <Route path="/librarian/dashboard" element={<LibrarianDashboard />} />
                        <Route path="/librarian/documents" element={<LibrarianDashboard />} />
                        <Route path="/librarian/documents/:documentId" element={<LibrarianDocumentDetail />} />
                        <Route path="/librarian/documents/create" element={<NewDocument />} />
                        <Route path="/librarian/documents/:documentId/borrowers" element={<Borrowers />} />
                        <Route path="/documents/:documentId" element={<DocumentDetail />} />
                        <Route path="secure/bookmarks/me" element={<Bookmark />} />
                        <Route path="/payment/success" element={<PaymentSuccess />} />
                        <Route path="/payment/cancel" element={<PaymentCancel />} />

                    </Routes>

                </Container>

                <Footer />
            </BrowserRouter>
        </MyUserContext.Provider>
    );
};

export default App;