/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.controllers;

import com.trangnhk.dto.ai.AiChatRequestDTO;
import com.trangnhk.dto.ai.AiChatResponseDTO;
import com.trangnhk.services.AiChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Admin
 */
@RestController
@RequestMapping("/api/ai/chat")
public class ApiAiChatController {
    private final AiChatService aiChatService;

    public ApiAiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }
    
    @PostMapping
    public ResponseEntity<AiChatResponseDTO> chat(@RequestBody AiChatRequestDTO request){
        AiChatResponseDTO response = aiChatService.chat(request);
        return ResponseEntity.ok(response);
    }
}
