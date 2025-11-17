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
                    
                    {/* FIX ADICIONAL: El backend no envía 'isAdmin'. 
                      Por ahora, lo quitamos, o deberías añadir lógica de roles en el backend.
                    */}
                    {/* {isAuthenticated && user?.isAdmin && ( 
                        <NavLink to="/usuarios" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                            Usuarios
                        </NavLink>
                    )} */}
                    
                    {/* Solución temporal: Mostrar Usuarios si está autenticado (para pruebas) */}
                    {isAuthenticated && (
                         <NavLink to="/usuarios" className={({isActive}) => isActive ? "nav-link active" : "nav-link"}>
                            Usuarios
                        </NavLink>
                    )}
                </div>

                <div className="navbar-auth">
                    {isAuthenticated ? (
                        <>
                            {/* LA CORRECCIÓN ESTÁ AQUÍ: 
                              Usamos user?.nombreCompleto y user?.email como respaldo
                            */}
                            <Link to="/perfil" className="nav-user">
                                Hola, {user?.nombreCompleto || user?.email}
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