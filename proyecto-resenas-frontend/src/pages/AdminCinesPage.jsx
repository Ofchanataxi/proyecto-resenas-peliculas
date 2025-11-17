import React, { useState, useEffect, useCallback } from 'react';
import CineForm from '../components/CineForm'; // <-- El nuevo formulario
import * as api from '../api/cineApi'; // <-- La API corregida

function AdminCinesPage() {
    const [cines, setCines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Estados para el formulario
    const [showForm, setShowForm] = useState(false);
    const [editingCine, setEditingCine] = useState(null); // null = crear, objeto = editar

    const loadData = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            
            const data = await api.getCinesRequest(); // Carga pública de cines
            setCines(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadData();
    }, [loadData]);

    // --- Manejadores de Acciones ---

    const handleDelete = async (id) => {
        if (window.confirm("¿Estás seguro de eliminar este cine?")) {
            try {
                await api.deleteCineRequest(id);
                loadData(); // Recargamos la lista
            } catch (err) {
                alert(`Error al eliminar: ${err.message}`);
            }
        }
    };
    
    // --- Manejadores de Formulario ---

    const handleShowCreateForm = () => {
        setEditingCine(null);
        setShowForm(true);
    };

    const handleShowEditForm = (cine) => {
        setEditingCine(cine);
        setShowForm(true);
    };

    const handleCancelForm = () => {
        setShowForm(false);
        setEditingCine(null);
    };

    const handleSaveCine = async (cineData) => {
        try {
            if (editingCine) {
                // Es edición
                await api.updateCineRequest(editingCine.id, cineData);
                alert('Cine actualizado correctamente');
            } else {
                // Es creación
                await api.createCineRequest(cineData);
                alert('Cine creado correctamente');
            }
            setShowForm(false);
            setEditingCine(null);
            loadData();
        } catch (error) {
            alert(`Error al guardar: ${error.message}`);
        }
    };

    // --- Renderizado ---

    return (
        <div style={{ padding: '20px' }}>
            <h2>Gestión de Cines</h2>
            
            {loading && <p>Cargando...</p>}
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}

            {showForm ? (
                <CineForm
                    initialData={editingCine}
                    onSave={handleSaveCine}
                    onCancel={handleCancelForm}
                />
            ) : (
                <>
                    <button onClick={handleShowCreateForm} style={{ marginBottom: '20px' }}>
                        Crear Nuevo Cine
                    </button>
                    
                    {/* Tabla de Cines */}
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                            <tr style={{ borderBottom: '2px solid black' }}>
                                <th style={{ textAlign: 'left' }}>Nombre</th>
                                <th style={{ textAlign: 'left' }}>Dirección</th>
                                <th style={{ textAlign: 'left' }}>Ciudad</th>
                                <th style={{ textAlign: 'left' }}>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {cines.map((cine) => (
                                <tr key={cine.id} style={{ borderBottom: '1px solid #ccc' }}>
                                    <td>{cine.nombre}</td>
                                    <td>{cine.direccion}</td>
                                    <td>{cine.ciudad}</td>
                                    <td>
                                        <button onClick={() => handleShowEditForm(cine)}>Editar</button>
                                        <button 
                                            onClick={() => handleDelete(cine.id)} 
                                            style={{ marginLeft: '8px', backgroundColor: '#8B0000', color: 'white' }}
                                        >
                                            Eliminar
                                        </button>
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

export default AdminCinesPage;