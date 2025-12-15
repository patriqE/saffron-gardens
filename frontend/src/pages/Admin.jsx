import React from "react";
import { useAuth } from "../AuthContext";

export default function Admin() {
  const auth = useAuth();

  const stats = [
    { label: "Pending Applications", value: 8 },
    { label: "Today’s Bookings", value: 5 },
    { label: "Payments (24h)", value: "₦250k" },
  ];

  const recent = [
    { id: "BK-1023", name: "Adebola & Tayo", status: "Pending" },
    { id: "BK-1022", name: "Acme Corp Summit", status: "Confirmed" },
    { id: "BK-1021", name: "Ife & Kunle", status: "Confirmed" },
  ];

  const quickActions = [
    { label: "Review Vendor Apps", href: "/vendors" },
    { label: "View Bookings", href: "#" },
    { label: "Update Content", href: "#" },
  ];

  return (
    <div className="container dashboard">
      <div className="dashboard-header">
        <h2>Admin Dashboard</h2>
        <div className="dashboard-actions">
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
      </div>

      <section className="stat-grid">
        {stats.map((s) => (
          <div key={s.label} className="stat-card card">
            <div className="stat-value">{s.value}</div>
            <div className="stat-label">{s.label}</div>
          </div>
        ))}
      </section>

      <section className="dashboard-grid">
        <div className="card">
          <h3>Quick Actions</h3>
          <div className="quick-actions">
            {quickActions.map((a) => (
              <a key={a.label} className="btn" href={a.href}>
                {a.label}
              </a>
            ))}
          </div>
        </div>

        <div className="card">
          <h3>Recent Bookings</h3>
          <div className="table">
            <div className="table-row table-head">
              <div>Ref</div>
              <div>Client / Event</div>
              <div>Status</div>
            </div>
            {recent.map((r) => (
              <div key={r.id} className="table-row">
                <div>{r.id}</div>
                <div>{r.name}</div>
                <div>{r.status}</div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
