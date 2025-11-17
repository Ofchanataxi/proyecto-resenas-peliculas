import React from 'react';
import './CineCard.css'; // <-- Nuevo CSS

function CineCard({ cine }) {
    // Generar un enlace de Google Maps (¡un detalle funcional!)
    const googleMapsUrl = `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(cine.direccion + ', ' + cine.ciudad)}`;

    return (
        <div className="cine-card">
            <div className="cine-card-body">
                <h3>{cine.nombre}</h3>
                <p>
                    <strong>Dirección:</strong> {cine.direccion}
                </p>
                <p>
                    <strong>Ciudad:</strong> {cine.ciudad}
                </p>

                <a
                    href={googleMapsUrl}
                    target="_blank" // Abre en una nueva pestaña
                    rel="noopener noreferrer" // Buenas prácticas de seguridad
                    className="card-link-cine"
                >
                    Ver en Mapa
                </a>
            </div>
        </div>
    );
}

export default CineCard;