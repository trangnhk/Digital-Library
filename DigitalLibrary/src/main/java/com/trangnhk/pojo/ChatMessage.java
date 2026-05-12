/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.pojo;

import jakarta.persistence.*;
import java.util.Date;

/**
 *
 * @author Admin
 */

@Entity
@Table(name = "chat_messages")
class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_time")
    private Date sentTime = new Date();

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

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
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the sentTime
     */
    public Date getSentTime() {
        return sentTime;
    }

    /**
     * @param sentTime the sentTime to set
     */
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    /**
     * @return the conversation
     */
    public Conversation getConversation() {
        return conversation;
    }

    /**
     * @param conversation the conversation to set
     */
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    /**
     * @return the sender
     */
    public User getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(User sender) {
        this.sender = sender;
    }
}
