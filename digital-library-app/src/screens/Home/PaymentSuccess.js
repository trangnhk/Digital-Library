import { useEffect } from "react";
import { Alert, Card } from "react-bootstrap";
import { useNavigate, useSearchParams } from "react-router-dom";
import MySpinner from "../../components/MySpinner";

const PaymentSuccess = () => {
    const [q] = useSearchParams();
    const nav = useNavigate();

    useEffect(() => {
        const sessionId = q.get("session_id");
        const documentId = sessionStorage.getItem("payment_document_id");

        console.log("DOCUMENT ID", documentId);

        if (!documentId) {
            nav("/home");
            return;
        }

        sessionStorage.removeItem("payment_document_id");
        sessionStorage.removeItem("payment_stripe_session_id");
        sessionStorage.removeItem("payment_return_path");

        nav(`/documents/${documentId}?payment=success&session_id=${sessionId || ""}`);

    }, []);

    return (
        <Card className="shadow-sm border-0 rounded-4 mt-5">
            <Card.Body className="p-4 text-center">
                <MySpinner />

                <h4 className="fw-bold mt-3">
                    Thanh toán thành công
                </h4>

                <Alert variant="success" className="mt-3 mb-0">
                    Đang chuyển bạn về trang tài liệu...
                </Alert>
            </Card.Body>
        </Card>
    );
};

export default PaymentSuccess;