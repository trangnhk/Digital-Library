/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services.impl;

import com.trangnhk.client.AiServiceClient;
import com.trangnhk.dto.ai.AiChatRequestDTO;
import com.trangnhk.dto.ai.AiChatResponseDTO;
import com.trangnhk.dto.ai.ReferencedBook;
import com.trangnhk.pojo.Document;
import com.trangnhk.repositories.DocumentRepository;
import com.trangnhk.services.AiChatService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Admin
 */
@Service
@Transactional
public class AiChatServiceImpl implements AiChatService {

    private final AiServiceClient aiServiceClient;

    @Autowired
    private final DocumentRepository documentRepo;

    public AiChatServiceImpl(AiServiceClient aiServiceClient, DocumentRepository documentRepo) {
        this.aiServiceClient = aiServiceClient;
        this.documentRepo = documentRepo;
    }
    
    private void enrichReferenceBook (AiChatResponseDTO response){
        // Get Reference Book
        ReferencedBook referenceBook = response.getReferencedBook();

        if (referenceBook == null) {
            return;
        }

        // Validate Book id
        Long bookId = referenceBook.getBookId();

        if (bookId == null) {
            return;
        }

        Document doc = documentRepo.getPublicDocumentById(bookId);
        if (doc == null) {
            return;
        }

        String thumbNail = doc.getThumbnail();
        if (thumbNail.isEmpty() || thumbNail.isBlank()) {
            referenceBook.setThumbnail("https://res.cloudinary.com/dxfbpkmen/image/upload/v1782756551/images_zjv1zb.jpg");
        } else {
            referenceBook.setThumbnail(thumbNail);
        }
    }

    @Override
    public AiChatResponseDTO chat(AiChatRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Chat request cannot be null.");
        }

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty.");
        }

        if (request.getConversationHistory() == null) {
            request.setConversationHistory(new ArrayList<>());
        }

        if (request.getTopK() == null) {
            request.setTopK(5);
        }

        AiChatResponseDTO response = aiServiceClient.chat(request);

        this.enrichReferenceBook(response);

        return response;
    }
}
