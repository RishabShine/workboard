import { TICKET_STATUSES, TICKET_STATUS_LABELS, type TicketListItemDto } from "@/types/api";
import { TicketCard } from "./TicketCard";

export function TicketBoard({
  projectId,
  tickets,
}: {
  projectId: number;
  tickets: TicketListItemDto[];
}) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-5">
      {TICKET_STATUSES.map((status) => {
        const columnTickets = tickets.filter((t) => t.status === status);
        return (
          <div key={status} className="min-w-0">
            <div className="mb-3 flex items-center justify-between px-1">
              <h3 className="text-xs font-semibold uppercase tracking-wide text-navy-400">
                {TICKET_STATUS_LABELS[status]}
              </h3>
              <span className="text-xs font-medium text-navy-300">{columnTickets.length}</span>
            </div>
            <div className="space-y-2.5">
              {columnTickets.map((ticket) => (
                <TicketCard key={ticket.id} projectId={projectId} ticket={ticket} />
              ))}
              {columnTickets.length === 0 && (
                <div className="rounded-xl border border-dashed border-navy-100 px-3 py-6 text-center text-xs text-navy-300">
                  No tickets
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}
