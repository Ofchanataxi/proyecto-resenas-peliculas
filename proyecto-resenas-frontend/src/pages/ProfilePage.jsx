import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import * as usuarioApi from '../api/usuarioApi'; // Usamos la API de usuario

function ProfilePage() {
    const { user, logout } = useAuth(); // Obtenemos el usuario logueado
    const navigate = useNavigate();

    if (!user) {
        return <p>Cargando perfil...</p>;
    }

    // Desactivar la *propia* cuenta
    const handleDeactivate = async () => {
        if (window.confirm("¿Estás seguro de que quieres desactivar tu cuenta? Tendrás que volver a iniciar sesión para reactivarla.")) {
            try {
                await usuarioApi.deactivateUsuario(user.id);
                alert("Cuenta desactivada. Serás desconectado.");
                logout();
                navigate('/login');
            } catch (error) {
                alert(`Error: ${error.message}`);
            }
        }
    };

    // Eliminar la *propia* cuenta
    const handleDelete = async () => {
        if (window.confirm("¡ADVERTENCIA! ¿Estás seguro de que quieres ELIMINAR tu cuenta permanentemente? Esta acción no se puede deshacer.")) {
            try {
                await usuarioApi.deleteUsuario(user.id);
                alert("Cuenta eliminada permanentemente. Serás desconectado.");
                logout();
                navigate('/');
            } catch (error) {
                alert(`Error: ${error.message}`);
            }
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>Mi Perfil</h2>
            <p><strong>Nombre:</strong> {user.nombreCompleto}</p>
            <p><strong>Email:</strong> {user.email}</p>
            <p><strong>ID de Usuario:</strong> {user.id}</p>
            <p><strong>Estado:</strong> {user.activo ? 'Activo' : 'Inactivo'}</p>

            <hr style={{ margin: '20px 0' }} />

            <h3>Administrar Cuenta</h3>
            
            <button 
                onClick={handleDeactivate} 
                style={{ backgroundColor: 'orange', color: 'black' }}
                disabled={!user.activo} // No puedes desactivar una cuenta ya inactiva
            >
                Desactivar mi Cuenta
            </button>
            
            <button 
                onClick={handleDelete} 
                style={{ backgroundColor: 'red', color: 'white', marginLeft: '10px' }}
            >
                Eliminar mi Cuenta Permanentemente
            </button>
        </div>
    );
}

export default ProfilePage;