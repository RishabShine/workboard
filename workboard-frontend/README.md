# Workboard Frontend

React + TypeScript + Vite frontend for the Workboard Spring Boot backend.

## Setup

```bash
npm install
npm run dev
```

The app expects the backend at the URL in `.env` (`VITE_API_BASE_URL`), defaulting to `http://localhost:8080`.

## Stack

- React + TypeScript + Vite
- React Router for routing
- TanStack Query for data fetching/mutations/refetching
- Axios (centralized client in `src/api/client.ts`) with JWT attach + global 401 handling
- Tailwind CSS, navy/white theme

## Known backend quirks this frontend was built around

These were confirmed against the actual controller source, not the original spec doc, so a few things differ from a "clean" Jira clone:

- **No `PATCH /projects/{projectId}` endpoint exists yet.** `projectApi.updateProject` is wired up to the conventional path in case it's added later, but there's currently no UI entry point for editing a project's name — add one once the endpoint exists.
- **No member role update endpoint exists yet** (`UpdateMemberRequest` DTO exists but isn't wired to a controller method). No UI was built for changing a member's role — add it once the endpoint ships.
- **`GET /users/me` returns HTTP 201** instead of 200 (likely a copy-paste bug in the controller). Axios doesn't care either way, so this doesn't affect the frontend, but worth fixing on the backend for correctness.
- **`POST /auth/register` returns a plain string**, not a DTO or token. The frontend currently redirects to `/login` after registering rather than auto-logging in. Revisit this once the backend's intended response shape is finalized.
- **Comment DTOs use `replyTo`**, not `replyToId`.
- **Ticket list responses are wrapped**: `GET /tickets/{projectId}/tickets` returns `{ tickets: [...] }` (a `TicketPageDto`), not a bare array.
- **`ProjectDetailDto` has no `members` field.** Project members are fetched separately from `GET /membership/{projectId}`.
- **`InviteRequest` DTO is unused/dead code** — invite creation uses `CreateProjectInviteRequest` (`recipientUserId`) instead.
- **CORS**: if requests fail with a network/CORS error in the browser console, the Spring Boot backend needs to allow the frontend's origin (`http://localhost:5173` by default). Not yet confirmed as configured.

## Folder structure

```
src/
  api/          - one file per backend resource, all funnel through client.ts
  components/   - layout, common (buttons/badges/modals/etc), and per-feature components
  contexts/     - AuthContext (token storage, current user, login/register/logout)
  hooks/        - useAuth
  pages/        - one component per route
  routes/       - AppRouter + ProtectedRoute
  types/        - all DTO/request TypeScript types, matching the real backend source
```
