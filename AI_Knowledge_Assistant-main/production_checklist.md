# Production Readiness Checklist

Before pushing the AI Knowledge Assistant to a live production environment, verify all items on this checklist.

## 1. Security Checklist
- [ ] **JWT Keys:** Ensure `JWT_SECRET` is a minimum 256-bit secure random key injected via environment variable, not hardcoded.
- [ ] **HTTPS/TLS:** Traffic must be forced over HTTPS. Set `server.ssl.enabled=true` if not handled by a reverse proxy.
- [ ] **CORS Hardening:** In `SecurityConfig.java`, replace `cors.setAllowedOrigins(List.of("*"))` with the specific production domain (e.g., `https://your-vercel-app.com`).
- [ ] **Rate Limiting:** Verify Bucket4j limits (100 req/min) are active by spamming an endpoint and observing HTTP 429 Too Many Requests.
- [ ] **Admin Credentials:** Do NOT use default passwords. Ensure the initial ADMIN account is seeded with a securely hashed password.

## 2. Performance Checklist
- [ ] **Database Indexing:** Ensure indices on `uploaded_by_id`, `file_id`, and `token` exist in MySQL via `SHOW INDEX FROM table_name;`.
- [ ] **Connection Pooling:** Verify HikariCP max pool size is sufficient (default 15) and does not exceed the DB provider's connection limit (important for Railway/PlanetScale free tiers).
- [ ] **Redis Caching:** Run a summarization request twice. Check backend logs to ensure the second request returns instantly without hitting the OpenAI API.
- [ ] **Compression:** Use Chrome DevTools network tab to ensure JSON payloads have `Content-Encoding: gzip`.

## 3. Deployment Checklist
- [ ] **Environment Variables:** All `.env` variables mapped correctly in Render and Vercel.
- [ ] **Storage Mounts:** If deploying via Docker, ensure the `/app/uploads` volume is mapped to persistent storage so uploaded files survive container restarts.
- [ ] **Health Checks:** Hit `/actuator/health` to confirm `status: "UP"` for DB, Redis, and Application space.
- [ ] **Error Boundaries:** Force a React error in the frontend to ensure the `react-error-boundary` cleanly catches it instead of showing a blank white screen.

## 4. AI / API Safety
- [ ] **Cost Controls:** Set hard billing limits in your OpenAI and Pinecone dashboards.
- [ ] **Context Windows:** Ensure Langchain4j's `MaxTokenSize` parameters match the limitations of `gpt-4o-mini` to prevent TokenOverflow exceptions on massive documents.
