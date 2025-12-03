import React from "react";

export default function Home() {
  return (
    <section className="hero">
      <div className="container">
        <h2>Host unforgettable weddings, conferences and celebrations.</h2>
        <p className="tagline">
          Saffron Gardens is an elegant event center offering venue hire,
          catering, and full event planning.
        </p>
        <div className="actions">
          <a className="btn primary" href="#contact">
            Book Now
          </a>
          <a className="btn" href="/vendors">
            Venue Details
          </a>
        </div>
      </div>
    </section>
  );
}
