import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const auth = useAuth();
  const nav = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    try {
      const data = await auth.login(username, password);
      if (data && data.accessToken) nav("/admin");
      else setError("Login failed");
    } catch (err) {
      setError(err?.response?.data?.message || "Login error");
    }
  };

  return (
    <div className="container">
      <h3>Admin Login</h3>
      <form onSubmit={submit} style={{ maxWidth: 400 }}>
        <label>
          Username
          <br />
          <input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>
        <label>
          Password
          <br />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <div style={{ marginTop: 12 }}>
          <button className="btn primary" type="submit">
            Login
          </button>
        </div>
      </form>
      {error && <div style={{ color: "red", marginTop: 12 }}>{error}</div>}
    </div>
  );
}
