import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

// Páginas de Autenticación y Usuario
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import ProfilePage from './pages/ProfilePage';
import UsuariosPage from './pages/UsuariosPage';

// Páginas principales
import HomePage from "./pages/homePage";
import PeliculaDetallePage from './pages/PeliculaDetallePage';
import CinesPage from './pages/CinesPage';

// Admin
import AdminPeliculasPage from './pages/AdminPeliculasPage';
import AdminCinesPage from './pages/AdminCinesPage';
import ChatPage from './pages/ChatPage';
import Chat from './components/Chat';

function App() {
    return (
        <div className="App">
            <Navbar /> {/* Siempre visible */}
            <main>
                <Routes>

                    {/* --- RUTAS PÚBLICAS --- */}
                    <Route path="/" element={<HomePage />} />
                    <Route path="/pelicula/:id" element={<PeliculaDetallePage />} />
                    <Route path="/cines" element={<CinesPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />

                    {/* --- RUTAS PROTEGIDAS --- */}
                    <Route
                        path="/perfil"
                        element={
                            <ProtectedRoute>
                                <ProfilePage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/chat"
                        element={
                            <ProtectedRoute>
                                <ChatPage />
                            </ProtectedRoute>
                        }
                    />

                    {/* --- ADMIN --- */}
                    <Route
                        path="/usuarios"
                        element={
                            <ProtectedRoute>
                                <UsuariosPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/admin/peliculas"
                        element={
                            <ProtectedRoute>
                                <AdminPeliculasPage />
                            </ProtectedRoute>
                        }
                    />

                    <Route
                        path="/admin/cines"
                        element={
                            <ProtectedRoute>
                                <AdminCinesPage />
                            </ProtectedRoute>
                        }
                    />

                    {/* Para las rese;as */}
                    <Route path="/chat-resenas" element={<Chat />} />


                </Routes>
            </main>
        </div>
    );
}

export default App;
