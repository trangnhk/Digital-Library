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
                    </Routes>
                </Container>

                <Footer />
            </BrowserRouter>
        </MyUserContext.Provider>
    );
};

export default App;