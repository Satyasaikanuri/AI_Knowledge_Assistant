package com.knowledge.assistant.service;

import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.repository.UploadedFileRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfProcessingService {

    private final UploadedFileRepository fileRepository;
    private final EmbeddingService embeddingService;

    public PdfProcessingService(UploadedFileRepository fileRepository, EmbeddingService embeddingService) {
        this.fileRepository = fileRepository;
        this.embeddingService = embeddingService;
    }

    @Async
    public void processPdfAsync(Long fileId) {
        System.out.println("Starting background processing for PDF file ID: " + fileId);
        try {
            UploadedFile uploadedFile = fileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("File not found for processing: " + fileId));

            Path filePath = Paths.get(uploadedFile.getFilePath());
            
            // Load and parse PDF using Langchain4j with Apache PDFBox
            Document document = FileSystemDocumentLoader.loadDocument(filePath, new ApachePdfBoxDocumentParser());
            String extractedText = document.text();
            
            if (extractedText != null) {
                extractedText = extractedText.replaceAll("\\s+", " ");
                extractedText = extractedText.replaceAll("\\n{2,}", "\n");
                document = dev.langchain4j.data.document.Document.from(extractedText, document.metadata());
            }
            
            // To save memory and prevent JPA bloat, we only store a truncated preview
            // of the text in the database (used for Summarization later).
            String previewText = extractedText;
            if (previewText != null && previewText.length() > 50000) {
                previewText = previewText.substring(0, 50000) + "...";
            }
            uploadedFile.setExtractedText(previewText);
            fileRepository.save(uploadedFile);
            
            System.out.println("Successfully extracted text from PDF: " + uploadedFile.getOriginalFileName());

            // Next step: Chunk and embed
            embeddingService.chunkAndEmbedDocument(document, uploadedFile);

            // Clear references to massive strings and suggest GC to JVM
            extractedText = null;
            document = null;
            System.gc();

        } catch (Exception e) {
            System.out.println("Error processing PDF for file ID: " + fileId + ". Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
