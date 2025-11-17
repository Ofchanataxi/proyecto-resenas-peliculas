// src/api/peliculaApi.js (CORREGIDO)

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const API_URL = `${API_BASE_URL}/api/resenas/peliculas`;
// --- Helpers (copiados de tu usuarioApi.js) ---
const getAuthHeader = () => {
    const token = localStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
};

const getHeaders = (customHeaders = {}) => ({
    ...getAuthHeader(),
    ...customHeaders,
});

// ... (Tu handleResponse está bien, lo dejamos como está)
const handleResponse = async (response) => {
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Error ${response.status}`);
    }

    const contentType = response.headers.get('content-type');

    if (response.status === 204) {
        return { success: true };
    }

    if (contentType && contentType.indexOf('application/json') !== -1) {
        return response.json();
    }
    
    // CORRECCIÓN: Tu `handleResponse` anterior tenía un bug aquí.
    // Lo mejor es devolver un objeto vacío si no es JSON pero la respuesta es OK.
    return {}; 
};


// GET: Obtener todas las películas
// CORRECCIÓN: Esta ruta es segura y necesita el token.
export const getPeliculasRequest = async () => {
    // ¡FALTABA ESTO! Añadimos los headers con el token.
    const response = await fetch(API_URL, {
        headers: getHeaders()
    });
    return handleResponse(response);
};

// GET: Obtener una película por ID
// CORRECCIÓN: Esta ruta también es segura.
export const getPeliculaRequest = async (id) => {
    const response = await fetch(`${API_URL}/${id}`, {
        headers: getHeaders()
    });
    return handleResponse(response);
};

// POST: Crear una nueva película (Protegido)
export const createPeliculaRequest = async (pelicula) => {
    
    // CORRECCIÓN: Los nombres de las claves deben ser camelCase
    // para coincidir con tu DTO de Java (PeliculaRequest.java)
    const peliculaData = {
        titulo: pelicula.titulo,
        director: pelicula.director,
        genero: pelicula.genero,
        duracionMinutos: parseInt(pelicula.duracionMinutos, 10), // ANTES: duracion_minutos
        fechaEstreno: pelicula.fechaEstreno,                     // ANTES: fecha_estreno
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
    
    // CORRECCIÓN: Igual que en create, usamos camelCase
    const peliculaData = {
        titulo: pelicula.titulo,
        director: pelicula.director,
        genero: pelicula.genero,
        duracionMinutos: parseInt(pelicula.duracionMinutos, 10), // ANTES: duracion_minutos
        fechaEstreno: pelicula.fechaEstreno,                     // ANTES: fecha_estreno
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