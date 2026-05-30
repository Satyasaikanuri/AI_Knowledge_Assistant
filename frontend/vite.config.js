import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'https://ai-knowledge-assistant-nmqw.onrender.com',
        changeOrigin: true,
      },
      '/ws': {
        target: 'wss://ai-knowledge-assistant-nmqw.onrender.com',
        ws: true,
      }
    }
  }
})
