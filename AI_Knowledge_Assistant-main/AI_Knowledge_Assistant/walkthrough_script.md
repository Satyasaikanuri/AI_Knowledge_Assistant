# Walkthrough Video Script

**Target Duration:** 5-7 Minutes
**Tone:** Professional, Technical, Confident

---

### [0:00 - 0:45] 1. Introduction & Overview
*Screen: Show the Dashboard homepage.*
"Hello, my name is [Your Name], and I'm a full-stack developer. Today I'm presenting the AI Knowledge Assistant. This is an enterprise-grade web application built with Java 21, Spring Boot, React, and Pinecone that allows users to upload documents and multimedia, and interact with them using an advanced Retrieval-Augmented Generation, or RAG, pipeline. Let's dive in."

### [0:45 - 2:00] 2. Security & File Uploads
*Screen: Demonstrate dragging and dropping a PDF and an MP4 video in the Uploads page.*
"Security and scalability start at the upload layer. When I upload these files, they pass through a strict Magic Byte validation in the backend to ensure no malware is masquerading as a PDF. The upload is handled via a multi-part streaming API, and we use Bucket4j to strictly rate-limit uploads to 20 per hour per IP to prevent Denial of Service attacks."

### [2:00 - 3:30] 3. The RAG & Whisper Pipeline (Backend Explanation)
*Screen: Show the Swagger UI (/swagger-ui.html) or briefly show the IntelliJ backend code.*
"While those files upload, let me explain what's happening asynchronously. For the PDF, we use Langchain4j and Apache PDFBox to chunk the document into 1000-token segments, embed them using OpenAI, and store them in Pinecone. 
For the video, we send the audio track to the OpenAI Whisper API. Crucially, we request a verbose JSON format. This gives us precise timestamp metadata for every spoken sentence, which we also embed and store in our vector database."

### [3:30 - 5:00] 4. AI Chat & Timestamp Jumping (The "Wow" Factor)
*Screen: Navigate to the Chat UI. Select the uploaded video. Ask a specific question.*
"Now for the chat interface. I'm going to ask a specific question about the video. 
*Type question.*
Notice how the AI responds dynamically. It didn't just search for keywords; it performed a semantic similarity search in Pinecone. Even better, because of our Whisper pipeline, it returns the exact timestamps where the answer is discussed. In a production environment, clicking this timestamp automatically jumps the media player to the exact second the topic is discussed."

### [5:00 - 6:00] 5. Admin Panel & Observability
*Screen: Navigate to the Admin Dashboard. Show the stats and user table.*
"The system includes a fully RBAC-protected Admin dashboard. Here we see live statistics fetched from the database. Furthermore, the backend is highly observable. We utilize Spring Boot Actuator..."
*Open a new tab to http://localhost:8080/actuator/health*
"...which exposes deep health metrics and Prometheus endpoints for production monitoring."

### [6:00 - 6:30] 6. Deployment & Conclusion
*Screen: Show the docker-compose.yml file.*
"The entire stack is containerized using multi-stage Docker builds. With a single `docker-compose up` command, we spin up the React frontend via NGINX, the Spring Boot backend, a Redis cache, and a MySQL database.
Thank you for watching. This project demonstrates my ability to integrate modern AI tools into secure, scalable, and production-ready enterprise architectures."
