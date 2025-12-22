const BASE_URL = "http://localhost:8080/api/resenas/chat";

export const startSimulation = async () => {
    const res = await fetch(`${BASE_URL}/simulator/start`, {
        method: "POST",
    });
    return res.json();
};

export const stopSimulation = async () => {
    const res = await fetch(`${BASE_URL}/simulator/stop`, {
        method: "POST",
    });
    return res.json();
};

export const getSimulationStatus = async () => {
    const res = await fetch(`${BASE_URL}/simulator/status`);
    return res.json();
};

export const getRecentMessages = async () => {
    const res = await fetch(`${BASE_URL}/messages/recent`);
    return res.json();
};

export const connectToStream = (onMessage, onError) => {
    const eventSource = new EventSource(`${BASE_URL}/stream`);

    eventSource.onmessage = (event) => {
        onMessage(JSON.parse(event.data));
    };

    eventSource.onerror = (err) => {
        if (onError) onError(err);
        eventSource.close();
    };

    return eventSource;
};
