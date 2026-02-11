const ChatMessages = ({ messages = [] }) => {
  return (
    <div style={styles.container}>
      {messages.length === 0 && (
        <p style={{ color: "#666", textAlign: "center" }}>
          No hay mensajes aún
        </p>
      )}

      {messages.map((msg, index) => (
        <div
          key={msg.id ?? index}
          style={{
            ...styles.message,
            backgroundColor: msg.esSimulado ? "#28372cff" : "#000000ff",
            color: "#ffffff", // ✅ CLAVE
          }}
        >
          <strong>{msg.usuarioNombre ?? "Usuario"}</strong>
          <p>{msg.contenido}</p>
          <small>
            {msg.fechaEnvio
              ? new Date(msg.fechaEnvio).toLocaleTimeString()
              : ""}
          </small>
        </div>
      ))}
    </div>
  );
};

const styles = {
  container: {
    height: "300px",
    overflowY: "auto",
    marginBottom: "10px",
    padding: "5px",
  },
  message: {
    padding: "8px",
    marginBottom: "6px",
    borderRadius: "6px",
  },
};

export default ChatMessages;
