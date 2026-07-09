import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import {RouterProvider} from "react-router-dom";
import router from "./routes/router.tsx";

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    {/* RouterProvider boots up the client side application, manages global route configuration*/}
    <RouterProvider router={router} />
  </StrictMode>,
)
