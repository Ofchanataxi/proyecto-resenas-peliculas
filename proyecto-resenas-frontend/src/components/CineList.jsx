import React from 'react';
import CineCard from './CineCard';
import './CineList.css'; // <-- Nuevo CSS

function CineList({ cines }) {
    if (cines.length === 0) {
        return <h2>No hay cines para mostrar.</h2>;
    }

    return (
        <div className="cine-list">
            {cines.map((cine) => (
                <CineCard key={cine.id} cine={cine} />
            ))}
        </div>
    );
}

export default CineList;