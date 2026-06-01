import { useEffect, useState } from "react";
import {
    Alert,
    Badge,
    Button,
    Card,
    Collapse,
    Spinner,
    Table
} from "react-bootstrap";

import Apis, { endpoints } from "../../configs/Apis";

const DocumentFileContent = ({ documentId, canViewContent = false }) => {
    const [open, setOpen] = useState(false);
    const [files, setFiles] = useState([]);

    const [loading, setLoading] = useState(false);
    const [openingFileId, setOpeningFileId] = useState(null);

    const [err, setErr] = useState("");
    const [contentErr, setContentErr] = useState("");

    useEffect(() => {
        if (!documentId) {
            return;
        }

        loadFiles();
    }, [documentId]);

    const loadFiles = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await Apis.get(endpoints.documentFiles(documentId));

            if (Array.isArray(res.data)) {
                setFiles(res.data);
            } else if (res.data && res.data.items) {
                setFiles(res.data.items);
            } else {
                setFiles([]);
            }

        } catch (ex) {
            console.error("LOAD DOCUMENT FILES ERROR:", ex);

            if (ex.response && ex.response.data && ex.response.data.message) {
                setErr(ex.response.data.message);
            } else {
                setErr("Không thể tải danh sách file của tài liệu.");
            }

        } finally {
            setLoading(false);
        }
    };

    const activeFiles = files.filter(file => file.active === true);

    const openFile = async (fileId) => {
        try {
            setOpeningFileId(fileId);
            setContentErr("");

            const res = await Apis.get(
                endpoints.documentContent(documentId),
                {
                    params: {
                        fileId: fileId
                    }
                }
            );

            console.log(res.data);

            const fileUrl = res.data.fileUrl;

            if (!fileUrl) {
                setContentErr("Backend không trả về fileUrl.");
                return;
            }

            window.open(fileUrl, "_blank", "noopener,noreferrer");

        } catch (ex) {
            console.error("OPEN DOCUMENT CONTENT ERROR:", ex);

            if (ex.response && ex.response.status === 403) {
                const message = ex.response.data?.message
                    || "Vui lòng mượn tài liệu để xem nội dung của file.";

                setContentErr(message);
                return;
            }

            if (ex.response && ex.response.status === 404) {
                const message = ex.response.data?.message
                    || "Không tìm thấy file hoặc tài liệu.";

                setContentErr(message);
                return;
            }

            if (ex.response && ex.response.status === 401) {
                setContentErr("Vui lòng đăng nhập để xem file.");
                return;
            }

            if (ex.response && ex.response.data && ex.response.data.message) {
                setContentErr(ex.response.data.message);
                return;
            }

            setContentErr("Không thể mở file. Vui lòng thử lại.");

        } finally {
            setOpeningFileId(null);
        }
    };

    const formatSize = (size) => {
        if (size === null || size === undefined) {
            return "N/A";
        }

        if (size < 1024) {
            return `${size} B`;
        }

        if (size < 1024 * 1024) {
            return `${(size / 1024).toFixed(1)} KB`;
        }

        return `${(size / (1024 * 1024)).toFixed(1)} MB`;
    };

    const formatDate = (timestamp) => {
        if (!timestamp) {
            return "N/A";
        }

        return new Date(timestamp).toLocaleDateString("vi-VN");
    };

    const getFileName = (file) => {
        return `File #${file.id}`;
    };

    return (
        <div className="container p-3 mb-2">
            <Card className="border-0 shadow-sm rounded-4">
                <Card.Header
                    className="bg-white d-flex justify-content-between align-items-center"
                    style={{
                        cursor: "pointer"
                    }}
                    onClick={() => setOpen(!open)}
                >
                    <div>
                        <h4 className="fw-bold mb-0">
                            Document File
                        </h4>

                        <div className="text-muted small">
                            Danh sách file đã được librarian upload.
                        </div>
                    </div>

                    <Button
                        type="button"
                        variant="outline-secondary"
                        size="sm"
                        onClick={(e) => {
                            e.stopPropagation();
                            setOpen(!open);
                        }}
                    >
                        {open ? "Ẩn ▲" : "Hiển thị ▼"}
                    </Button>
                </Card.Header>

                <Collapse in={open}>
                    <div>
                        <Card.Body className="p-4">
                            {err && (
                                <Alert variant="danger">
                                    {err}
                                </Alert>
                            )}

                            {contentErr && (
                                <Alert variant="danger">
                                    {contentErr}
                                </Alert>
                            )}

                            {!canViewContent && (
                                <Alert variant="warning">
                                    Vui lòng mượn tài liệu để xem nội dung của file.
                                </Alert>
                            )}

                            {loading ? (
                                <div className="text-center py-4">
                                    <Spinner animation="border" />

                                    <div className="text-muted mt-2">
                                        Đang tải danh sách file...
                                    </div>
                                </div>
                            ) : (
                                <>
                                    {activeFiles.length === 0 ? (
                                        <Alert variant="info" className="mb-0">
                                            Tài liệu hiện chưa có file active.
                                        </Alert>
                                    ) : (
                                        <Table bordered hover responsive className="mb-0 align-middle">
                                            <thead className="table-light">
                                                <tr>
                                                    <th style={{ minWidth: "180px" }}>
                                                        File
                                                    </th>

                                                    <th style={{ width: "120px" }}>
                                                        Type
                                                    </th>

                                                    <th style={{ width: "140px" }}>
                                                        Size
                                                    </th>

                                                    <th style={{ width: "160px" }}>
                                                        Upload Date
                                                    </th>

                                                    <th style={{ width: "130px" }}>
                                                        View
                                                    </th>
                                                </tr>
                                            </thead>

                                            <tbody>
                                                {activeFiles.map(file => (
                                                    <tr key={`document-content-file-${file.id}`}>
                                                        <td className="fw-semibold">
                                                            {getFileName(file)}
                                                        </td>

                                                        <td>
                                                            <Badge bg="secondary">
                                                                {file.fileExtension || "N/A"}
                                                            </Badge>
                                                        </td>

                                                        <td>
                                                            {formatSize(file.fileSize)}
                                                        </td>

                                                        <td>
                                                            {formatDate(file.uploadedDate)}
                                                        </td>

                                                        <td>
                                                            {canViewContent ? (
                                                                <Button
                                                                    type="button"
                                                                    variant="outline-primary"
                                                                    size="sm"
                                                                    disabled={openingFileId === file.id}
                                                                    onClick={() => openFile(file.id)}
                                                                >
                                                                    {openingFileId === file.id
                                                                        ? "Đang mở..."
                                                                        : "Xem file"}
                                                                </Button>
                                                            ) : (
                                                                <span className="text-muted small">
                                                                    Cần mượn
                                                                </span>
                                                            )}
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </Table>
                                    )}
                                </>
                            )}
                        </Card.Body>
                    </div>
                </Collapse>
            </Card>
        </div>
    );
};

export default DocumentFileContent;