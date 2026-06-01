import { useContext, useEffect, useState } from "react";
import { Table, Button, Badge } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Context";

const BorrowsList = () => {
    const [user] = useContext(MyUserContext);
    const [borrows, setBorrows] = useState([]);
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

    return (
        <div className="mt-4">
            <h5 className="text-primary">📚 Sách đã mượn</h5>

            <Table striped bordered hover size="sm" className="mt-2">
                <thead>
                    <tr>
                        <th>Title</th>
                        <th>Borrow date</th>
                        <th>Return date</th>
                        <th>Due date</th>
                        <th>Status</th>
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
                                <Button
                                    size="sm"
                                    onClick={() => nav(`/documents/${b.documentId}`)}
                                >
                                    Mở
                                </Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </Table>
        </div>
    );
};

export default BorrowsList;