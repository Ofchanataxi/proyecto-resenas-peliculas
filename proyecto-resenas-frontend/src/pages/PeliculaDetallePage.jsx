import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getPeliculaRequest } from '../api/peliculaApi';
import { getResenasPorPeliculaRequest, createResenaRequest, deleteResenaRequest } from '../api/resenaApi';
import './PeliculaDetallePage.css';

function PeliculaDetallePage() {
    const { id } = useParams(); // Obtiene el 'id' de la URL
    const { isAuthenticated, user } = useAuth(); // Para saber si puede comentar

    const [pelicula, setPelicula] = useState(null);
    const [reseñas, setReseñas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // --- Estados para el formulario de nueva reseña ---
    const [comentario, setComentario] = useState('');
    const [calificacion, setCalificacion] = useState(0);
    const [formError, setFormError] = useState(null);

    const handleResenaDelete = async (resenaId) => {
        if (window.confirm("¿Estás seguro de que quieres eliminar esta reseña?")) {
            try {
                await deleteResenaRequest(resenaId);
                // Actualiza el estado local para quitar la reseña eliminada
                setReseñas(reseñas.filter(r => r.id !== resenaId));
                alert("Reseña eliminada.");
            } catch (err) {
                alert(`Error al eliminar: ${err.message}`);
            }
        }
    };

    // Carga los datos de la película y las reseñas
    const cargarDatos = async () => {
        try {
            setLoading(true);
            // Peticiones en paralelo
            const [dataPelicula, dataReseñas] = await Promise.all([
                getPeliculaRequest(id),
                getResenasPorPeliculaRequest(id)
            ]);
            setPelicula(dataPelicula);
            setReseñas(dataReseñas);
            setError(null);
        } catch (err) {
            console.error("Error al cargar datos:", err);
            setError("No se pudo cargar la información de la película.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarDatos();
    }, [id]); // Se vuelve a ejecutar si el ID de la película cambia

    // Manejador para enviar el formulario de reseña
    const handleResenaSubmit = async (e) => {
        e.preventDefault();
        if (calificacion === 0) {
            setFormError("Por favor, selecciona una calificación (1-5 estrellas).");
            return;
        }
        setFormError(null);

        try {
            const nuevaResena = {
                comentario,
                calificacion,
                usuarioId: user.id, // Obtenido del AuthContext
                peliculaId: parseInt(id, 10), // Obtenido de useParams
            };

            const res = await createResenaRequest(nuevaResena);

            // Actualiza la lista de reseñas al instante
            setReseñas([res, ...reseñas]);

            // Limpia el formulario
            setComentario('');
            setCalificacion(0);

        } catch (err) {
            setFormError(`Error al enviar la reseña: ${err.message}`);
        }
    };


    if (loading) return <h2 style={{ textAlign: 'center' }}>Cargando...</h2>;
    if (error) return <h2 style={{ color: 'red', textAlign: 'center' }}>{error}</h2>;
    if (!pelicula) return <h2>Película no encontrada.</h2>;

    const fechaEstreno = new Date(pelicula.fecha_estreno).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    });

    return (
        <div className="detalle-container">
            {/* Sección de Detalles de la Película */}
            <div className="detalle-header">
                <h1>{pelicula.titulo}</h1>
                <p className="detalle-info">
                    Dirigida por <strong>{pelicula.director}</strong>
                </p>
                <p className="detalle-info">
                    {pelicula.genero} | {pelicula.duracion_minutos} minutos | Estreno: {fechaEstreno}
                </p>
            </div>

            <div className="detalle-body">
                {/* Sección de Reseñas */}
                <div className="reseñas-section">
                    <h2>Reseñas</h2>

                    {/* Formulario para crear reseña */}
                    {isAuthenticated ? (
                        <form onSubmit={handleResenaSubmit} className="resena-form">
                            <h3>Deja tu reseña</h3>
                            <div className="star-rating">
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <span
                                        key={star}
                                        className={star <= calificacion ? 'star active' : 'star'}
                                        onClick={() => setCalificacion(star)}
                                    >
                    ★
                  </span>
                                ))}
                            </div>
                            <textarea
                                rows="4"
                                placeholder="Escribe tu comentario..."
                                value={comentario}
                                onChange={(e) => setComentario(e.target.value)}
                                required
                            ></textarea>
                            <button type="submit">Publicar Reseña</button>
                            {formError && <p className="error-msg">{formError}</p>}
                        </form>
                    ) : (
                        <div className="resena-login-prompt">
                            <p>
                                <Link to={`/login?redirect=/pelicula/${id}`}>Inicia sesión</Link> para dejar tu reseña.
                            </p>
                        </div>
                    )}

                    {/* Lista de Reseñas */}
                    <div className="resena-list">
                        {reseñas.length > 0 ? (
                            reseñas.map((resena) => (
                                <div key={resena.id} className="resena-card">
                                    <div className="resena-card-header">
                                        <strong>{resena.usuarioNombre || 'Anónimo'}</strong> {/* (El backend debería enviar esto) */}
                                        <span className="resena-calificacion">
                      {'★'.repeat(resena.calificacion)}
                                            {'☆'.repeat(5 - resena.calificacion)}
                    </span>
                                    </div>
                                    <p>{resena.comentario}</p>
                                    {/* --- AÑADIR ESTA LÓGICA --- */}
                                    {/* Mostrar botones si el usuario es dueño de la reseña */}
                                    {isAuthenticated && user?.id === resena.usuarioId && (
                                        <div className="resena-actions" style={{marginTop: '10px'}}>
                                            {/* (Aquí podrías poner un botón de Editar) */}
                                            <button 
                                                onClick={() => handleResenaDelete(resena.id)}
                                                style={{backgroundColor: '#8B0000', color: 'white', fontSize: '0.8em'}}
                                            >
                                                Eliminar
                                            </button>
                                        </div>
                                    )}
                                </div>
                            ))
                        ) : (
                            <p>Todavía no hay reseñas para esta película. ¡Sé el primero!</p>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default PeliculaDetallePage;