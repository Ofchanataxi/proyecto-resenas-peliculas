import React from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css'; 

function Navbar() {
    const { isAuthenticated, user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="navbar-container">
                
                
                <Link to="/" className="navbar-logo">
                    🎬 CineReseñas
                </Link>

                <div className="navbar-links">
                    
                    <NavLink to="/" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                        Películas
                    </NavLink>
                    <NavLink to="/cines" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                        Cines
                    </NavLink>
                    
                    {/* --- INICIO DE LA CORRECCIÓN --- */}
                    {/* Ahora mostramos todos los enlaces de admin si el usuario está logueado */}
                    {isAuthenticated && (
                        <>
                            <NavLink to="/usuarios" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                                Usuarios
                            </NavLink>
                            <NavLink to="/admin/peliculas" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                                Admin Películas
                            </NavLink>
                            <NavLink to="/admin/cines" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                                Admin Cines
                            </NavLink>
                        </>
                    )}
                    {/* --- FIN DE LA CORRECCIÓN --- */}
                </div>

                <div className="navbar-auth">
                    {isAuthenticated ? (
                        <>
                            <Link to="/perfil" className="nav-user">
                                Hola, {user?.nombreCompleto || user?.email}
                            </Link>
                            <Link to="/chat" className="nav-user">Chat 💬</Link>
                            <button onClick={handleLogout} className="nav-button-logout">
                                Cerrar Sesión
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="nav-button">
                                Iniciar Sesión
                            </Link>
                            <Link to="/register" className="nav-button-primary">
                                Registrarse
                            </Link>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;