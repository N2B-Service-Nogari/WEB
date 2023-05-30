import React from 'react'
import ReactDOM from 'react-dom/client'
import '@/styles/global.css'
import { HelmetProvider } from 'react-helmet-async'
import { QueryClient, QueryClientProvider } from 'react-query'
import { RouterProvider } from 'react-router-dom'

import routers from './routes'
// theme
import ThemeProvider from './theme'

const queryClient = new QueryClient()

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  // <React.StrictMode>
  <QueryClientProvider client={queryClient}>
    <HelmetProvider>
      <ThemeProvider>
        <RouterProvider router={routers} />
      </ThemeProvider>
    </HelmetProvider>
  </QueryClientProvider>
  // </React.StrictMode>
)
