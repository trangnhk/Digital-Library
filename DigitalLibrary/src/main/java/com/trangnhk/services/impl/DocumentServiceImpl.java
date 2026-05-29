/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trangnhk.dto.CreateLibrarianDocumentRequestDTO;
import com.trangnhk.dto.DocumentFileResponseDTO;
import com.trangnhk.dto.DocumentResponseDTO;
import com.trangnhk.dto.LibrarianDocumentResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.dto.UpdateLibrarianDocumentRequestDTO;
import com.trangnhk.pojo.Category;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.DocumentFile;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.DocumentType;
import com.trangnhk.repositories.CategoryRepository;
import com.trangnhk.repositories.DocumentFileRepository;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.repositories.PaymentRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.DocumentService;
import com.trangnhk.services.UserService;
import com.trangnhk.utils.DocumentSorts;
import com.trangnhk.utils.SortUtils;
import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of("PDF", "DOCX", "EPUB", "VIDEO", "AUDIO");

    @Autowired
    private DocumentRepository documentRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private DocumentFileRepository documentFileRepo;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Override
    public PageResponseDTO<DocumentResponseDTO> getPublicDocuments(Map<String, String> params) {
        List<Document> docs = this.documentRepo.getPublicDocuments(params);

        long totalItems = this.documentRepo.countPublicDocuments(params);

        int page = this.getPage(params);
        int size = this.getSize(params);

        List<DocumentResponseDTO> items = docs.stream()
                .map(DocumentResponseDTO::fromDocument)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(items, page, size, totalItems);

    }

    private int getPage(Map<String, String> params) {
        if (params == null) {
            return 1;
        }

        try {
            int page = Integer.parseInt(params.getOrDefault("page", "1"));

            if (page < 1) {
                return 1;
            }

            return page;

        } catch (NumberFormatException ex) {
            return 1;
        }
    }

    private int getSize(Map<String, String> params) {
        if (params == null) {
            return 10;
        }

        try {
            int size = Integer.parseInt(params.getOrDefault("size", "10"));

            if (size < 1) {
                return 10;
            }

            if (size > 20) {
                return 20;
            }

            return size;

        } catch (NumberFormatException ex) {
            return 10;
        }
    }

    @Override
    public DocumentResponseDTO getPublicDocumentById(Long documentId) {
        Document doc = this.documentRepo.getPublicDocumentById(documentId);

        if (doc == null) {
            return null;
        }

        return DocumentResponseDTO.fromDocument(doc);

    }

    @Override
    public String validatePublicDocumentParams(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        String size = params.get("size");

        if (size != null && !size.trim().isEmpty()) {
            try {
                int sizeValue = Integer.parseInt(size);

                if (sizeValue < 1) {
                    return "Size must be more than 0";
                }

                if (sizeValue > 20) {
                    return "Size mustn't be over 20";
                }

            } catch (NumberFormatException ex) {
                return "Size must be integer";
            }
        }

        if (!SortUtils.isValidSort(params, DocumentSorts.PUBLIC_DOCUMENT_SORT, DocumentSorts.DEFAULT_SORT)) {
            return "Only sort by: title, publishYear, popular, newest";
        }

        String publishYear = params.get("publishYear");

        if (publishYear != null && !publishYear.trim().isEmpty()) {
            try {
                Integer.valueOf(publishYear);
            } catch (NumberFormatException ex) {
                return "publishYear must be integer";
            }
        }

        String premium = params.get("premium");

        if (premium != null && !premium.trim().isEmpty()) {
            if (!premium.equalsIgnoreCase("true")
                    && !premium.equalsIgnoreCase("false")) {
                return "premium only be true or false";
            }
        }

        String documentType = params.get("documentType");

        if (documentType != null && !documentType.trim().isEmpty()) {
            if (!ALLOWED_DOCUMENT_TYPES.contains(documentType.trim())) {
                return "documentType must be one of: PDF, DOCX, EPUB, VIDEO, AUDIO";
            }
        }

        String categoryId = params.get("categoryId");

        if (categoryId != null && !categoryId.trim().isEmpty()) {
            try {
                Long id = Long.valueOf(categoryId);

                if (this.categoryRepo.getActiveCategoryById(id) == null) {
                    return "CATEGORY_NOT_FOUND";
                }

            } catch (NumberFormatException ex) {
                return "categoryId must be integer";
            }
        }

        return null;

    }

    @Override
    public List<DocumentFileResponseDTO> getPublicDocumentFiles(Long documentId) {
        Document doc = this.documentRepo.getPublicDocumentById(documentId);

        if (doc == null) {
            return null;
        }

        List<DocumentFile> files = this.documentFileRepo.getFilesByDocumentId(documentId);

        return files.stream().map(DocumentFileResponseDTO::fromDocumentFile)
                .collect(Collectors.toList());
    }

    // LIBRARIAN ROLE
    @Override
    public PageResponseDTO<LibrarianDocumentResponseDTO> getManagedDocuments(String username, Map<String, String> params) {
        User currentU = this.userService.getUserByUsername(username);

        this.checkCanAccessLibrarianDocumentManagement(currentU);

        boolean admin = this.isAdmin(currentU);

        List<Document> docs = this.documentRepo.getManagedDocuments(currentU, admin, params);

        long totalItems = this.documentRepo.countManagedDocument(currentU, admin, params);

        int page = this.getPage(params);
        int Size = this.getSize(params);

        List<LibrarianDocumentResponseDTO> items = docs.stream().map(LibrarianDocumentResponseDTO::fromDocument)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(items, page, Size, totalItems);

    }

    private void checkCanAccessLibrarianDocumentManagement(User u) {
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        System.out.println("USERNAME = " + u.getUsername());
        System.out.println("ROLE FROM getRole = " + u.getRole());
        System.out.println("LIBRARIAN VERIFIED = " + u.getLibrarianVerified());

        if (this.isAdmin(u)) {
            return;
        }

        if (!this.isLibrarian(u)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are NOT LIBRARIAN");

        }

        if (!Boolean.TRUE.equals(u.getLibrarianVerified())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account hasn't been verified");
        }

    }

    private boolean isAdmin(User user) {
        return user != null && "ROLE_ADMIN".equals(user.getRole().toString());
    }

    private boolean isLibrarian(User user) {
        return user != null && "ROLE_LIBRARIAN".equals(user.getRole().toString());
    }

    @Override
    public LibrarianDocumentResponseDTO createLibrarianDocument(String username, CreateLibrarianDocumentRequestDTO dto) {
        User currentU = this.userService.getUserByUsername(username);

        this.checkCanCreateDocument(currentU);

        DocumentType docType = this.parseDocumentType(dto.getDocumentType());

        Category cate = this.categoryRepo.getActiveCategoryById(dto.getCategoryId());

        if (cate == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
        }

        this.validatePrice(dto);
        this.validateThumbnail(dto.getThumbnail());
        this.validateFiles(dto.getFiles(), docType);

        Document doc = new Document();

        doc.setTitle(dto.getTitle().trim());
        doc.setDescription(dto.getDescription());
        doc.setAuthor(dto.getAuthor());
        doc.setPublisher(dto.getPublisher());
        doc.setPublishYear(dto.getPublishYear());
        doc.setDocumentType(docType);
        doc.setPremium(Boolean.TRUE.equals(dto.getPremium()));

        if (Boolean.TRUE.equals(dto.getPremium())) {
            doc.setPrice(dto.getPrice());
        } else {
            doc.setPrice(0.0);
        }

        if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
            String thumbnailUrl = this.uploadThumbnail(dto.getThumbnail());
            doc.setThumbnail(thumbnailUrl);
        }

        doc.setCategory(cate);
        doc.setUploadedBy(currentU);

        Document saveDoc = this.documentRepo.add(doc);

        for (MultipartFile file : dto.getFiles()) {
            DocumentFile docFile = this.uploadDocumentFile(file, saveDoc);
            this.documentFileRepo.add(docFile);
        }

        return LibrarianDocumentResponseDTO.fromDocument(saveDoc);

    }

    private void checkCanCreateDocument(User u) {
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        if (!this.isLibrarian(u)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are NOT LIBRARIAN");

        }

        if (!Boolean.TRUE.equals(u.getLibrarianVerified())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account hasn't been verified");
        }
    }

    // VALIDATE, BUSINESS LOGIC
    private DocumentType parseDocumentType(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document Type is required");

        }

        try {
            return DocumentType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Document Type must be in one of: PDF, DOCX, EPUB, VIDEO, AUDIO");
        }
    }

    private void validatePrice(CreateLibrarianDocumentRequestDTO dto) {
        if (dto.getPrice() == null) {
            dto.setPrice(0.0);
        }

        if (dto.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Price must be positive number");
        }

        if (!Boolean.TRUE.equals(dto.getPremium()) && dto.getPrice() > 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Premium = false , price must equal 0");
        }

    }

    private void validateThumbnail(MultipartFile thumbnail) {
        if (thumbnail == null || thumbnail.isEmpty()) {
            return;
        }

        String contentType = thumbnail.getContentType();

        if (!this.isValidThumbnailType(contentType)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Thumbnail is only be JPG, PNG, WEBP");
        }
    }

    private boolean isValidThumbnailType(String contentType) {
        if (contentType == null) {
            return false;
        }

        return contentType.equals("image/jpeg")
                || contentType.equals("image/png")
                || contentType.equals("image/webp");
    }

    private void validateFiles(List<MultipartFile> files, DocumentType documentType) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "You have to upload at least 1 file");
        }

        for (MultipartFile file : files) {
            this.validateDocumentFile(file, documentType);
        }
    }

    private void validateDocumentFile(MultipartFile file, DocumentType documentType) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "File is NOT NULL");
        }

        String extension = this.getFileExtension(file.getOriginalFilename());

        if (!this.isValidFileExtensionForDocumentType(extension, documentType)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File upload is not suitable with documentType " + documentType);
        }
    }

    private boolean isValidFileExtensionForDocumentType(String extension, DocumentType documentType) {
        if (extension == null || extension.isBlank() || documentType == null) {
            return false;
        }

        switch (documentType) {
            case PDF:
                return extension.equals("pdf");

            case DOCX:
                return extension.equals("docx");

            case EPUB:
                return extension.equals("epub");

            case VIDEO:
                return extension.equals("mp4");

            case AUDIO:
                return extension.equals("mp3") || extension.equals("wav");

            default:
                return false;
        }
    }

    private String uploadThumbnail(MultipartFile thumbnail) {
        try {
            Map uploadResult = this.cloudinary.uploader().upload(
                    thumbnail.getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );

            return uploadResult.get("secure_url").toString();

        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload thumbnail FAILED");
        }
    }

    private DocumentFile uploadDocumentFile(MultipartFile file, Document document) {
        try {
            Map uploadResult = this.cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            DocumentFile documentFile = new DocumentFile();
            documentFile.setFileUrl(uploadResult.get("secure_url").toString());

            if (uploadResult.get("public_id") != null) {
                documentFile.setPublicId(uploadResult.get("public_id").toString());
            }

            documentFile.setFileSize(file.getSize());
            documentFile.setFileExtension(this.getFileExtension(file.getOriginalFilename()));
            documentFile.setDocument(document);

            return documentFile;

        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload document file FAILED");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    @Override
    public DocumentResponseDTO getDocumentDetail(Long documentId, String username) {

        User currentUser = userRepo.getUserByUsername(username);

        Document document = documentRepo.getDocumentById(documentId);

        if (document == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Document not found"
            );
        }

//        boolean isAdmin = currentUser.getRole().name().equals("ROLE_ADMIN");
//        boolean isOwner = document.getUploadedBy().getId().equals(currentUser.getId());
//        boolean isVerifiedLibrarian = currentUser.getRole().name().equals("ROLE_LIBRARIAN")&& currentUser.getLibrarianVerified();
//        
//        if (!isAdmin && !isVerifiedLibrarian) {
//            throw new ResponseStatusException(
//                HttpStatus.FORBIDDEN,"Librarian not verified"
//        );
//    }
//        
//        if(!isAdmin && !isOwner){
//            throw new ResponseStatusException(
//                    HttpStatus.FORBIDDEN, "You don't have permission"
//            );
//        }
        this.checkCanModifyDocument(currentUser, document);

        return DocumentResponseDTO.fromDocument(document);
    }

    @Override
    public LibrarianDocumentResponseDTO updateLibrarianDocument(String username, Long documentId, UpdateLibrarianDocumentRequestDTO dto) {
        User currentU = this.userService.getUserByUsername(username);

        Document doc = this.documentRepo.getDocumentById(documentId);

        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found");

        }

        this.checkCanModifyDocument(currentU, doc); // Check permission

        // UPDATE FIELD
        DocumentType docType = doc.getDocumentType();
        DocumentType finalDocType = docType;

        if (dto.getDocumentType() != null && !dto.getDocumentType().trim().isEmpty()) {
            finalDocType = this.parseDocumentType(dto.getDocumentType());
            doc.setDocumentType(finalDocType);
        }

        if (dto.getCategoryId() != null) {
            Category cate = this.categoryRepo.getActiveCategoryById(dto.getCategoryId());

            if (cate == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found");
            }
            doc.setCategory(cate);
        }

        if (dto.getTitle() != null) {
            if (dto.getTitle().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
            }
            doc.setTitle(dto.getTitle().trim());

        }

        if (dto.getDescription() != null) {
            doc.setDescription(dto.getDescription().trim());

        }

        if (dto.getAuthor() != null) {
            if (dto.getAuthor().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author is required");
            }
            doc.setAuthor(dto.getAuthor().trim());

        }

        if (dto.getPublisher() != null) {
            if (dto.getPublisher().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Publisher is required");
            }
            doc.setPublisher(dto.getPublisher().trim());

        }

        if (dto.getPublishYear() != null) {
            Integer yearNow = Year.now().getValue();

            if (dto.getPublishYear() > yearNow) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PublishYear can't be more than this year");
            }

            doc.setPublishYear(dto.getPublishYear());
        }

        if (dto.getIsPremium() != null) {
            doc.setPremium(dto.getIsPremium());

            if (Boolean.TRUE.equals(doc.getPremium())) {
                if (dto.getPrice() == null) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Price is required for premium document");
                }
                if (dto.getPrice() < 0) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Price must be a positive number");
                }
                doc.setPrice(dto.getPrice());
            }
            else{
                doc.setPrice(0.0);
            }

            
        }

        if (Boolean.TRUE.equals(doc.getPremium()) && dto.getPrice() != null && doc.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Price must be positive number");
        }

        if (dto.getThumbnail() != null && !dto.getThumbnail().isEmpty()) {
            this.validateThumbnail(dto.getThumbnail());
            String thumbnailUrl = this.uploadThumbnail(dto.getThumbnail());
            doc.setThumbnail(thumbnailUrl);
        }

        if (dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            for (MultipartFile file : dto.getFiles()) {
                this.validateDocumentFile((MultipartFile) file, finalDocType);
                DocumentFile documentFile = this.uploadDocumentFile((MultipartFile) file, doc);
                this.documentFileRepo.add(documentFile);
            }
        }

        // Reset approved
        doc.setApproved(Boolean.FALSE);

        Document updatedDoc = this.documentRepo.update(doc);

        return LibrarianDocumentResponseDTO.fromDocument(updatedDoc);

    }

    private void checkCanModifyDocument(User u, Document doc) {
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        boolean admin = this.isAdmin(u);

        if (admin) {
            return;
        }

        if (!this.isLibrarian(u)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are NOT LIBRARIAN");
        }

        if (!Boolean.TRUE.equals(u.getLibrarianVerified())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account hasn't been verified");
        }

        if (doc.getUploadedBy() == null || !doc.getUploadedBy().getId().equals(u.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission");
        }

    }

    @Override
    public void deleteLibrarianDocument(String username, Long documentId) {
        User currentU = this.userService.getUserByUsername(username);

        Document doc = this.documentRepo.getDocumentById(documentId);

        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "document not found");
        }

        this.checkCanModifyDocument(currentU, doc);

        this.documentFileRepo.deleteByDocumentId(documentId);
        doc.setDeleted(Boolean.TRUE);

//        this.documentRepo.delete(doc);
    }

}
