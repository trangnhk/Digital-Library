import { useEffect, useState } from "react";
import {Alert, Badge, Button, Card, Col, Form, Image, Pagination, Row, Spinner} from "react-bootstrap";
import { Link, useSearchParams } from "react-router-dom";

import Apis, { endpoints } from "../../configs/Apis";

const Home = () => {
    const [documentsPage, setDocumentsPage] = useState(null);
    const [documents, setDocuments] = useState([]);
    const [categories, setCategories] = useState([]);

    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");

    const [q, setQ] = useSearchParams();

    const [filters, setFilters] = useState({
        keyword: q.get("keyword") || "",
        categoryId: q.get("categoryId") || "",
        documentType: q.get("documentType") || "",
        publishYear: q.get("publishYear") || "",
        sort: q.get("sort") || "popular",
        page: q.get("page") || "1",
        size: q.get("size") || "10"
    });

    const loadCategories = async () => {
        try {
            const res = await Apis.get(endpoints.categories, {
                params: {
                    page: 1,
                    size: 20
                }
            });

            const data = res.data.items ? res.data.items : res.data;

            setCategories(data);

        } catch (ex) {
            console.error(ex);
        }
    };

    const loadDocuments = async () => {
        try {
            setLoading(true);
            setErr("");

            const params = buildParams();

            const res = await Apis.get(endpoints.documents, {
                params: params
            });

            setDocumentsPage(res.data);
            setDocuments(res.data.items || []);

        } catch (ex) {
            console.error(ex);

            if (ex.response && ex.response.data && ex.response.data.message) {
                setErr(ex.response.data.message);
            } else {
                setErr("Không thể tải danh sách tài liệu.");
            }

        } finally {
            setLoading(false);
        }
    };

    const buildParams = () => {
        const params = {
            page: filters.page,
            size: filters.size,
            sort: filters.sort
        };

        if (filters.keyword.trim()) {
            params.keyword = filters.keyword.trim();
        }

        if (filters.categoryId) {
            params.categoryId = filters.categoryId;
        }

        if (filters.documentType) {
            params.documentType = filters.documentType;
        }

        if (filters.publishYear) {
            params.publishYear = filters.publishYear;
        }

        return params;
    };

    useEffect(() => {
        loadCategories();
    }, []);

    useEffect(() => {
        loadDocuments();
    }, [q]);

    const updateField = (field, value) => {
        setFilters({
            ...filters,
            [field]: value
        });
    };

    const applyFilter = (e) => {
        e.preventDefault();

        const params = buildParams();
        params.page = "1";

        setFilters({...filters, page: "1"});

        setQ(params);
    };

    const clearFilter = () => {
        const defaultFilters = {
            keyword: "",
            categoryId: "",
            documentType: "",
            publishYear: "",
            sort: "popular",
            page: "1",
            size: "10"
        };

        setFilters(defaultFilters);

        setQ({
            page: "1",
            size: "10",
            sort: "popular"
        });
    };

    const changePage = (newPage) => {
        const params = buildParams();

        params.page = String(newPage);

        setFilters({
            ...filters,
            page: String(newPage)
        });

        setQ(params);
    };

    const getDocumentTypeLabel = (documentType) => {
        return documentType || "N/A";
    };

    return (
        <div className="py-4">

            
            <Card className="shadow-sm border-0 rounded-4 mb-4">
                <Card.Body className="p-4">
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                        <div>
                            <h3 className="fw-bold mb-1">Digital Library</h3>

                            <p className="text-muted mb-0">Tìm kiếm và truy cập học liệu số.</p>
                        </div>

                        <Badge bg="primary" className="px-3 py-2 rounded-pill">Documents</Badge>
                    </div>

                    <Form onSubmit={applyFilter}>
                        <Row className="g-3">
                            <Col md={9}>
                                <Form.Control
                                    type="text"
                                    placeholder="Search keyword..."
                                    value={filters.keyword}
                                    onChange={(e) => updateField("keyword", e.target.value)}
                                />
                            </Col>

                            <Col md={3} className="d-grid">
                                <Button type="submit" variant="primary">Tìm kiếm</Button>
                            </Col>

                            <Col md={3}>
                                <Form.Label className="fw-semibold">Category</Form.Label>

                                <Form.Select
                                    value={filters.categoryId}
                                    onChange={(e) => updateField("categoryId", e.target.value)}
                                >
                                    <option value="">All</option>

                                    {categories.map(c => (
                                        <option key={c.id} value={c.id}>
                                            {c.name}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Col>

                            <Col md={2}>
                                <Form.Label className="fw-semibold">Type</Form.Label>

                                <Form.Select
                                    value={filters.documentType}
                                    onChange={(e) => updateField("documentType", e.target.value)}
                                >
                                    <option value="">
                                        All
                                    </option>

                                    <option value="PDF">
                                        PDF
                                    </option>

                                    <option value="DOCX">
                                        DOCX
                                    </option>

                                    <option value="EPUB">
                                        EPUB
                                    </option>

                                    <option value="VIDEO">
                                        VIDEO
                                    </option>

                                    <option value="AUDIO">
                                        AUDIO
                                    </option>
                                </Form.Select>
                            </Col>

                            <Col md={2}>
                                <Form.Label className="fw-semibold">
                                    Year
                                </Form.Label>

                                <Form.Control
                                    type="number"
                                    placeholder="2026"
                                    value={filters.publishYear}
                                    onChange={(e) => updateField("publishYear", e.target.value)}
                                />
                            </Col>

                            <Col md={2}>
                                <Form.Label className="fw-semibold">
                                    Sort
                                </Form.Label>

                                <Form.Select
                                    value={filters.sort}
                                    onChange={(e) => updateField("sort", e.target.value)}
                                >
                                    <option value="popular">
                                        Popular
                                    </option>

                                    <option value="newest">
                                        Newest
                                    </option>

                                    <option value="title">
                                        Title
                                    </option>

                                    <option value="publishYear">
                                        Publish Year
                                    </option>
                                </Form.Select>
                            </Col>

                            <Col md={3} className="d-flex align-items-end gap-2">
                                <Button type="submit" variant="primary" className="flex-fill">
                                    Lọc
                                </Button>

                                <Button
                                    type="button"
                                    variant="outline-secondary"
                                    className="flex-fill"
                                    onClick={clearFilter}
                                >
                                    Xóa lọc
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>

            {err && (
                <Alert variant="danger">
                    {err}
                </Alert>
            )}

            {loading && (
                <div className="text-center py-5">
                    <Spinner animation="border" />
                    <div className="text-muted mt-2">
                        Đang tải tài liệu...
                    </div>
                </div>
            )}

            {!loading && (
                <>
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h5 className="fw-bold mb-0">
                            Danh sách tài liệu
                        </h5>

                        <span className="text-muted">
                            Total: {documentsPage?.totalItems || 0}
                        </span>
                    </div>

                    <Row className="g-4">
                        {documents.map(d => (
                            <Col key={d.id} xs={12} md={6} lg={4}>
                                <Card className="h-100 shadow-sm border-0 rounded-4 overflow-hidden">
                                    <Image
                                        src={d.thumbnail}
                                        alt={d.title}
                                        style={{
                                            height: "180px",
                                            objectFit: "cover"
                                        }}
                                    />

                                    <Card.Body className="d-flex flex-column">
                                        <div className="d-flex justify-content-between align-items-start gap-2 mb-2">
                                            <Badge bg="secondary">
                                                {getDocumentTypeLabel(d.documentType)}
                                            </Badge>

                                            {d.premium ? (
                                                <Badge bg="warning" text="dark">
                                                    Premium
                                                </Badge>
                                            ) : (
                                                <Badge bg="success">
                                                    Free
                                                </Badge>
                                            )}
                                        </div>

                                        <Card.Title className="fw-bold">
                                            {d.title}
                                        </Card.Title>

                                        <Card.Text className="text-muted mb-2">
                                            {d.author || "Unknown author"}
                                        </Card.Text>

                                        <div className="small text-muted mb-3">
                                            <div>
                                                Category: {d.categoryName || "N/A"}
                                            </div>

                                            <div>
                                                Year: {d.publishYear || "N/A"}
                                            </div>

                                            <div>
                                                Views: {d.totalViews || 0}
                                            </div>

                                            {d.averageRating !== undefined && (
                                                <div>
                                                    Rating: {d.averageRating}
                                                </div>
                                            )}
                                        </div>

                                        <div className="mt-auto d-grid">
                                            <Button
                                                as={Link}
                                                to={`/documents/${d.id}`}
                                                variant="outline-primary"
                                            >
                                                Xem
                                            </Button>
                                        </div>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>

                    {documents.length === 0 && (
                        <Alert variant="warning" className="mt-3">
                            Không tìm thấy tài liệu phù hợp.
                        </Alert>
                    )}
                </>
            )}

            {!loading && documentsPage && documentsPage.totalPages > 0 && (
                <div className="d-flex justify-content-between align-items-center mt-4">
                    <Button
                        variant="outline-secondary"
                        disabled={documentsPage.page <= 1}
                        onClick={() => changePage(documentsPage.page - 1)}
                    >
                        Trang trước
                    </Button>

                    <Pagination className="mb-0">
                        <Pagination.Item active>
                            Page {documentsPage.page}/{documentsPage.totalPages}
                        </Pagination.Item>
                    </Pagination>

                    <Button
                        variant="outline-secondary"
                        disabled={documentsPage.page >= documentsPage.totalPages}
                        onClick={() => changePage(documentsPage.page + 1)}
                    >
                        Trang sau
                    </Button>
                </div>
            )}
        </div>
    );
};

export default Home;