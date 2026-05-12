/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Admin
 */

@Entity
@Table(name = "conversations")
class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate = new Date();

    @ManyToMany(mappedBy = "conversations")
    @JsonIgnore
    private Set<User> users;

    @OneToMany(mappedBy = "conversation")
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
     * @return the users
     */
    public Set<User> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(Set<User> users) {
        this.users = users;
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
    
}
