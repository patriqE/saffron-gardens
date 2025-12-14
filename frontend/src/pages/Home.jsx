import React from "react";

function Icon({ name }) {
  switch (name) {
    case "space":
      return (
        <svg
          width="36"
          height="36"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <rect
            x="3"
            y="6"
            width="18"
            height="12"
            rx="2"
            stroke="currentColor"
            strokeWidth="1.5"
          />
          <path
            d="M7 10h.01M11 10h.01M15 10h.01"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      );
    case "catering":
      return (
        <svg
          width="36"
          height="36"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M4 20h16"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
          />
          <path
            d="M7 20v-6a3 3 0 013-3h4a3 3 0 013 3v6"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      );
    case "vendor":
      return (
        <svg
          width="36"
          height="36"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <circle
            cx="12"
            cy="8"
            r="3"
            stroke="currentColor"
            strokeWidth="1.5"
          />
          <path
            d="M5 20c1.5-4 5-6 7-6s5.5 2 7 6"
            stroke="currentColor"
            strokeWidth="1.5"
            strokeLinecap="round"
            strokeLinejoin="round"
          />
        </svg>
      );
    default:
      return null;
  }
}

export default function Home() {
  return (
    <div>
      <section className="hero large-hero">
        <div className="hero-bg" aria-hidden />
        <div className="container hero-inner">
          <div className="hero-copy">
            <h1>
              Saffron Gardens
              <span className="accent">—</span>
              Memories start here
            </h1>
            <p className="lead">
              Elegant venues, curated vendors and full-service event support for
              weddings, conferences and celebrations.
            </p>
            <div className="actions">
              <a className="btn primary large" href="#contact">
                Check Availability
              </a>
              <a className="btn ghost" href="/vendors">
                Browse Vendors
              </a>
            </div>
            <div className="hero-stats">
              <div>
                <strong>120+</strong> Events hosted
              </div>
              <div>
                <strong>50+</strong> Trusted vendors
              </div>
              <div>
                <strong>10+</strong> Years experience
              </div>
            </div>
          </div>
          <div className="hero-preview">
            <div className="preview-grid">
              <img
                src="https://images.unsplash.com/photo-1506629082955-511b1d6d5c59?w=900&q=60&auto=format&fit=crop"
                alt="banquet"
              />
              <img
                src="https://images.unsplash.com/photo-1526304640581-d334cdbbf45e?w=900&q=60&auto=format&fit=crop"
                alt="garden"
              />
              <img
                src="https://images.unsplash.com/photo-1506377247377-2a5b3b417ebb?w=900&q=60&auto=format&fit=crop"
                alt="table"
              />
            </div>
          </div>
        </div>
      </section>

      <section className="features">
        <div className="container grid">
          <div className="card feature">
            <div className="icon">
              <Icon name="space" />
            </div>
            <h3>Beautiful spaces</h3>
            <p>
              Indoor halls, lush gardens and flexible layouts tailored to your
              guest list.
            </p>
          </div>
          <div className="card feature">
            <div className="icon">
              <Icon name="catering" />
            </div>
            <h3>Catering & Service</h3>
            <p>
              Seasonal menus and experienced staff to execute your vision
              flawlessly.
            </p>
          </div>
          <div className="card feature">
            <div className="icon">
              <Icon name="vendor" />
            </div>
            <h3>Vendor Marketplace</h3>
            <p>
              Discover vetted DJs, florists, photographers and more, bookable
              from our marketplace.
            </p>
            <p className="vendor-cta">
              <a className="btn" href="/vendors">
                Explore vendors
              </a>
            </p>
          </div>
        </div>
      </section>

      <section className="gallery container">
        <h3>Moments</h3>
        <div className="photo-grid">
          <img
            src="https://images.unsplash.com/photo-1505577058444-a3dab5c3f8ad?w=800&q=60&auto=format&fit=crop"
            alt="event 1"
          />
          <img
            src="https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800&q=60&auto=format&fit=crop"
            alt="event 2"
          />
          <img
            src="https://images.unsplash.com/photo-1487412947147-5cebf100ffc2?w=800&q=60&auto=format&fit=crop"
            alt="event 3"
          />
          <img
            src="https://images.unsplash.com/photo-1508610048659-a06f1c10f0e5?w=800&q=60&auto=format&fit=crop"
            alt="event 4"
          />
        </div>
      </section>

      <section className="testimonials container">
        <h3>What clients say</h3>
        <div className="testimonial-grid">
          <div className="card">
            <p>
              "The team made our wedding effortless — everything was perfect."
            </p>
            <p className="muted">— Aisha & Daniel</p>
          </div>
          <div className="card">
            <p>
              "Professional, calm and creative. Highly recommended for corporate
              events."
            </p>
            <p className="muted">— M. Opoku</p>
          </div>
        </div>
      </section>

      <section className="container contact" id="contact">
        <div className="card contact-grid">
          <div>
            <h3>Request availability</h3>
            <p>
              Tell us about your date and guest count — we'll follow up within
              24 hours.
            </p>
            <p className="muted">
              <strong>Email:</strong> bookings@saffrongardens.example
            </p>
          </div>
          <form
            className="contact-form"
            onSubmit={(e) => {
              e.preventDefault();
              alert("Thanks — we will reach out!");
            }}
          >
            <input name="name" placeholder="Your name" required />
            <input
              name="email"
              placeholder="Email address"
              type="email"
              required
            />
            <input name="date" placeholder="Event date (optional)" />
            <textarea
              name="notes"
              placeholder="Notes (guest count, style)"
              rows="3"
            ></textarea>
            <div className="form-actions">
              <button className="btn primary" type="submit">
                Send Request
              </button>
              <a className="btn ghost" href="/login">
                Manager Login
              </a>
            </div>
          </form>
        </div>
      </section>
    </div>
  );
}
