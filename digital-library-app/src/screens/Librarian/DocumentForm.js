import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";
import { Alert, Badge, Button, Card, Col, Form, Image, Row } from "react-bootstrap";
import MySpinner from "../../components/MySpinner";


const NewDocument = () => {
    const [form, setForm] = useState({
        title: "",
        description: "",
        author: "",
        publisher: "",
        publishYear: "",
        categoryId: "",
        documentType: "PDF",
        premium: false,
        price: "0"
    });

    const [categories, setCategories] = useState([]);
    const [thumbnailFile, setThumbnailFile] = useState(null);
    const [thumbnailPreview, setThumbnailPreview] = useState(null);
    const [files, setFiles] = useState([]);

    const [loadingCategories, setLoadingCategories] = useState(false);
    const [saving, setSaving] = useState(false);
    const [err, setErr] = useState("");
    const [success, setSuccess] = useState("");

    const fileInputRef = useRef(null);
    const nav = useNavigate();

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            setLoadingCategories(true);

            const res = await Apis.get(endpoints.categories);

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
        setForm({...form, [field]: value});
    };

    const updatePremium = (checked) => {
        setForm({...form, premium: checked,  price: checked ? form.price : "0"});
    };

    const updateDocumentType = (value) => {
        setForm({...form, documentType: value});

        setFiles([]);

        if (fileInputRef.current) {
            fileInputRef.current.value = "";
        }
    };

    const handleThumbnailChange = (e) => {
        const file = e.target.files[0];

        if (!file) {
            setThumbnailFile(null);
            setThumbnailPreview(null);
            return;
        }

        setThumbnailFile(file);
        setThumbnailPreview(URL.createObjectURL(file));
    };

    const handleFilesChange = (e) => {
        const selectedFiles = Array.from(e.target.files || []);

        if (selectedFiles.length === 0) {
            return;
        }

        const mergedFiles = [...files];

        selectedFiles.forEach(file => {
            const existed = mergedFiles.some(
                f => f.name === file.name && f.size === file.size && f.lastModified === file.lastModified
            );

            if (!existed) {
                mergedFiles.push(file);
            }
        });

        setFiles(mergedFiles);

        if (fileInputRef.current) {
            fileInputRef.current.value = "";
        }
    };

    const removeFile = (index) => {
        const newFiles = files.filter((_, i) => i !== index);
        setFiles(newFiles);
    };

    const getFileAccept = () => {
        switch (form.documentType) {
            case "PDF":
                return ".pdf,application/pdf";

            case "DOCX":
                return ".docx,application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            case "EPUB":
                return ".epub,application/epub+zip";

            case "VIDEO":
                return ".mp4,video/mp4";

            case "AUDIO":
                return ".mp3,.wav,audio/mpeg,audio/wav";

            default:
                return "";
        }
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

    const validate = () => {
        if (!form.title.trim()) {
            setErr("Vui lòng nhập title.");
            return false;
        }

        if (!form.author.trim()) {
            setErr("Vui lòng nhập author.");
            return false;
        }

        if (!form.publisher.trim()) {
            setErr("Vui lòng nhập publisher.");
            return false;
        }

        if (!form.publishYear) {
            setErr("Vui lòng nhập publish year.");
            return false;
        }

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

        if (!form.categoryId) {
            setErr("Vui lòng chọn category.");
            return false;
        }

        if (!form.documentType) {
            setErr("Vui lòng chọn document type.");
            return false;
        }

        if (form.premium) {
            if (form.price === "" || form.price === null || form.price === undefined) {
                setErr("Tài liệu premium bắt buộc phải nhập price.");
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

        if (files.length === 0) {
            setErr("Vui lòng chọn ít nhất 1 file tài liệu.");
            return false;
        }

        for (let file of files) {
            if (!isValidFileForDocumentType(file, form.documentType)) {
                setErr(`File "${file.name}" không phù hợp với documentType ${form.documentType}.`);
                return false;
            }
        }

        return true;
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

            if (data && data.details) {
                return Object.values(data.details).join(" ");
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
            return "Tài khoản thủ thư chưa được duyệt hoặc bạn không có quyền tạo tài liệu.";
        }

        if (status === 404) {
            return "Không tìm thấy category.";
        }

        if (status === 415) {
            return "Thumbnail hoặc file upload không đúng định dạng.";
        }

        if (status === 422) {
            if (data && data.message) {
                return data.message;
            }

            return "Dữ liệu không thể xử lý. Vui lòng kiểm tra lại file, price hoặc document type.";
        }

        if (typeof data === "string") {
            return data;
        }

        if (data && data.message) {
            return data.message;
        }

        return "Tạo tài liệu thất bại.";
    };

    const submitCreate = async (e) => {
        e.preventDefault();

        setErr("");
        setSuccess("");

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
            formData.append("publishYear", form.publishYear);
            formData.append("documentType", form.documentType);
            formData.append("categoryId", form.categoryId);
            formData.append("premium", form.premium ? "true" : "false");

            if (form.premium) {
                formData.append("price", form.price);
            } else {
                formData.append("price", "0");
            }

            if (thumbnailFile !== null) {
                formData.append("thumbnail", thumbnailFile);
            }

            files.forEach(file => {
                formData.append("files", file);
            });

            for (let pair of formData.entries()) {
                console.log(pair[0], pair[1]);
            }

            const res = await Apis.post(endpoints.librarianDocuments, formData);

            setSuccess("Tạo tài liệu thành công. Tài liệu đang chờ admin duyệt.");

            const createdDocumentId = res.data.id;

            setTimeout(() => {
                if (createdDocumentId) {
                    nav(`/librarian/documents/${createdDocumentId}`);
                } else {
                    nav("/librarian/documents");
                }
            }, 800);

        } catch (ex) {
            console.error("CREATE DOCUMENT ERROR:", ex);
            console.error("STATUS:", ex.response?.status);
            console.error("DATA:", ex.response?.data);

            setErr(getErrorMessage(ex));

        } finally {
            setSaving(false);
        }
    };

    const cancel = () => {
        nav("/librarian/documents");
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

    return (
        <div className="py-4">
            <Card className="shadow-sm border-0 rounded-4">
                <Card.Header className="bg-white">
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <div>
                            <h3 className="fw-bold mb-1">
                                Thêm tài liệu mới
                            </h3>

                            <p className="text-muted mb-0">
                                Nhập metadata, chọn thumbnail và upload file tài liệu.
                            </p>
                        </div>

                        <Button type="button" variant="outline-secondary" onClick={cancel} disabled={saving} >
                            Hủy
                        </Button>
                    </div>
                </Card.Header>

                <Card.Body className="p-4">
                    {err && (<Alert variant="danger">{err}</Alert>)}

                    {success && (<Alert variant="success">{success}</Alert>)}

                    <Form onSubmit={submitCreate}>
                        <Row>
                            <Col md={6}>
                                <Form.Group className="mb-3" controlId="title">
                                    <Form.Label>Tiêu đề</Form.Label>

                                    <Form.Control
                                        type="text"
                                        placeholder="Nhập tiêu đề"
                                        value={form.title}
                                        onChange={(e) => updateField("title", e.target.value)}
                                        disabled={saving}
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group className="mb-3" controlId="author">
                                    <Form.Label>Tác giả</Form.Label>

                                    <Form.Control
                                        type="text"
                                        placeholder="Nhập tác giả"
                                        value={form.author}
                                        onChange={(e) => updateField("author", e.target.value)}
                                        disabled={saving}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row>
                            <Col md={4}>
                                <Form.Group className="mb-3" controlId="publisher">
                                    <Form.Label>Nhà xuất bản</Form.Label>

                                    <Form.Control
                                        type="text"
                                        placeholder="Nhập nhà xuất bản"
                                        value={form.publisher}
                                        onChange={(e) => updateField("publisher", e.target.value)}
                                        disabled={saving}
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={3}>
                                <Form.Group className="mb-3" controlId="publishYear">
                                    <Form.Label>Năm xuất bản</Form.Label>

                                    <Form.Control
                                        type="number"
                                        placeholder="2026"
                                        value={form.publishYear}
                                        onChange={(e) => updateField("publishYear", e.target.value)}
                                        disabled={saving}
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={5}>
                                <Form.Group className="mb-3" controlId="categoryId">
                                    <Form.Label>Danh mục</Form.Label>

                                    <Form.Select
                                        value={form.categoryId}
                                        onChange={(e) => updateField("categoryId", e.target.value)}
                                        disabled={saving || loadingCategories}
                                    >
                                        <option value="">Chọn danh mục</option>

                                        {categories.map(c => (<option key={c.id} value={c.id}>{c.name}</option>))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row>
                            <Col md={4}>
                                <Form.Group className="mb-3" controlId="documentType">
                                    <Form.Label>
                                        Type
                                    </Form.Label>

                                    <Form.Select
                                        value={form.documentType}
                                        onChange={(e) => updateDocumentType(e.target.value)}
                                        disabled={saving}
                                    >
                                        <option value="PDF">PDF</option>
                                        <option value="DOCX">DOCX</option>
                                        <option value="EPUB">EPUB</option>
                                        <option value="VIDEO">VIDEO</option>
                                        <option value="AUDIO">AUDIO</option>
                                    </Form.Select>

                                    <Form.Text className="text-muted">
                                        Đổi type sẽ xóa danh sách file đã chọn.
                                    </Form.Text>
                                </Form.Group>
                            </Col>

                            <Col md={3}>
                                <Form.Group className="mb-3" controlId="premium">
                                    <Form.Label>Premium</Form.Label>

                                    <div className="border rounded-3 px-3 py-2 bg-light">
                                        <Form.Check
                                            type="checkbox"
                                            label="Premium"
                                            checked={form.premium}
                                            onChange={(e) => updatePremium(e.target.checked)}
                                            disabled={saving}
                                        />
                                    </div>
                                </Form.Group>
                            </Col>

                            <Col md={5}>
                                <Form.Group className="mb-3" controlId="price">
                                    <Form.Label>Giá</Form.Label>

                                    <Form.Control
                                        type="number"
                                        min="0"
                                        value={form.premium ? form.price : "0"}
                                        onChange={(e) => updateField("price", e.target.value)}
                                        disabled={saving || !form.premium}
                                        placeholder="Nhập giá nếu là premium"
                                    />

                                    <Form.Text className="text-muted">
                                        Nếu không tick Premium, price sẽ là 0.
                                    </Form.Text>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Form.Group className="mb-3" controlId="description">
                            <Form.Label>Mô tả</Form.Label>

                            <Form.Control
                                as="textarea"
                                rows={3}
                                placeholder="Nhập mô tả tài liệu"
                                value={form.description}
                                onChange={(e) => updateField("description", e.target.value)}
                                disabled={saving}
                            />
                        </Form.Group>

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
                                        Không bắt buộc. Hỗ trợ JPG, PNG, WEBP.
                                    </Form.Text>
                                </Form.Group>

                                {thumbnailPreview && (
                                    <Image src={thumbnailPreview}
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
                                        Files
                                    </Form.Label>

                                    <div className="d-flex gap-2">
                                        <Form.Control
                                            ref={fileInputRef}
                                            type="file"
                                            multiple
                                            accept={getFileAccept()}
                                            onChange={handleFilesChange}
                                            disabled={saving}
                                        />

                                        <Button
                                            type="button"
                                            variant="outline-primary"
                                            onClick={() => fileInputRef.current?.click()}
                                            disabled={saving}
                                        >
                                            + Thêm file
                                        </Button>
                                    </div>

                                    <Form.Text className="text-muted">
                                        Có thể chọn nhiều file. File phải đúng với type hiện tại:
                                        {" "}
                                        <strong>{form.documentType}</strong>
                                    </Form.Text>
                                </Form.Group>

                                {files.length > 0 && (
                                    <div className="border rounded-3 p-3 bg-light">
                                        <div className="d-flex justify-content-between align-items-center mb-2">
                                            <div className="fw-semibold">
                                                Files đã chọn
                                            </div>

                                            <Badge bg="secondary">
                                                {files.length} file
                                            </Badge>
                                        </div>

                                        {files.map((file, index) => (
                                            <div
                                                key={`${file.name}-${file.size}-${file.lastModified}`}
                                                className="d-flex justify-content-between align-items-center border-bottom py-2"
                                            >
                                                <div>
                                                    <div className="fw-semibold small">
                                                        {file.name}
                                                    </div>

                                                    <div className="text-muted small">
                                                        {formatSize(file.size)}
                                                    </div>
                                                </div>

                                                <Button
                                                    type="button"
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => removeFile(index)}
                                                    disabled={saving}
                                                >
                                                    Xóa
                                                </Button>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </Col>
                        </Row>

                        <div className="d-flex justify-content-end gap-2 mt-4">
                            <Button
                                type="button"
                                variant="outline-secondary"
                                onClick={cancel}
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
                                    "Lưu/Gửi duyệt"
                                )}
                            </Button>
                        </div>
                    </Form>
                </Card.Body>
            </Card>
        </div>
    );
};

export default NewDocument;