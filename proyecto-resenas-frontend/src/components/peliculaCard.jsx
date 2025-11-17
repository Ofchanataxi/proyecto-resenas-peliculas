import React from 'react';
import { Link } from 'react-router-dom'; // Para navegar al detalle
import './PeliculaCard.css'; // <-- Crearemos este CSS para la estética

function PeliculaCard({ pelicula }) {
    // Formatear la fecha para que sea más legible
    const fechaEstreno = new Date(pelicula.fecha_estreno).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
    });

    return (
        <div className="pelicula-card">
            {/* Puedes agregar un <img> aquí si tuvieras un campo de imagen */}
            <div className="pelicula-card-body">
                <h3>{pelicula.titulo}</h3>
                <p><strong>Director:</strong> {pelicula.director}</p>
                <p><strong>Género:</strong> {pelicula.genero}</p>
                <p><strong>Duración:</strong> {pelicula.duracion_minutos} min.</p>
                <p><strong>Estreno:</strong> {fechaEstreno}</p>

                {/* Este enlace usa la ruta dinámica que definimos en App.jsx */}
                <Link to={`/pelicula/${pelicula.id}`} className="card-link">
                    Ver Reseñas
                </Link>
            </div>
        </div>
    );
}

export default PeliculaCard;