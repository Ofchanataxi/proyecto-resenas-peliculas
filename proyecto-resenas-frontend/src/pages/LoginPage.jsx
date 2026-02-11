import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import * as authApi from '../api/authApi';

function LoginPage() {
    const [email, setEmail] = useState('');
    const [contrasena, setContrasena] = useState('');
    const [error, setError] = useState(null);

    // Estado para manejar la reactivación
    const [showReactivate, setShowReactivate] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/"; // A dónde ir después del login

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        setShowReactivate(false);

        try {
            const data = await authApi.login(email, contrasena);
            // ✅ GUARDAR TOKEN + ID
            login({
                token: data.token,
                usuarioId: data.id
            });

            navigate(from, { replace: true }); // Envía al usuario a donde quería ir
        } catch (err) {
            if (err.status === 403 && err.data.error === "ACCOUNT_DEACTIVATED") {
                // ¡El usuario está desactivado!
                setError(err.data.message);
                setShowReactivate(true);
            } else {
                setError(err.data.message || "Email o contraseña incorrectos");
            }
        }
    };

    const handleReactivate = async () => {
        try {
            const data = await authApi.reactivateAccount(email);
            alert(data.message); // "Cuenta reactivada..."
            setError(null);
            setShowReactivate(false);
            // Ahora pueden intentar loguearse de nuevo
        } catch (err) {
            setError(err.data.message || "No se pudo reactivar la cuenta");
        }
    };

    return (
        <div>
            <h2>Iniciar Sesión</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Email: </label>
                    <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div>
                    <label>Contraseña: </label>
                    <input type="password" value={contrasena} onChange={(e) => setContrasena(e.target.value)} required />
                </div>
                <button type="submit">Login</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}

            {/* Lógica de Reactivación */}
            {showReactivate && (
                <div style={{ marginTop: '1rem', border: '1px solid orange', padding: '1rem' }}>
                    <p>Tu cuenta está desactivada.</p>
                    <button onClick={handleReactivate}>Reactivar mi cuenta</button>
                </div>
            )}
        </div>
    );
}

export default LoginPage;