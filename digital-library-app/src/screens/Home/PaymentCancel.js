import { Alert, Button, Card } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

const PaymentCancel = () => {
    const nav = useNavigate();

    const documentId = sessionStorage.getItem("payment_document_id");

    const backToDocument = () => {
        if (documentId) {
            nav(`/documents/${documentId}?payment=cancel`);
            return;
        }

        nav("/home");
    };

    return (
        <Card className="shadow-sm border-0 rounded-4 mt-5">
            <Card.Body className="p-4 text-center">
                <h4 className="fw-bold">
                    Thanh toán đã bị hủy
                </h4>

                <Alert variant="warning" className="mt-3">
                    Bạn chưa hoàn tất thanh toán nên chưa thể mượn tài liệu premium.
                </Alert>

                <Button
                    variant="primary"
                    onClick={backToDocument}
                >
                    Quay lại tài liệu
                </Button>
            </Card.Body>
        </Card>
    );
};

export default PaymentCancel;