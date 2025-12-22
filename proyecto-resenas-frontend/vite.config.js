import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '127.0.0.1',
    port: 5174,     // 👈 cambia (puede ser 3001, 4000, 8081)
    strictPort: true
  }
})
