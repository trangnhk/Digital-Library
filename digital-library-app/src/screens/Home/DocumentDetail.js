import { useContext, useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { Badge, Button, Card, Col, Form, Image, ListGroup, Modal, Row } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Context";
import DocumentFileContent from "./DocumentFileContent";
import Review from "./Review";

const DocumentDetail = () => {
    const { documentId } = useParams();
    const [document, setDocument] = useState(null);

    const [bookmark, setBookmark] = useState(false);
    const [bookmarked, setBookmarked] = useState(false);
    const [user,] = useContext(MyUserContext);
    const [borrowed, setBorrowed] = useState(false);
    const [borrowLoading, setBorrowLoading] = useState(false);
    const [canViewContent, setCanViewContent] = useState(false);
    const nav = useNavigate();

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const [q] = useSearchParams();
    const [paymentLoading, setPaymentLoading] = useState(false);
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [selectedPaymentMethod, setSelectedPaymentMethod] = useState("STRIPE");

    const loadDocument = async () => {
        let res = await Apis.get(endpoints['documentDetails'](documentId));
        setDocument(res.data);
    }

    const checkBookmark = async () => {
        if (!user)
            return;

        try {
            const res = await Apis.get(endpoints["myBookmarks"]);

            const isBookmarked = res.data.some(
                b => b.documentId === Number(documentId)
            );

            setBookmarked(isBookmarked);

        } catch (err) {
            console.error(err);
        }
    };
    const addorDeleteBookmark = async () => {
        if (!user) {
            alert("Vui lòng đăng nhập để sử dụng chức năng Bookmark!");
            return;
        }

        try {
            setBookmark(true);

            if (!bookmarked) {
                await Apis.post(
                    endpoints.bookmark(documentId)
                );

                setBookmarked(true);
                alert("Đã thêm vào bookmark");
            } else {
                await Apis.delete(
                    endpoints.bookmark(documentId)
                );

                setBookmarked(false);
                alert("Đã xoá bookmark");
            }
        } catch (err) {
            console.error(err);
            alert("Lỗi! Không thêm được bookmark");
        } finally {
            setBookmark(false);
        }
    };

    const borrowAfterPaymentPassed = async () => {
        await Apis.post(endpoints.borrowDocument(documentId));

        setBorrowed(true);
        setCanViewContent(true);
        setSuccess("Mượn tài liệu thành công. Bạn có thể xem file.");
    };

    const createPaymentForPremiumDocument = async (paymentMethod) => {
        setPaymentLoading(true);

        try {

            const method = paymentMethod.toUpperCase();

            const res = await Apis.post(endpoints.payments, {
                documentId: Number(documentId),
                paymentMethod: method
            });

            const payment = res.data;

            if (payment.paymentStatus === "SUCCESS") {
                setShowPaymentModal(false);
                await borrowAfterPaymentPassed();
                return;
            }

            if (method === "CASH") {
                setShowPaymentModal(false);

                setSuccess(
                    "Yêu cầu thanh toán tiền mặt đã được ghi nhận. " +
                    "Vui lòng đến thư viện để thanh toán. Sau khi thủ thư xác nhận thanh toán, bạn mới có thể mượn và xem nội dung tài liệu."
                );

                return;
            }

            if (method === "STRIPE" && payment.checkoutUrl) {
                sessionStorage.setItem("payment_document_id", String(documentId));
                sessionStorage.setItem("payment_stripe_session_id", payment.stripeSessionId || "");
                sessionStorage.setItem("payment_return_path", `/documents/${documentId}`);

                window.location.href = payment.checkoutUrl;
                return;
            }

            setError("Không tạo được link thanh toán. Vui lòng thử lại.");

        } catch (err) {
            console.error("CREATE PAYMENT ERROR:", err);

            if (!err.response) {
                setError("Không thể kết nối đến server thanh toán.");
                return;
            }

            switch (err.response.status) {
                case 401:
                    setError("Vui lòng đăng nhập để thanh toán.");
                    return;

                case 403:
                    setError(err.response.data?.message || "Bạn không có quyền thanh toán tài liệu này.");
                    return;

                case 404:
                    setError("Không tìm thấy tài liệu cần thanh toán.");
                    return;

                case 422:
                    setError(err.response.data?.message || "Phương thức thanh toán không hợp lệ.");
                    return;

                default:
                    setError(err.response.data?.message || "Tạo thanh toán thất bại.");
                    return;
            }

        } finally {
            setPaymentLoading(false);
        }
    };

    const borrowDocument = async () => {
        if (!user) {
            return nav(`/login?next=/documents/${documentId}`);
        }

        try {
            setBorrowLoading(true);
            setError("");
            setSuccess("");

            const hasPaid = await Apis.get(endpoints.paymentByDocument(documentId));

            if (document?.premium === true && !hasPaid.data) {
                setSelectedPaymentMethod("STRIPE");
                setShowPaymentModal(true);
                return;
            }

            await borrowAfterPaymentPassed();

        } catch (err) {
            console.error("Borrow Error: ", err);

            if (!err.response) {
                setError("Không thể kết nối đến server. Vui lòng thử lại sau.");
                return;
            }

            switch (err.response.status) {
                case 401:
                    setError("Vui lòng đăng nhập để mượn tài liệu.");
                    return;

                case 403:
                    setError(
                        err.response.data?.message ||
                        "Bạn không có quyền mượn tài liệu này."
                    );
                    return;

                case 404:
                    setError("Không tìm thấy tài liệu cần mượn.");
                    return;

                case 409:
                    setBorrowed(true);
                    setCanViewContent(true);
                    setSuccess("Bạn đang mượn tài liệu này rồi. Bạn có thể xem file.");
                    return;

                case 422:
                    setError(err.response.data?.message || "Tài liệu này hiện không thể mượn.");
                    return;

                default:
                    setError(err.response.data?.message || "Không thể mượn tài liệu. Vui lòng thử lại.");
                    return;
            }

        } finally {
            setBorrowLoading(false);
        }
    };

    const checkBorrowStatus = async () => {
        if (!user) {
            setBorrowed(false);
            setCanViewContent(false);
            return;
        }

        try {
            const res = await Apis.get(endpoints.myBorrows);

            const isBorrowed = res.data.some(
                b => Number(b.documentId) === Number(documentId) && b.status === "BORROWING"
            );

            setBorrowed(isBorrowed);
            setCanViewContent(isBorrowed);

        } catch (err) {
            console.error("Check borrow status error:", err);
            setBorrowed(false);
            setCanViewContent(false);
        }
    };

    const confirmPaymentMethod = async () => {
        await createPaymentForPremiumDocument(selectedPaymentMethod);
    };



    useEffect(() => {
        if (!documentId) return;

        loadDocument();
    }, [documentId]);

    useEffect(() => {
        if (!user || !documentId) return;

        checkBookmark();
        checkBorrowStatus();

    }, [user, documentId]);

    useEffect(() => {
        const autoBorrowAfterPayment = async () => {
            if (!user || !documentId || !document || borrowed) {
                return;
            }

            const paymentResult = q.get("payment");

            if (paymentResult !== "success") {
                return;
            }

            try {
                setBorrowLoading(true);
                setError("");
                setSuccess("Thanh toán thành công. Đang hoàn tất mượn tài liệu...");

                await borrowAfterPaymentPassed();

            } catch (err) {
                console.error("AUTO BORROW AFTER PAYMENT ERROR:", err);

                if (err.response?.status === 403) {
                    setError(
                        err.response.data?.message ||
                        "Thanh toán đang được xác nhận. Vui lòng thử bấm Borrow lại sau vài giây."
                    );
                    return;
                }

                setError(
                    err.response?.data?.message ||
                    "Thanh toán thành công nhưng chưa thể mượn tài liệu. Vui lòng thử lại."
                );

            } finally {
                setBorrowLoading(false);
            }
        };

        autoBorrowAfterPayment();
    }, [user, documentId, document]);

    return (
        <>
            <div className="container py-5">

                <div className="bg-white rounded-4 shadow-sm p-4 p-md-5">

                    <Row className="g-5 align-items-start">

                        <Col md={4} lg={3}>
                            <img
                                src={document?.thumbnail}
                                alt={document?.title}
                                className="w-100 rounded-4 shadow-sm"
                                style={{
                                    height: "420px",
                                    objectFit: "cover"
                                }}
                            />
                        </Col>
                        <Col md={8} lg={9}>
                            <h1
                                className="fw-bold mb-3"
                                style={{
                                    fontSize: "3rem",
                                    lineHeight: "1.2"
                                }}
                            >
                                {document?.title}
                            </h1>

                            <div className="d-flex flex-column gap-3 mb-4">

                                <div className="d-flex gap-2">
                                    <span className="fw-bolder fs-5">
                                        Author:
                                    </span>

                                    <span className="fs-5">
                                        {document?.author}
                                    </span>
                                </div>

                                <div className="d-flex gap-2">
                                    <span className="fw-bold fs-5">
                                        Category:
                                    </span>

                                    <span className="fs-5">
                                        {document?.categoryName}
                                    </span>
                                </div>

                                <div className="d-flex gap-2">
                                    <span className="fw-bold fs-5">
                                        Publish year:
                                    </span>

                                    <span className="fs-5">
                                        {document?.publishYear}
                                    </span>
                                </div>
                                <div className="d-flex gap-2">
                                    <span className="fw-bold fs-5">
                                        Format:
                                    </span>

                                    <span className="fs-5">
                                        {document?.documentType}
                                    </span>
                                </div>
                                <div className="d-flex gap-2">
                                    <span className="fw-bold fs-5">
                                        Price:
                                    </span>

                                    <span className="fs-5">
                                        {document?.price === 0
                                            ? "Miễn phí"
                                            : `${document?.price} VNĐ`}
                                    </span>
                                </div>
                                <div className="d-flex gap-2">
                                    <span className="fw-bold fs-5">
                                        Views:
                                    </span>

                                    <span className="fs-5">
                                        {document?.totalViews}
                                    </span>
                                </div>
                                <div className="d-flex gap-2">
                                    <span className="fw-bold fs-5">
                                        Rating:
                                    </span>

                                    <span className="fs-5">
                                        {document?.averageRating}
                                    </span>
                                </div>

                            </div>
                            {error && (<div className="alert alert-danger mt-3">{error}</div>)}

                            {success && (<div className="alert alert-success mt-3">{success}</div>)}

                            <div className="d-flex flex-wrap gap-3">
                                <button
                                    onClick={addorDeleteBookmark}
                                    disabled={bookmark}
                                    className={
                                        bookmarked
                                            ? "btn btn-success rounded-pill px-4 py-2"
                                            : "btn btn-outline-dark rounded-pill px-4 py-2"
                                    }
                                >
                                    {bookmark ? "Saving..." : bookmarked ? "✓ Bookmarked" : "Bookmark"}
                                </button>


                                <button
                                    className="rounded-pill px-4 py-2 fw-semibold"
                                    style={{
                                        backgroundColor: borrowed ? "#0d6efd" : "#fff",
                                        color: borrowed ? "#fff" : "#0d6efd",
                                        border: "1px solid #0d6efd",
                                        opacity: borrowLoading || paymentLoading ? 0.7 : 1
                                    }}
                                    disabled={borrowed || borrowLoading || paymentLoading}
                                    onClick={borrowDocument}
                                >
                                    {paymentLoading
                                        ? "Redirecting payment..."
                                        : borrowLoading
                                            ? "Borrowing..."
                                            : borrowed
                                                ? "✓ Borrowed"
                                                : document?.premium
                                                    ? "Pay & Borrow"
                                                    : "Borrow"}
                                </button>
                                

                            </div>

                        </Col>
                    </Row>

                </div>

            </div>
            <div className="container p-3 mb-2">
                <div className="bg-white rounded-4 shadow-sm p-4">
                    <div className="mb-3">
                        <h4 className="fw-bold mb-4">
                            Description
                        </h4>
                    </div>

                    <div className="fs-5">
                        {document?.description || "Chưa có mô tả cho tài liệu này."}
                    </div>

                </div>
            </div>
            <DocumentFileContent documentId={documentId} canViewContent={canViewContent} />

            <Review documentId={documentId} />

            <Modal show={showPaymentModal}
                onHide={() => {
                    if (!paymentLoading) {
                        setShowPaymentModal(false);
                    }
                }} centered backdrop="static" >
                <Modal.Header closeButton={!paymentLoading}>
                    <Modal.Title>
                        Chọn phương thức thanh toán
                    </Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    <div className="mb-3">
                        <div className="fw-semibold mb-2"> Tài liệu premium </div>

                        <div className="text-muted"> Bạn cần thanh toán trước khi mượn tài liệu này. </div>
                    </div>

                    <Form>
                        <div className="border rounded-3 p-3 mb-3">
                            <Form.Check type="radio" id="payment-stripe"
                                name="paymentMethod"
                                label="Thanh toán online bằng Stripe"
                                value="STRIPE"
                                checked={selectedPaymentMethod === "STRIPE"}
                                onChange={(e) => setSelectedPaymentMethod(e.target.value)}
                                disabled={paymentLoading}
                            />

                            <div className="text-muted small ms-4">
                                Hệ thống sẽ chuyển bạn sang trang Stripe Checkout để thanh toán.
                            </div>
                        </div>

                        <div className="border rounded-3 p-3">
                            <Form.Check
                                type="radio"
                                id="payment-cash"
                                name="paymentMethod"
                                label="Thanh toán tiền mặt tại thư viện"
                                value="CASH"
                                checked={selectedPaymentMethod === "CASH"}
                                onChange={(e) => setSelectedPaymentMethod(e.target.value)}
                                disabled={paymentLoading}
                            />

                            <div className="text-muted small ms-4">
                                Hệ thống sẽ tạo yêu cầu thanh toán ở trạng thái PENDING.
                                Bạn cần đến thư viện để thanh toán và chờ thủ thư xác nhận.
                            </div>
                        </div>
                    </Form>
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="outline-secondary" onClick={() => setShowPaymentModal(false)} disabled={paymentLoading} >
                        Hủy
                    </Button>

                    <Button variant="primary" onClick={confirmPaymentMethod} disabled={paymentLoading} >
                        {paymentLoading
                            ? "Đang xử lý..." : selectedPaymentMethod === "STRIPE" ? "Tiếp tục thanh toán Stripe" : "Tạo yêu cầu thanh toán tiền mặt"}
                    </Button>
                </Modal.Footer>
            </Modal>

        </>

    );

}


export default DocumentDetail;