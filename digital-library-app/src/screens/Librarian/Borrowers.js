import { useNavigate, useParams } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { useEffect, useState } from "react";
import { Alert, Badge, Button, Card, Table } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";

const Borrowers = () => {
    const { documentId } = useParams();
    const nav = useNavigate();

    const [borrowers, setBorrowers] = useState([]);
    const [documentTitle, setDocumentTitle] = useState("");
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");

    const loadBorrowers = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await Apis.get(endpoints.librarianDocumentBorrowers(documentId));

            const data = res.data || [];

            setBorrowers(data);

            if (data.length > 0) {
                setDocumentTitle(data[0].documentTitle || "");
            }

        } catch (ex) {
            console.error("LOAD BORROWERS ERROR:", ex);

            if (!ex.response) {
                setErr("Không thể kết nối đến server.");
                return;
            }

            if (ex.response.status === 401) {
                setErr("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                return;
            }

            if (ex.response.status === 403) {
                setErr(
                    ex.response.data?.message ||
                    "Bạn không có quyền xem danh sách người mượn của tài liệu này."
                );
                return;
            }

            if (ex.response.status === 404) {
                setErr("Không tìm thấy tài liệu.");
                return;
            }

            setErr(
                ex.response.data?.message ||
                "Không thể tải danh sách người mượn."
            );

        } finally {
            setLoading(false);
        }
    };

    const renderStatus = (status) => {
        switch (status) {
            case "BORROWING":
                return (
                    <Badge bg="warning" text="dark" className="px-3 py-2 rounded-pill">
                        BORROWING
                    </Badge>
                );

            case "RETURNED":
                return (
                    <Badge bg="success" className="px-3 py-2 rounded-pill">
                        RETURNED
                    </Badge>
                );

            case "EXPIRED":
                return (
                    <Badge bg="danger" className="px-3 py-2 rounded-pill">
                        EXPIRED
                    </Badge>
                );

            default:
                return (
                    <Badge bg="secondary" className="px-3 py-2 rounded-pill">
                        {status || "N/A"}
                    </Badge>
                );
        }
    };

    const navToDocuments = () => {
        nav("/librarian/documents");
    }


    useEffect(() => {
        if (!documentId) {
            return;
        }

        loadBorrowers();
    }, [documentId]);

    return (
        <div className="py-4">
            <Card className="shadow-sm border-0 rounded-4 mb-4">
                <Card.Body className="p-4">
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div>
                            <h1 className="fw-bold mb-1">
                                Danh sách người mượn
                            </h1>

                            <h3 className="text-muted mb-0">
                                {documentTitle ? `Tài liệu: ${documentTitle}` : `Document ID: ${documentId}`}
                            </h3>
                        </div>

                        <Button type="button" variant="outline-secondary" onClick={navToDocuments}>
                            Quay lại
                        </Button>
                    </div>
                </Card.Body>
            </Card>

            {err && (<Alert variant="danger">{err}</Alert>)}

            <Card className="shadow-sm border-0 rounded-4">
                <Card.Header className="bg-white d-flex justify-content-between align-items-center">
                    <h4 className="fw-bold mb-0">Borrowers</h4>

                    <span className="text-muted small">Total: {borrowers.length}</span>
                </Card.Header>

                <Card.Body className="p-0">
                    {loading ? (
                        <div className="text-center py-5">
                            <MySpinner />
                            <div className="text-muted mt-2">Đang tải danh sách người mượn...</div>
                        </div>
                    ) : (
                        <>
                            {borrowers.length === 0 ? (
                                <Alert variant="warning" className="m-3">Chưa có user nào mượn tài liệu này.</Alert>
                            ) : (
                                <Table bordered hover responsive className="mb-0 align-middle">
                                    <thead className="table-light">
                                        <tr>
                                            <th style={{ width: "90px" }}>Borrow ID</th>
                                            <th style={{ minWidth: "220px" }}>Fullname</th>
                                            <th style={{ minWidth: "160px" }}>Username</th>
                                            <th style={{ width: "140px" }}>Status</th>
                                            <th style={{ width: "140px" }}>Borrow Date</th>
                                            <th style={{ width: "140px" }}>Return Date</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {borrowers.map((b) => (
                                            <tr key={`borrower-${b.borrowHistoryId}`}>
                                                <td>{b.borrowHistoryId}</td>

                                                <td>
                                                    <div className="fw-semibold">{b.fullname || "N/A"}</div>
                                                    <div className="text-muted small">User ID: {b.userId}</div>
                                                </td>
                                                <td>{b.username || "N/A"}</td>
                                                <td>{renderStatus(b.status)}</td>
                                                <td>{b.borrowDate || "N/A"}</td>
                                                <td>{b.returnDate || "Chưa trả"}</td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            )}
                        </>
                    )}
                </Card.Body>
            </Card>
        </div>
    );
};
export default Borrowers;