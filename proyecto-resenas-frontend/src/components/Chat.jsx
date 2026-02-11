import React, { useEffect, useRef, useState } from "react";
import {
  getRecentMessages,
  sendMessage,
  connectToStream,
} from "../api/chatApi";
import ChatMessages from "./ChatMessages";
import ChatInput from "./ChatInput";
import ChatSimulatorControls from "./ChatSimulatorControls";

const Chat = ({ usuarioId }) => {
  const [messages, setMessages] = useState([]);
  const eventSourceRef = useRef(null);

  useEffect(() => {
    if (!usuarioId) return;

    // 🔒 Cerrar stream anterior si existe
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }

    const loadMessages = async () => {
      try {
        const data = await getRecentMessages();
        setMessages(Array.isArray(data) ? data.reverse() : []);
      } catch (err) {
        console.error("Error cargando mensajes:", err);
      }
    };

    loadMessages();

    // 🔴 Conexión SSE
    eventSourceRef.current = connectToStream((newMessage) => {
      setMessages((prev) => [...prev, newMessage]);
    });

    return () => {
      eventSourceRef.current?.close();
      eventSourceRef.current = null;
    };
  }, [usuarioId]);

  const handleSend = async (contenido) => {
    if (!contenido.trim()) return;

    try {
      await sendMessage({ usuarioId, contenido });
    } catch (err) {
      console.error("Error enviando mensaje:", err);
      alert("No se pudo enviar el mensaje");
    }
  };

  return (
    <div style={styles.chatContainer}>
      <h2>💬 Chat en tiempo real</h2>

      <ChatSimulatorControls />
      <ChatMessages messages={messages} />
      <ChatInput onSend={handleSend} />
    </div>
  );
};

const styles = {
  chatContainer: {
    width: "400px",
    margin: "auto",
    border: "1px solid #ccc",
    padding: "10px",
    borderRadius: "8px",
  },
};

export default Chat;
