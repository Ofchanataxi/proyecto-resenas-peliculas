import React, { useState, useEffect, useCallback } from 'react';
import PeliculaForm from '../components/PeliculaForm';
import * as api from '../api/peliculaApi'; // Importamos la API de películas

function AdminPeliculasPage() {
    const [peliculas, setPeliculas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [showForm, setShowForm] = useState(false);
    const [editingPelicula, setEditingPelicula] = useState(null); 

    const loadData = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await api.getPeliculasRequest();
            setPeliculas(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    const handleDelete = async (id) => {
        if (window.confirm("¿Estás seguro de eliminar esta película?")) {
            try {
                await api.deletePeliculaRequest(id);
                loadData();
            } catch (err) {
                alert(`Error al eliminar: ${err.message}`);
            }
        }
    };
    
    const handleShowCreateForm = () => {
        setEditingPelicula(null);
        setShowForm(true);
    };

    const handleShowEditForm = (pelicula) => {
        setEditingPelicula(pelicula);
        setShowForm(true);
    };

    const handleCancelForm = () => {
        setShowForm(false);
        setEditingPelicula(null);
    };

    const handleSavePelicula = async (peliculaData) => {
        try {
            if (editingPelicula) {
                await api.updatePeliculaRequest(editingPelicula.id, peliculaData);
                alert('Película actualizada');
            } else {
                await api.createPeliculaRequest(peliculaData);
                alert('Película creada');
            }
            setShowForm(false);
            setEditingPelicula(null);
            loadData();
        } catch (error) {
            alert(`Error al guardar: ${error.message}`);
        }
    };

    return (
        <div style={{ padding: '20px' }}>
            <h2>Gestión de Películas</h2>

            {loading && <p>Cargando...</p>}
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}

            {showForm ? (
                <PeliculaForm
                    initialData={editingPelicula}
                    onSave={handleSavePelicula}
                    onCancel={handleCancelForm}
                />
            ) : (
                <>
                    <button onClick={handleShowCreateForm} style={{ marginBottom: '20px' }}>
                        Crear Nueva Película
                    </button>
                    {/* Aquí mostramos una tabla simple de películas */}
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                            <tr>
                                <th>Título</th>
                                <th>Director</th>
                                <th>Género</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {peliculas.map((p) => (
                                <tr key={p.id}>
                                    <td>{p.titulo}</td>
                                    <td>{p.director}</td>
                                    <td>{p.genero}</td>
                                    <td>
                                        <button onClick={() => handleShowEditForm(p)}>Editar</button>
                                        <button onClick={() => handleDelete(p.id)} style={{ marginLeft: '8px' }}>Eliminar</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </>
            )}
        </div>
    );
}

export default AdminPeliculasPage;