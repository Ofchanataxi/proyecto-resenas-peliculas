import React, { createContext, useContext, useState, useEffect } from 'react';
import * as authApi from '../api/authApi'; // Crearemos este archivo

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [user, setUser] = useState(null); // Aquí guardaremos los datos del usuario logueado
    const [loading, setLoading] = useState(true); // Para saber si estamos verificando el token

    useEffect(() => {
        // Al cargar la app, verifica si hay un token válido
        const verifyToken = async () => {
            if (token) {
                try {
                    // Llama al nuevo endpoint /me
                    const userData = await authApi.getMiPerfil(token); 
                    setUser(userData); // Guarda los datos del usuario
                } catch (error) {
                    // El token es inválido o expiró
                    console.error("Token inválido", error);
                    setToken(null);
                    localStorage.removeItem('token');
                }
            }
            setLoading(false);
        };
        verifyToken();
    }, [token]);

    const login = (newToken) => {
        setToken(newToken);
        localStorage.setItem('token', newToken);
        // El useEffect se encargará de llamar a /me y setear el usuario
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
    };

    const value = {
        token,
        user,
        isAuthenticated: !!user,
        loading,
        login,
        logout,
    };

    // No renderiza la app hasta que no se haya verificado el token
    return (
        <AuthContext.Provider value={value}>
            {!loading && children}
        </AuthContext.Provider>
    );
};