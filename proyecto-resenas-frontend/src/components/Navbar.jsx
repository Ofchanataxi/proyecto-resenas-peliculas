import React from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Navbar.css'; // <-- Importamos el nuevo CSS

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
                    {isAuthenticated && user?.isAdmin && ( // Asumimos que el user tiene un flag 'isAdmin'
                        <NavLink to="/usuarios" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                            Usuarios
                        </NavLink>
                    )}
                </div>

                <div className="navbar-auth">
                    {isAuthenticated ? (
                        <>
                            <Link to="/perfil" className="nav-user">
                                Hola, {user.nombreCompleto}
                            </Link>
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