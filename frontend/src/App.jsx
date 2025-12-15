import React, { useState, useEffect } from "react";
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
  const auth = useAuth();

  // Default to dark theme, allow user override
  const [theme, setTheme] = useState(() => {
    const saved = localStorage.getItem("theme");
    return saved || "dark";
  });

  useEffect(() => {
    document.documentElement.setAttribute("data-theme", theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === "dark" ? "light" : "dark"));
  };

  const handleLogout = async () => {
    await auth.logout();
    window.location.href = "/";
  };

  return (
    <div className="app-root">
      <header className="site-header">
        <div className="container header-content">
          <div className="header-left">
            <img src="/logo.png" alt="Saffron Gardens Logo" className="logo" />
            <h1 className="brand">
              <Link
                to="/"
                style={{ color: "var(--accent)", textDecoration: "none" }}
              >
                Saffron Gardens
              </Link>
            </h1>
          </div>
          <nav className="nav">
            <Link to="/">Home</Link>
            <Link to="/vendors">Vendors</Link>
            {auth?.accessToken ? (
              <>
                <Link to="/admin">Dashboard</Link>
                <button className="btn-logout" onClick={handleLogout}>
                  Logout
                </button>
              </>
            ) : (
              <Link to="/login">Admin</Link>
            )}
            <button
              className="theme-toggle"
              onClick={toggleTheme}
              aria-label="Toggle theme"
              title={`Switch to ${theme === "dark" ? "light" : "dark"} mode`}
            >
              {theme === "dark" ? (
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <circle
                    cx="12"
                    cy="12"
                    r="5"
                    stroke="currentColor"
                    strokeWidth="1.5"
                  />
                  <path
                    d="M12 2v2m0 16v2M4.22 4.22l1.42 1.42m12.72 12.72l1.42 1.42M2 12h2m16 0h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"
                    stroke="currentColor"
                    strokeWidth="1.5"
                    strokeLinecap="round"
                  />
                </svg>
              ) : (
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  <path
                    d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z"
                    stroke="currentColor"
                    strokeWidth="1.5"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              )}
            </button>
          </nav>
        </div>
      </header>

      <main>
        <AppRoutes />
      </main>

      <footer className="site-footer">
        <div className="container">
          <div style={{ marginBottom: 12 }}>
            © Saffron Gardens Event Center — Built with care.
          </div>
          <div className="social-links">
            <a
              href="https://www.instagram.com/the_saffron_gardens"
              target="_blank"
              rel="noopener noreferrer"
              title="Follow us on Instagram"
            >
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <rect
                  x="2"
                  y="2"
                  width="20"
                  height="20"
                  rx="5"
                  stroke="currentColor"
                  strokeWidth="1.5"
                />
                <circle
                  cx="12"
                  cy="12"
                  r="5"
                  stroke="currentColor"
                  strokeWidth="1.5"
                />
                <circle cx="18" cy="6" r="1" fill="currentColor" />
              </svg>
            </a>
            <a href="mailto:saffrongardens2@gmail.com" title="Email us">
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M2 6h20v12a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                  stroke="currentColor"
                  strokeWidth="1.5"
                />
                <path
                  d="M2 6l10 8 10-8"
                  stroke="currentColor"
                  strokeWidth="1.5"
                  strokeLinecap="round"
                />
              </svg>
            </a>
          </div>
        </div>
      </footer>
    </div>
  );
}
