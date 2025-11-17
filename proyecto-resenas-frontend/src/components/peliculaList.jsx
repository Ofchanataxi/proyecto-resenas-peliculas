import React from 'react';
import PeliculaCard from './PeliculaCard'; // <-- El componente que muestra cada película
import './PeliculaList.css'; // <-- Crearemos este CSS para la estética

function PeliculaList({ peliculas }) {
    if (peliculas.length === 0) {
        return <h2>No hay películas para mostrar.</h2>;
    }

    return (
        <div className="pelicula-list">
            {peliculas.map((pelicula) => (
                <PeliculaCard key={pelicula.id} pelicula={pelicula} /> // Asumo que el backend devuelve un 'id'
            ))}
        </div>
    );
}

export default PeliculaList;