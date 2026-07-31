import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { ArrowLeft, Milestone, Pencil } from "lucide-react";
import { AppLayout } from "@/components/layout/AppLayout";
import { Button } from "@/components/common/Button";
import { StatusBadge } from "@/components/tickets/StatusBadge";
import { TagPill } from "@/components/common/Badge";
import { Avatar } from "@/components/common/Avatar";
import { ErrorState, PageSpinner } from "@/components/common/States";
import { CommentList } from "@/components/comments/CommentList";
import { CommentForm } from "@/components/comments/CommentForm";
import { TicketFormModal } from "@/components/tickets/TicketFormModal";
import { getErrorMessage } from "@/api/client";
import { ticketApi } from "@/api/ticketApi";
import { commentApi } from "@/api/commentApi";
import { projectApi } from "@/api/projectApi";
import { membershipApi } from "@/api/membershipApi";

function formatDateTime(iso: string) {
  try {
    return new Date(iso).toLocaleString(undefined, {
      month: "short",
      day: "numeric",
      year: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  } catch {
    return iso;
  }
}

export function TicketDetailPage() {
  const { projectId, ticketId } = useParams<{ projectId: string; ticketId: string }>();
  const pId = Number(projectId);
  const tId = Number(ticketId);
  const queryClient = useQueryClient();
  const [showEdit, setShowEdit] = useState(false);

  const ticketQuery = useQuery({
    queryKey: ["ticket", tId],
    queryFn: () => ticketApi.getTicket(tId),
    enabled: !!tId,
  });

  const commentsQuery = useQuery({
    queryKey: ["comments", tId],
    queryFn: () => commentApi.getComments(tId),
    enabled: !!tId,
  });

  const projectQuery = useQuery({
    queryKey: ["project", pId],
    queryFn: () => projectApi.getProject(pId),
    enabled: !!pId,
  });

  const membersQuery = useQuery({
    queryKey: ["members", pId],
    queryFn: () => membershipApi.getMembers(pId),
    enabled: !!pId,
  });

  function refetchTicketData() {
    queryClient.invalidateQueries({ queryKey: ["ticket", tId] });
    queryClient.invalidateQueries({ queryKey: ["tickets", pId] });
  }

  if (ticketQuery.isLoading) {
    return (
      <AppLayout>
        <PageSpinner />
      </AppLayout>
    );
  }

  if (ticketQuery.error || !ticketQuery.data) {
    return (
      <AppLayout>
        <ErrorState message={getErrorMessage(ticketQuery.error, "Couldn't load this ticket.")} />
      </AppLayout>
    );
  }

  const ticket = ticketQuery.data;

  return (
    <AppLayout>
      <Link
        to={`/projects/${pId}`}
        className="mb-5 inline-flex items-center gap-1.5 text-sm font-medium text-navy-500 hover:text-navy-800"
      >
        <ArrowLeft className="h-4 w-4" /> Back to project
      </Link>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_260px]">
        <div className="rounded-2xl border border-navy-100 bg-white p-6 shadow-soft">
          <div className="mb-3 flex items-start justify-between gap-4">
            <h1 className="text-lg font-semibold text-navy-900">{ticket.title}</h1>
            <Button variant="secondary" size="sm" onClick={() => setShowEdit(true)}>
              <Pencil className="h-3.5 w-3.5" /> Edit
            </Button>
          </div>

          <div className="mb-4 flex flex-wrap items-center gap-2">
            <StatusBadge status={ticket.status} />
            {ticket.tags.map((tag) => (
              <TagPill key={tag.id} name={tag.name} color={tag.color} />
            ))}
          </div>

          <p className="whitespace-pre-wrap text-sm leading-relaxed text-navy-700">
            {ticket.body || <span className="text-navy-300">No description provided.</span>}
          </p>

          <div className="mt-8 border-t border-navy-50 pt-6">
            <h2 className="mb-3 text-sm font-semibold text-navy-900">
              Comments ({ticket.numComments})
            </h2>
            {commentsQuery.isLoading && <PageSpinner />}
            {commentsQuery.error && (
              <ErrorState message={getErrorMessage(commentsQuery.error, "Couldn't load comments.")} />
            )}
            {commentsQuery.data && <CommentList comments={commentsQuery.data} />}
            <div className="mt-4">
              <CommentForm
                ticketId={tId}
                onAdded={() => {
                  queryClient.invalidateQueries({ queryKey: ["comments", tId] });
                  refetchTicketData();
                }}
              />
            </div>
          </div>
        </div>

        <div className="space-y-4">
          <div className="rounded-2xl border border-navy-100 bg-white p-5 shadow-soft">
            <h3 className="mb-3 text-xs font-semibold uppercase tracking-wide text-navy-400">
              Details
            </h3>
            <dl className="space-y-3 text-sm">
              <div>
                <dt className="text-navy-400">Assignee</dt>
                <dd className="mt-1 flex items-center gap-2 text-navy-800">
                  {ticket.assignee ? (
                    <>
                      <Avatar name={ticket.assignee.username} size="sm" />
                      {ticket.assignee.username}
                    </>
                  ) : (
                    <span className="text-navy-300">Unassigned</span>
                  )}
                </dd>
              </div>
              <div>
                <dt className="text-navy-400">Milestone</dt>
                <dd className="mt-1 flex items-center gap-1.5 text-navy-800">
                  {ticket.milestone ? (
                    <>
                      <Milestone className="h-3.5 w-3.5 text-navy-400" />
                      {ticket.milestone.name}
                    </>
                  ) : (
                    <span className="text-navy-300">None</span>
                  )}
                </dd>
              </div>
              <div>
                <dt className="text-navy-400">Created by</dt>
                <dd className="mt-1 flex items-center gap-2 text-navy-800">
                  <Avatar name={ticket.createdBy.username} size="sm" />
                  {ticket.createdBy.username}
                </dd>
              </div>
              <div>
                <dt className="text-navy-400">Created</dt>
                <dd className="mt-1 text-navy-800">{formatDateTime(ticket.createdAt)}</dd>
              </div>
            </dl>
          </div>
        </div>
      </div>

      {showEdit && projectQuery.data && (
        <TicketFormModal
          projectId={pId}
          members={membersQuery.data ?? []}
          milestones={projectQuery.data.milestones}
          tags={projectQuery.data.tags}
          existingTicket={ticket}
          onClose={() => setShowEdit(false)}
          onSaved={() => {
            setShowEdit(false);
            refetchTicketData();
          }}
        />
      )}
    </AppLayout>
  );
}
