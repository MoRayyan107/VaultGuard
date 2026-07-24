import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import {RouterProvider} from "react-router-dom";
import router from "./routes/router.tsx";
import {AuthProvider} from "./context/AuthContext.tsx";
import {TransactionProvider} from "./context/TransactionContext.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
      {/* AuthProvider provides authentication context to the entire application */}
    <AuthProvider>
        {/* Transaction Provider for holding data across all endpoints*/}
        <TransactionProvider>
            {/* RouterProvider boots up the client side application, manages global route configuration*/}
            <RouterProvider router={router} />
        </TransactionProvider>
    </AuthProvider>
  </StrictMode>,
)
