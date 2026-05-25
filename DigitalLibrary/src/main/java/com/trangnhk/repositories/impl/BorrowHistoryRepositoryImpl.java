/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.repositories.impl;

import com.trangnhk.dto.DocumentBorrowerDTO;
import com.trangnhk.pojo.BorrowHistory;
import com.trangnhk.pojo.enums.BorrowStatus;
import com.trangnhk.repositories.BorrowHistoryRepository;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Repository
@Transactional
public class BorrowHistoryRepositoryImpl implements BorrowHistoryRepository {

    @Autowired
    private LocalSessionFactoryBean factory;

    @Override
    public BorrowHistory add(BorrowHistory borrow) {
        Session s = this.factory.getObject().getCurrentSession();

        s.persist(borrow);

        return borrow;
    }

    @Override
    public boolean existOpenBorrow(Long userId, Long documentId) {
        Session s = this.factory.getObject().getCurrentSession();

        Query query = s.createQuery("SELECT COUNT(b.id) FROM BorrowHistory b "
                + "WHERE b.user.id = :userId "
                + "AND b.document.id = :documentId "
                + "AND b.status = :status", Long.class);

        query.setParameter("userId", userId);
        query.setParameter("documentId", documentId);
        query.setParameter("status", BorrowStatus.BORROWING);

        Long count = (Long) query.getSingleResult();

        return count > 0;
    }

    @Override
    public List<DocumentBorrowerDTO> getBorrowerByDocumentId(Long documentId, Map<String, String> params) {
        Session s = this.factory.getObject().getCurrentSession();

        StringBuilder hql = new StringBuilder("""
                        SELECT new com.trangnhk.dto.DocumentBorrowerDTO(
                            b.id,
                            d.id,
                            d.title,
                            u.id,
                            u.username,
                            CONCAT(COALESCE(u.firstName, ''), ' ', COALESCE(u.lastName, '')),
                            b.status,
                            b.borrowDate,
                            b.returnDate
                        )
                        FROM BorrowHistory b
                        JOIN b.document d
                        JOIN b.user u
                        WHERE d.id = :documentId
                    """);
        
        BorrowStatus status = this.getValidBorrowStatus(params);
        
        if (status != null){
            hql.append(" AND b.status = :status ");
        }
        
        hql.append(this.buildOrderBy(params));
        Query query = s.createQuery(hql.toString(), DocumentBorrowerDTO.class);
        
        query.setParameter("documentId", documentId);
        
        if (status != null){
            query.setParameter("status", status);
        }
        
        return query.getResultList();
        
        
    }
    private BorrowStatus getValidBorrowStatus(Map<String, String> params) {
        if (params == null) {
            return null;
        }

        String status = params.get("status");

        if (status == null || status.trim().isEmpty()) {
            return null;
        }

        try {
            return BorrowStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
    
    private String buildOrderBy(Map<String, String> params) {
        String sort = "borrowDate";
        String direction = "desc";

        if (params != null) {
            if (params.get("sort") != null && !params.get("sort").trim().isEmpty()) {
                sort = params.get("sort").trim();
            }

            if (params.get("direction") != null
                    && params.get("direction").equalsIgnoreCase("asc")) {
                direction = "asc";
            }
        }

        String orderDirection = direction.equalsIgnoreCase("asc") ? "ASC" : "DESC";

        switch (sort) {
            case "status":
                return " ORDER BY b.status " + orderDirection
                        + ", b.borrowDate DESC ";

            case "title":
            case "documentTitle":
                return " ORDER BY d.title " + orderDirection
                        + ", b.borrowDate DESC ";

            case "borrowerName":
                return " ORDER BY u.firstName " + orderDirection
                        + ", u.lastName " + orderDirection
                        + ", u.username " + orderDirection
                        + ", b.borrowDate DESC ";

            case "returnDate":
                return " ORDER BY b.returnDate " + orderDirection
                        + ", b.borrowDate DESC ";

            case "borrowDate":
            default:
                return " ORDER BY b.borrowDate " + orderDirection;
        }
    }
    
}
