/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trangnhk.pojo.enums.UserRole;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Admin
 */
@Entity
@Table(name="users")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findByPhone", query = "SELECT u FROM User u WHERE u.phone = :phone"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByPassword", query = "SELECT u FROM User u WHERE u.password = :password"),
    @NamedQuery(name = "User.findByActive", query = "SELECT u FROM User u WHERE u.active = :active"),
    @NamedQuery(name = "User.findByRole", query = "SELECT u FROM User u WHERE u.role =  role"),
    @NamedQuery(name = "User.findByAvatar", query = "SELECT u FROM User u WHERE u.avatar = :avatar")})
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    @Basic(optional = false)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    @Basic(optional = false)
    private String lastName;

    @Column(nullable = false, unique = true, length = 50)
    @Basic(optional = false)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @Basic(optional = false)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(nullable = false)
    @Basic(optional = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(length = 500)
    private String avatar;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Basic(optional = false)
    private UserRole role;

    @Column
    @Basic(optional = false)
    private Boolean active = true;

    @Column(name = "librarian_verified")
    @Basic(optional = false)
    private Boolean librarianVerified = false;

    @Column(length = 20)
    private String provider = "LOCAL";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    @Basic(optional = false)
    private Date createdDate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate = new Date();
    
    // RELATIONSHIPS
    @OneToMany(mappedBy = "uploadedBy")
    @JsonIgnore
    private Set<Document> documents;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Review> reviews;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Payment> payments;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<BorrowHistory> borrowHistories;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<AccessHistory> accessHistories;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Bookmark> bookmarks;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Notification> notifications;

    @ManyToMany
    @JoinTable(
        name = "conversation_users",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "conversation_id")
    )
    @JsonIgnore
    private Set<Conversation> conversations;

    @OneToMany(mappedBy = "sender")
    @JsonIgnore
    private Set<ChatMessage> messages;
    

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
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar the avatar to set
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return the role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the librarianVerified
     */
    public Boolean getLibrarianVerified() {
        return librarianVerified;
    }

    /**
     * @param librarianVerified the librarianVerified to set
     */
    public void setLibrarianVerified(Boolean librarianVerified) {
        this.librarianVerified = librarianVerified;
    }

    /**
     * @return the provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the documents
     */
    public Set<Document> getDocuments() {
        return documents;
    }

    /**
     * @param documents the documents to set
     */
    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    /**
     * @return the reviews
     */
    public Set<Review> getReviews() {
        return reviews;
    }

    /**
     * @param reviews the reviews to set
     */
    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     * @return the payments
     */
    public Set<Payment> getPayments() {
        return payments;
    }

    /**
     * @param payments the payments to set
     */
    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    /**
     * @return the borrowHistories
     */
    public Set<BorrowHistory> getBorrowHistories() {
        return borrowHistories;
    }

    /**
     * @param borrowHistories the borrowHistories to set
     */
    public void setBorrowHistories(Set<BorrowHistory> borrowHistories) {
        this.borrowHistories = borrowHistories;
    }

    /**
     * @return the accessHistories
     */
    public Set<AccessHistory> getAccessHistories() {
        return accessHistories;
    }

    /**
     * @param accessHistories the accessHistories to set
     */
    public void setAccessHistories(Set<AccessHistory> accessHistories) {
        this.accessHistories = accessHistories;
    }

    /**
     * @return the bookmarks
     */
    public Set<Bookmark> getBookmarks() {
        return bookmarks;
    }

    /**
     * @param bookmarks the bookmarks to set
     */
    public void setBookmarks(Set<Bookmark> bookmarks) {
        this.bookmarks = bookmarks;
    }

    /**
     * @return the notifications
     */
    public Set<Notification> getNotifications() {
        return notifications;
    }

    /**
     * @param notifications the notifications to set
     */
    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * @return the conversations
     */
    public Set<Conversation> getConversations() {
        return conversations;
    }

    /**
     * @param conversations the conversations to set
     */
    public void setConversations(Set<Conversation> conversations) {
        this.conversations = conversations;
    }

    /**
     * @return the messages
     */
    public Set<ChatMessage> getMessages() {
        return messages;
    }

    /**
     * @param messages the messages to set
     */
    public void setMessages(Set<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.trangnhk.pojo.User[ id=" + id + " ]";
    }
    
    
}
