import { useEffect, useState } from "react";
import Apis, { endpoints } from "../../configs/Apis";
import { Alert, Badge, Button, Image, Modal, Table } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import { ArrowDownShort, ArrowUpShort, Dash } from "react-bootstrap-icons";

const DocumentCompare = ({ show, document, onHide }) => {
    const [baseDocument, setBaseDocument] = useState(null);
    const [comparedDocuments, setComparedDocuments] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setErr] = useState("");

    const loadCompare = async () => {
        try {
            setLoading(true);
            setErr("");

            const res = await Apis.get(endpoints.compareDocument(document.id));

            setBaseDocument(res.data.baseDocument || null);
            setComparedDocuments(res.data.comparedDocuments || []);

        } catch (ex) {
            console.error("LOAD COMPARE ERROR:", ex);

            if (ex.response?.status === 404) {
                setErr("Không tìm thấy tài liệu để so sánh.");
                return;
            }

            setErr("Không thể tải dữ liệu so sánh tài liệu.");

        } finally {
            setLoading(false);
        }
    };

    const getDownloads = (doc) => {
        return doc.totalDowloads ?? doc.totalDownloads ?? 0;
    };

    const renderDocumentCell = (doc, isBase = false) => {
        return (
            <div className="d-flex align-items-center gap-3">
                {doc.thumbnail && (
                    <Image src={doc.thumbnail} alt={doc.title} rounded width={70} height={90} style={{ objectFit: "cover", border: "1px solid #dee2e6" }} />
                )}

                <div>
                    <div className="fw-bold"> {doc.title} </div>
                    <div className="text-muted small"> {doc.author || "Unknown author"} </div>

                    {isBase && (<Badge bg="primary" className="mt-2">Tài liệu hiện tại</Badge>)}
                </div>
            </div>
        );
    };

    const getCompareRows = () => {
        const rows = [];

        if (baseDocument) {
            rows.push({ ...baseDocument, isBase: true });
        }

        comparedDocuments.forEach(doc => {
            rows.push({ ...doc, isBase: false });
        });

        return rows;
    };


    useEffect(() => {
        if (show && document?.id) {
            loadCompare();
        }
    }, [show, document]);

    const renderTrendIcon = (value, baseValue) => {
        const current = Number(value || 0);
        const base = Number(baseValue || 0);

        if (current > base) {
            return (
                <ArrowUpShort size={22} className="text-success ms-1" title="Cao hơn tài liệu hiện tại" />
            );
        }

        if (current < base) {
            return (
                <ArrowDownShort size={22} className="text-danger ms-1" title="Thấp hơn tài liệu hiện tại" />
            );
        }

        return (
            <Dash size={18} className="text-muted ms-1" title="Bằng tài liệu hiện tại" />
        );
    };

    const renderMetricValue = (doc, value, baseValue) => {
        return (
            <div className="d-flex align-items-center">
                <span>{value ?? 0}</span>
                {!doc.isBase && renderTrendIcon(value, baseValue)}
            </div>
        );
    };

    return (
        <Modal show={show} onHide={onHide} size="xl" centered>
            <Modal.Header closeButton>
                <Modal.Title>
                    So sánh Top 3 rating trong danh mục "{document?.categoryName || "N/A"}"
                </Modal.Title>
            </Modal.Header>

            <Modal.Body>
                {err && (<Alert variant="danger">{err}</Alert>)}

                {loading ? (
                    <div className="text-center py-5">
                        <MySpinner />

                        <div className="text-muted mt-2"> Đang tải bảng so sánh... </div>
                    </div>
                ) : (
                    <>
                        {getCompareRows().length === 0 ? (
                            <Alert variant="warning" className="mb-0">Không có dữ liệu so sánh.</Alert>
                        ) : (
                            <Table bordered hover responsive className="mb-0 align-middle">
                                <thead className="table-light">
                                    <tr>
                                        <th style={{ minWidth: "320px" }}>Document</th>
                                        <th style={{ width: "140px" }}>Publish Year</th>
                                        <th style={{ width: "120px" }}>Views</th>
                                        <th style={{ width: "120px" }}>Rating</th>
                                        <th style={{ width: "130px" }}>Downloads</th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {getCompareRows().map(doc => (
                                        <tr
                                            key={`compare-document-${doc.id}`}
                                            className={doc.isBase ? "table-primary" : ""}
                                        >
                                            <td>{renderDocumentCell(doc, doc.isBase)}</td>
                                            <td>{doc.publishYear || "N/A"}</td>
                                            <td>{renderMetricValue( doc, doc.totalViews || 0, baseDocument?.totalViews || 0)}</td>

                                            <td>
                                                <div className="d-flex align-items-center">
                                                    <span className="text-warning fw-bold me-1">★</span>
                                                    {renderMetricValue( doc, doc.averageRating ?? 0, baseDocument?.averageRating ?? 0)}
                                                </div>
                                            </td>

                                            <td> {renderMetricValue( doc, getDownloads(doc), getDownloads(baseDocument || {}))}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        )}
                    </>
                )}
            </Modal.Body>

        </Modal>
    );
};

export default DocumentCompare;