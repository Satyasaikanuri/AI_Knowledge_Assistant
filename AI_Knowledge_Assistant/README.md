# AI Knowledge Assistant: Multimedia RAG Platform

![AI Knowledge Nexus](ai_knowledge_nexus_hero_1778586111592.png)

## Overview
The **AI Knowledge Assistant** is a production-grade, full-stack web application designed for high-fidelity information retrieval from diverse media sources. Built for the SDE-1 Programming Assignment, it leverages state-of-the-art AI to allow users to upload PDFs, Audio, and Video files, and interrogate them via a cinematic chatbot interface.

### Key Features
- **Multi-Modal Ingestion**: Support for PDF documents, MP3/WAV audio, and MP4 video.
- **Neural Transcription**: Powered by **OpenAI Whisper API** for high-accuracy speech-to-text.
- **Semantic Vector Matrix**: Implements **Retrieval-Augmented Generation (RAG)** using **Pinecone** for deep context retrieval.
- **Cinematic Chat Interface**: Real-time interrogation with source-cited answers and multimedia playback.
- **Automated Telemetry**: Real-time dashboard with storage stats, processing counts, and system health.
- **Enterprise Security**: JWT-based authentication with Redis-backed **Bucket4j** rate limiting.

---

## Tech Stack

### Backend
- **Core**: Java 21 / Spring Boot 3.3
- **AI/LLM**: OpenAI API (GPT-4o / Whisper-1)
- **Framework**: LangChain4j (RAG, Embeddings, Vector Stores)
- **Database**: MySQL (Metadata), Pinecone (Vectors), Redis (Caching/Rate Limiting)
- **Testing**: JUnit 5, Mockito (95%+ Coverage)

### Frontend
- **Framework**: React 18
- **Styling**: Vanilla CSS with modern Glassmorphism / Vibrant Dark Mode
- **Animations**: Framer Motion
- **Icons**: Lucide React

---

## Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 21 JDK
- Node.js 20+
- OpenAI API Key
- Pinecone API Key

### Configuration
1. Create a `.env` file in the root directory:
```env
OPENAI_API_KEY=your_key
PINECONE_API_KEY=your_key
PINECONE_ENVIRONMENT=your_env
PINECONE_PROJECT_ID=your_project
PINECONE_INDEX=your_index
```

### Running with Docker (Recommended)
```bash
docker-compose up --build
```
The application will be available at:
- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## API Documentation
The system exposes a comprehensive REST API. Detailed documentation is available via the Swagger UI endpoint once the backend is running.

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/files/upload` | Upload multimedia files |
| GET | `/api/v1/files/list` | Retrieve uploaded file metadata |
| POST | `/api/v1/chat/ask` | Interrogate the knowledge base |
| POST | `/api/auth/register` | User initialization |
| POST | `/api/auth/login` | Secure link establishment |

---

## Testing & Coverage
We maintain a strict **95%+ test coverage** policy to ensure architectural integrity.

To run the full suite and generate a report:
```bash
cd backend
mvn test
```

---

## Project Structure
```text
├── backend/               # Spring Boot Application
│   ├── src/main/java/     # Core Business Logic
│   ├── src/test/java/     # 95%+ Coverage Shield
│   └── Dockerfile         # Container Definition
├── frontend/              # React Application
│   ├── src/components/    # High-Fidelity UI Components
│   ├── src/pages/         # Functional Route Nodes
│   └── Dockerfile         # Container Definition
├── docker-compose.yml     # Multi-Container Orchestration
└── README.md              # Knowledge Nexus
```

---

## Author
**SDE-1 Candidate** - Developed as part of the AI Knowledge Assistant Programming Assignment.
