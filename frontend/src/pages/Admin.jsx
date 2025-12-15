import React from "react";
import { useAuth } from "../AuthContext";

export default function Admin() {
  const auth = useAuth();
  const user = auth.user;

  return (
    <div className="container">
      <h3>Admin Dashboard</h3>
      <div style={{ marginBottom: 12 }}>
        <button
          className="btn"
          onClick={() => {
            auth.logout();
            window.location.href = "/login";
          }}
        >
          Logout
        </button>
      </div>
      {user ? (
        <pre>{JSON.stringify(user, null, 2)}</pre>
      ) : (
        <div>Loading...</div>
      )}
    </div>
  );
}
