import React, { createContext, useContext, useState, useEffect } from "react";
import * as authApi from "../api/authApi";

const AuthContext = createContext(null);
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [usuarioId, setUsuarioId] = useState(
    localStorage.getItem("usuarioId")
  );
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const verifyToken = async () => {
      if (token) {
        try {
          const userData = await authApi.getMiPerfil(token);
          setUser(userData);
        } catch (error) {
          logout();
        }
      }
      setLoading(false);
    };

    verifyToken();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  // ✅ LOGIN GUARDA TOKEN + ID
  const login = ({ token, usuarioId }) => {
    setToken(token);
    setUsuarioId(usuarioId);

    localStorage.setItem("token", token);
    localStorage.setItem("usuarioId", usuarioId);
  };

  const logout = () => {
    setToken(null);
    setUsuarioId(null);
    setUser(null);
    localStorage.removeItem("token");
    localStorage.removeItem("usuarioId");
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        usuarioId,
        user,
        loading,
        isAuthenticated: !!token,
        login,
        logout,
      }}
    >
      {!loading && children}
    </AuthContext.Provider>
  );
};
