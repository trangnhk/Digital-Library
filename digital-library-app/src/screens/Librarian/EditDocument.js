import { useEffect, useState } from "react";
import { Alert, Button, Col, Form, Image, Modal, Row } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";
import Apis, { endpoints } from "../../configs/Apis";

const EditDocument = ({ show, document, onHide, onUpdated }) => {
    const [form, setForm] = useState({
        title: "",
        description: "",
        author: "",
        publisher: "",
        publishYear: "",
        categoryId: "",
        documentType: "",
        isPremium: false,
        price: ""
    });

    const [categories, setCategories] = useState([]);
    const [thumbnailFile, setThumbnailFile] = useState(null);
    const [thumbnailPreview, setThumbnailPreview] = useState(null);
    const [documentFiles, setDocumentFiles] = useState([]);

    const [loadingCategories, setLoadingCategories] = useState(false);
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState("");


    useEffect(() => {
        if (show && document) {
            setForm({
                title: document.title || "",
                description: document.description || "",
                author: document.author || "",
                publisher: document.publisher || "",
                publishYear: document.publishYear || "",
                categoryId: document.categoryId || "",
                documentType: document.documentType || "",
                isPremium: Boolean(document.premium),
                price: document.price !== null && document.price !== undefined
                    ? String(document.price)
                    : ""
            });

            setThumbnailFile(null);
            setThumbnailPreview(document.thumbnail || null);
            setDocumentFiles([]);
            setErr("");

            loadCategories();
        }
    }, [show, document]);

    const loadCategories = async () => {
        try {
            setLoadingCategories(true);

            const res = await Apis.get(endpoints.categories, {
                params: {
                    page: 1,
                    size: 50
                }
            });

            if (res.data && res.data.items) {
                setCategories(res.data.items);
            } else if (Array.isArray(res.data)) {
                setCategories(res.data);
            } else {
                setCategories([]);
            }

        } catch (ex) {
            console.error(ex);
            setCategories([]);

        } finally {
            setLoadingCategories(false);
        }
    };

    const updateField = (field, value) => {
        setForm({
            ...form,
            [field]: value
        });
    };

    const updatePremium = (checked) => {
        setForm({
            ...form,
            isPremium: checked,
            price: checked ? form.price : 0
        });
    };

    const handleThumbnailChange = (e) => {
        const file = e.target.files[0];

        if (!file) {
            setThumbnailFile(null);
            setThumbnailPreview(document?.thumbnail || null);
            return;
        }

        setThumbnailFile(file);
        setThumbnailPreview(URL.createObjectURL(file));
    };

    const handleFilesChange = (e) => {
        const files = Array.from(e.target.files || []);
        setDocumentFiles(files);
    };

    const validate = () => {
        if (!form.title.trim()) {
            setErr("Title không được để trống.");
            return false;
        }

        if (!form.author.trim()) {
            setErr("Author không được để trống.");
            return false;
        }

        if (!form.publisher.trim()) {
            setErr("Publisher không được để trống.");
            return false;
        }

        if (!form.categoryId) {
            setErr("Vui lòng chọn category.");
            return false;
        }

        if (!form.documentType) {
            setErr("Vui lòng chọn document type.");
            return false;
        }

        if (form.publishYear) {
            const year = Number(form.publishYear);
            const currentYear = new Date().getFullYear();

            if (Number.isNaN(year)) {
                setErr("Publish year phải là số.");
                return false;
            }

            if (year > currentYear) {
                setErr("Publish year không được lớn hơn năm hiện tại.");
                return false;
            }
        }

        if (form.isPremium) {
            if (form.price === "" || form.price === null || form.price === undefined) {
                setErr("Tài liệu Premium bắt buộc phải nhập price.");
                return false;
            }

            const price = Number(form.price);

            if (Number.isNaN(price)) {
                setErr("Price phải là số.");
                return false;
            }

            if (price < 0) {
                setErr("Price không được nhỏ hơn 0.");
                return false;
            }
        }

        if (thumbnailFile !== null) {
            const allowedThumbnailTypes = ["image/jpeg", "image/png", "image/webp"];

            if (!allowedThumbnailTypes.includes(thumbnailFile.type)) {
                setErr("Thumbnail chỉ hỗ trợ JPG, PNG hoặc WEBP.");
                return false;
            }
        }

        if (documentFiles.length > 0) {
            for (let file of documentFiles) {
                if (!isValidFileForDocumentType(file, form.documentType)) {
                    setErr(`File "${file.name}" không phù hợp với documentType ${form.documentType}.`);
                    return false;
                }
            }
        }

        return true;
    };

    const isValidFileForDocumentType = (file, documentType) => {
        if (!file || !file.name || !documentType) {
            return false;
        }

        const extension = file.name.split(".").pop().toLowerCase();

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
        }
    };

    const getErrorMessage = (ex) => {
        if (!ex.response) {
            return "Không thể kết nối đến server.";
        }

        const status = ex.response.status;
        const data = ex.response.data;

        if (status === 400) {
            if (Array.isArray(data)) {
                return data
                    .map(item => item.defaultMessage || item.message || "Dữ liệu không hợp lệ.")
                    .join(" ");
            }

            if (data && data.message) {
                return data.message;
            }

            return "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.";
        }

        if (status === 401) {
            return "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
        }

        if (status === 403) {
            return "Bạn không có quyền sửa tài liệu này hoặc tài khoản thủ thư chưa được duyệt.";
        }

        if (status === 404) {
            return "Không tìm thấy tài liệu hoặc category.";
        }

        if (status === 415) {
            return "File upload không đúng định dạng.";
        }

        if (status === 422) {
            if (data && data.message) {
                return data.message;
            }

            return "Dữ liệu không thể xử lý. Vui lòng kiểm tra lại.";
        }

        if (typeof data === "string") {
            return data;
        }

        if (data && data.message) {
            return data.message;
        }

        return "Cập nhật tài liệu thất bại.";
    };

    const submitEdit = async (e) => {
        e.preventDefault();

        setErr("");

        if (!validate()) {
            return;
        }

        try {
            setSaving(true);

            const formData = new FormData();

            formData.append("title", form.title.trim());
            formData.append("description", form.description || "");
            formData.append("author", form.author.trim());
            formData.append("publisher", form.publisher.trim());
            formData.append("documentType", form.documentType);

            if (form.categoryId !== "") {
                formData.append("categoryId", form.categoryId);
            }

            if (form.publishYear !== "") {
                formData.append("publishYear", form.publishYear);
            }

            formData.append("isPremium", form.isPremium ? "true" : "false");

            if (form.isPremium) {
                formData.append("price", form.price);
            }

            if (thumbnailFile !== null) {
                formData.append("thumbnail", thumbnailFile);
            }

            if (documentFiles.length > 0) {
                documentFiles.forEach(file => {
                    formData.append("files", file);
                });
            }

            // Debug dữ liệu gửi lên
            for (let pair of formData.entries()) {
                console.log(pair[0], pair[1]);
            }

            const res = await Apis.patch(
                endpoints.librarianDocumentDetails(document.id),
                formData
            );

            onUpdated(res.data);

        } catch (ex) {
            console.error("UPDATE DOCUMENT ERROR:", ex);
            console.error("STATUS:", ex.response?.status);
            console.error("DATA:", ex.response?.data);

            setErr(getErrorMessage(ex));

        } finally {
            setSaving(false);
        }
    };

    const closeModal = () => {
        if (saving) {
            return;
        }

        onHide();
    };

    return (
        <Modal
            show={show}
            onHide={closeModal}
            size="lg"
            centered
            backdrop="static"
        >
            <Form onSubmit={submitEdit}>
                <Modal.Header closeButton={!saving}>
                    <Modal.Title>
                        Sửa tài liệu
                    </Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {err && (
                        <Alert variant="danger">
                            {err}
                        </Alert>
                    )}

                    <Alert variant="info">
                        Sau khi sửa, tài liệu sẽ chuyển về trạng thái Pending để admin duyệt lại.
                    </Alert>

                    <Row>
                        <Col md={8}>
                            <Form.Group className="mb-3" controlId="title">
                                <Form.Label>
                                    Title
                                </Form.Label>

                                <Form.Control
                                    type="text"
                                    value={form.title}
                                    onChange={(e) => updateField("title", e.target.value)}
                                    disabled={saving}
                                />
                            </Form.Group>
                        </Col>

                        <Col md={4}>
                            <Form.Group className="mb-3" controlId="publishYear">
                                <Form.Label>
                                    Publish year
                                </Form.Label>

                                <Form.Control
                                    type="number"
                                    value={form.publishYear}
                                    onChange={(e) => updateField("publishYear", e.target.value)}
                                    disabled={saving}
                                />
                            </Form.Group>
                        </Col>
                    </Row>

                    <Row>
                        <Col md={6}>
                            <Form.Group className="mb-3" controlId="author">
                                <Form.Label>
                                    Author
                                </Form.Label>

                                <Form.Control
                                    type="text"
                                    value={form.author}
                                    onChange={(e) => updateField("author", e.target.value)}
                                    disabled={saving}
                                />
                            </Form.Group>
                        </Col>

                        <Col md={6}>
                            <Form.Group className="mb-3" controlId="publisher">
                                <Form.Label>
                                    Publisher
                                </Form.Label>

                                <Form.Control
                                    type="text"
                                    value={form.publisher}
                                    onChange={(e) => updateField("publisher", e.target.value)}
                                    disabled={saving}
                                />
                            </Form.Group>
                        </Col>
                    </Row>

                    <Form.Group className="mb-3" controlId="description">
                        <Form.Label>
                            Description
                        </Form.Label>

                        <Form.Control
                            as="textarea"
                            rows={3}
                            value={form.description}
                            onChange={(e) => updateField("description", e.target.value)}
                            disabled={saving}
                        />
                    </Form.Group>

                    <Row>
                        <Col md={6}>
                            <Form.Group className="mb-3" controlId="categoryId">
                                <Form.Label>
                                    Category
                                </Form.Label>

                                <Form.Select
                                    value={form.categoryId}
                                    onChange={(e) => updateField("categoryId", e.target.value)}
                                    disabled={saving || loadingCategories}
                                >
                                    <option value="">
                                        Chọn category
                                    </option>

                                    {categories.map(c => (
                                        <option key={c.id} value={c.id}>
                                            {c.name}
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>

                        <Col md={6}>
                            <Form.Group className="mb-3" controlId="documentType">
                                <Form.Label>
                                    Document type
                                </Form.Label>

                                <Form.Select
                                    value={form.documentType}
                                    onChange={(e) => updateField("documentType", e.target.value)}
                                    disabled={saving}
                                >
                                    <option value="">
                                        Chọn type
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
                            </Form.Group>
                        </Col>
                    </Row>

                    <Row>
                        <Col md={4}>
                            <Form.Group className="mb-3" controlId="isPremium">
                                <Form.Label>
                                    Premium
                                </Form.Label>

                                <div className="border rounded-3 px-3 py-2 bg-light">
                                    <Form.Check
                                        type="checkbox"
                                        label="IsPremium"
                                        checked={form.isPremium}
                                        onChange={(e) => updatePremium(e.target.checked)}
                                        disabled={saving}
                                    />
                                </div>

                                <Form.Text className="text-muted">
                                    Tick nếu đây là tài liệu có phí.
                                </Form.Text>
                            </Form.Group>
                        </Col>

                        <Col md={8}>
                            <Form.Group className="mb-3" controlId="price">
                                <Form.Label>
                                    Price
                                </Form.Label>

                                <Form.Control
                                    type="number"
                                    min="0"
                                    value={form.isPremium ? form.price : "0"}
                                    onChange={(e) => updateField("price", e.target.value)}
                                    disabled={saving || !form.isPremium}
                                    placeholder="Nhập giá tài liệu premium"
                                />

                                <Form.Text className="text-muted">
                                    Nếu không tick Premium, price sẽ bị khóa và backend tự set 0.0.
                                </Form.Text>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Row>
                        <Col md={5}>
                            <Form.Group className="mb-3" controlId="thumbnail">
                                <Form.Label>
                                    Thumbnail
                                </Form.Label>

                                <Form.Control
                                    type="file"
                                    accept="image/jpeg,image/png,image/webp"
                                    onChange={handleThumbnailChange}
                                    disabled={saving}
                                />

                                <Form.Text className="text-muted">
                                    Không bắt buộc. Chỉ chọn nếu muốn đổi ảnh.
                                </Form.Text>
                            </Form.Group>

                            {thumbnailPreview && (
                                <Image
                                    src={thumbnailPreview}
                                    rounded
                                    width={160}
                                    height={200}
                                    style={{
                                        objectFit: "cover",
                                        border: "1px solid #dee2e6"
                                    }}
                                />
                            )}
                        </Col>

                        <Col md={7}>
                            <Form.Group className="mb-3" controlId="files">
                                <Form.Label>
                                    Document files
                                </Form.Label>

                                <Form.Control
                                    type="file"
                                    multiple
                                    onChange={handleFilesChange}
                                    disabled={saving}
                                />

                                <Form.Text className="text-muted">
                                    Không bắt buộc. Nếu chọn file mới, file phải phù hợp với document type.
                                </Form.Text>
                            </Form.Group>

                            {documentFiles.length > 0 && (
                                <div className="small text-muted">
                                    {documentFiles.map(file => (
                                        <div key={file.name}>
                                            {file.name}
                                        </div>
                                    ))}
                                </div>
                            )}
                        </Col>
                    </Row>
                </Modal.Body>

                <Modal.Footer>
                    <Button
                        type="button"
                        variant="outline-secondary"
                        onClick={closeModal}
                        disabled={saving}
                    >
                        Hủy
                    </Button>

                    <Button
                        type="submit"
                        variant="primary"
                        disabled={saving}
                    >
                        {saving ? (
                            <>
                                <MySpinner />
                                Đang lưu...
                            </>
                        ) : (
                            "Lưu thay đổi"
                        )}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
};

export default EditDocument;