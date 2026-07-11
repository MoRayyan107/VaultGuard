import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import {RouterProvider} from "react-router-dom";
import router from "./routes/router.tsx";
import {AuthProvider} from "./auth/AuthContext.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      {/* AuthProvider provides authentication context to the entire application */}
    <AuthProvider>
        {/* RouterProvider boots up the client side application, manages global route configuration*/}
        <RouterProvider router={router} />
    </AuthProvider>
  </StrictMode>,
)
