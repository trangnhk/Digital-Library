import { useContext, useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { Badge, Button, Card, Col, Image, ListGroup, Row } from "react-bootstrap";
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

    const createPaymentForPremiumDocument = async () => {
        setPaymentLoading(true);

        try {
            const res = await Apis.post(endpoints.payments, {
                documentId: Number(documentId),
                paymentMethod: "STRIPE"
            });

            const payment = res.data;

            if (payment.paymentStatus === "SUCCESS") {
                await borrowAfterPaymentPassed();
                return;
            }

            if (payment.checkoutUrl) {
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
                await createPaymentForPremiumDocument();
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

        </>

    );

}


export default DocumentDetail;