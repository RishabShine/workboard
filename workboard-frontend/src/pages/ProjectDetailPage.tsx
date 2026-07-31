import { useState } from "react";
import { useParams } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Plus, Users, UserPlus } from "lucide-react";
import { AppLayout } from "@/components/layout/AppLayout";
import { Button } from "@/components/common/Button";
import { Badge } from "@/components/common/Badge";
import { ErrorState, PageSpinner } from "@/components/common/States";
import { TicketBoard } from "@/components/tickets/TicketBoard";
import { TicketFormModal } from "@/components/tickets/TicketFormModal";
import { MembersList } from "@/components/projects/MembersList";
import { CreateInviteModal } from "@/components/invites/CreateInviteModal";
import { getErrorMessage } from "@/api/client";
import { projectApi } from "@/api/projectApi";
import { ticketApi } from "@/api/ticketApi";
import { membershipApi } from "@/api/membershipApi";

export function ProjectDetailPage() {
  const { projectId } = useParams<{ projectId: string }>();
  const id = Number(projectId);
  const queryClient = useQueryClient();

  const [showCreateTicket, setShowCreateTicket] = useState(false);
  const [showInvite, setShowInvite] = useState(false);

  const projectQuery = useQuery({
    queryKey: ["project", id],
    queryFn: () => projectApi.getProject(id),
    enabled: !!id,
  });

  const ticketsQuery = useQuery({
    queryKey: ["tickets", id],
    queryFn: () => ticketApi.getTicketsForProject(id),
    enabled: !!id,
  });

  const membersQuery = useQuery({
    queryKey: ["members", id],
    queryFn: () => membershipApi.getMembers(id),
    enabled: !!id,
  });

  function invalidateTickets() {
    queryClient.invalidateQueries({ queryKey: ["tickets", id] });
  }

  if (projectQuery.isLoading) {
    return (
      <AppLayout>
        <PageSpinner />
      </AppLayout>
    );
  }

  if (projectQuery.error || !projectQuery.data) {
    return (
      <AppLayout>
        <ErrorState message={getErrorMessage(projectQuery.error, "Couldn't load this project.")} />
      </AppLayout>
    );
  }

  const project = projectQuery.data;

  return (
    <AppLayout>
      <div className="mb-6 flex items-start justify-between">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-xl font-semibold text-navy-900">{project.name}</h1>
            {project.userRole && <Badge color="navy">{project.userRole.name}</Badge>}
          </div>
          {project.milestones.length > 0 && (
            <div className="mt-2 flex flex-wrap gap-1.5">
              {project.milestones.map((m) => (
                <Badge key={m.id}>{m.name}</Badge>
              ))}
            </div>
          )}
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" onClick={() => setShowInvite(true)}>
            <UserPlus className="h-4 w-4" /> Invite
          </Button>
          <Button onClick={() => setShowCreateTicket(true)}>
            <Plus className="h-4 w-4" /> New ticket
          </Button>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_280px]">
        <div>
          {ticketsQuery.isLoading && <PageSpinner />}
          {ticketsQuery.error && (
            <ErrorState message={getErrorMessage(ticketsQuery.error, "Couldn't load tickets.")} />
          )}
          {ticketsQuery.data && <TicketBoard projectId={id} tickets={ticketsQuery.data} />}
        </div>

        <div className="rounded-2xl border border-navy-100 bg-white p-5 shadow-soft">
          <div className="mb-3 flex items-center gap-2 text-navy-800">
            <Users className="h-4 w-4" />
            <h2 className="text-sm font-semibold">Members</h2>
          </div>
          {membersQuery.isLoading && <PageSpinner />}
          {membersQuery.error && (
            <ErrorState message={getErrorMessage(membersQuery.error, "Couldn't load members.")} />
          )}
          {membersQuery.data && <MembersList members={membersQuery.data} />}
        </div>
      </div>

      {showCreateTicket && (
        <TicketFormModal
          projectId={id}
          members={membersQuery.data ?? []}
          milestones={project.milestones}
          tags={project.tags}
          onClose={() => setShowCreateTicket(false)}
          onSaved={() => {
            setShowCreateTicket(false);
            invalidateTickets();
          }}
        />
      )}

      {showInvite && (
        <CreateInviteModal
          projectId={id}
          onClose={() => setShowInvite(false)}
          onInvited={() => setShowInvite(false)}
        />
      )}
    </AppLayout>
  );
}
