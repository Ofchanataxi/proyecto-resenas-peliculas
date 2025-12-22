import { useEffect, useRef, useState } from "react";
import {
    startSimulation,
    stopSimulation,
    getSimulationStatus,
    getRecentMessages,
    connectToStream,
} from "../api/chatApi";


//funciopnes del chat
export default function ChatPage() {
    const [messages, setMessages] = useState([]);
    const [status, setStatus] = useState(null);
    const streamRef = useRef(null);

    const start = async () => {
        await startSimulation();
        connectStream();
    };

    const stop = async () => {
        await stopSimulation();
        if (streamRef.current) streamRef.current.close();
        streamRef.current = null;
    };

    const statusCheck = async () => {
        const s = await getSimulationStatus();
        setStatus(s);
    };

    const connectStream = () => {
        if (streamRef.current) streamRef.current.close();

        streamRef.current = connectToStream((msg) => {
            setMessages((prev) => [msg, ...prev]);
        });
    };

    useEffect(() => {
        getRecentMessages().then((data) => setMessages(data.reverse()));
        connectStream();

        return () => {
            if (streamRef.current) streamRef.current.close();
        };
    }, []);

    return (
        <div style={{ padding: 20 }}>
            <h2>Generador de Reseñas (WebFlux)</h2>

            <div style={{ display: "flex", gap: 10, marginBottom: 10 }}>
                <button onClick={start}>Generar reseñas</button>
                <button onClick={stop}>Detener</button>
                <button onClick={statusCheck}>Estado</button>
            </div>

            {status && (
                <pre style={{ background: "#f5f5f5", padding: 10 }}>
          {JSON.stringify(status, null, 2)}
        </pre>
            )}

            <div
                style={{
                    marginTop: 15,
                    border: "1px solid #ccc",
                    height: 400,
                    overflowY: "auto",
                    padding: 10,
                }}
            >
                {messages.map((m, i) => (
                    <div key={i} style={{ marginBottom: 8 }}>
                        <strong>{m.username ?? "Bot"}:</strong> {m.message}
                    </div>
                ))}
            </div>
        </div>
    );
}
