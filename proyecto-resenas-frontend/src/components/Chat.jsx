import React, { useEffect, useRef, useState } from "react";
import { getRecentMessages, sendMessage, connectToStream } from "../api/chatApi";
import ChatMessages from "./ChatMessages";
import ChatInput from "./ChatInput";
import ChatSimulatorControls from "./ChatSimulatorControls";

const Chat = ({ usuarioId }) => {
  const [messages, setMessages] = useState([]);
  const eventSourceRef = useRef(null);

  useEffect(() => {
    const loadMessages = async () => {
      try {
        const data = await getRecentMessages();
        setMessages(Array.isArray(data) ? data.reverse() : []);
      } catch (err) {
        console.error(err);
      }
    };

    loadMessages();

    eventSourceRef.current = connectToStream((newMessage) => {
      setMessages((prev) => [...prev, newMessage]);
    });

    return () => {
      eventSourceRef.current?.close();
    };
  }, []);

  const handleSend = async (contenido) => {
    console.log("Enviando mensaje:", contenido, "desde usuarioId:", usuarioId);
    try {
      await sendMessage({ usuarioId, contenido });
    } catch (err) {
      alert(err.message);
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
