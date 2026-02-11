import { useEffect, useState } from "react";
import {
  startSimulation,
  stopSimulation,
  getSimulationStatus,
} from "../api/chatApi";

const ChatSimulatorControls = () => {
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadStatus = async () => {
    try {
      const data = await getSimulationStatus();
      setStatus(data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    loadStatus();
  }, []);

  const handleStart = async () => {
    setLoading(true);
    try {
      const res = await startSimulation();
      alert(res.message);
      loadStatus();
    } catch (err) {
      alert(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleStop = async () => {
    setLoading(true);
    try {
      const res = await stopSimulation();
      alert(res.message);
      loadStatus();
    } catch (err) {
      alert(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <h4>Simulador de Chat</h4>

      <button
        onClick={handleStart}
        disabled={loading || status?.isRunning}
        style={{ ...styles.button, backgroundColor: "#198754" }}
      >
        ▶ Iniciar simulación
      </button>

      <button
        onClick={handleStop}
        disabled={loading || !status?.isRunning}
        style={{ ...styles.button, backgroundColor: "#dc3545" }}
      >
        ⏹ Detener simulación
      </button>

      {status && (
        <div style={styles.status}>
          <strong>Estado:</strong>{" "}
          {status.isRunning ? "En ejecución" : "Detenida"} <br />
          <strong>Usuarios bot:</strong> {status.activeUsers}
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    padding: "10px",
    border: "1px solid #ccc",
    borderRadius: "6px",
    marginBottom: "10px",
  },
  button: {
    color: "white",
    border: "none",
    padding: "8px 12px",
    marginRight: "8px",
    cursor: "pointer",
    borderRadius: "4px",
  },
  status: {
    marginTop: "10px",
    fontSize: "14px",
  },
};

export default ChatSimulatorControls;
