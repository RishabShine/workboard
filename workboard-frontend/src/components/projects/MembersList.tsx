import { Avatar } from "@/components/common/Avatar";
import { Badge } from "@/components/common/Badge";
import type { MemberDto } from "@/types/api";

function formatDate(iso: string) {
  try {
    return new Date(iso).toLocaleDateString(undefined, { month: "short", day: "numeric", year: "numeric" });
  } catch {
    return iso;
  }
}

export function MembersList({ members }: { members: MemberDto[] }) {
  if (members.length === 0) {
    return <p className="text-sm text-navy-400">No members yet.</p>;
  }

  return (
    <ul className="divide-y divide-navy-50">
      {members.map((m) => (
        <li key={m.user.id} className="flex items-center justify-between py-3">
          <div className="flex items-center gap-3">
            <Avatar name={m.user.username} size="sm" />
            <div>
              <p className="text-sm font-medium text-navy-900">{m.user.username}</p>
              <p className="text-xs text-navy-400">{m.user.email ?? m.email}</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Badge>{m.role.name}</Badge>
            <span className="hidden text-xs text-navy-400 sm:inline">
              Joined {formatDate(m.joinedOn)}
            </span>
          </div>
        </li>
      ))}
    </ul>
  );
}
