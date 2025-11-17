// Lee la URL base de las variables de entorno de Vite
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// URL base para los usuarios
const API_URL = `${API_BASE_URL}/api/resenas/usuarios`; // <-- CAMBIO AQUÍ

// Helper para obtener el token de localStorage
const getAuthHeader = () => {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
};

// Helper para combinar cabeceras
const getHeaders = (customHeaders = {}) => ({
    ...getAuthHeader(),
    ...customHeaders,
});

const handleResponse = async (response) => {
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}`);
    }
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.indexOf('application/json') !== -1) {
        return response.json();
    }
    return {};
};

// --- Funciones del CRUD (Ahora protegidas) ---

export const getUsuarios = async () => {
    const response = await fetch(API_URL, { headers: getHeaders() });
    return handleResponse(response);
};

export const createUsuario = async (usuarioData) => {
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(usuarioData),
    });
    return handleResponse(response);
};

export const updateUsuario = async (id, usuarioData) => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(usuarioData),
    });
    return handleResponse(response);
};

export const activateUsuario = async (id) => {
    const response = await fetch(`${API_URL}/${id}/activate`, {
        method: 'PATCH',
        headers: getHeaders(),
    });
    return handleResponse(response);
};

export const deactivateUsuario = async (id) => {
    const response = await fetch(`${API_URL}/${id}/deactivate`, {
        method: 'PATCH',
        headers: getHeaders(),
    });
    return handleResponse(response);
};

export const getStats = async () => {
    const response = await fetch(`${API_URL}/stats`, { headers: getHeaders() });
    return handleResponse(response);
};

// --- NUEVO MÉTODO DELETE ---
export const deleteUsuario = async (id) => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: getHeaders(),
    });
    return handleResponse(response); // Esperará un 204 No Content
};