import { Badge } from "@/components/common/Badge";
import { TICKET_STATUS_LABELS, type TicketStatus } from "@/types/api";

const STATUS_COLORS: Record<TicketStatus, "neutral" | "navy" | "green" | "amber"> = {
  BACKLOG: "neutral",
  READY: "amber",
  IN_PROGRESS: "navy",
  IN_REVIEW: "amber",
  COMPLETED: "green",
};

export function StatusBadge({ status }: { status: TicketStatus }) {
  return <Badge color={STATUS_COLORS[status]}>{TICKET_STATUS_LABELS[status]}</Badge>;
}
