// src/api/peliculaApi.js (CORREGIDO)

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const API_URL = `${API_BASE_URL}/peliculas`;

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

    // Caso 1: DELETE exitoso (No Content)
    if (response.status === 204) {
        return { success: true };
    }

    // Caso 2: Contenido JSON (el bueno)
    if (contentType && contentType.indexOf('application/json') !== -1) {
        return response.json();
    }

    // Caso 3: Respuesta inesperada (OK pero no JSON, etc.)
    // ¡Aquí estaba el bug! Ya no devolvemos {}
    throw new Error('Respuesta inesperada del servidor: no es formato JSON.');
};

// GET: Obtener todas las películas (Público, no necesita token)
export const getPeliculasRequest = async () => {
    const response = await fetch(API_URL);
    return handleResponse(response);
};

// GET: Obtener una película por ID (Público)
export const getPeliculaRequest = async (id) => {
    const response = await fetch(`${API_URL}/${id}`);
    return handleResponse(response);
};

// POST: Crear una nueva película (Protegido)
export const createPeliculaRequest = async (pelicula) => {
    const peliculaData = {
        titulo: pelicula.titulo,
        director: pelicula.director,
        genero: pelicula.genero,
        duracion_minutos: parseInt(pelicula.duracionMinutos, 10),
        fecha_estreno: pelicula.fechaEstreno,
    };

    const response = await fetch(API_URL, {
        method: 'POST',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(peliculaData),
    });
    return handleResponse(response);
};

// PUT: Actualizar una película (Protegido)
export const updatePeliculaRequest = async (id, pelicula) => {
    const peliculaData = {
        titulo: pelicula.titulo,
        director: pelicula.director,
        genero: pelicula.genero,
        duracion_minutos: parseInt(pelicula.duracionMinutos, 10),
        fecha_estreno: pelicula.fechaEstreno,
    };

    const response = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: getHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(peliculaData),
    });
    return handleResponse(response);
};

// DELETE: Borrar una película (Protegido)
export const deletePeliculaRequest = async (id) => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: getHeaders(),
    });
    return handleResponse(response);
};