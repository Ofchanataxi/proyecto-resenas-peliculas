// src/App.jsx

import React from 'react';
import UsuariosPage from './pages/UsuariosPage'; // Asegúrate que la ruta sea correcta
import './App.css'; // Opcional: para estilos generales

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <h1>Mi App de Reseñas de Películas</h1>
      </header>
      <main>
        {/* Aquí es donde se renderiza tu página de gestión de usuarios */}
        <UsuariosPage />
        
        {/* Más adelante podrás agregar tus otras páginas aquí
            <PeliculasPage />
            <CinesPage />
        */}
      </main>
    </div>
  );
}

export default App;