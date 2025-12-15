import React, { useState } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import { useAuth } from "../AuthContext";

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const auth = useAuth();
  const nav = useNavigate();
  const location = useLocation();

  // Get the redirect path from location state, or default to /dashboard
  const from = location.state?.from?.pathname || "/dashboard";

  const submit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const data = await auth.login(username, password);
      if (data && data.accessToken) {
        // Successful login - redirect to original destination or admin
        nav(from, { replace: true });
      } else {
        setError("Login failed. Please check your credentials.");
      }
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Unable to connect. Please try again.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="container">
        <div className="login-container">
          <div className="login-card card">
            <div className="login-header">
              <h2>Welcome Back</h2>
              <p className="muted">Sign in to access the admin dashboard</p>
            </div>

            <form onSubmit={submit} className="login-form">
              {error && (
                <div className="alert alert-error">
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
                      r="10"
                      stroke="currentColor"
                      strokeWidth="1.5"
                    />
                    <path
                      d="M12 8v4m0 4h.01"
                      stroke="currentColor"
                      strokeWidth="1.5"
                      strokeLinecap="round"
                    />
                  </svg>
                  <span>{error}</span>
                </div>
              )}

              <div className="form-group">
                <label htmlFor="username">Username</label>
                <input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Enter your username"
                  required
                  autoComplete="username"
                  disabled={loading}
                />
              </div>

              <div className="form-group">
                <label htmlFor="password">Password</label>
                <input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Enter your password"
                  required
                  autoComplete="current-password"
                  disabled={loading}
                />
              </div>

              <button
                className="btn primary btn-block"
                type="submit"
                disabled={loading || !username || !password}
              >
                {loading ? (
                  <>
                    <span className="spinner"></span>
                    Signing in...
                  </>
                ) : (
                  "Sign In"
                )}
              </button>
            </form>

            <div className="login-footer">
              <Link to="/" className="back-link">
                ← Back to home
              </Link>
            </div>
          </div>

          <div className="login-info">
            <h3>Admin Access</h3>
            <p>
              This area is restricted to authorized event managers and
              administrators. From here you can:
            </p>
            <ul>
              <li>Manage vendor applications</li>
              <li>Review booking requests</li>
              <li>Update event content</li>
              <li>Monitor system activity</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
