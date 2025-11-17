// API para Cines
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const API_URL = `${API_BASE_URL}/api/resenas/cines`;

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
        // Lanza un error con el mensaje del backend
        const error = new Error(errorData.message || `Error ${response.status}`);
        error.data = errorData;
        error.status = response.status;
        throw error;
    }
    
    // Manejar respuesta 204 No Content (para DELETE)
    if (response.status === 204) {
        return { success: true };
    }

    const contentType = response.headers.get('content-type');
    if (contentType && contentType.indexOf('application/json') !== -1) {
        return response.json();
    }
    // Si la respuesta es OK pero no es JSON (raro, pero posible)
    return {};
};
// --- ---

// GET: Obtener todos los cines (Público Y Admin)
export const getCinesRequest = async () => {
    // CORRECCIÓN: Faltaba enviar los headers
    const response = await fetch(API_URL, {
        headers: getHeaders()
    });
    return handleResponse(response);
};

// GET: Obtener un cine por ID (Público Y Admin)
export const getCineRequest = async (id) => {
    // CORRECCIÓN: Faltaba enviar los headers
    const response = await fetch(`${API_URL}/${id}`, {
        headers: getHeaders()
    });
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
    // Usamos el handleResponse actualizado que maneja 204
    return handleResponse(response);
};