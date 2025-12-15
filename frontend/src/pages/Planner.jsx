import React from "react";

export default function Planner() {
  const stats = [
    { label: "Upcoming Events", value: 4 },
    { label: "Vendors Assigned", value: 12 },
    { label: "Tasks Due Today", value: 3 },
  ];

  const tasks = [
    { id: 1, title: "Confirm catering menu", due: "Today" },
    { id: 2, title: "Send invites for summit", due: "Tomorrow" },
    { id: 3, title: "Finalize decor plan", due: "Fri" },
  ];

  return (
    <div className="container dashboard">
      <div className="dashboard-header">
        <h2>Event Planner Dashboard</h2>
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
            <a className="btn" href="#">
              Create Booking
            </a>
            <a className="btn" href="#">
              Assign Vendor
            </a>
            <a className="btn" href="#">
              Upload Schedule
            </a>
          </div>
        </div>

        <div className="card">
          <h3>Tasks</h3>
          <ul className="tasks">
            {tasks.map((t) => (
              <li key={t.id}>
                <span>{t.title}</span>
                <span className="muted">{t.due}</span>
              </li>
            ))}
          </ul>
        </div>
      </section>
    </div>
  );
}
