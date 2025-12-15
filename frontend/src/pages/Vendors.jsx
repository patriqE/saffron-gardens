import React, { useState } from "react";
import { Link } from "react-router-dom";
import api from "../api";

export default function Vendors() {
  const [form, setForm] = useState({
    businessName: "",
    category: "",
    description: "",
    email: "",
    username: "",
    password: "",
  });
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);

  const onChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.post("/api/vendors", form);
      setStatus({
        ok: true,
        msg: "Application submitted. We will be in touch.",
      });
      setForm({
        businessName: "",
        category: "",
        description: "",
        email: "",
        username: "",
        password: "",
      });
    } catch (err) {
      setStatus({
        ok: false,
        msg: err?.response?.data?.message || "Submission failed",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="vendor-page">
      <div className="container">
        <div className="vendor-container">
          <div className="vendor-card card">
            <div className="vendor-header">
              <h2>Become a Vendor</h2>
              <p className="muted">
                Join our marketplace and reach event planners
              </p>
            </div>

            {status && (
              <div
                className={`alert ${
                  status.ok ? "alert-success" : "alert-error"
                }`}
              >
                <svg
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  xmlns="http://www.w3.org/2000/svg"
                >
                  {status.ok ? (
                    <>
                      <path
                        d="M20 6L9 17l-5-5"
                        stroke="currentColor"
                        strokeWidth="1.5"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                      />
                    </>
                  ) : (
                    <>
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
                    </>
                  )}
                </svg>
                <span>{status.msg}</span>
              </div>
            )}

            <form onSubmit={submit} className="vendor-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="businessName">Business Name</label>
                  <input
                    id="businessName"
                    name="businessName"
                    value={form.businessName}
                    onChange={onChange}
                    placeholder="Your business name"
                    required
                    disabled={loading}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="category">Category</label>
                  <select
                    id="category"
                    name="category"
                    value={form.category}
                    onChange={onChange}
                    required
                    disabled={loading}
                  >
                    <option value="">Select a category</option>
                    <option value="Catering">Catering</option>
                    <option value="Photography">Photography</option>
                    <option value="Music & DJ">Music & DJ</option>
                    <option value="Florist">Florist</option>
                    <option value="Decorator">Decorator</option>
                    <option value="Videography">Videography</option>
                    <option value="Other">Other</option>
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="email">Contact Email</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  value={form.email}
                  onChange={onChange}
                  placeholder="your@email.com"
                  required
                  disabled={loading}
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="username">Username</label>
                  <input
                    id="username"
                    name="username"
                    value={form.username}
                    onChange={onChange}
                    placeholder="Choose a username"
                    required
                    disabled={loading}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="password">Password</label>
                  <input
                    id="password"
                    name="password"
                    type="password"
                    value={form.password}
                    onChange={onChange}
                    placeholder="Create a password"
                    required
                    disabled={loading}
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="description">Business Description</label>
                <textarea
                  id="description"
                  name="description"
                  value={form.description}
                  onChange={onChange}
                  placeholder="Tell us about your services and experience"
                  rows="4"
                  disabled={loading}
                ></textarea>
              </div>

              <button
                className="btn primary btn-block"
                type="submit"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <span className="spinner"></span>
                    Submitting...
                  </>
                ) : (
                  "Submit Application"
                )}
              </button>
            </form>

            <div className="vendor-footer">
              <Link to="/" className="back-link">
                ← Back to home
              </Link>
            </div>
          </div>

          <div className="vendor-info">
            <h3>Why Join Us?</h3>
            <ul>
              <li>Reach hundreds of event planners</li>
              <li>Grow your business exposure</li>
              <li>Secure bookings through our platform</li>
              <li>Professional network of vendors</li>
              <li>24/7 customer support</li>
            </ul>

            <h3 style={{ marginTop: 28 }}>Categories We Need</h3>
            <ul>
              <li>Catering & Food Services</li>
              <li>Photography & Videography</li>
              <li>Music & Entertainment</li>
              <li>Floral & Decoration</li>
              <li>Event Planning</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
