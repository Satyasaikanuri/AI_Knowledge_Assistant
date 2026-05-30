# Deployment Guide

This guide explains how to deploy the AI Knowledge Assistant to production using modern cloud providers.

## Architecture Topology
- **Backend**: Render.com (Web Service)
- **Frontend**: Vercel (Static Site)
- **Database**: Railway.app (MySQL 8)
- **Cache**: Upstash (Serverless Redis)
- **Vector DB**: Pinecone (Serverless)

---

## 1. Database & Cache Provisioning

### Railway (MySQL)
1. Go to [Railway.app](https://railway.app/).
2. Create a new project -> Provision MySQL.
3. Copy the `MYSQL_URL`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLHOST`, and `MYSQLPORT` from the Connect tab.

### Upstash (Redis)
1. Go to [Upstash.com](https://upstash.com/).
2. Create a new Redis Database.
3. Copy the `UPSTASH_REDIS_REST_URL` and `UPSTASH_REDIS_REST_TOKEN` (or the standard Redis connection string: `redis://default:password@endpoint:port`).

---

## 2. Backend Deployment (Render)

1. Push your code to a GitHub repository.
2. Go to [Render.com](https://render.com/) and create a new **Web Service**.
3. Connect your GitHub repository.
4. **Configuration:**
   - **Environment**: Docker
   - **Build Command**: `docker build -t app ./backend` (Render auto-detects the Dockerfile in the root, so point the Root Directory to `backend/`).
5. **Environment Variables**:
   Add the variables you gathered earlier:
   ```env
   MYSQL_HOST=your-railway-host
   MYSQL_PORT=3306
   MYSQL_USER=root
   MYSQL_PASSWORD=your-railway-password
   MYSQL_DATABASE=railway
   REDIS_HOST=your-upstash-host
   REDIS_PORT=your-upstash-port
   REDIS_PASSWORD=your-upstash-password
   JWT_SECRET=generate_a_secure_random_64_character_string
   OPENAI_API_KEY=sk-...
   PINECONE_API_KEY=...
   ```
6. Click **Deploy**. Note the assigned Render URL (e.g., `https://ai-knowledge-api.onrender.com`).

---

## 3. Frontend Deployment (Vercel)

1. Go to [Vercel.com](https://vercel.com/) and create a new Project.
2. Import your GitHub repository.
3. **Framework Preset**: Vite
4. **Root Directory**: `frontend/`
5. **Environment Variables**:
   ```env
   VITE_API_URL=https://ai-knowledge-api.onrender.com/api/v1
   VITE_WS_URL=wss://ai-knowledge-api.onrender.com/ws
   ```
6. Click **Deploy**.

---

## 4. Troubleshooting Deployments

- **CORS Errors**: Ensure the backend `CorsConfigurationSource` inside `SecurityConfig.java` allows the Vercel production URL.
- **Upload Size Limits**: NGINX on Render might block large uploads. If uploading large videos fails, configure `client_max_body_size` in your cloud provider proxy settings.
- **Cold Starts**: Render's free tier spins down the backend after 15 minutes of inactivity. Initial requests may take up to 60 seconds. Upgrade to the $7/mo Starter tier for production usage.
