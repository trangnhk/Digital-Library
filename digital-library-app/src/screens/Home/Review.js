import { useContext, useEffect, useRef, useState } from "react";
import { MyUserContext } from "../../configs/Context";
import { useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { Alert, Button, Card, Image, ListGroup } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import moment from "moment";
import "moment/locale/vi";

const Review = ({ documentId }) => {
    const [user] = useContext(MyUserContext);
    const [reviews, setReviews] = useState([]);
    const [page, setPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [totalItems, setTotalItems] = useState(0);

    const [rating, setRating] = useState(0);
    const [comment, setComment] = useState("");
    const [editingReview, setEditingReview] = useState(null);

    const [loading, setLoading] = useState(false);
    const [loadingMore, setLoadingMore] = useState(false);
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");

    const size = 10;
    const scrollRef = useRef(null);

    const nav = useNavigate();
    moment.locale("vi");

    useEffect(() => {
        if (!documentId)
            return;
        resetAndLoadReviews();


    }, [documentId]);

    const formatTimeAgo = (time) => {
        if (!time) {
            return "Không xác định";
        }

        const parsedTime = moment(
            time,
            [
                "DD-MM-YYYY HH:mm:ss",
                "YYYY-MM-DD HH:mm:ss",
                "YYYY-MM-DD HH:mm:ss.SSSSSS"
            ], true
        );

        if (!parsedTime.isValid()) {
            return time;
        }

        return parsedTime.add(7, "hours").fromNow();
    };

    const resetAndLoadReviews = () => {
        setPage(1);
        setTotalPages(1);
        setTotalItems(0);
        setReviews([]);
        loadReviews(1);
    }

    const loadReviews = async (page = 1, reset = false) => {
        try {
            if (reset)
                setLoading(true);
            else
                setLoadingMore(true);

            setErr("");

            const res = await Apis.get(endpoints.documentReviews(documentId), {
                params: {
                    page: page,
                    size: size
                }
            });

            const data = res.data;
            const newReviews = data.items || [];

            if (reset)
                setReviews(newReviews);
            else {
                setReviews(prev => {
                    const map = new Map();
                    [...prev, ...newReviews].forEach(item => { map.set(item.id, item); });
                    return Array.from(map.values());
                });
            }

            setPage(data.page || page);
            setTotalPages(data.totalPages || 1);
            setTotalItems(data.totalItems || newReviews.length);

        } catch (err) {
            console.error(err);
            setErr("Không thể tải review");
        } finally {
            setLoading(false);
            setLoadingMore(false);
        }



    };

    const handleScroll = (e) => {
        const items = e.target;

        const isNearBottom = items.scrollTop + items.clientHeight >= items.scrollHeight - 40;

        if (isNearBottom && !loading && !loadingMore && page < totalPages) {
            loadReviews(page + 1, false);
        }
    };

    const getCurrentUserId = () => {
        return user?.userId || user?.id || null;
    };

    const getMyLoadedReview = () => {
        if (!user)
            return null;

        return reviews.find(r => r.userId === getCurrentUserId()) || null;

    };

    const validateReview = () => {
        if (!rating || rating < 1 || rating > 5) {
            setErr("Vui lòng chọn rating từ 1 đến 5 sao.");
            return false;
        }

        if (!comment.trim()) {
            setErr("Vui lòng nhập nội dung review.");
            return false;
        }

        return true;
    };

    const submitReview = async () => {
        if (!user) {
            nav(`/login?next=/documents/${documentId}`);
            return;
        }

        setErr("");
        setSuccess("");

        if (!validateReview()) {
            return;
        }

        try {
            if (editingReview) {
                await Apis.patch(endpoints.editReview(editingReview), {
                    rating: rating,
                    comment: comment
                });

                setSuccess("Đã cập nhật review.");

            } else {
                const myLoadedReview = getMyLoadedReview();

                if (myLoadedReview) {
                    setErr("Bạn đã review tài liệu này. Hãy bấm Sửa review cũ.");
                    return;
                }

                await Apis.post(endpoints.addReview, {
                    documentId: Number(documentId),
                    rating: rating,
                    comment: comment
                });

                setSuccess("Đã thêm review.");
            }

            setRating(0);
            setComment("");
            setEditingReview(null);

            await resetAndLoadReviews();

            if (scrollRef.current) {
                scrollRef.current.scrollTop = 0;
            }

        } catch (ex) {
            console.error("SUBMIT REVIEW ERROR:", ex);

            switch (ex.response?.status) {
                case 401:
                    setErr(ex.response.data?.message || "Vui lòng đăng nhập để review");
                    return;
                case 409:
                    setErr(ex.response.data?.message || "Bạn đã review tài liệu này.");
                    return;

                default:
                    setErr(ex.response?.data?.message || "Không thể xử lý review.");
                    return;
            }
        }
    };

    const editReview = (review) => {
        setEditingReview(review.id);
        setRating(review.rating);
        setComment(review.comment || "");

        setErr("");
        setSuccess("");
    };

    const cancelEdit = () => {
        setEditingReview(null);
        setRating(0);
        setComment("");
        setErr("");
    };

    const deleteReview = async (reviewId) => {
        if (!window.confirm("Bạn đồng ý xóa review này không?")) {
            return;
        }

        try {
            setErr("");
            setSuccess("");

            await Apis.delete(endpoints.editReview(reviewId));

            setSuccess("Đã xóa review.");

            if (editingReview === reviewId) {
                cancelEdit();
            }

            await resetAndLoadReviews();

        } catch (ex) {
            console.error("DELETE REVIEW ERROR:", ex);

            if (ex.response?.status === 401) {
                setErr("Vui lòng đăng nhập.");
                return;
            }

            if (ex.response?.status === 403) {
                setErr(ex.response.data?.message || "Bạn không có quyền xóa review này.");
                return;
            }

            setErr(ex.response?.data?.message || "Không thể xóa review.");
        }
    };

    return (
        <div className="container p-3 mb-4">
            <Card className="border-0 shadow-sm rounded-4">
                <Card.Body className="p-4">
                    <h4 className="fw-bold mb-4">Review</h4>

                    {err && (<Alert variant="danger">{err}</Alert>)}

                    {success && (<Alert variant="success">{success}</Alert>)}

                    <div className="mb-3">
                        <div className="d-flex align-items-baseline gap-3 mb-3">
                            <span className="fw-semibold fs-5" style={{ marginTop: "-4px" }}>Rating:</span>

                            <div className="d-flex align-items-center" style={{ fontSize: "32px", lineHeight: 1 }}
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
                        <textarea className="form-control" rows="4" placeholder="Write your review..." value={comment} onChange={(e) => setComment(e.target.value)} />
                    </div>

                    <div className="d-flex gap-2 mb-4">
                        <Button variant="primary" onClick={submitReview} >
                            {editingReview ? "Update Review" : "Submit Review"}
                        </Button>

                        {editingReview && (
                            <Button variant="outline-secondary" onClick={cancelEdit} >
                                Hủy sửa
                            </Button>
                        )}
                    </div>

                    <div ref={scrollRef} onScroll={handleScroll} style={{ maxHeight: "520px", overflowY: "auto" }} >
                        {loading ? (
                            <div className="text-center py-4">
                                <MySpinner />
                                <div className="text-muted mt-2">Đang tải review...</div>
                            </div>
                        ) : (
                            <>
                                {reviews.length === 0 ? (<p className="text-muted mb-0">Chưa có đánh giá nào.</p>
                                ) : (
                                    <ListGroup variant="flush">
                                        {reviews.map(r => (
                                            <ListGroup.Item key={r.id} className="py-3 border-0 border-bottom" >
                                                <div className="d-flex">
                                                    <Image src={r.avatar} roundedCircle width={55} height={55} className="me-3" style={{ objectFit: "cover" }} />

                                                    <div className="flex-grow-1">
                                                        <div className="d-flex justify-content-between">
                                                            <h6 className="fw-bold mb-1"> {r.username} </h6>
                                                            <small className="text-muted" title={r.createdAt}>{formatTimeAgo(r.createdAt)}</small>
                                                        </div>

                                                        <div className="text-warning mb-2">
                                                            {"★".repeat(r.rating)}
                                                            {"☆".repeat(5 - r.rating)}
                                                        </div>

                                                        <p className="mb-0"> {r.comment} </p>
                                                    </div>

                                                    {user && Number(getCurrentUserId()) === Number(r.userId) && (
                                                        <div className="ms-3 d-flex flex-column gap-2">
                                                            <Button size="sm" variant="primary" onClick={() => editReview(r)} >
                                                                Sửa
                                                            </Button>

                                                            <Button size="sm" variant="danger" onClick={() => deleteReview(r.id)} >
                                                                Xóa
                                                            </Button>
                                                        </div>
                                                    )}
                                                </div>
                                            </ListGroup.Item>
                                        ))}
                                    </ListGroup>
                                )}

                                {loadingMore && (
                                    <div className="text-center py-3">
                                        <MySpinner />
                                        <div className="text-muted small mt-2">Đang tải thêm review...</div>
                                    </div>
                                )}

                                {!loadingMore && page >= totalPages && reviews.length > 0 && (
                                    <div className="text-center text-muted small py-3">Đã hiển thị tất cả review.</div>
                                )}
                            </>
                        )}
                    </div>
                </Card.Body>
            </Card>
        </div>
    );


}

export default Review;