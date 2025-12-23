const API_URL = "http://localhost:8080/api/resenas/chat";

/* =========================
   AUTH HEADERS
========================= */
const getAuthHeaders = () => {
  const token = localStorage.getItem("token");

  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
};

/* =========================
   CHAT
========================= */
export const getRecentMessages = async () => {
  const res = await fetch(`${API_URL}/messages/recent`, {
    headers: getAuthHeaders(),
  });

  if (!res.ok) throw new Error("Error al obtener mensajes");

  return res.json();
};

export const sendMessage = async ({ usuarioId, contenido }) => {
  const res = await fetch(`${API_URL}/messages`, {
    method: "POST",
    headers: getAuthHeaders(),
    body: JSON.stringify({ usuarioId, contenido }),
  });

  if (!res.ok) {
    const error = await res.text();
    console.error(error);
    throw new Error("Error al enviar mensaje");
  }

  return res.json();
};

/* =========================
   SSE (TOKEN EN QUERY)
========================= */
export const connectToStream = (onMessage) => {
  const eventSource = new EventSource(
    "http://localhost:8080/api/resenas/chat/stream"
  );

  eventSource.onmessage = (event) => {
    const message = JSON.parse(event.data);
    onMessage(message);
  };

  eventSource.onerror = (err) => {
    console.error("Error en SSE:", err);
    eventSource.close();
  };

  return eventSource;
};


/* =========================
   SIMULADOR
========================= */
export const startSimulation = async () => {
  const res = await fetch(`${API_URL}/simulator/start`, {
    method: "POST",
    headers: getAuthHeaders(),
  });

  if (!res.ok) throw new Error("Error al iniciar simulación");

  return res.json();
};

export const stopSimulation = async () => {
  const res = await fetch(`${API_URL}/simulator/stop`, {
    method: "POST",
    headers: getAuthHeaders(),
  });

  if (!res.ok) throw new Error("Error al detener simulación");

  return res.json();
};

export const getSimulationStatus = async () => {
  const res = await fetch(`${API_URL}/simulator/status`, {
    headers: getAuthHeaders(),
  });

  if (!res.ok) throw new Error("Error al obtener estado");

  return res.json();
};
