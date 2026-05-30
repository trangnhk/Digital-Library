import { useEffect, useState } from "react";
import { Alert, Button, Col, Form, Modal, Row } from "react-bootstrap";
import Apis, { endpoints } from "../../configs/Apis";
import MySpinner from "../../components/MySpinner";

const DocumentFile = ({ show, document, onHide, onUploaded }) => {

    const [files, setFiles] = useState([]);
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState("");

    useEffect(() => {
        if (show && document) {
            setFiles([]);
            setErr("");
        }
    }, [show, document]);

    const handleFileChange = (e) => {
        const selectedFiles = Array.from(e.target.files || []);
        setFiles(selectedFiles);
    };

    const isValidFileForDocumentType = (file, documentType) => {
        if (!file || file.name || !documentType)
            return false;

        const extension = file.name.split('.').pop().toLowerCase();

        switch (documentType) {
            case "PDF":
                return extension === "pdf";

            case "DOCX":
                return extension === "docx";

            case "EPUB":
                return extension === "epub";

            case "VIDEO":
                return extension === "mp4";

            case "AUDIO":
                return extension === "mp3" || extension === "wav";

            default:
                return false;
        };

    };

    const validate = () => {
        if (!document) {
            setErr("Không có tài liệu nào được chọn.");
            return false;
        }

        if (files.length === 0) {
            setErr("Vui lòng chọn ít nhất một tệp để tải lên.");
            return false;
        }

        for (let file of files) {
            if (!isValidFileForDocumentType(file, document.type)) {
                setErr(`Tệp ${file.name} không hợp lệ cho loại tài liệu ${document.type}.`);
                return false;
            }
        }

        return true;
    };

    const getErrorMessage = (ex) => {
        if (!ex.response) {
            return "Không thể kết nối đến máy chủ. Vui lòng thử lại sau.";
        }

        const status = ex.response.status;
        const data = ex.response.data;

        switch (status) {
            case 400:
                return "Yêu cầu không hợp lệ. Vui lòng kiểm tra lại dữ liệu.";

            case 401:
                return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";

            case 403:
                return "Bạn không có quyền thêm file hoặc tài khoản thủ thư chưa được duyệt. Vui lòng liên hệ quản trị viên.";

            case 404:
                return "Tài liệu không tồn tại. Vui lòng kiểm tra lại.";

            case 415:
                return "File upload không hợp lệ. Vui lòng chọn file đúng định dạng.";

            case 422:
                if (data && data.message) {
                    return data.message;
                }
                return "File khong phu hợp với loại tài liệu. Vui lòng chọn file đúng định dạng.";

        }

        if (typeof data === "string") {
            return data;
        }

        if (data && data.message) {
            return data.message;
        }

        return "Thêm file thất bại";



    };

    const submitFiles = async (e) => {
        e.preventDefault();
        setErr("");

        if (!validate)
            return;

        try {
            setSaving(true);

            const formData = new FormData();

            files.forEach(file => {
                formData.append("files", file);
            });

            for (let pair of formData.entries()) {
                console.log(pair[0], pair[1]);
            }

            const res = await Apis.patch(endpoints.librarianDocumentDetails(document.id), formData);

            onUploaded(res.data);

        } catch (ex) {
            console.error("UPLOAD DOCUMENT FILE ERROR:", ex);
            console.error("STATUS:", ex.response?.status);
            console.error("DATA:", ex.response?.data);

            setErr(getErrorMessage(ex));

        } finally {
            setSaving(false);
        }

    };

    const closeModal = () => {
        if (saving)
            return;

        onHide();
    }

    return (
        <Modal show={show} onHide={closeModal} size="lg" centered backdrop="static" >
            <Form onSubmit={submitFiles}>
                <Modal.Header closeButton={!saving}>
                    <Modal.Title>Thêm file tài liệu</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {err && (<Alert variant="danger">{err}</Alert>)}

                    <Alert variant="info">
                        Chỉ gửi <strong>files</strong>
                    </Alert>

                    <Row>
                        <Col md={7}>
                            <Form.Group className="mb-3" controlId="files">
                                <Form.Label>Document files</Form.Label>

                                <Form.Control type="file"multiple onChange={handleFileChange} disabled={saving} />

                                <Form.Text className="text-muted">
                                    File phải phù hợp với loại tài liệu hiện tại:
                                    {" "}
                                    <strong>{document?.documentType || "N/A"}</strong>
                                </Form.Text>
                            </Form.Group>

                            {files.length > 0 && (
                                <div className="border rounded-3 p-3 bg-light">
                                    <div className="fw-semibold mb-2">Files đã chọn</div>

                                    {files.map(file => (
                                        <div key={`${file.name}-${file.size}`} className="small text-muted">
                                            {file.name}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </Col>
                    </Row>
                </Modal.Body>

                <Modal.Footer>
                    <Button type="button" variant="outline-secondary" onClick={closeModal} disabled={saving}>
                        Hủy
                    </Button>

                    <Button type="submit" variant="primary" disabled={saving} >
                        {saving ? (
                            <><MySpinner />Đang thêm file...</>
                        ) : (
                            "Thêm file"
                        )}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
};


export default DocumentFile;