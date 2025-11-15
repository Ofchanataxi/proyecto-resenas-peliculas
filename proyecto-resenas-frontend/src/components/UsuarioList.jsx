// src/components/UsuarioList.js

import React from 'react';

/**
 * @param {Array} usuarios - La lista de usuarios a mostrar.
 * @param {function} onEdit - Función que se llama al presionar 'Editar'.
 * @param {function} onActivate - Función que se llama al presionar 'Activar'.
 * @param {function} onDeactivate - Función que se llama al presionar 'Desactivar'.
 */
function UsuarioList({ usuarios, onEdit, onActivate, onDeactivate }) {

    if (!usuarios || usuarios.length === 0) {
        return <p>No hay usuarios registrados.</p>;
    }

    return (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
                <tr style={{ borderBottom: '2px solid black' }}>
                    <th style={{ textAlign: 'left' }}>Nombre</th>
                    <th style={{ textAlign: 'left' }}>Email</th>
                    <th style={{ textAlign: 'left' }}>Estado</th>
                    <th style={{ textAlign: 'left' }}>Acciones</th>
                </tr>
            </thead>
            <tbody>
                {usuarios.map((usuario) => (
                    <tr key={usuario.id} style={{ borderBottom: '1px solid #ccc' }}>
                        <td>{usuario.nombreCompleto}</td>
                        <td>{usuario.email}</td>
                        <td>{usuario.activo ? 'Activo' : 'Inactivo'}</td>
                        <td>
                            <button onClick={() => onEdit(usuario)}>Editar</button>
                            {usuario.activo ? (
                                <button onClick={() => onDeactivate(usuario.id)} style={{ marginLeft: '8px' }}>Desactivar</button>
                            ) : (
                                <button onClick={() => onActivate(usuario.id)} style={{ marginLeft: '8px' }}>Activar</button>
                            )}
                        </td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}

export default UsuarioList;