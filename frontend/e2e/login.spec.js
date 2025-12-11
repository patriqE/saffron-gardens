const { test, expect } = require("@playwright/test");

test("login and access protected admin (real backend)", async ({
  page,
  request,
}) => {
  const backend = "http://localhost:8080";
  // seed test admin via backend test endpoint
  const seed = await request.post(backend + "/api/test/seed-admin", {
    data: { username: "e2e-admin", password: "password" },
    headers: { "X-TEST-SECRET": "e2e-test-secret" },
  });
  expect(seed.ok()).toBeTruthy();

  await page.goto("/");
  await page.click("text=Admin");
  await expect(page).toHaveURL(/\/login/);
  await page.fill('input[type="text"]', "e2e-admin");
  await page.fill('input[type="password"]', "password");
  await page.click("text=Login");
  // wait for navigation to admin dashboard
  await page.waitForURL("**/admin");
  await expect(page.locator("text=Admin Dashboard")).toBeVisible();
});
