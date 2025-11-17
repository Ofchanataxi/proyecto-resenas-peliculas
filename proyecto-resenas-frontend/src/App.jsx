import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

// Páginas de Autenticación y Usuario

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';
import UsuariosPage from './pages/UsuariosPage'; // Admin

// --- NUEVAS PÁGINAS (Debemos importarlas) ---
import PeliculaDetallePage from './pages/PeliculaDetallePage'; // Nueva
import CinesPage from './pages/CinesPage';
import HomePage from "./pages/homePage"; // Nueva

function App() {
    return (
        <div className="App">
            <Navbar /> {/* <-- La barra de navegación siempre visible */}
            <main>
                <Routes>
                    {/* --- Rutas Públicas --- */}

                    {/* La ruta raíz ahora es la cartelera de películas */}
                    <Route path="/" element={<HomePage />} />

                    {/* Página de detalles de una película */}
                    <Route path="/pelicula/:id" element={<PeliculaDetallePage />} />

                    {/* Página de cines */}
                    <Route path="/cines" element={<CinesPage />} />

                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />

                    {/* --- Rutas Protegidas (Usuario logueado) --- */}
                    <Route element={<ProtectedRoute />}>
                        <Route path="/perfil" element={<ProfilePage />} />
                        {/* Nota: Crear/editar reseñas se manejará dentro
              de PeliculaDetallePage, verificando al usuario.
            */}
                    </Route>

                    {/* --- Rutas Protegidas (Admin) --- */}
                    {/* Asumimos que ProtectedRoute podría checar roles a futuro */}
                    <Route element={<ProtectedRoute />}>
                        <Route path="/usuarios" element={<UsuariosPage />} />
                        {/* Aquí irían las rutas de /admin/peliculas/crear, etc. */}
                    </Route>
                </Routes>
            </main>
        </div>
    );
}

export default App;