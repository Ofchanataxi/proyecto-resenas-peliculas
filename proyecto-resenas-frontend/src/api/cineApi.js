// API para Cines
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const API_URL = `${API_BASE_URL}/api/cines`;

// --- Helpers (copiados de tu usuarioApi.js) ---
const getAuthHeader = () => {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
};
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
// --- ---

// GET: Obtener todos los cines (Público)
export const getCinesRequest = async () => {
    const response = await fetch(API_URL);
    return handleResponse(response);
};

// GET: Obtener un cine por ID (Público)
export const getCineRequest = async (id) => {
    const response = await fetch(`${API_URL}/${id}`);
    return handleResponse(response);
};

// POST: Crear un nuevo cine (Protegido)
export const createCineRequest = async (cineData) => {
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(cineData),
    });
    return handleResponse(response);
};

// PUT: Actualizar un cine (Protegido)
export const updateCineRequest = async (id, cineData) => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(cineData),
    });
    return handleResponse(response);
};

// DELETE: Borrar un cine (Protegido)
export const deleteCineRequest = async (id) => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: getHeaders(),
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}`);
    }
    return response.status === 204;
};