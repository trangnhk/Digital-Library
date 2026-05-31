import { useContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button, Card, Col, Row } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import { MyUserContext } from "../../configs/Context";
import MySpinner from "../../components/MySpinner";

const Bookmark = () => {
    const [bookmarks, setBookmarks] = useState([]);
    const [loading, setLoading] = useState(false);

    const [user] = useContext(MyUserContext);
    const navigate = useNavigate();

    const loadBookmarks = async () => {
        try {
            setLoading(true);

            const res = await Apis.get(
                endpoints.myBookmarks
            );

            const bookmarksWithDocument = await Promise.all(
                res.data.map(async (b) => {
                    const docRes = await Apis.get(
                        endpoints.documentDetails(b.documentId)
                    );

                    return {
                        ...b,
                        document: docRes.data
                    };
                })
            );

            setBookmarks(bookmarksWithDocument);

        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const removeBookmark = async (documentId) => {
        try {
            await Apis.delete(
                endpoints.bookmark(documentId)
            );

            setBookmarks(prev =>
                prev.filter(
                    b => b.documentId !== documentId
                )
            );

        } catch (err) {
            console.error(err);
            alert("Không thể xoá bookmark");
        }
    };

    useEffect(() => {
        if (!user) {
            navigate("/login");
            return;
        }

        loadBookmarks();
    }, [user]);

    return (
        <div className="container py-4">

            <h2 className="fw-bold mb-4">
                My Bookmarks
            </h2>

            {loading ? (
                <div className="d-flex justify-content-center py-5"><MySpinner /></div>

            ) : bookmarks.length === 0 ? (
                <div className="alert alert-info">
                    Chưa có tài liệu yêu thích.
                </div>
            ) : (
                <Row>
                    {bookmarks.map(b => (
                        <Col md={6} lg={4} key={b.documentId}>
                            <Card className="shadow-sm mb-4">

                                <Card.Img
                                    variant="top"
                                    src={b.document?.thumbnail}
                                    style={{
                                        height: "220px",
                                        objectFit: "cover"
                                    }}
                                />

                                <Card.Body>
                                    <div className="d-flex justify-content-between align-items-center mb-2">
                                        <Card.Title>
                                            {b.document?.title}
                                        </Card.Title>
                                        <span className="badge bg-primary">
                                            {b.document?.documentType}
                                        </span>
                                    </div>

                                    <Card.Text>
                                        {b.document?.author}
                                    </Card.Text>

                                    <div className="d-flex gap-2">

                                        <Button
                                            variant="dark"
                                            onClick={() =>
                                                navigate(
                                                    `/documents/${b.documentId}`
                                                )
                                            }
                                        >
                                            View
                                        </Button>

                                        <Button
                                            variant="outline-danger"
                                            onClick={() =>
                                                removeBookmark(
                                                    b.documentId
                                                )
                                            }
                                        >
                                            Remove
                                        </Button>

                                    </div>

                                </Card.Body>

                            </Card>
                        </Col>
                    ))}

                </Row>
            )}

        </div>
    );
};

export default Bookmark;