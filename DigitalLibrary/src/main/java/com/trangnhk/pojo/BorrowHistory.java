/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.pojo;

import com.trangnhk.pojo.enums.BorrowStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Admin
 */

@Entity
@Table(name = "borrow_histories")
@NamedQueries({
    @NamedQuery(name = "BorrowHistory.findAll", query = "SELECT b FROM BorrowHistory b"),
    @NamedQuery(name = "BorrowHistory.findById", query = "SELECT b FROM BorrowHistory b WHERE b.id = :id"),
    @NamedQuery(name = "BorrowHistory.findByBorrowDate", query = "SELECT b FROM BorrowHistory b WHERE b.borrowDate = :borrowDate"),
    @NamedQuery(name = "BorrowHistory.findByStatus", query = "SELECT b FROM BorrowHistory b WHERE b.status = :status")})
public class BorrowHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "borrow_date")
    private Date borrowDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "return_date")
    private Date returnDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "due_date")
    private Date dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BorrowStatus status = BorrowStatus.BORROWING;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Document document;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the borrowDate
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * @param borrowDate the borrowDate to set
     */
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * @return the returnDate
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * @param returnDate the returnDate to set
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * @return the status
     */
    public BorrowStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * @param document the document to set
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
