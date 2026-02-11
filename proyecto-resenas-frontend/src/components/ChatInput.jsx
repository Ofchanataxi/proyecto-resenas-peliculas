import React, { useState } from "react";

const ChatInput = ({ onSend }) => {
  const [text, setText] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!text.trim()) return;

    onSend(text);
    setText("");
  };

  return (
    <form onSubmit={handleSubmit} style={styles.form}>
      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Escribe un mensaje..."
        style={styles.input}
      />
      <button type="submit">Enviar</button>
    </form>
  );
};

const styles = {
  form: {
    display: "flex",
    gap: "5px",
  },
  input: {
    flex: 1,
    padding: "8px",
  },
};

export default ChatInput;
