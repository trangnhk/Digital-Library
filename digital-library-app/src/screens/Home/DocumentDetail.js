import { useContext, useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { Badge, Button, Card, Col, Image, ListGroup, Row } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Context";
import DocumentFileContent from "./DocumentFileContent";

const DocumentDetail = () => {
    const { documentId } = useParams();
    const [document, setDocument] = useState(null);

    const [bookmark, setBookmark] = useState(false);
    const [bookmarked, setBookmarked] = useState(false);
    const [user,] = useContext(MyUserContext);
    const [borrowed, setBorrowed] = useState(false);
    const [borrowLoading, setBorrowLoading] = useState(false);
    const [canViewContent, setCanViewContent] = useState(false);
    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState("");
    const [editingReview, setEditingReview] = useState(null);
    const nav = useNavigate();
    const [page, setPage] = useState(1);
    const size = 2;

    const [reviews, setReviews] = useState([])
    const [reviewsPage, setReviewsPage] = useState(null);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const [q] = useSearchParams();
    const [paymentLoading, setPaymentLoading] = useState(false);

    const loadDocument = async () => {
        let res = await Apis.get(endpoints['documentDetails'](documentId));
        setDocument(res.data);
    }
    const loadReviews = async (page) => {
        try {
            console.log("LOAD PAGE:", page);

            const res = await Apis.get(
                endpoints['documentReviews'](documentId),
                {
                    params: {
                        page,
                        size
                    }
                }
            );

            const data = res.data;
            setReviews(Array.isArray(data) ? data : data.content);

            const totalPages = data.totalPages
                || Math.ceil((data.totalItems || data.length || 1) / size);

            setReviewsPage({
                page: page,
                totalPages: totalPages
            });

        } catch (err) {
            console.error(err);
        }
    };
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

    const getMyReview = () => {
        if (!user) return null;

        return reviews.find(r => r.userId === user.userId);
    };

    const addReview = async () => {
        if (!user) {
            nav("/login");
            return;
        }
        const myReview = getMyReview();
        if (reviews && !editingReview) {
            alert("Đã review tài liệu rồi, bạn có thể sửa hoặc xoá review cũ để thêm review mới.");
        }

        try {
            setError("");
            setSuccess("");

            if (editingReview) {
                await Apis.patch(endpoints.editReview(editingReview), {
                    rating,
                    comment
                });

                setSuccess("Đã cập nhật review!");
            } else {
                await Apis.post(endpoints.addReview,
                    {
                        documentId,
                        rating,
                        comment
                    }
                );

                setSuccess("Đã thêm review!");
            }

            setRating(0);
            setComment("");
            setEditingReview(null);

            await loadReviews(page);

        } catch (err) {
            console.error(err);
            setError("Không thể xử lý review.");
        }
    };
    const editReview = (r) => {
        setEditingReview(r.id);
        setRating(r.rating);
        setComment(r.comment);
    };
    const deleteReview = async (id) => {
        if (!window.confirm("Xóa review này?")) return;

        try {
            await Apis.delete(endpoints.editReview(id));

            setSuccess("Đã xoá review!");

            if (editingReview === id) {
                setEditingReview(null);
                setRating(0);
                setComment("");
            }

            loadReviews(page);
        } catch (err) {
            console.error(err);
            setError("Không thể xoá review.");
        }
    };
    const changePage = (newPage) => {
        setPage(newPage);
    };

    const getCurrentPage = () => page;

    const getTotalPages = () => {
        return reviewsPage?.totalPages || 1;
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
        if (!documentId) return;

        loadReviews(page);
    }, [documentId, page]);

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

            <div className="container p-3 mb-2">
                <h4 className="fw-bold mb-4">
                    Review
                </h4>

                <div className="mb-3">
                    <div className="d-flex align-items-baseline gap-3 mb-3">

                        <span
                            className="fw-semibold fs-5"
                            style={{ marginTop: "-4px" }}
                        >
                            Rating:
                        </span>

                        <div
                            className="d-flex align-items-center"
                            style={{ fontSize: "32px", lineHeight: 1 }}
                        >
                            {[1, 2, 3, 4, 5].map(star => (
                                <span
                                    key={star}
                                    onClick={() => setRating(star)}
                                    style={{
                                        cursor: "pointer",
                                        color:
                                            star <= rating
                                                ? "#ffc107"
                                                : "#dee2e6"
                                    }}
                                >
                                    ★
                                </span>
                            ))}
                        </div>

                    </div>

                </div>

                <div className="mb-3">
                    <textarea
                        className="form-control"
                        rows="4"
                        placeholder="Write your review..."
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                    />
                </div>

                <button
                    className="btn btn-primary"
                    onClick={addReview}
                >
                    {editingReview ? "Update Review" : "Submit Review"}
                </button>
            </div>
            <div className="container p-3 mb-4">
                <div className="bg-white rounded-4 shadow-sm p-4">

                    <h4 className="fw-bold mb-4">
                        Reviews ({reviews.length})
                    </h4>
                    <Card.Body className="p-0">

                        {reviews.length === 0 ? (
                            <p className="text-muted">
                                Chưa có đánh giá nào.
                            </p>
                        ) : (
                            <ListGroup variant="flush">
                                {reviews.map(r => (
                                    <ListGroup.Item key={r.id} className="py-3 border-0 border-bottom">
                                        <div className="d-flex">
                                            <Image
                                                src={r.avatar}
                                                roundedCircle
                                                width={55}
                                                height={55}
                                                className="me-3"
                                            />

                                            <div className="flex-grow-1">
                                                <div className="d-flex justify-content-between">
                                                    <h6 className="fw-bold mb-1">{r.username}</h6>
                                                    <small className="text-muted">{r.createdAt}</small>
                                                </div>

                                                <div className="text-warning mb-2">
                                                    {"★".repeat(r.rating)}
                                                    {"☆".repeat(5 - r.rating)}
                                                </div>

                                                <p className="mb-0">{r.comment}</p>
                                            </div>

                                            {user && user?.userId === r.userId && (
                                                <div className="mt-5">
                                                    <button
                                                        className="btn btn-sm btn-outline-primary me-2"
                                                        onClick={() => editReview(r)}
                                                    >
                                                        Sửa
                                                    </button>

                                                    <button
                                                        className="btn btn-sm btn-outline-danger"
                                                        onClick={() => deleteReview(r.id)}
                                                    >
                                                        Xóa
                                                    </button>
                                                </div>
                                            )}
                                        </div>
                                    </ListGroup.Item>
                                ))}
                            </ListGroup>
                        )}
                    </Card.Body>

                </div>
            </div>
            <div className="d-flex justify-content-between align-items-center mt-3">
                <button
                    className="btn btn-outline-secondary"
                    disabled={page <= 1}
                    onClick={() => changePage(page - 1)}
                >
                    Trang trước
                </button>

                <span>
                    Page {page} / {getTotalPages()}
                </span>

                <button
                    className="btn btn-outline-secondary"
                    disabled={page >= getTotalPages()}
                    onClick={() => changePage(page + 1)}
                >
                    Trang sau
                </button>
            </div>

        </>

    );

}


export default DocumentDetail;