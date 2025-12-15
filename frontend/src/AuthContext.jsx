import React, { createContext, useContext, useState, useEffect } from "react";
import api, { setAccessToken } from "./api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [accessToken, setLocalAccessToken] = useState(null);
  const [user, setUser] = useState(null);

  // attempt to obtain access token from refresh cookie on mount
  useEffect(() => {
    let mounted = true;
    api
      .post("/api/auth/refresh", {}, { withCredentials: true })
      .then((res) => {
        const data = res.data || {};
        if (data.accessToken && mounted) {
          setAccessToken(data.accessToken);
          setLocalAccessToken(data.accessToken);
          // Fetch user info after refresh
          if (data.user) {
            setUser(data.user);
          } else {
            // If user not in refresh response, fetch from /me endpoint
            api
              .get("/api/auth/me")
              .then((meRes) => {
                if (meRes.data && mounted) {
                  setUser(meRes.data);
                }
              })
              .catch(() => {
                // ignore if /me fails
              });
          }
        }
      })
      .catch(() => {
        // no-op if no valid refresh cookie
      });
    return () => {
      mounted = false;
    };
  }, []);

  const login = async (username, password) => {
    const res = await api.post(
      "/api/auth/login",
      { username, password },
      { withCredentials: true }
    );
    const data = res.data || {};
    if (data.accessToken) {
      setAccessToken(data.accessToken);
      setLocalAccessToken(data.accessToken);
      if (data.user) setUser(data.user);
    }
    return data;
  };

  const logout = async () => {
    try {
      await api.post("/api/auth/logout", {}, { withCredentials: true });
    } catch (e) {
      // ignore
    }
    setAccessToken(null);
    setLocalAccessToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ accessToken, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}

export default AuthContext;
