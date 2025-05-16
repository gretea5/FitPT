import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server:{
    host: true, // 외부 접근 허용
    port: 5174,
    strictPort: true,
    allowedHosts: ['localhost']
  }
})
