// src/pages/UsuariosPage.js

import React, { useState, useEffect, useCallback } from 'react';
import UsuarioList from '../components/UsuarioList';
import UsuarioForm from '../components/UsuarioForm';
import * as api from '../api/usuarioApi'; // Importamos todas las funciones de la API

function UsuariosPage() {
    // Estados principales
    const [usuarios, setUsuarios] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Estados para el formulario
    const [showForm, setShowForm] = useState(false);
    const [editingUsuario, setEditingUsuario] = useState(null); // null = crear, objeto = editar

    // --- Funciones de Carga de Datos ---

    // Usamos useCallback para evitar re-crear la función en cada render
    const loadData = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            
            // Cargamos usuarios y estadísticas en paralelo
            const [usuariosData, statsData] = await Promise.all([
                api.getUsuarios(),
                api.getStats()
            ]);
            
            setUsuarios(usuariosData);
            setStats(statsData);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);

    // Carga inicial al montar el componente
    useEffect(() => {
        loadData();
    }, [loadData]); // El 'dependency array' asegura que se llame solo una vez

    // --- Manejadores de Acciones ---

    const handleActivate = async (id) => {
        try {
            await api.activateUsuario(id);
            loadData(); // Recargamos todo
        } catch (err) {
            alert(`Error al activar: ${err.message}`);
        }
    };
    
    const handleDeactivate = async (id) => {
        try {
            await api.deactivateUsuario(id);
            loadData(); // Recargamos todo
        } catch (err) {
            alert(`Error al desactivar: ${err.message}`);
        }
    };

    // --- Manejadores de Formulario ---

    const handleShowCreateForm = () => {
        setEditingUsuario(null); // 'null' significa 'crear'
        setShowForm(true);
    };

    const handleShowEditForm = (usuario) => {
        setEditingUsuario(usuario); // Pasamos el usuario a editar
        setShowForm(true);
    };

    const handleCancelForm = () => {
        setShowForm(false);
        setEditingUsuario(null);
    };

    const handleSaveUsuario = async (usuarioData) => {
        try {
            if (editingUsuario) {
                // Es edición
                await api.updateUsuario(editingUsuario.id, usuarioData);
                alert('Usuario actualizado correctamente');
            } else {
                // Es creación
                await api.createUsuario(usuarioData);
                alert('Usuario creado correctamente');
            }
            // Al guardar, cerramos el form y recargamos todo
            setShowForm(false);
            setEditingUsuario(null);
            loadData();
        } catch (error) {
            alert(`Error al guardar: ${error.message}`);
        }
    };

    // --- Renderizado ---

    return (
        <div style={{ padding: '20px' }}>
            <h2>Gestión de Usuarios</h2>

            {/* Mostramos estadísticas si existen */}
            {stats && (
                <div style={{ padding: '10px', backgroundColor: '#f4f4f4', marginBottom: '20px' }}>
                    <strong>Estadísticas:</strong> Total: {stats.total} | Activos: {stats.active} | Inactivos: {stats.inactive}
                </div>
            )}
            
            {loading && <p>Cargando...</p>}
            {error && <p style={{ color: 'red' }}>Error: {error}</p>}

            {/* Decidimos si mostrar el form o la lista */}
            {showForm ? (
                <UsuarioForm
                    initialData={editingUsuario}
                    onSave={handleSaveUsuario}
                    onCancel={handleCancelForm}
                />
            ) : (
                <>
                    <button onClick={handleShowCreateForm} style={{ marginBottom: '20px' }}>
                        Crear Nuevo Usuario
                    </button>
                    <UsuarioList
                        usuarios={usuarios}
                        onEdit={handleShowEditForm}
                        onActivate={handleActivate}
                        onDeactivate={handleDeactivate}
                    />
                </>
            )}
        </div>
    );
}

export default UsuariosPage;