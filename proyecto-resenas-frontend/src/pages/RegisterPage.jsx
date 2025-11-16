import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import * as authApi from '../api/authApi';

function RegisterPage() {
    const [formData, setFormData] = useState({
        nombreCompleto: '',
        email: '',
        contrasena: '',
    });
    const [error, setError] = useState(null);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            // El backend registra Y devuelve un token
            const data = await authApi.register(formData); 
            login(data.token); // Logueamos al usuario inmediatamente
            navigate('/perfil'); // Lo enviamos a su perfil
        } catch (err) {
            setError(err.data.message || 'Error al registrarse');
        }
    };

    return (
        <div>
            <h2>Registrarse</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Nombre Completo: </label>
                    <input type="text" name="nombreCompleto" value={formData.nombreCompleto} onChange={handleChange} required />
                </div>
                <div>
                    <label>Email: </label>
                    <input type="email" name="email" value={formData.email} onChange={handleChange} required />
                </div>
                <div>
                    <label>Contraseña: </label>
                    <input type="password" name="contrasena" value={formData.contrasena} onChange={handleChange} required minLength={8} />
                </div>
                <button type="submit">Crear Cuenta</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
}

export default RegisterPage;