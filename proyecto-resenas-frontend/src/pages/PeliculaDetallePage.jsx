import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
// Asegúrate de que los nombres coincidan con tus archivos de API
import { getPeliculaRequest } from '../api/peliculaApi'; 
import { 
    getResenasByPelicula, // Método que consume el Flux
    createResenaRequest, 
    deleteResenaRequest,
    triggerBackpressureDemo // Importamos el trigger del laboratorio
} from '../api/resenaApi';
import './PeliculaDetallePage.css';

function PeliculaDetallePage() {
    const { id } = useParams();
    const { isAuthenticated, user } = useAuth();

    const [pelicula, setPelicula] = useState(null);
    const [resenas, setResenas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    // Estado para el feedback del demo de Backpressure
    const [demoStatus, setDemoStatus] = useState('');
    const [demoLogs, setDemoLogs] = useState([]);

    // --- Estados para el formulario de nueva reseña ---
    const [comentario, setComentario] = useState('');
    const [calificacion, setCalificacion] = useState(0);
    const [formError, setFormError] = useState(null);

    // Carga los datos de la película y las reseñas (Flux)
    const cargarDatos = async () => {
        try {
            // Ajuste: usar getPeliculaRequest si así se llama en tu API
            const peliData = await getPeliculaRequest(id);
            setPelicula(peliData);

            // Consumimos el Flux (que llega como array JSON desde Spring WebFlux)
            const resenasData = await getResenasByPelicula(id);
            setResenas(resenasData);
        } catch (error) {
            console.error("Error cargando datos", error);
            setError("Error al cargar la película o las reseñas.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarDatos();
    }, [id]);

    // --- MANEJADOR: Eliminar Reseña ---
    const handleResenaDelete = async (resenaId) => {
        if (window.confirm("¿Estás seguro de que quieres eliminar esta reseña?")) {
            try {
                await deleteResenaRequest(resenaId);
                setResenas(resenas.filter(r => r.id !== resenaId));
                alert("Reseña eliminada.");
            } catch (err) {
                alert(`Error al eliminar: ${err.message}`);
            }
        }
    };

    // --- MANEJADOR: Crear Reseña ---
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
                usuarioId: user.id,
                peliculaId: parseInt(id, 10),
            };

            const res = await createResenaRequest(nuevaResena);

            // Simulamos el nombre del usuario para mostrarlo inmediatamente
            const resenaConNombre = {
                ...res,
                usuarioNombre: user.nombreCompleto // O user.username según tu AuthContext
            };
            setResenas([resenaConNombre, ...resenas]);

            setComentario('');
            setCalificacion(0);
        } catch (err) {
            setFormError(`Error al enviar la reseña: ${err.message}`);
        }
    };

    // --- MANEJADOR: Demo Backpressure (Laboratorio) ---
    const handleTestBackpressure = async () => {
        setDemoStatus('Solicitando proceso batch al servidor...');
        setDemoLogs([]); // Limpiar logs previos
        try {
            // Ahora la respuesta es un array de strings (los logs)
            const logsRecibidos = await triggerBackpressureDemo();
            
            setDemoLogs(logsRecibidos); // Guardamos los logs
            setDemoStatus('✅ Proceso finalizado. Resultados:');
        } catch (error) {
            setDemoStatus('❌ Error al invocar demo');
            console.error(error);
        }
    };

    if (loading) return <h2 style={{ textAlign: 'center', marginTop: '2rem' }}>Cargando...</h2>;
    if (error) return <h2 style={{ color: 'red', textAlign: 'center', marginTop: '2rem' }}>{error}</h2>;
    if (!pelicula) return <h2 style={{ textAlign: 'center', marginTop: '2rem' }}>Película no encontrada.</h2>;

    const fechaEstreno = new Date(pelicula.fecha_estreno).toLocaleDateString('es-ES', {
        year: 'numeric', month: 'long', day: 'numeric',
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
                    <h2>Reseñas <span style={{fontSize:'0.6em', color:'#888'}}>(Reactive Flux)</span></h2>

                    {/* Formulario */}
                    {isAuthenticated ? (
                        <form onSubmit={handleResenaSubmit} className="resena-form">
                            <h3>Deja tu reseña</h3>
                            <div className="star-rating">
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <span
                                        key={star}
                                        className={star <= calificacion ? 'star active' : 'star'}
                                        onClick={() => setCalificacion(star)}
                                        style={{cursor: 'pointer', fontSize: '1.5rem', color: star <= calificacion ? '#FFD700' : '#ccc'}}
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
                            <button type="submit" style={{marginTop:'10px'}}>Publicar Reseña</button>
                            {formError && <p className="error-msg" style={{color:'red'}}>{formError}</p>}
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
                        {resenas.length > 0 ? (
                            resenas.map((resena) => {
                                const calificacionSegura = Math.max(0, Math.min(5, resena.calificacion || 0));
                                return (
                                    <div key={resena.id} className="resena-card">
                                        <div className="resena-card-header">
                                            <strong>{resena.usuarioNombre || `Usuario ${resena.usuarioId}`}</strong>
                                            <span className="resena-calificacion" style={{color: '#FFD700'}}>
                                                {'★'.repeat(calificacionSegura)}
                                                <span style={{color: '#ccc'}}>{'★'.repeat(5 - calificacionSegura)}</span>
                                            </span>
                                        </div>
                                        <p>{resena.comentario}</p>
                                        
                                        {isAuthenticated && user?.id === resena.usuarioId && (
                                            <div className="resena-actions" style={{ marginTop: '10px' }}>
                                                <button
                                                    onClick={() => handleResenaDelete(resena.id)}
                                                    style={{ backgroundColor: '#dc3545', color: 'white', border: 'none', padding: '5px 10px', borderRadius: '4px', cursor: 'pointer' }}
                                                >
                                                    Eliminar
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                );
                            })
                        ) : (
                            <p>Todavía no hay reseñas. ¡Sé el primero!</p>
                        )}
                    </div>
                </div>

                <hr style={{margin: '40px 0'}} />

                {/* --- ZONA DE LABORATORIO (Backpressure) --- */}
                <div className="lab-zone" style={{ backgroundColor: '#2d2d2d', color: '#f8f8f2', padding: '20px', borderRadius: '8px', marginTop: '30px', fontFamily: 'monospace' }}>
                    <h4 style={{marginTop: 0, color: '#ff79c6'}}>🧪 Laboratorio: Visualización de Backpressure</h4>
                    <p style={{marginBottom: '15px'}}>
                        Al ejecutar, verás aquí los logs generados por el <code>CustomSubscriber</code> en el servidor.
                    </p>
                    
                    <button 
                        onClick={handleTestBackpressure}
                        style={{ backgroundColor: '#6272a4', color: 'white', padding: '10px 20px', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold'}}
                    >
                        ▶ Ejecutar Demo
                    </button>

                    {demoStatus && (
                        <div style={{ marginTop: '15px', color: demoStatus.includes('Error') ? '#ff5555' : '#50fa7b' }}>
                            {demoStatus}
                        </div>
                    )}
                    
                    {/* VISUALIZADOR DE LOGS TIPO TERMINAL */}
                    {demoLogs.length > 0 && (
                        <div style={{ 
                            marginTop: '15px', 
                            backgroundColor: '#000', 
                            padding: '10px', 
                            borderRadius: '5px', 
                            maxHeight: '300px', 
                            overflowY: 'auto',
                            border: '1px solid #444'
                        }}>
                            {demoLogs.map((linea, index) => (
                                <div key={index} style={{ borderBottom: '1px solid #333', padding: '2px 0' }}>
                                    <span style={{color: '#8be9fd'}}>$ </span>{linea}
                                </div>
                            ))}
                        </div>
                    )}
                </div>

            </div>
        </div>
    );
}

export default PeliculaDetallePage;