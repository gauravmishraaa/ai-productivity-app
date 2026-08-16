# AI Task Manager — Roadmap

## Tech Stack
- **Backend**: Java 21, Spring Boot 3.5.x, Spring AI 1.1.8, Maven
- **LLM Providers** (switch via one config line, see `docs/LLM_PROVIDER_ARCHITECTURE.md`):
  - Ollama (local, free, self-hosted) — default
  - Google Gemini (needs `GOOGLE_API_KEY`)
  - Easy to add more (OpenAI, Claude, etc.) — just add the Spring AI starter
- **Database**: PostgreSQL, Spring Data JPA, pgvector
- **Frontend**: React.js, Vite, Zustand, Tailwind CSS
- **Chat Memory**: Spring AI ChatMemory with JDBC

---

## ✅ Phase 1 — AI Chat Foundation (DONE)

- ✅ Spring Boot setup
- ✅ Spring AI + Ollama integration
- ✅ ChatClient configuration
- ✅ REST API with streaming (SSE)
- ✅ Markdown rendering
- ✅ Code highlighting (Highlight.js)
- ✅ Copy code buttons
- ✅ Stop generation
- ✅ Auto-growing textarea

---

## ✅ Phase 2 — Conversation Memory (DONE)

- ✅ Spring AI ChatMemory
- ✅ MessageChatMemoryAdvisor
- ✅ Multiple conversations
- ✅ JDBC persistence (SPRING_AI_CHAT_MEMORY table)

**API Endpoints:**
```
POST   /api/v1/conversations
GET    /api/v1/conversations
GET    /api/v1/conversations/{id}
PUT    /api/v1/conversations/{id}
DELETE /api/v1/conversations/{id}
POST   /api/v1/chat/stream (with streaming)
GET    /api/v1/chat/history/{conversationId}
```

---

## ✅ Phase 3 — Better Chat Experience (DONE)

### Backend ✅
- ✅ Conversation CRUD
- ✅ Chat history API
- ✅ Exception handling
- ✅ Pause/resume/cancel streaming

### Frontend ✅
- ✅ Sidebar with conversation list
- ✅ Create/rename/delete conversations
- ✅ Dark mode toggle
- ✅ LocalStorage persistence
- ✅ Chat interface with streaming
- ✅ Edit & regenerate message
- ✅ Responsive UI

---

## ✅ Phase 4 — Task Manager (DONE)

### Backend ✅
- ✅ Task entity (id, title, description, status, priority, dueDate, createdAt, updatedAt)
- ✅ TaskRepository with JPA Specifications
- ✅ TaskService interface + impl
- ✅ TaskController with full CRUD
- ✅ DTOs (CreateTaskRequest, UpdateTaskRequest, TaskResponse)
- ✅ Mapper (Task ↔ TaskResponse)
- ✅ CRUD operations
- ✅ Pagination & filtering
- ✅ Sorting by createdAt (DESC)
- ✅ Search by title/description
- ✅ Status filtering (TODO, IN_PROGRESS, DONE)
- ✅ Priority filtering (HIGH, MEDIUM, LOW)
- ✅ Validation

### Frontend ✅
- ✅ Tasks view with CRUD
- ✅ Dynamic filtering (status, priority, search)
- ✅ Pagination
- ✅ Task creation modal
- ✅ Task editing modal
- ✅ Quick status change dropdown
- ✅ Task deletion with confirmation
- ✅ Responsive UI

**API Endpoints:**
```
POST   /api/v1/tasks
GET    /api/v1/tasks?page=0&size=20&status=TODO&priority=HIGH&search=spring
GET    /api/v1/tasks/{id}
PUT    /api/v1/tasks/{id}
PATCH  /api/v1/tasks/{id}/status
DELETE /api/v1/tasks/{id}
```

---

## 🔄 Phase 5 — AI Tool Calling (IN PROGRESS)

Enable AI to execute backend operations.

### Status
- ✅ TaskToolService with @Tool methods
- ✅ Spring AI tool registration
- ⚠️ Inconsistent tool calling (qwen2.5 model limitation)

### Features (Implemented)
- ✅ Spring AI @Tool annotations
- ✅ createTask() tool
- ✅ listTasks() tool with filters
- ✅ getTask() tool
- ✅ updateTaskStatus() tool
- ✅ deleteTask() tool
- ✅ Tool response formatting

**Note:** Tool calling works intermittently with qwen2.5. Reliable with more capable models (Mistral, etc.)

---

## 🚀 Phase 6 — RAG (Retrieval Augmented Generation) (NEXT)

AI answers from your own documents.

### Planned Features
- [ ] pgvector integration (vector DB in PostgreSQL)
- [ ] Document entity (id, filename, content, embedding, createdAt)
- [ ] Document upload endpoint (PDF, TXT, DOCX)
- [ ] PDF parsing (Apache PDFBox)
- [ ] Text chunking strategy
- [ ] Embedding generation (Spring AI embedding models)
- [ ] Vector storage in pgvector
- [ ] Similarity search
- [ ] Hybrid search (vector + keyword)
- [ ] Metadata filtering
- [ ] RAG retrieval injection into chat context

