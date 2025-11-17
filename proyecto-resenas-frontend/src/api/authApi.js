// Lee la URL base de las variables de entorno de Vite
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// URL base para la autenticación
const AUTH_URL = `${API_BASE_URL}/api/resenas/auth`; // <-- CAMBIO AQUÍ


const handleResponse = async (response) => {
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        // Pasa el errorData completo para que el login lo pueda leer
        throw { status: response.status, data: errorData };
    }
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.indexOf('application/json') !== -1) {
        return response.json();
    }
    return {};
};

export const login = async (email, contrasena) => {
    const response = await fetch(`${AUTH_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, contrasena }),
    });
    return handleResponse(response);
};

export const register = async (userData) => {
    const response = await fetch(`${AUTH_URL}/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(userData),
    });
    return handleResponse(response);
};

export const reactivateAccount = async (email) => {
    const response = await fetch(`${AUTH_URL}/reactivate`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
    });
    return handleResponse(response);
};

// Llama al nuevo endpoint /me
export const getMiPerfil = async (token) => {
    const response = await fetch(`${AUTH_URL}/me`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return handleResponse(response); // Reusa el handleResponse
};