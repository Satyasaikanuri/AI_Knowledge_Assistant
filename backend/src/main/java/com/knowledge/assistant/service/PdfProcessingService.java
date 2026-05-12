package com.knowledge.assistant.service;

import com.knowledge.assistant.entity.UploadedFile;
import com.knowledge.assistant.repository.UploadedFileRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfProcessingService {

    private final UploadedFileRepository fileRepository;
    private final EmbeddingService embeddingService;

    @Async
    public void processPdfAsync(Long fileId) {
        log.info("Starting background processing for PDF file ID: {}", fileId);
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
            
            uploadedFile.setExtractedText(extractedText);
            fileRepository.save(uploadedFile);
            
            log.info("Successfully extracted text from PDF: {}", uploadedFile.getOriginalFileName());

            // Next step: Chunk and embed
            embeddingService.chunkAndEmbedDocument(document, uploadedFile);

        } catch (Exception e) {
            log.error("Error processing PDF for file ID: {}", fileId, e);
        }
    }
}
