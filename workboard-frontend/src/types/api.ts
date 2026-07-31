// ---------------------------------------------------------------------------
// Enums
// ---------------------------------------------------------------------------

export type TicketStatus =
  | "BACKLOG"
  | "READY"
  | "IN_PROGRESS"
  | "IN_REVIEW"
  | "COMPLETED";

export const TICKET_STATUSES: TicketStatus[] = [
  "BACKLOG",
  "READY",
  "IN_PROGRESS",
  "IN_REVIEW",
  "COMPLETED",
];

export const TICKET_STATUS_LABELS: Record<TicketStatus, string> = {
  BACKLOG: "Backlog",
  READY: "Ready",
  IN_PROGRESS: "In progress",
  IN_REVIEW: "In review",
  COMPLETED: "Completed",
};

export type InviteStatus = "PENDING" | "ACCEPTED" | "DECLINED" | "EXPIRED";

// ---------------------------------------------------------------------------
// Common DTOs
// ---------------------------------------------------------------------------

export interface UserSummaryDto {
  id: number;
  username: string;
  email: string | null;
  profileImageKey: string | null;
}

export interface RoleDto {
  id: number;
  name: string;
}

export interface TagDto {
  id: number;
  name: string;
  color: string | null;
}

export interface MilestoneDto {
  id: number;
  name: string;
}

// ---------------------------------------------------------------------------
// Auth DTOs
// ---------------------------------------------------------------------------

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

// ---------------------------------------------------------------------------
// Project DTOs
// ---------------------------------------------------------------------------

export interface ProjectOverviewDto {
  id: number;
  name: string;
  createdAt: string;
}

export interface ProjectListDto {
  projects: ProjectOverviewDto[];
}

export interface ProjectDetailDto {
  id: number;
  name: string;
  milestones: MilestoneDto[];
  tags: TagDto[];
  userRole: RoleDto | null;
}

export interface MemberDto {
  user: UserSummaryDto;
  role: RoleDto;
  email: string | null;
  joinedOn: string;
}

export interface ProjectInviteDto {
  id: number;
  projectId: number;
  projectName: string;
  recipient: UserSummaryDto;
  invitedBy: UserSummaryDto;
  status: InviteStatus;
  createdAt: string;
}

export interface CreateMilestoneRequest {
  projectId?: number;
  name: string;
}

export interface UpdateMilestoneRequest {
  projectId?: number;
  id: number;
  name: string;
}

export interface CreateTagRequest {
  name: string;
  color?: string | null;
}

export interface CreateProjectRequest {
  name: string;
  milestones: CreateMilestoneRequest[];
  tags: CreateTagRequest[];
}

export interface UpdateProjectRequest {
  name: string;
}

export interface CreateRoleRequest {
  name: string;
}

export interface UpdateRoleRequest {
  name: string;
}

export interface CreateProjectInviteRequest {
  recipientUserId: number;
}

// ---------------------------------------------------------------------------
// Ticket DTOs
// ---------------------------------------------------------------------------

export interface TicketListItemDto {
  id: number;
  title: string;
  status: TicketStatus;
  assignee: UserSummaryDto | null;
  milestone: MilestoneDto | null;
  numComments: number;
  tags: TagDto[];
}

export interface TicketPageDto {
  tickets: TicketListItemDto[];
}

export interface TicketDetailDto {
  id: number;
  title: string;
  body: string;
  status: TicketStatus;
  assignee: UserSummaryDto | null;
  milestone: MilestoneDto | null;
  numComments: number;
  tags: TagDto[];
  createdAt: string;
  createdBy: UserSummaryDto;
}

export interface CreateTicketRequest {
  title: string;
  body: string;
  status: TicketStatus;
  assignedToUserId: number | null;
  milestoneId: number | null;
  tagIds: number[];
}

export interface UpdateTicketRequest {
  title?: string;
  body?: string;
  status?: TicketStatus;
  assignedToUserId?: number | null;
  milestoneId?: number | null;
  tagIds?: number[];
}

// ---------------------------------------------------------------------------
// Comment DTOs
// ---------------------------------------------------------------------------

export interface CommentDto {
  id: number;
  body: string;
  createdAt: string;
  user: UserSummaryDto;
  replyTo: number | null;
}

export interface CreateCommentRequest {
  body: string;
  replyTo: number | null;
}

// ---------------------------------------------------------------------------
// User DTOs
// ---------------------------------------------------------------------------

export interface UpdateUserRequest {
  bio?: string;
  username?: string;
  email?: string;
}

// ---------------------------------------------------------------------------
// API error shape
// ---------------------------------------------------------------------------

export interface ApiErrorBody {
  error: string;
  message: string;
}
