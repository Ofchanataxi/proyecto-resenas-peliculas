import React, { useState, useEffect } from 'react';

function PeliculaForm({ initialData, onSave, onCancel }) {
    
    const [formData, setFormData] = useState({
        titulo: '',
        director: '',
        genero: '',
        duracionMinutos: 0,
        fechaEstreno: '',
    });
    
    const isEdit = initialData != null;

    useEffect(() => {
        if (isEdit) {
            setFormData({
                titulo: initialData.titulo,
                director: initialData.director,
                genero: initialData.genero,
                duracionMinutos: initialData.duracionMinutos,
                // Formatea la fecha para el input type="date" (YYYY-MM-DD)
                fechaEstreno: initialData.fechaEstreno.split('T')[0], 
            });
        } else {
            setFormData({
                titulo: '',
                director: '',
                genero: '',
                duracionMinutos: 0,
                fechaEstreno: '',
            });
        }
    }, [initialData, isEdit]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === 'duracionMinutos' ? parseInt(value, 10) : value,
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(formData); // Envía los datos completos
    };

    return (
        <form onSubmit={handleSubmit} style={{ border: '1px solid #ccc', padding: '16px', borderRadius: '8px' }}>
            <h3>{isEdit ? 'Editar Película' : 'Crear Nueva Película'}</h3>
            <div>
                <label>Título: </label>
                <input type="text" name="titulo" value={formData.titulo} onChange={handleChange} required />
            </div>
            <div>
                <label>Director: </label>
                <input type="text" name="director" value={formData.director} onChange={handleChange} required />
            </div>
            <div>
                <label>Género: </label>
                <input type="text" name="genero" value={formData.genero} onChange={handleChange} required />
            </div>
            <div>
                <label>Duración (minutos): </label>
                <input type="number" name="duracionMinutos" value={formData.duracionMinutos} onChange={handleChange} required />
            </div>
            <div>
                <label>Fecha Estreno: </label>
                <input type="date" name="fechaEstreno" value={formData.fechaEstreno} onChange={handleChange} required />
            </div>
            
            <div style={{ marginTop: '16px' }}>
                <button type="submit">Guardar</button>
                <button type="button" onClick={onCancel} style={{ marginLeft: '8px' }}>Cancelar</button>
            </div>
        </form>
    );
}

export default PeliculaForm;