# Interview Preparation Guide: AI Knowledge Assistant

This guide provides structured answers and explanations for technical interviews regarding the AI Knowledge Assistant project.

## 1. System Architecture Explanation
"The system is built on a decoupled, microservices-ready architecture. The frontend is a React 18 Vite SPA acting as the client. The backend is a Java 21 Spring Boot REST API. We use MySQL for relational data (users, metadata, chat history), Redis for caching expensive LLM summarization calls, and Pinecone as a Vector Database for semantic search. Everything is containerized using Docker and orchestrated via Docker Compose."

## 2. RAG Pipeline Flow (Retrieval-Augmented Generation)
"When a user asks a question, we don't just send it to the LLM. Instead:
1. We embed the user's question using OpenAI's `text-embedding-3-small`.
2. We query our Pinecone vector database using this embedding to find the top K most semantically similar text chunks that were previously extracted from the uploaded PDF/Video.
3. We take these retrieved chunks and inject them into a system prompt as 'context'.
4. We send this enriched prompt to `gpt-4o-mini`. The LLM reads the context and formulates a highly accurate, hallucination-free answer based *only* on the provided documents."

## 3. Whisper Transcription Pipeline
"Handling multimedia requires an asynchronous pipeline because processing takes time.
1. When a user uploads an MP4 or MP3, the `FileStorageService` saves it and fires an asynchronous event.
2. The `WhisperTranscriptionService` picks this up and sends the file to OpenAI's Whisper API.
3. Crucially, we request the `verbose_json` response format. This allows us to extract not just the raw text, but granular 'segments' with precise start and end timestamps.
4. We chunk these text segments, embed them, and store them in Pinecone with metadata linking back to the exact timestamp, allowing the frontend to jump directly to the relevant part of the video when a user asks a question."

## 4. Security Hardening
"Security was a primary focus.
- **Authentication**: Stateless JWT access tokens (15m expiry) paired with database-backed Refresh Tokens (7 days). This allows us to manually revoke sessions if an account is compromised.
- **Rate Limiting**: Implemented `Bucket4j` via a Spring OncePerRequestFilter. We limit global traffic to 100 req/min/IP, but strictly limit expensive operations like uploads (20/hr) and chat queries (30/min).
- **Malware Prevention**: We don't just rely on file extensions. We read the first 8 bytes (Magic Bytes) of every upload to verify file signatures (e.g., `%PDF`, `ftyp`) to prevent executable malware injection."

## 5. Dockerized Architecture
"The app uses a multi-stage Docker approach. 
- The backend compiles the `jar` using a Maven builder image, then runs it on a lightweight Alpine JRE image.
- The frontend builds the static assets using Node, and serves them using an NGINX Alpine image, which also acts as a reverse proxy, routing `/api` traffic to the backend container.
- `docker-compose` networks the Frontend, Backend, MySQL, and Redis containers together."

## 6. Common Interview Questions & Answers

**Q: Why use Pinecone instead of searching MySQL?**
A: MySQL is optimized for exact keyword matches (or full-text search). LLM context retrieval requires *semantic* search (understanding the meaning of the query). Pinecone calculates cosine similarity between high-dimensional mathematical vectors, finding concepts that mean the same thing even if the exact keywords differ.

**Q: How do you handle large PDF uploads?**
A: We use Langchain4j and Apache PDFBox to stream the text extraction. Instead of loading the whole string into memory and hitting the LLM limit, we use a `RecursiveCharacterTextSplitter` to chunk the text into 1000-token segments with a 200-token overlap, preserving context between chunks before embedding them.

**Q: Why use Redis?**
A: Summarizing a large document via an LLM takes several seconds and costs API credits. By wrapping the `SummaryService` in Spring's `@Cacheable`, we cache the generated summary in Redis. Subsequent requests for that document's summary return in milliseconds and cost $0.
