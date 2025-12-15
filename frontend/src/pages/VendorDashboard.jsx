import React from "react";

export default function VendorDashboard() {
  const stats = [
    { label: "Upcoming Assignments", value: 3 },
    { label: "Completed This Month", value: 6 },
    { label: "Rating", value: "4.7" },
  ];

  const assignments = [
    { id: "AS-2104", event: "Ife & Kunle Wedding", date: "Sat" },
    { id: "AS-2103", event: "Corporate Summit", date: "Thu" },
    { id: "AS-2102", event: "Birthday Soirée", date: "Wed" },
  ];

  return (
    <div className="container dashboard">
      <div className="dashboard-header">
        <h2>Vendor Dashboard</h2>
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
          <h3>Availability</h3>
          <div className="quick-actions">
            <button className="btn">Set Available</button>
            <button className="btn">Set Unavailable</button>
            <button className="btn">Update Profile</button>
          </div>
        </div>

        <div className="card">
          <h3>Upcoming Assignments</h3>
          <div className="table">
            <div className="table-row table-head">
              <div>Ref</div>
              <div>Event</div>
              <div>When</div>
            </div>
            {assignments.map((a) => (
              <div key={a.id} className="table-row">
                <div>{a.id}</div>
                <div>{a.event}</div>
                <div>{a.date}</div>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
