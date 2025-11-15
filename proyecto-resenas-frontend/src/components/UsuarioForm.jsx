// src/components/UsuarioForm.js

import React, { useState, useEffect } from 'react';

/**
 * @param {object | null} initialData - Los datos del usuario si estamos editando, o null si estamos creando.
 * @param {function} onSave - Función que se llama al guardar (envía los datos del formulario).
 * @param {function} onCancel - Función que se llama para cerrar el formulario.
 */
function UsuarioForm({ initialData, onSave, onCancel }) {
    
    const [formData, setFormData] = useState({
        nombreCompleto: '',
        email: '',
        contrasena: '',
        activo: true,
    });
    
    // Determina si estamos en modo "edición"
    const isEdit = initialData != null;

    // Cuando 'initialData' cambia, actualizamos el formulario
    useEffect(() => {
        if (isEdit) {
            setFormData({
                nombreCompleto: initialData.nombreCompleto,
                email: initialData.email,
                contrasena: '', // Por seguridad, nunca mostramos la contraseña
                activo: initialData.activo,
            });
        } else {
            // Reseteamos si es para 'crear'
            setFormData({ nombreCompleto: '', email: '', contrasena: '', activo: true });
        }
    }, [initialData, isEdit]);

    // Manejador de cambios en los inputs
    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    // Manejador del envío del formulario
    const handleSubmit = (e) => {
        e.preventDefault();
        
        // Preparamos los datos a enviar
        const dataToSend = {
            nombreCompleto: formData.nombreCompleto,
            email: formData.email,
            activo: formData.activo, // El backend espera 'activo' en el PUT
        };

        if (isEdit) {
            // Si es edición y el usuario escribió una nueva contraseña, la incluimos
            if (formData.contrasena) {
                dataToSend.contrasena = formData.contrasena;
            }
        } else {
            // Si es 'crear', la contraseña es obligatoria
            dataToSend.contrasena = formData.contrasena;
        }

        onSave(dataToSend); // Llamamos a la función del padre
    };

    return (
        <form onSubmit={handleSubmit} style={{ border: '1px solid #ccc', padding: '16px', borderRadius: '8px' }}>
            <h3>{isEdit ? 'Editar Usuario' : 'Crear Nuevo Usuario'}</h3>
            <div>
                <label>Nombre Completo: </label>
                <input
                    type="text"
                    name="nombreCompleto"
                    value={formData.nombreCompleto}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label>Email: </label>
                <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label>Contraseña: </label>
                <input
                    type="password"
                    name="contrasena"
                    value={formData.contrasena}
                    onChange={handleChange}
                    placeholder={isEdit ? 'Dejar en blanco para no cambiar' : ''}
                    required={!isEdit} // Solo requerida al crear
                />
            </div>
            
            {/* Solo mostramos el toggle de 'activo' en modo edición */}
            {isEdit && (
                <div>
                    <label>Activo: </label>
                    <input
                        type="checkbox"
                        name="activo"
                        checked={formData.activo}
                        onChange={handleChange}
                    />
                </div>
            )}
            
            <div style={{ marginTop: '16px' }}>
                <button type="submit">Guardar</button>
                <button type="button" onClick={onCancel} style={{ marginLeft: '8px' }}>Cancelar</button>
            </div>
        </form>
    );
}

export default UsuarioForm;