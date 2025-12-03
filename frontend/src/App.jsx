import React from "react";
import { Routes, Route, Link, Navigate } from "react-router-dom";
import Home from "./pages/Home";
import Vendors from "./pages/Vendors";
import Login from "./pages/Login";
import Admin from "./pages/Admin";
import { useAuth } from "./AuthContext";

function AppRoutes() {
  const auth = useAuth();
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/vendors" element={<Vendors />} />
      <Route path="/login" element={<Login />} />
      <Route
        path="/admin"
        element={
          auth?.accessToken ? <Admin /> : <Navigate to="/login" replace />
        }
      />
    </Routes>
  );
}

export default function App() {
  return (
    <div className="app-root">
      <header className="site-header">
        <div className="container">
          <h1 className="brand">
            <Link
              to="/"
              style={{ color: "var(--accent)", textDecoration: "none" }}
            >
              Saffron Gardens Event Center
            </Link>
          </h1>
          <nav className="nav">
            <Link to="/">Home</Link>
            <Link to="/vendors">Vendors</Link>
            <Link to="/login">Admin</Link>
          </nav>
        </div>
      </header>

      <main>
        <AppRoutes />
      </main>

      <footer className="site-footer">
        <div className="container">
          © Saffron Gardens Event Center — Built with care.
        </div>
      </footer>
    </div>
  );
}
