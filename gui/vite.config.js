import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
    resolve: {
        fallback: {
            global: false
        }
    },
    plugins: [
        react(),
        tailwindcss(),
    ],
    define: {
        global: 'window',
    },
    server: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                secure: false,
            },
        },
    }
})
