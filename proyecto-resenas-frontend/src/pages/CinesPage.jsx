import React, { useEffect, useState } from 'react';
import { getCinesRequest } from '../api/cineApi'; // <-- Usamos la API de cines
import CineList from '../components/CineList'; // <-- Creamos este componente

function CinesPage() {
    const [cines, setCines] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        async function cargarCines() {
            try {
                setLoading(true);
                const data = await getCinesRequest();
                setCines(data);
                setError(null);
            } catch (err) {
                console.error("Error al cargar cines:", err);
                setError("No se pudieron cargar los cines.");
            } finally {
                setLoading(false);
            }
        }

        cargarCines();
    }, []);

    if (loading) return <h2 style={{ textAlign: 'center' }}>Cargando cines...</h2>;
    if (error) return <h2 style={{ color: 'red', textAlign: 'center' }}>{error}</h2>;

    return (
        <div>
            <h1>Nuestros Cines</h1>
            <CineList cines={cines} />
        </div>
    );
}

export default CinesPage;