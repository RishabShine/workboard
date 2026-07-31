import { Check, X } from "lucide-react";
import { Button } from "@/components/common/Button";
import type { ProjectInviteDto } from "@/types/api";

function formatDate(iso: string) {
  try {
    return new Date(iso).toLocaleDateString(undefined, { month: "short", day: "numeric", year: "numeric" });
  } catch {
    return iso;
  }
}

export function InviteCard({
  invite,
  onAccept,
  onReject,
  isBusy,
}: {
  invite: ProjectInviteDto;
  onAccept: () => void;
  onReject: () => void;
  isBusy: boolean;
}) {
  return (
    <div className="flex items-center justify-between rounded-2xl border border-navy-100 bg-white p-5 shadow-soft">
      <div>
        <p className="text-sm font-semibold text-navy-900">{invite.projectName}</p>
        <p className="mt-0.5 text-xs text-navy-400">
          Invited by {invite.invitedBy.username} &middot; {formatDate(invite.createdAt)}
        </p>
      </div>
      <div className="flex gap-2">
        <Button variant="secondary" size="sm" onClick={onReject} disabled={isBusy}>
          <X className="h-3.5 w-3.5" /> Decline
        </Button>
        <Button size="sm" onClick={onAccept} disabled={isBusy}>
          <Check className="h-3.5 w-3.5" /> Accept
        </Button>
      </div>
    </div>
  );
}
