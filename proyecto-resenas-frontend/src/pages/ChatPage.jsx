import React from "react";
import Chat from "../components/Chat";
import { useAuth } from "../context/AuthContext";

const ChatPage = () => {
  const { usuarioId, loading } = useAuth();

  if (loading) return <p>Cargando sesión...</p>;
  if (!usuarioId) return <p>No autenticado</p>;

  return (
    <div style={{ padding: "20px" }}>
      <h1>Chat de Reseñas 🎬</h1>
      <Chat usuarioId={usuarioId} />
    </div>
  );
};

export default ChatPage;
