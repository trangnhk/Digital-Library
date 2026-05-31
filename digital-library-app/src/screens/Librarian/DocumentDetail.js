import { useEffect, useState } from "react";
import EditDocument from "./EditDocument";
import { useNavigate, useParams } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { Alert, Badge, Button, Card, Col, Image, Row, Table } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import DocumentFile from "./DocumentFile";

const LibrarianDocumentDetail = () => {
    const { documentId } = useParams();
    const [document, setDocument] = useState(null);
    const [files, setFiles] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingFiles, setLoadingFiles] = useState(false);

    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");

    const [showEditModal, setShowEditModal] = useState(false);
    const [showFileModal, setShowFileModal] = useState(false);

    const nav = useNavigate();

    useEffect(() => {
        loadDetail();
        loadFiles();
    }, [documentId]);

    const loadDetail = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await Apis.get(endpoints.librarianDocumentDetails(documentId));

            setDocument(res.data);


        } catch (ex) {
            console.error("Load DETAIL ERROR: ", ex);
            setErr(getErrorMessage(ex, "Không thể tải chi tiết tài liệu"));
        } finally {
            setLoading(false);
        }

    };

    const loadFiles = async () => {
        try {
            setLoadingFiles(true);

            const res = await Apis.get(endpoints.librarianDocumentFiles(documentId));

            if (Array.isArray(res.data)) {
                setFiles(res.data);
            } else if (res.data && res.data.items) {
                setFiles(res.data.items);
            } else {
                setFiles([]);
            }

        } catch (ex) {
            console.error("Load FILES ERROR:", ex);
            setErr(getErrorMessage(ex, "Không thể tải danh sách file."));

        } finally {
            setLoadingFiles(false);
        }
    };

    const deleteFile = async (fileId) => {
        const confirmed = window.confirm("Bạn có chắc muốn xóa file này không?");

        if (!confirmed)
            return;

        try {
            setErr("");
            setSuccess("");

            await Apis.delete(endpoints.librarianDeleteDocumentFiles(documentId, fileId));

            setSuccess("Xóa file thành công.");
            await loadFiles();

        } catch (ex) {
            console.error("DELETE ERROR:", ex);
            setErr(getErrorMessage(ex, "Xóa file thất bại."));
        }
    };

    const handleDocumentUpdated = async (updatedDocument) => {
        setShowEditModal(false);
        setDocument(updatedDocument);
        setSuccess("Cập nhật tài liệu thành công. Tài liệu đã chuyển về trạng thái Pending nếu backend reset approved.");
        await loadDetail();
    };

    const handleFilesUploaded = async (updatedDocument) => {
        setShowFileModal(false);

        if (updatedDocument) {
            setDocument(updatedDocument);
        }

        setSuccess("Thêm file thành công.");
        await loadDetail();
        await loadFiles();
    };

    const getErrorMessage = (ex, defaultMessage) => {
        if (!ex.response) {
            return "Không thể kết nối đến server";
        }

        const status = ex.response.status;
        const data = ex.response.data;

        switch (status) {
            case 401:
                return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại";
            case 403:
                return "Bạn không có quyền truy cập tài liệu này hoặc tài khoản thủ thư của bạn chưa được duyệt";
            case 404: {
                if (data && data.message) {
                    return data.message;
                }
                return "Không tìm thấy tài liệu hoặc file";
            };

        };

        if (typeof data === "string") {
            return data;
        }

        if (data && data.message) {
            return data.message;
        }

        return defaultMessage;


    };

    const getStatusBadge = () => {
        if (!document) {
            return null;
        }

        if (document.approved) {
            return (
                <Badge bg="success" className="px-3 py-2 rounded-pill">
                    Approved
                </Badge>
            );
        }

        return (
            <Badge bg="warning" text="dark" className="px-3 py-2 rounded-pill">
                Pending
            </Badge>
        );
    };

    const getPremiumBadge = () => {
        if (!document) {
            return null;
        }

        if (document.premium) {
            return (
                <Badge bg="danger" className="px-3 py-2 rounded-pill">
                    Premium
                </Badge>
            );
        }

        return (
            <Badge bg="secondary" className="px-3 py-2 rounded-pill">
                Free
            </Badge>
        );
    };

    if (loading) {
        return (
            <div className="text-center py-5">
                <MySpinner />

                <div className="text-muted mt-2">
                    Đang tải chi tiết tài liệu...
                </div>
            </div>
        );
    }

    if (!document) {
        return (
            <Alert variant="warning" className="mt-4">
                Không tìm thấy tài liệu.
            </Alert>
        );
    }

    

    return (
        <div className="py-4">

            {err && (<Alert variant="danger">{err}</Alert>)}

            {success && (<Alert variant="success">{success}</Alert>)}

            <Card className="shadow-sm border-0 rounded-4 mb-4">
                <Card.Header className="bg-white">
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <div className="d-flex align-items-center gap-2">
                            <Button type="button"
                                variant="outline-secondary"
                                size="sm"
                                onClick={() => nav("/librarian/dashboard")}>
                                Quay lại
                            </Button>

                            <h5 className="fw-bold mb-0">Chi tiết tài liệu của tôi</h5>
                        </div>

                        <Button type="button"
                            variant="outline-primary"
                            size="sm"
                            onClick={() => setShowEditModal(true)}
                        >
                            Sửa
                        </Button>
                    </div>
                </Card.Header>

                <Card.Body className="p-4">
                    <Row className="g-4">
                        <Col xs={12} md={3} className="text-center">
                            {document.thumbnail ? (
                                <Image src={document.thumbnail} rounded fluid
                                    style={{
                                        maxHeight: "260px",
                                        objectFit: "cover",
                                        border: "1px solid #dee2e6"
                                    }} />
                            ) : (
                                <div className="border rounded-4 bg-light d-flex align-items-center justify-content-center"
                                    style={{ height: "220px" }}>
                                    <span className="text-muted">No thumbnail</span>
                                </div>
                            )}
                        </Col>

                        <Col xs={12} md={9}>
                            <div className="d-flex align-items-start justify-content-between flex-wrap gap-2 mb-3">
                                <div>
                                    <h3 className="fw-bold mb-1">{document.title}</h3>

                                    <div className="text-muted">{document.author || "Unknown author"}</div>
                                </div>

                                <div className="d-flex gap-2 flex-wrap">
                                    {getStatusBadge()}
                                    {getPremiumBadge()}
                                </div>
                            </div>

                            <Row className="g-3">
                                <Col md={6}>
                                    <div className="text-muted small">Category</div>

                                    <div className="fw-semibold">{document.categoryName || "N/A"}</div>
                                </Col>

                                <Col md={6}>
                                    <div className="text-muted small">Type</div>

                                    <div className="fw-semibold">{document.documentType || "N/A"}</div>
                                </Col>

                                <Col md={6}>
                                    <div className="text-muted small">Publisher</div>

                                    <div className="fw-semibold">{document.publisher || "N/A"}</div>
                                </Col>

                                <Col md={6}>
                                    <div className="text-muted small">Publish year</div>

                                    <div className="fw-semibold">{document.publishYear || "N/A"}</div>
                                </Col>

                                <Col md={6}>
                                    <div className="text-muted small">Price</div>

                                    <div className="fw-semibold">{document.price}</div>
                                </Col>

                                <Col md={6}>
                                    <div className="text-muted small">Views / Downloads</div>

                                    <div className="fw-semibold">{document.totalViews} / {document.totalDownloads}</div>
                                </Col>

                                <Col md={12}>
                                    <div className="text-muted small">Description</div>

                                    <div className="fw-semibold">{document.description || "N/A"}</div>
                                </Col>
                            </Row>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

            <Card className="shadow-sm border-0 rounded-4">
                <Card.Header className="bg-white d-flex justify-content-between align-items-center">
                    <h5 className="fw-bold mb-0">Files</h5>

                    <Button type="button" variant="primary" size="sm" onClick={() => setShowFileModal(true)}>
                        Thêm file
                    </Button>
                </Card.Header>

                <Card.Body className="p-0">
                    {loadingFiles ? (
                        <div className="text-center py-5">
                            <MySpinner />

                            <div className="text-muted mt-2">
                                Đang tải danh sách file...
                            </div>
                        </div>
                    ) : (
                        <>
                            {files.length === 0 ? (
                                <Alert variant="warning" className="m-3">
                                    Tài liệu chưa có file nào.
                                </Alert>
                            ) : (
                                <Table bordered hover responsive className="mb-0 align-middle">
                                    <thead className="table-light">
                                        <tr>
                                            <th style={{ minWidth: "280px" }}>
                                                Name
                                            </th>

                                            <th style={{ width: "120px" }}>
                                                Type
                                            </th>

                                            <th style={{ width: "160px" }}>
                                                Actions
                                            </th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {files.filter(file => file.active === true)
                                                .map(file => (
                                            <tr key={`document-file-${file.id}`}>
                                                <td>
                                                    <a target="_blank" rel="noreferrer" className="fw-semibold text-decoration-none">
                                                        FILE: {file.id}
                                                    </a>
                                                </td>

                                                <td>
                                                    <Badge bg="secondary">
                                                        {file.fileExtension || "N/A"}
                                                    </Badge>
                                                </td>

                                                <td>
                                                    <Button type="button" variant="outline-danger" size="sm" onClick={() => deleteFile(file.id)} >
                                                        Xóa
                                                    </Button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </Table>
                            )}
                        </>
                    )}
                </Card.Body>
            </Card>

            <EditDocument
                show={showEditModal}
                document={document}
                onHide={() => setShowEditModal(false)}
                onUpdated={handleDocumentUpdated}
            />

            <DocumentFile
                show={showFileModal}
                document={document}
                onHide={() => setShowFileModal(false)}
                onUploaded={handleFilesUploaded}
            />
        </div>
    );
};

export default LibrarianDocumentDetail;