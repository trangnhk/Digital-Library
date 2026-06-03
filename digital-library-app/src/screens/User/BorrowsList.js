import { useContext, useEffect, useState } from "react";
import { Table, Button, Badge, Alert } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Context";

const BorrowsList = () => {
    const [user] = useContext(MyUserContext);
    const [borrows, setBorrows] = useState([]);
    const [returningId, setReturningId] = useState(null);
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const nav = useNavigate();


    const loadBorrows = async () => {
        try {
            const res = await Apis.get(endpoints["myBorrows"]);

            const data = res.data || [];

            if (data.length === 0) {
                setBorrows([]);
                return;
            }

            const mapped = data.map(item => ({
                id: item.id,
                documentId: item.documentId,
                documentTitle: item.title,
                borrowDate: new Date(item.borrowDate).toLocaleDateString("vi-VN"),
                returnDate: item.returnDate
                    ? new Date(item.returnDate).toLocaleDateString("vi-VN")
                    : "Chưa trả",
                dueDate: item.dueDate
                    ? new Date(item.dueDate).toLocaleDateString("vi-VN")
                    : "Chưa xác định",
                status: item.status
            }));

            setBorrows(mapped);

        } catch (err) {
            console.error(err);
            setBorrows([]); // fallback an toàn
            setErr("Không thể tải danh sách tài liệu đã mượn.");
        }
    };

    useEffect(() => {
        console.log("BORROWS:", endpoints.myBorrows);
        if (user) loadBorrows();

    }, [user]);

    const renderStatus = (status) => {
        switch (status) {
            case "BORROWING":
                return <Badge bg="warning">BORROWING</Badge>;
            case "RETURNED":
                return <Badge bg="success">RETURNED</Badge>;
            case "OVERDUE":
                return <Badge bg="danger">OVERDUE</Badge>;
            default:
                return <Badge bg="secondary">{status}</Badge>;
        }
    };

    const isBorrowing = (status) => {
        return status === "BORROWING";
    };

    const returnDocument = async (borrowId) => {
        const confirmed = window.confirm("Bạn có chắc muốn trả tài liệu này không?");

        if (!confirmed) {
            return;
        }

        try {
            setReturningId(borrowId);
            setErr("");
            setSuccess("");

            await Apis.patch(endpoints.returnDocument(borrowId));

            setSuccess("Trả tài liệu thành công.");

            await loadBorrows();

        } catch (err) {
            console.error("RETURN DOCUMENT ERROR:", err);

            if (!err.response) {
                setErr("Không thể kết nối đến server.");
                return;
            }

            switch (err.response.status) {
                case 401:
                    setErr("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                    return;

                case 403:
                    setErr(err.response.data?.message || "Bạn không có quyền trả tài liệu này hoặc tài liệu đã quá hạn");
                    return;

                case 404:
                    setErr("Không tìm thấy lượt mượn cần trả.");
                    return;

                case 409:
                    setErr(err.response.data?.message || "Tài liệu này không thể trả ở trạng thái hiện tại.");
                    return;


                default:
                    setErr(err.response.data?.message || "Trả tài liệu thất bại.");
                    return;
            }

        } finally {
            setReturningId(null);
        }
    };

    return (
        <div className="mt-4">
            <h5 className="text-primary">📚 Sách đã mượn</h5>
            {err && <Alert variant="danger">{err}</Alert>}
            {success && <Alert variant="success">{success}</Alert>}

            <Table striped bordered hover size="sm" className="mt-2">
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Borrow date</th>
                        <th>Return date</th>
                        <th>Due date</th>
                        <th>Status</th>
                        <th style={{ width: "160px" }}>Actions</th>
                        <th></th>
                    </tr>
                </thead>

                <tbody>
                    {borrows.map((b) => (
                        <tr key={b.id}>
                            <td>{b.documentTitle}</td>
                            <td>{b.borrowDate}</td>
                            <td>{b.returnDate}</td>
                            <td>{b.dueDate}</td>
                            <td>{renderStatus(b.status)}</td>
                            <td>
                                {isBorrowing(b.status) ? (
                                    <div className="d-flex gap-2">
                                        <Button size="sm" variant="outline-primary"
                                            onClick={() => nav(`/documents/${b.documentId}`)} >
                                            Mở
                                        </Button>

                                        <Button size="sm" variant="outline-danger" disabled={returningId === b.id}
                                            onClick={() => returnDocument(b.id)} >
                                            {returningId === b.id ? "Đang trả..." : "Trả"}
                                        </Button>
                                    </div>
                                ) : (<span className="text-muted small">Không khả dụng</span>)}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </div>
    );
};

export default BorrowsList;