import { useContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Badge, Col, Image, ListGroup, Row } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Context";

const DocumentDetail = () => {
    const { documentId } = useParams();
    const [document, setDocument] = useState(null);
    const [reviews, setReviews] = useState([])
    const [bookmark, setBookmark] = useState(false);
    const [bookmarked, setBookmarked] = useState(false);
    const [user,] = useContext(MyUserContext);

    const loadDocument = async () => {
        let res = await Apis.get(endpoints['documentDetails'](documentId));
        setDocument(res.data);
    }
    const loadReviews = async () => {
        let res = await Apis.get(endpoints['documentReviews'](documentId));
        setReviews(res.data);
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



    useEffect(() => {
        loadDocument();
        loadReviews();

        if (user) {
            checkBookmark();
        }

    }, [documentId]);

    return (
        <>
            <div className="container py-5">

                <div className="bg-white rounded-4 shadow-sm p-4 p-md-5">

                    <Row className="g-5 align-items-start">

                        {/* IMAGE */}
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

                            <div className="d-flex flex-wrap gap-3">

                                <button className="btn btn-dark rounded-pill px-4 py-2 fw-semibold">
                                    Access
                                </button>

                                <button
                                    onClick={addorDeleteBookmark}
                                    disabled={bookmark}
                                    className={
                                        bookmarked
                                            ? "btn btn-success rounded-pill px-4 py-2"
                                            : "btn btn-outline-dark rounded-pill px-4 py-2"
                                    }
                                >
                                    {bookmark
                                        ? "Saving..."
                                        : bookmarked
                                            ? "✓ Bookmarked"
                                            : "Bookmark"}
                                </button>

                                <button className="btn btn-outline-primary rounded-pill px-4 py-2">
                                    Borrow
                                </button>

                            </div>

                        </Col>
                    </Row>

                </div>

            </div>
            <div className="container p-3 mb-2">
                <div className="bg-white rounded-4 shadow-sm p-4">
                    <div className="mb-3">
                        <span className="fw-bold fs-5 text-dark">
                            Description
                        </span>
                    </div>

                    <div className="fs-5">
                        {document?.description || "Chưa có mô tả cho tài liệu này."}
                    </div>

                </div>
            </div>
            <div className="container p-3 mb-4">
                <div className="bg-white rounded-4 shadow-sm p-4">

                    <h4 className="fw-bold mb-4">
                        Reviews ({reviews.length})
                    </h4>

                    {reviews.length === 0 ? (
                        <p className="text-muted">
                            Chưa có đánh giá nào.
                        </p>
                    ) : (
                        <ListGroup variant="flush">

                            {reviews.map(r => (
                                <ListGroup.Item
                                    key={r.id}
                                    className="py-3 border-0 border-bottom"
                                >
                                    <div className="d-flex">

                                        <Image
                                            src={r.avatar}
                                            roundedCircle
                                            width={55}
                                            height={55}
                                            className="me-3"
                                            style={{
                                                objectFit: "cover"
                                            }}
                                        />

                                        <div className="flex-grow-1">

                                            <div className="d-flex justify-content-between">
                                                <h6 className="fw-bold mb-1">
                                                    {r.username}
                                                </h6>

                                                <small className="text-muted">
                                                    {r.createdAt}
                                                </small>
                                            </div>

                                            <div
                                                className="text-warning mb-2"
                                                style={{ fontSize: "18px" }}
                                            >
                                                {"★".repeat(r.rating)}
                                                {"☆".repeat(5 - r.rating)}
                                            </div>

                                            <p className="mb-0">
                                                {r.comment}
                                            </p>

                                        </div>

                                    </div>
                                </ListGroup.Item>
                            ))}

                        </ListGroup>
                    )}

                </div>
            </div>

        </>

    );

}


export default DocumentDetail;