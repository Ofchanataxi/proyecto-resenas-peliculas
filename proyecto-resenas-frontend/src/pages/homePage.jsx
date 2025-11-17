

import React, { useEffect, useState } from 'react';
import { getPeliculasRequest } from '../api/peliculaApi';
import PeliculaList from '../components/PeliculaList';

function HomePage() {
    const [peliculas, setPeliculas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null); // <-- El error se guardará aquí

    useEffect(() => {
        async function cargarPeliculas() {
            try {
                setLoading(true);
                setError(null); // Limpiamos errores anteriores
                const data = await getPeliculasRequest();

                // Si la data no es un array, forzamos un array vacío
                // (Doble seguridad por si el backend devuelve 'null')
                setPeliculas(Array.isArray(data) ? data : []);

            } catch (err) {
                console.error("Error al cargar películas:", err);
                // ¡Guardamos el mensaje de error específico!
                setError(err.message || "No se pudieron cargar las películas.");
            } finally {
                setLoading(false);
            }
        }

        cargarPeliculas();
    }, []);

    // --- Renderizado ---

    if (loading) return <h1 style={{ textAlign: 'center' }}>Cargando películas...</h1>;

    // ¡Ahora el h1 rojo nos dirá el error exacto!
    if (error) return <h1 style={{ color: '#ff6b6b', textAlign: 'center' }}>{error}</h1>;

    return (
        <div>
            <h1>Cartelera de Películas</h1>
            <PeliculaList peliculas={peliculas} />
        </div>
    );
}

export default HomePage;