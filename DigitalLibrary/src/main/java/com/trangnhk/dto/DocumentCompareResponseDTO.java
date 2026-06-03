/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.dto;

import java.util.List;

/**
 *
 * @author user
 */
public class DocumentCompareResponseDTO {
    private CompareDocumentDTO baseDocument;

    private List<CompareDocumentDTO> comparedDocuments;

    /**
     * @return the baseDocument
     */
    public CompareDocumentDTO getBaseDocument() {
        return baseDocument;
    }

    /**
     * @param baseDocument the baseDocument to set
     */
    public void setBaseDocument(CompareDocumentDTO baseDocument) {
        this.baseDocument = baseDocument;
    }

    /**
     * @return the comparedDocuments
     */
    public List<CompareDocumentDTO> getComparedDocuments() {
        return comparedDocuments;
    }

    /**
     * @param comparedDocuments the comparedDocuments to set
     */
    public void setComparedDocuments(List<CompareDocumentDTO> comparedDocuments) {
        this.comparedDocuments = comparedDocuments;
    }
}