**Flow:**
```
Upload Document
         ↓
Extract Text / Chunk (configurable strategy)
         ↓
Generate Embeddings (using Spring AI)
         ↓
Store Vectors in pgvector
         ↓
User Query
         ↓
Similarity Search (semantic + keyword)
         ↓
Inject Retrieved Context into LLM
         ↓
LLM Answer
```

**API Endpoints:**
```
POST   /api/v1/documents/upload      (upload file)
GET    /api/v1/documents             (list)
GET    /api/v1/documents/{id}        (get)
DELETE /api/v1/documents/{id}        (delete)
POST   /api/v1/documents/search      (semantic search)
```

---

## 🚀 Phase 7 — Agentic AI (NEXT)

Multi-step autonomous workflows with planning & execution.

### Planned Features
- [ ] Agent orchestration (loop: plan → act → observe)
- [ ] Multi-step planning capability
- [ ] Memory/context management across steps
- [ ] Retry logic with backoff
- [ ] Tool chaining (task creation → scheduling → reminders)
- [ ] Agent state machine
- [ ] Stream planning steps to frontend
- [ ] Execution logging

**Example Workflows:**
```
"Plan my week" 
         ↓
Agent reads all tasks
         ↓
Agent categorizes by priority
         ↓
Agent creates schedule
         ↓
Agent creates reminder tasks
         ↓
Return plan + actions
```

```
"Extract tasks from this document"
         ↓
Agent reads document
         ↓
Agent identifies tasks
         ↓
Agent extracts due dates
         ↓
Agent creates tasks via tool call
         ↓
Return created tasks
```

**API Endpoint:**
```
POST /api/v1/agents/execute
Body: { agentType, goal, context }
Response: { steps, actions, result }
```

---

## 🔮 Phase 8+ — Advanced Features (Future)

- [ ] Spring Security config
- [ ] User entity
- [ ] JWT token generation
- [ ] Refresh token mechanism
- [ ] Role-based access (USER, ADMIN)
- [ ] Login/Register endpoints
- [ ] Secure endpoints with @PreAuthorize

**Relationships:**
```
User 1:N Conversations
User 1:N Tasks
```

---

## 🟡 Phase 7 — File Upload

- [ ] File upload endpoint
- [ ] Store: PDF, DOCX, TXT, Images
- [ ] File metadata (filename, size, type, path)
- [ ] Associate files with conversations/tasks
- [ ] File retrieval endpoint

---

## 🟡 Phase 8 — RAG (Retrieval Augmented Generation)

AI answers from your own documents.

- [ ] pgvector integration
- [ ] Document chunking
- [ ] Embedding generation
- [ ] Vector storage
- [ ] Similarity search
- [ ] Hybrid search (vector + keyword)
- [ ] Metadata filtering

**Flow:**
```
Upload Document
         ↓
Extract Text / Chunk
         ↓
Generate Embeddings
         ↓
Store in pgvector
         ↓
User Query
         ↓
Similarity Search
         ↓
LLM Answer
```

---

## 🟡 Phase 9 — AI Agents

Multi-step autonomous workflows.

- [ ] Agent orchestration
- [ ] Planning capability
- [ ] Memory/context management
- [ ] Retry logic
- [ ] Tool chaining

**Example:**
```
User: "Plan my week"
         ↓
Agent checks tasks
         ↓
Agent prioritizes
         ↓
Agent creates schedule
         ↓
Return plan with actions
```

---

## 🟡 Phase 10 — Advanced AI

- [ ] Prompt templates
- [ ] Structured output (JSON mode)
- [ ] Multiple model support
- [ ] Vision models
- [ ] OCR on documents
- [ ] Speech input/output
- [ ] Model context protocol (MCP)
- [ ] AI-based evaluation

---

## 🟡 Phase 11 — AI Coding Assistant

Claude Code-style assistant for the project.

- [ ] Codebase indexing
- [ ] Repository understanding
- [ ] File editing
- [ ] Terminal execution
- [ ] Git integration
- [ ] Refactoring suggestions
- [ ] Test generation
- [ ] Documentation generation

---

## 🟡 Phase 12 — Production Ready

### Infrastructure
- [ ] Docker & Docker Compose
- [ ] Redis cache
- [ ] Rate limiting
- [ ] Centralized logging
- [ ] Monitoring (Prometheus/Grafana)
- [ ] Health checks

### Deployment
- [ ] Nginx reverse proxy
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] Environment configs

---

## 🟡 Phase 13 — Cloud Deployment

- [ ] AWS/Azure/GCP migration
- [ ] Kubernetes setup
- [ ] Helm charts
- [ ] Load balancing
- [ ] Object storage (S3/equivalent)
- [ ] Secrets management

---

## Progress Overview

```
█████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░

~35% Complete (Phases 1-2 done, Phase 3 nearly done)
```

### Completed
- ✅ AI Chat (SSE streaming)
- ✅ Markdown & code highlighting
- ✅ Conversation memory
- ✅ Conversation CRUD
- ✅ Chat history
- ✅ Sidebar UI
- ✅ Dark mode

### Next Immediate Steps
1. Complete Phase 3 UI polish
2. Start Phase 4: Task Entity + CRUD
3. Phase 5: AI Tool Calling (make AI productive)

---

## Development Guidelines

- Keep frontend vanilla (no framework) for learning
- Maintain API response wrapper format
- Use SSE for streaming responses
- Write tests for service layer
- Keep code modular and loosely coupled
