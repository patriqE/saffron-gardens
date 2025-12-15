import axios from "axios";

const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";

const api = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
});

// module-scoped access token (kept in memory)
let accessToken = null;

export function setAccessToken(token) {
  accessToken = token;
  if (token) api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  else delete api.defaults.headers.common["Authorization"];
}

// Attach access token from in-memory store
api.interceptors.request.use((config) => {
  if (accessToken)
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${accessToken}`,
    };
  return config;
});

// On 401 try to refresh using cookie-based refresh token
api.interceptors.response.use(
  (r) => r,
  async (err) => {
    const original = err.config;
    if (err.response && err.response.status === 401 && !original._retry) {
      original._retry = true;
      try {
        const resp = await axios.post(
          API_BASE + "/api/auth/refresh",
          {},
          { withCredentials: true }
        );
        const data = resp.data || {};
        if (data.accessToken) {
          setAccessToken(data.accessToken);
          original.headers["Authorization"] = `Bearer ${data.accessToken}`;
          return api(original);
        }
      } catch (e) {
        // fall through
      }
    }
    return Promise.reject(err);
  }
);

export default api;
