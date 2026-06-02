import { useEffect, useState } from "react";
import Apis, { endpoints } from "../../configs/Apis";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { Alert, Badge, Button, Card, Col, Form, Image, Row, Table } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import EditDocument from "./EditDocument";

const LibrarianDashboard = () => {
    const [documentsPage, setDocumentsPage] = useState(null);
    const [documents, setDocuments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");
    const [q, setQ] = useSearchParams();
    const nav = useNavigate();

    const [filters, setFilters] = useState({
        keyword: "",
        approved: "",
        page: "1",
        size: "10"
    });

    const [showEditModal, setShowEditModal] = useState(false);
    const [editingDocument, setEditingDocument] = useState(null);


    useEffect(() => {
        const currentFilters = {
            keyword: q.get("keyword") || "",
            approved: q.get("approved") || "",
            page: q.get("page") || "1",
            size: q.get("size") || "10"
        };

        setFilters(currentFilters);
        loadDocuments(currentFilters);
    }, [q]);

    const buildParams = (currentFilters) => {
        const params = {
            page: currentFilters.page,
            size: currentFilters.size
        };

        if (currentFilters.keyword.trim()) {
            params.keyword = currentFilters.keyword.trim();
        }

        if (currentFilters.approved !== "") {
            params.approved = currentFilters.approved;
        }

        return params;
    };

    const loadDocuments = async (currentFilters) => {
        try {
            setLoading(true);
            setErr("");
            setSuccess("");

            const res = await Apis.get(endpoints.librarianDocuments, {
                params: buildParams(currentFilters)
            });


            setDocumentsPage(res.data);
            setDocuments(res.data.items || []);


        } catch (ex) {
            console.error(ex);

            if (ex.response && ex.response.status === 403) {
                setErr("Tài khoản thủ thư của bạn chưa được admin duyệt hoặc bạn không có quyền truy cập chức năng này.");
                return;
            }

            if (ex.response && ex.response.status === 401) {
                setErr("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.");
                return;
            }

            if (ex.response && ex.response.data && ex.response.data.message) {
                setErr(ex.response.data.message);
                return;
            }

            setErr("Không thể tải danh sách tài liệu của bạn.");

        } finally {
            setLoading(false);
        }
    };

    const updateFilter = (field, value) => {
        setFilters({
            ...filters,
            [field]: value
        });
    };

    const applyFilter = (e) => {
        e.preventDefault();

        const newFilters = {
            ...filters,
            page: "1"
        };

        setFilters(newFilters);
        setQ(buildParams(newFilters));
    };

    const clearFilter = () => {
        const defaultFilters = {
            keyword: "",
            approved: "",
            page: "1",
            size: "10"
        };

        setFilters(defaultFilters);
        setQ({
            page: "1",
            size: "10"
        });
    };

    const changePage = (newPage) => {
        const newFilters = {
            ...filters,
            page: String(newPage)
        };

        setFilters(newFilters);
        setQ(buildParams(newFilters));
    };

    const deleteDocument = async (documentId, title) => {
        const confirmed = window.confirm(
            `Bạn có chắc muốn xóa tài liệu "${title}" không?`
        );

        if (!confirmed) {
            return;
        }

        try {
            setLoading(true);
            setErr("");
            setSuccess("");

            await Apis.delete(endpoints.librarianDocumentDetails(documentId));

            setSuccess("Xóa tài liệu thành công.");

            await loadDocuments(filters);

        } catch (ex) {
            console.error(ex);

            if (ex.response && ex.response.status === 403) {
                setErr("Bạn không có quyền xóa tài liệu này hoặc tài khoản thủ thư chưa được duyệt.");
                return;
            }

            if (ex.response && ex.response.status === 404) {
                setErr("Không tìm thấy tài liệu cần xóa.");
                return;
            }

            if (ex.response && ex.response.data && ex.response.data.message) {
                setErr(ex.response.data.message);
                return;
            }

            setErr("Xóa tài liệu thất bại.");

        } finally {
            setLoading(false);
        }
    };

    const getStatusBadge = (approved) => {
        if (approved) {
            return (
                <Badge bg="success" className="px-3 py-2 rounded-pill">Approved</Badge>
            );
        }
        return (
            <Badge bg="warning" text="dark" className="px-3 py-2 rounded-pill">Pending</Badge>
        );
    };

    const getPremiumBadge = (premium) => {
        if (premium) {
            return (
                <Badge bg="success" className="px-3 py-2 rounded-pill">Premium</Badge>
            );
        }
        return (
            <Badge bg="secondary" className="px-3 py-2 rounded-pill">Free</Badge>
        );
    };

    const getThumbnailUrl = (document) => {
        if (document && document.thumbnail) {
            return document.thumbnail;
        }

        return null;
    }

    const getTotalItems = () => {
        if (!documentsPage) {
            return 0;
        }

        return documentsPage.totalItems || 0;
    };

    const getCurrentPage = () => {
        if (!documentsPage) {
            return 1;
        }

        return documentsPage.page || 1;
    };

    const getTotalPages = () => {
        if (!documentsPage) {
            return 1;
        }

        return documentsPage.totalPages || 1;
    };


    // XEM DOCUMENT DETAIL
    const navToDocumentDetail = (documentId) => {
        nav(`/librarian/documents/${documentId}`);
    };

    // EDIT DOCUMENT
    const openEditModal = (document) => {
        setEditingDocument(document);
        setShowEditModal(true);
        setSuccess("");
        setErr("");
    }

    const closeEditModal = () => {
        setEditingDocument(null);
        setShowEditModal(false);
    }

    const handleDocumentUpdated = async () => {
        closeEditModal();

        setSuccess("Cập nhật tài liệu thành công. Tài liệu đã chuyển về trạng thái Pending để admin duyệt lại.");

        await loadDocuments(filters);
    }



    return (
        <div className="py-4">

            <Card className="shadow-sm border-0 rounded-4 mb-4">
                <Card.Body className="p-4">
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div>
                            <h3 className="fw-bold mb-1">
                                Tài liệu của tôi
                            </h3>

                            <p className="text-muted mb-0">
                                Quản lý danh sách tài liệu do bạn upload và theo dõi trạng thái duyệt.
                            </p>
                        </div>

                        <div className="d-flex gap-2 align-items-center">
                            <Badge bg="danger" className="px-4 py-2 rounded-pill">
                                Total: {getTotalItems()}
                            </Badge>

                            <Button as={Link} to="/librarian/documents/create" variant="primary" >
                                + Thêm tài liệu
                            </Button>
                        </div>
                    </div>
                </Card.Body>
            </Card>

            <Card className="shadow-sm border-0 rounded-4 mb-4">
                <Card.Body className="p-4">
                    <Form onSubmit={applyFilter}>
                        <Row className="g-3 align-items-end">
                            <Col xs={12} md={6}>
                                <Form.Label className="fw-semibold">
                                    Keyword
                                </Form.Label>

                                <Form.Control type="text" placeholder="Tìm theo title..." value={filters.keyword}
                                    onChange={(e) => updateFilter("keyword", e.target.value)}
                                />
                            </Col>

                            <Col xs={12} md={3}>
                                <Form.Label className="fw-semibold">
                                    Status
                                </Form.Label>

                                <Form.Select value={filters.approved}
                                    onChange={(e) => updateFilter("approved", e.target.value)}
                                >
                                    <option value="">
                                        All
                                    </option>

                                    <option value="false">
                                        Pending
                                    </option>

                                    <option value="true">
                                        Approved
                                    </option>
                                </Form.Select>
                            </Col>

                            <Col xs={12} md={3} className="d-flex gap-2">
                                <Button type="submit" variant="primary" className="flex-fill" >
                                    Lọc
                                </Button>

                                <Button type="button" variant="outline-secondary" lassname="flex-fill"
                                    onClick={clearFilter} >
                                    Xóa lọc
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>

            {err && (
                <Alert variant="danger">{err}</Alert>
            )}

            {success && (
                <Alert variant="success">{success}</Alert>
            )}

            <Card className="shadow-sm border-0 rounded-4">
                <Card.Header className="bg-white d-flex justify-content-between align-items-center">
                    <h5 className="fw-bold mb-0">
                        Danh sách tài liệu
                    </h5>

                    <span className="text-muted small">
                        Page {getCurrentPage()} / {getTotalPages()}
                    </span>
                </Card.Header>

                <Card.Body className="p-0">
                    {loading ? (
                        <div className="text-center py-5">
                            <MySpinner />

                            <div className="text-muted mt-2">
                                Đang tải tài liệu...
                            </div>
                        </div>
                    ) : (
                        <>
                            {documents.length === 0 ? (
                                <Alert variant="warning" className="m-3">
                                    Không có tài liệu phù hợp.
                                </Alert>
                            ) : (
                                <Table bordered hover responsive className="mb-0 align-middle">
                                    <thead className="table-light">
                                        <tr>
                                            <th style={{ minWidth: "260px" }}>
                                                Document
                                            </th>

                                            <th style={{ minWidth: "160px" }}>
                                                Category
                                            </th>

                                            <th style={{ width: "110px" }}>
                                                Type
                                            </th>

                                            <th style={{ width: "110px" }}>
                                                Premium
                                            </th>

                                            <th style={{ width: "130px" }}>
                                                Status
                                            </th>

                                            <th style={{ width: "90px" }}>
                                                Views
                                            </th>

                                            <th style={{ minWidth: "210px" }}>
                                                Actions
                                            </th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {documents.map((doc) => (
                                            <tr key={`librarian-document-${doc.id}`}>
                                                <td>
                                                    <div className="d-flex align-items-center gap-3">
                                                        {getThumbnailUrl(doc) && (
                                                            <Image src={getThumbnailUrl(doc)} alt={doc.title} rounded
                                                                width={120} height={150} style={{
                                                                    objectFit: "cover",
                                                                    border: "1px solid #dee2e6"
                                                                }} />
                                                        )}
                                                        <div>
                                                            <div className="fw-bold">
                                                                {doc.title}
                                                            </div>

                                                            <div className="text-muted small">
                                                                {doc.author || "Unknown author"} - Published in {doc.publishYear || "N/A"}
                                                            </div>
                                                        </div>

                                                    </div>


                                                </td>

                                                <td>
                                                    {doc.categoryName || "N/A"}
                                                </td>

                                                <td>
                                                    <Badge bg="secondary">
                                                        {doc.documentType || "N/A"}
                                                    </Badge>
                                                </td>

                                                <td>
                                                    {getPremiumBadge(doc.premium)}
                                                </td>

                                                <td>
                                                    {getStatusBadge(doc.approved)}
                                                </td>

                                                <td>
                                                    {doc.totalViews || 0}
                                                </td>

                                                <td>
                                                    <div className="d-flex gap-2 flex-wrap">
                                                        <Button onClick={() => navToDocumentDetail(doc.id)}
                                                            variant="outline-primary"
                                                            size="sm" >
                                                            Xem
                                                        </Button>

                                                        <Button onClick={() => openEditModal(doc)}
                                                            variant="outline-secondary"
                                                            size="sm"
                                                        >
                                                            Sửa
                                                        </Button>

                                                        <Button
                                                            type="button"
                                                            variant="outline-danger"
                                                            size="sm"
                                                            onClick={() => deleteDocument(doc.id, doc.title)}
                                                        >
                                                            Xóa
                                                        </Button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            )}
                        </>
                    )}
                </Card.Body>

                <Card.Footer className="bg-white d-flex justify-content-between align-items-center">
                    <Button
                        variant="outline-secondary"
                        disabled={loading || getCurrentPage() <= 1}
                        onClick={() => changePage(getCurrentPage() - 1)}
                    >
                        Trang trước
                    </Button>

                    <span className="text-muted">
                        Page {getCurrentPage()} / {getTotalPages()}
                    </span>

                    <Button
                        variant="outline-secondary"
                        disabled={loading || getCurrentPage() >= getTotalPages()}
                        onClick={() => changePage(getCurrentPage() + 1)}
                    >
                        Trang sau
                    </Button>
                </Card.Footer>
            </Card>
            <EditDocument show={showEditModal} document={editingDocument} onHide={closeEditModal} onUpdated={handleDocumentUpdated} />
        </div>
    );
}

export default LibrarianDashboard;