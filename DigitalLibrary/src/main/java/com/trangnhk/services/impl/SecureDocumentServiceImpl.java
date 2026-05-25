/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.dto.AccessResponseDTO;
import com.trangnhk.dto.AdminDocumentActionResponseDTO;
import com.trangnhk.dto.AdminDocumentResponseDTO;
import com.trangnhk.dto.BorrowResponseDTO;
import com.trangnhk.dto.DocumentBorrowerDTO;
import com.trangnhk.dto.DocumentContentResponseDTO;
import com.trangnhk.dto.PageResponseDTO;
import com.trangnhk.dto.RejectDocumentRequestDTO;
import com.trangnhk.pojo.AccessHistory;
import com.trangnhk.pojo.BorrowHistory;
import com.trangnhk.pojo.Document;
import com.trangnhk.pojo.DocumentFile;
import com.trangnhk.pojo.Notification;
import com.trangnhk.pojo.User;
import com.trangnhk.pojo.enums.BorrowStatus;
import com.trangnhk.pojo.enums.UserRole;
import com.trangnhk.repositories.AccessHistoryRepository;
import com.trangnhk.repositories.BorrowHistoryRepository;
import com.trangnhk.repositories.CategoryRepository;
import com.trangnhk.repositories.DocumentFileRepository;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.repositories.NotificationRepository;
import com.trangnhk.repositories.UserRepository;
import com.trangnhk.services.SecureDocumentService;
import com.trangnhk.services.UserService;
import com.trangnhk.utils.AdminDocumentSorts;
import com.trangnhk.utils.SortUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class SecureDocumentServiceImpl implements SecureDocumentService {

    private static final int ACCESS_SPAM_SECONDS = 5;
    private static final String NOTIFICATION_APPROVED_TITLE = "Successfully Approved";
    private static final String NOTIFICATION_APPROVED_CONTENT = "Your document was approved at ";
    private static final String NOTIFICATION_REJECTED_TITLE = "DOCUMENT HAS BEEN REJECTED";

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentRepository docRepo;

    @Autowired
    private DocumentFileRepository docFileRepo;

    @Autowired
    private AccessHistoryRepository accessRepo;

    @Autowired
    private BorrowHistoryRepository borrowRepo;

    @Autowired
    private CategoryRepository cateReppo;

    @Autowired
    private UserRepository userReppo;

    @Autowired
    private NotificationRepository notiRepo;

    @Override
    public AccessResponseDTO recordAccess(String username, Long documentId, String ipAddress) {
        User u = this.getActiveUser(username);

        Document doc = this.getApprovedDocument(documentId);

        boolean spam = this.accessRepo.existRecentAccess(u.getId(), doc.getId(), ipAddress, ACCESS_SPAM_SECONDS);

        if (spam) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many access requests");

        }

        AccessHistory access = new AccessHistory();
        access.setUser(u);
        access.setDocument(doc);
        access.setIpAddress(ipAddress);

        AccessHistory savedAccess = this.accessRepo.add(access);

        Integer totalViews = doc.getTotalViews();
        if (totalViews == null) {
            totalViews = 0;
        }

        doc.setTotalViews(totalViews + 1);

        this.docRepo.update(doc);

        return AccessResponseDTO.fromAccessHistory(savedAccess, doc.getTotalViews());

    }

    private User getActiveUser(String username) {
        User u = this.userService.getUserByUsername(username);

        if (u == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "unauthorized");
        }

        if (!Boolean.TRUE.equals(u.getActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is deactive");
        }

        return u;

    }

    private Document getApprovedDocument(Long documentId) {
        Document doc = this.docRepo.getDocumentById(documentId);

        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not foudn");
        }

        if (Boolean.FALSE.equals(doc.getApproved())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document is not approved");
        }

        return doc;

    }

    @Override
    public DocumentContentResponseDTO getDocumentContent(String username, Long documentId, Long fileId) {
        User u = this.getActiveUser(username);

        Document doc = this.getApprovedDocument(documentId);

        if (Boolean.TRUE.equals(doc.getPremium()) && !this.hasPaidDocument(u, doc)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Payment required for premium document");
        }

        DocumentFile file;

        if (fileId != null) {
            file = this.docFileRepo.getFileByIdAndDocumnetId(fileId, documentId);
        } else {
            file = this.docFileRepo.getFirstFileByDocumentId(documentId);
        }

        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document file not found");
        }

        return DocumentContentResponseDTO.fromDocumentFile(file);

    }

    private boolean hasPaidDocument(User u, Document doc) {
        return false;
    }

    @Override
    public BorrowResponseDTO borrowDocument(String username, Long documentId) {
        User u = this.getActiveUser(username);
        Document doc = this.getApprovedDocument(documentId);

        if (this.isAdmin(u) || this.isLibrarian(u)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin and Librarian can't borrow document");
        }

        boolean alreadyBorrowing = this.borrowRepo.existOpenBorrow(u.getId(), doc.getId());

        if (alreadyBorrowing) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already borrowing this document");
        }

        BorrowHistory borrow = new BorrowHistory();
        borrow.setUser(u);
        borrow.setDocument(doc);
        borrow.setStatus(BorrowStatus.BORROWING);

        BorrowHistory savedBorrow = this.borrowRepo.add(borrow);

        return BorrowResponseDTO.fromBorrowHistory(savedBorrow);

    }

    private boolean isAdmin(User user) {
        return user != null && "ROLE_ADMIN".equals(user.getRole().toString());
    }

    private boolean isLibrarian(User user) {
        return user != null && "ROLE_LIBRARIAN".equals(user.getRole().toString());
    }

    @Override
    public PageResponseDTO<AdminDocumentResponseDTO> getAdminDocuments(Map<String, String> params) {
        List<Document> docs = this.docRepo.getAdminDocuments(params);

        long totalItems = this.docRepo.countAdminDocuments(params);

        int page = this.getPage(params);
        int size = this.getSize(params);

        List<AdminDocumentResponseDTO> items = docs.stream().map(AdminDocumentResponseDTO::fromDocument)
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
    public String validateAdminDocumentParams(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        String approved = params.get("approved");
        if (approved != null && !approved.trim().isEmpty()) {
            if (!approved.equalsIgnoreCase("true") && !approved.equalsIgnoreCase("false")) {
                return "approved requires only: true or false";
            }
        }

        String cateId = params.get("categoryId");
        if (cateId != null && !cateId.trim().isEmpty()) {
            try {
                Long id = Long.valueOf(cateId);

                if (this.cateReppo.getActiveCategoryById(id) == null) {
                    return "Category NOT FOUND";
                }

            } catch (NumberFormatException ex) {
                return "categoryId must be a positive integer";
            }
        }

        String uploadBy = params.get("uploadBy");
        if (uploadBy != null && !uploadBy.trim().isEmpty()) {
            try {
                Long id = Long.valueOf(uploadBy);
                if (this.userReppo.getUserById(id) == null) {
                    return "User NOT FOUND";
                }

            } catch (NumberFormatException ex) {
                return "uploadBy must be a positive integer";
            }
        }

        String size = params.get("size");
        if (size != null && !size.trim().isEmpty()) {
            try {
                int sizeValue = Integer.parseInt(size);
                if (sizeValue < 1) {
                    return "size must be more than 0";
                }
                if (sizeValue > 20) {
                    return "size can't over 20";
                }
            } catch (NumberFormatException ex) {
                return "size must be  a positive integer";
            }
        }
        if (!SortUtils.isValidSort(params, AdminDocumentSorts.ADMIN_DOCUMENT_SORT, AdminDocumentSorts.DEFAULT_SORT)) {
            return "Only sort by: newest, title, publishYear, popular, pending, approved";
        }

        return null;

    }

    @Override
    public AdminDocumentActionResponseDTO approveDocument(Long documentId) {
        Document doc = this.getNotApprovedDocument(documentId);

        User librarian = this.getValidLibrarianOwner(doc);

        Date approvedAt = new Date();

        doc.setApproved(Boolean.TRUE);

        Document savedDoc = this.docRepo.update(doc);

        Notification noti = new Notification();
        noti.setUser(librarian);
        noti.setTitle(NOTIFICATION_APPROVED_TITLE);
        noti.setContent(String.format(NOTIFICATION_APPROVED_TITLE + " " + doc.getTitle() + " at " + this.formatDateTime(approvedAt)));
        noti.setIsRead(Boolean.FALSE);
        noti.setCreatedDate(approvedAt);

        Notification savedNoti = this.notiRepo.save(noti);

        return new AdminDocumentActionResponseDTO(
                savedDoc.getId(),
                savedDoc.getTitle(),
                savedDoc.getApproved(),
                "Document approved successfully",
                savedNoti.getId(),
                savedNoti.getTitle(),
                savedNoti.getContent(),
                approvedAt);
    }

    private Document getNotApprovedDocument(Long documentId) {
        Document doc = this.docRepo.getDocumentById(documentId);

        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not foudn");
        }

        if (Boolean.TRUE.equals(doc.getApproved())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document is already approved");
        }

        return doc;

    }

    private User getValidLibrarianOwner(Document document) {
        User u = document.getUploadedBy();

        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document owner not found");
        }

        if (!UserRole.ROLE_LIBRARIAN.equals(u.getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document owner is not a valid Librarian");
        }

        return u;

    }

    @Override
    public AdminDocumentActionResponseDTO rejectDocument(Long documentId, RejectDocumentRequestDTO dto) {
        Document doc = this.docRepo.getDocumentById(documentId);

        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document NOT FOUND");
        }

        User librarian = getValidLibrarianOwner(doc);

        Date rejectAt = new Date();

        String reason = this.extractRejectReason(dto);

        doc.setApproved(Boolean.FALSE);

        Document savedDoc = this.docRepo.update(doc);

        String notificationContent = "Document: " + savedDoc.getTitle() + " has been rejected. Reason: " + reason;

        Notification noti = new Notification();
        noti.setUser(librarian);
        noti.setTitle(NOTIFICATION_REJECTED_TITLE);
        noti.setContent(notificationContent);
        noti.setIsRead(Boolean.FALSE);
        noti.setCreatedDate(rejectAt);

        Notification savedNoti = this.notiRepo.save(noti);

        return new AdminDocumentActionResponseDTO(
                savedDoc.getId(),
                savedDoc.getTitle(),
                savedDoc.getApproved(),
                "Document rejected successfully.",
                savedNoti.getId(),
                savedNoti.getTitle(),
                savedNoti.getContent(),
                rejectAt);

    }

    private String extractRejectReason(RejectDocumentRequestDTO dto) {
        if (dto == null || dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            return "Document doesn't meet the system's requirements";
        }

        return dto.getReason().trim();
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    @Override
    public List<DocumentBorrowerDTO> getDocumentBorrowers(String username, Long documentId, Map<String, String> params) {
        User currentU = this.getActiveUser(username);

        if (!this.isAdmin(currentU) && !this.isLibrarian(currentU)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin and Librarian can view document borrowers");
        }

        if (this.isLibrarian(currentU) && Boolean.FALSE.equals(currentU.getLibrarianVerified())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Librarian account has not been verified");
        }

        Document doc = this.docRepo.getDocumentById(documentId);
        if (doc == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document NOT FOUND");
        }

        User owner = doc.getUploadedBy();

        if (owner == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document owner not found.");
        }

        if (!this.isAdmin(currentU) && !owner.getId().equals(currentU.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view borrowers of your own document.");
        }
        
        return this.borrowRepo.getBorrowerByDocumentId(documentId, params);

    }
}
