/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.services;

import com.trangnhk.dto.ai.AiChatRequestDTO;
import com.trangnhk.dto.ai.AiChatResponseDTO;

/**
 *
 * @author Admin
 */
public interface AiChatService {
    public AiChatResponseDTO chat(AiChatRequestDTO request);
    
}
