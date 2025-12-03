# Saffron Frontend (React + Vite)

This folder contains a minimal React + Vite scaffold for the Saffron Gardens Event Center landing page.

Quick start (requires Node.js):

```cmd
cd frontend
npm install
npm run dev
```

Build for production:

```cmd
npm run build
npm run preview
```

Notes:

- This is intentionally minimal: expand into a full SPA (Vite + React Router) when you need dynamic pages such as booking flows.
- Keep backend API base URL in an environment file (e.g. `.env`) and do not commit secrets.

Suggested next features:

- Add a booking/contact form that POSTs to the backend bookings endpoint.
- Add image assets and venue galleries in `src/assets/`.
- Integrate calendar availability and booking workflow with the backend.

E2E tests (Playwright):

```cmd
cd frontend
npm install
npm run e2e:install
npm run e2e
```

The included Playwright tests stub API responses so they are self-contained. For full end-to-end runs against your backend, remove the route stubs in `e2e/login.spec.js`.

Configuration notes (production):

- The backend sets the refresh token as an HttpOnly cookie. Configure the cookie behavior in the backend `application.properties`:
  - `app.cookie.secure=true` (set to true in production when using HTTPS)
  - `app.cookie.path=/` (path scope for the cookie)
  - `app.cookie.samesite=Strict|Lax` (recommended `Lax` or `Strict`)

E2E test secret:

- The E2E seed endpoint uses `X-TEST-SECRET` header to protect the seed API. The default secret is `e2e-test-secret`. In CI, override it via `-Dapp.test.secret=your-secret` or set `APP_TEST_SECRET` in the environment.
