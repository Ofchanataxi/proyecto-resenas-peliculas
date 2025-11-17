import React, { useState, useEffect } from 'react';

/**
 * @param {object | null} initialData - Los datos del cine si estamos editando.
 * @param {function} onSave - Función que se llama al guardar.
 * @param {function} onCancel - Función que se llama para cerrar el formulario.
 */
function CineForm({ initialData, onSave, onCancel }) {
    
    const [formData, setFormData] = useState({
        nombre: '',
        direccion: '',
        ciudad: '',
    });
    
    const isEdit = initialData != null;

    useEffect(() => {
        if (isEdit) {
            setFormData({
                nombre: initialData.nombre,
                direccion: initialData.direccion,
                ciudad: initialData.ciudad,
            });
        } else {
            // Reseteamos si es para 'crear'
            setFormData({ nombre: '', direccion: '', ciudad: '' });
        }
    }, [initialData, isEdit]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(formData); // Llamamos a la función del padre con los datos
    };

    return (
        <form onSubmit={handleSubmit} style={{ border: '1px solid #ccc', padding: '16px', borderRadius: '8px' }}>
            <h3>{isEdit ? 'Editar Cine' : 'Crear Nuevo Cine'}</h3>
            <div>
                <label>Nombre del Cine: </label>
                <input
                    type="text"
                    name="nombre"
                    value={formData.nombre}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label>Dirección: </label>
                <input
                    type="text"
                    name="direccion"
                    value={formData.direccion}
                    onChange={handleChange}
                    required
                />
            </div>
            <div>
                <label>Ciudad: </label>
                <input
                    type="text"
                    name="ciudad"
                    value={formData.ciudad}
                    onChange={handleChange}
                    required
                />
            </div>
            
            <div style={{ marginTop: '16px' }}>
                <button type="submit">Guardar</button>
                <button type="button" onClick={onCancel} style={{ marginLeft: '8px' }}>Cancelar</button>
            </div>
        </form>
    );
}

export default CineForm;