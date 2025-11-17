// API para Reseñas
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const API_URL = `${API_BASE_URL}/reseñas`;

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

// GET: Obtener reseñas por ID de película (Público)
// Asumimos que tu backend soporta esto (ej: /reseñas/pelicula/1)
// Si no, tendremos que filtrar en el frontend.
export const getResenasPorPeliculaRequest = async (peliculaId) => {
    const response = await fetch(`${API_URL}/pelicula/${peliculaId}`);
    return handleResponse(response);
};

// POST: Crear una nueva reseña (Protegido)
export const createResenaRequest = async (resenaData) => {
    // resenaData debe ser { comentario, calificacion, usuarioId, peliculaId }
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(resenaData),
    });
    return handleResponse(response);
};

// PUT: Actualizar una reseña (Protegido)
export const updateResenaRequest = async (id, resenaData) => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(resenaData),
    });
    return handleResponse(response);
};

// DELETE: Borrar una reseña (Protegido)
export const deleteResenaRequest = async (id) => {
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