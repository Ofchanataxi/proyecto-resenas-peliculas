const ChatMessages = ({ messages }) => {
  return (
    <div style={styles.container}>
      {messages.map((msg) => (
        <div
          key={msg.id}
          style={{
            ...styles.message,
            backgroundColor: msg.esSimulado ? "#28372cff" : "#000000ff",
          }}
        >
          <strong>{msg.usuarioNombre}</strong>
          <p>{msg.contenido}</p>
          <small>{new Date(msg.fechaEnvio).toLocaleTimeString()}</small>
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
  },
  message: {
    padding: "8px",
    marginBottom: "5px",
    borderRadius: "5px",
  },
};

export default ChatMessages;
