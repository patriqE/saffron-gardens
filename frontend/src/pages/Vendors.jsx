import React, { useState } from "react";
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

  const onChange = (e) =>
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const submit = async (e) => {
    e.preventDefault();
    try {
      await api.post("/api/vendors", form);
      setStatus({
        ok: true,
        msg: "Application submitted. We will be in touch.",
      });
    } catch (err) {
      setStatus({
        ok: false,
        msg: err?.response?.data?.message || "Submission failed",
      });
    }
  };

  return (
    <div className="container">
      <h3>Apply as a Vendor</h3>
      <form onSubmit={submit} style={{ maxWidth: 700 }}>
        <label>
          Business name
          <br />
          <input
            name="businessName"
            value={form.businessName}
            onChange={onChange}
            required
          />
        </label>
        <label>
          Category
          <br />
          <input
            name="category"
            value={form.category}
            onChange={onChange}
            required
          />
        </label>
        <label>
          Contact email
          <br />
          <input
            name="email"
            type="email"
            value={form.email}
            onChange={onChange}
            required
          />
        </label>
        <label>
          Username
          <br />
          <input
            name="username"
            value={form.username}
            onChange={onChange}
            required
          />
        </label>
        <label>
          Password
          <br />
          <input
            name="password"
            type="password"
            value={form.password}
            onChange={onChange}
            required
          />
        </label>
        <label>
          Description
          <br />
          <textarea
            name="description"
            value={form.description}
            onChange={onChange}
          ></textarea>
        </label>
        <div style={{ marginTop: 12 }}>
          <button className="btn primary" type="submit">
            Submit Application
          </button>
        </div>
      </form>
      {status && (
        <div style={{ marginTop: 12, color: status.ok ? "green" : "red" }}>
          {status.msg}
        </div>
      )}
    </div>
  );
}
