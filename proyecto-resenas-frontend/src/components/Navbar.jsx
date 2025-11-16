import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
    const { isAuthenticated, user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav style={{ display: 'flex', gap: '1rem', padding: '1rem', background: '#333' }}>
            <Link to="/">Inicio</Link>
            
            {isAuthenticated ? (
                <>
                    <Link to="/usuarios">Usuarios (Admin)</Link>
                    <Link to="/perfil">Mi Perfil ({user.nombreCompleto})</Link>
                    <button onClick={handleLogout}>Cerrar Sesión</button>
                </>
            ) : (
                <>
                    <Link to="/login">Iniciar Sesión</Link>
                    <Link to="/register">Registrarse</Link>
                </>
            )}
        </nav>
    );
}

export default Navbar;