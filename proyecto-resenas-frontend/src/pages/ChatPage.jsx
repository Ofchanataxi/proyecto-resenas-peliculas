import React from "react";
import Chat from "../components/Chat";

const ChatPage = () => {
  const usuarioId = 1; // luego lo sacas del JWT

  return (
    <div style={{ padding: "20px" }}>
      <h1>Chat de Reseñas 🎬</h1>
      <Chat usuarioId={usuarioId} />
    </div>
  );
};

export default ChatPage;
