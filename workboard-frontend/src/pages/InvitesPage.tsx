import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Mail } from "lucide-react";
import { AppLayout } from "@/components/layout/AppLayout";
import { InviteCard } from "@/components/invites/InviteCard";
import { EmptyState, ErrorState, PageSpinner } from "@/components/common/States";
import { getErrorMessage } from "@/api/client";
import { membershipApi } from "@/api/membershipApi";

export function InvitesPage() {
  const queryClient = useQueryClient();
  const [busyId, setBusyId] = useState<number | null>(null);

  const { data, isLoading, error } = useQuery({
    queryKey: ["invites"],
    queryFn: () => membershipApi.getMyInvites(),
  });

  async function handleAccept(inviteId: number) {
    setBusyId(inviteId);
    try {
      await membershipApi.acceptInvite(inviteId);
      queryClient.invalidateQueries({ queryKey: ["invites"] });
      queryClient.invalidateQueries({ queryKey: ["projects"] });
    } finally {
      setBusyId(null);
    }
  }

  async function handleReject(inviteId: number) {
    setBusyId(inviteId);
    try {
      await membershipApi.rejectInvite(inviteId);
      queryClient.invalidateQueries({ queryKey: ["invites"] });
    } finally {
      setBusyId(null);
    }
  }

  const pendingInvites = data?.filter((i) => i.status === "PENDING") ?? [];

  return (
    <AppLayout>
      <div className="mb-7">
        <h1 className="text-xl font-semibold text-navy-900">Invites</h1>
        <p className="mt-1 text-sm text-navy-400">Projects waiting for your response.</p>
      </div>

      {isLoading && <PageSpinner />}
      {error && <ErrorState message={getErrorMessage(error, "Couldn't load your invites.")} />}

      {data && pendingInvites.length === 0 && (
        <EmptyState
          icon={<Mail className="h-5 w-5" />}
          title="No pending invites"
          description="When someone invites you to a project, it'll show up here."
        />
      )}

      {pendingInvites.length > 0 && (
        <div className="space-y-3">
          {pendingInvites.map((invite) => (
            <InviteCard
              key={invite.id}
              invite={invite}
              isBusy={busyId === invite.id}
              onAccept={() => handleAccept(invite.id)}
              onReject={() => handleReject(invite.id)}
            />
          ))}
        </div>
      )}
    </AppLayout>
  );
}
