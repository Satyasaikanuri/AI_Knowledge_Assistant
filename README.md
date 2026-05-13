# AI Knowledge Assistant (Neural RAG Core)

A production-ready Enterprise AI Knowledge Assistant capable of multi-modal ingestion (PDF, Audio, Video), neural transcription, and contextual interrogation using RAG (Retrieval-Augmented Generation).

## 🚀 Features
- **Multi-modal Ingestion**: Support for PDF, MP3, and MP4 files.
- **Neural Transcription**: Powered by Groq Whisper-Large-V3 for near-instant speech-to-text.
- **Contextual Ingestion**: Automatic chunking and embedding into Pinecone Vector Store.
- **Neural Chat UI**: Futuristic terminal-style chat with clickable timestamps and source citations.
- **Neural Summarization**: One-click comprehensive summaries of any uploaded knowledge unit.
- **Collapsible Media Player**: Synced video/audio player that jumps to specific neural sync points.

## 🛠 Tech Stack
- **Backend**: Java 21, Spring Boot 3.3.0, Spring Data JPA, Redis.
- **Frontend**: React, Vite, Framer Motion, TailwindCSS/Vanilla CSS.
- **AI/ML**: Groq API (Transcription/LLM), Pinecone (Vector Store), LangChain4j.
- **Database**: MySQL (Metadata), Redis (Session/Rate Limiting).

---

## ⚙️ Setup Instructions

### 1. Prerequisites
- **Java 21** installed.
- **Node.js** (v18+) installed.
- **MySQL** and **Redis** running locally.
- **Groq API Key** (Free tier available at [groq.com](https://groq.com)).
- **Pinecone API Key** and Index.

### 2. Backend Configuration
Navigate to `backend/src/main/resources/application.yml` and update your credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/knowledge_assistant
    username: your_mysql_user
    password: your_mysql_password
  data:
    redis:
      host: localhost
      port: 6379

groq:
  api-key: ${GROQ_API_KEY}
  model-name: llama-3.1-70b-versatile
  whisper-model: whisper-large-v3

pinecone:
  api-key: ${PINECONE_API_KEY}
  environment: your_environment
  project-id: your_project_id
  index-name: your_index_name
```

### 3. Frontend Configuration
Navigate to `frontend/src/api/api.js` and ensure the `baseURL` matches your backend:
```javascript
baseURL: 'http://localhost:8080/api/v1'
```

---

## 🏃 Running the Application

### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```
The application will be available at `http://localhost:5173`.

---

## 📖 API Documentation

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/files/upload` | Upload PDF/Audio/Video file. |
| `GET` | `/api/v1/files/list` | List all uploaded knowledge units. |
| `POST` | `/api/v1/chat/ask` | Ask a question about a specific file. |
| `GET` | `/api/v1/summary/{id}` | Generate/Retrieve AI summary of a file. |
| `GET` | `/api/v1/files/stream/{id}` | Stream video/audio with auth token. |

---

## 🧪 Testing
To run the full test suite (95%+ coverage target):
```bash
cd backend
mvn test
```

## 📄 License
This project is for demonstration and SDE-1 Assignment evaluation.
Designed by **Antigravity AI**.
