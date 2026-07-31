import { Link } from "react-router-dom";
import { MessageSquare, Milestone } from "lucide-react";
import { Avatar } from "@/components/common/Avatar";
import { TagPill } from "@/components/common/Badge";
import type { TicketListItemDto } from "@/types/api";

export function TicketCard({
  projectId,
  ticket,
}: {
  projectId: number;
  ticket: TicketListItemDto;
}) {
  return (
    <Link
      to={`/projects/${projectId}/tickets/${ticket.id}`}
      className="block rounded-xl border border-navy-100 bg-white p-3.5 shadow-soft transition-all hover:-translate-y-0.5 hover:shadow-card"
    >
      <p className="text-sm font-medium leading-snug text-navy-900">{ticket.title}</p>

      {ticket.tags.length > 0 && (
        <div className="mt-2.5 flex flex-wrap gap-1.5">
          {ticket.tags.map((tag) => (
            <TagPill key={tag.id} name={tag.name} color={tag.color} />
          ))}
        </div>
      )}

      <div className="mt-3 flex items-center justify-between">
        <div className="flex items-center gap-3 text-navy-400">
          {ticket.milestone && (
            <span className="flex items-center gap-1 text-xs">
              <Milestone className="h-3.5 w-3.5" />
              {ticket.milestone.name}
            </span>
          )}
          <span className="flex items-center gap-1 text-xs">
            <MessageSquare className="h-3.5 w-3.5" />
            {ticket.numComments}
          </span>
        </div>
        {ticket.assignee && <Avatar name={ticket.assignee.username} size="sm" />}
      </div>
    </Link>
  );
}
