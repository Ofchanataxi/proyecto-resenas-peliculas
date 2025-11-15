// src/api/usuarioApi.js

// Asegúrate que la URL base sea correcta (la de tu backend Spring)
const API_URL = 'http://localhost:8080/api/resenas/usuarios';

/**
 * Helper para manejar las respuestas del fetch.
 * Verifica si la respuesta es OK y maneja errores.
 */
const handleResponse = async (response) => {
    if (!response.ok) {
        // Intenta leer el JSON de error del backend
        const errorData = await response.json().catch(() => ({})); // Si no hay JSON, objeto vacío
        throw new Error(errorData.message || `Error ${response.status}: ${response.statusText}`);
    }
    
    // Maneja respuestas que no tienen contenido (ej. 204 No Content)
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.indexOf('application/json') !== -1) {
        return response.json(); // Devuelve el JSON
    }
    return {}; // Devuelve objeto vacío si no hay JSON
};

// --- Funciones del CRUD ---

export const getUsuarios = async () => {
    const response = await fetch(API_URL);
    return handleResponse(response);
};

export const createUsuario = async (usuarioData) => {
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuarioData),
    });
    return handleResponse(response);
};

export const updateUsuario = async (id, usuarioData) => {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(usuarioData),
    });
    return handleResponse(response);
};

export const activateUsuario = async (id) => {
    const response = await fetch(`${API_URL}/${id}/activate`, {
        method: 'PATCH',
    });
    return handleResponse(response);
};

export const deactivateUsuario = async (id) => {
    const response = await fetch(`${API_URL}/${id}/deactivate`, {
        method: 'PATCH',
    });
    return handleResponse(response);
};

export const getStats = async () => {
    const response = await fetch(`${API_URL}/stats`);
    return handleResponse(response);
};