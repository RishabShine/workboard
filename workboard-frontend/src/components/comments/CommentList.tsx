import { Avatar } from "@/components/common/Avatar";
import type { CommentDto } from "@/types/api";

function formatDateTime(iso: string) {
  try {
    return new Date(iso).toLocaleString(undefined, {
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  } catch {
    return iso;
  }
}

export function CommentList({ comments }: { comments: CommentDto[] }) {
  if (comments.length === 0) {
    return <p className="text-sm text-navy-400">No comments yet. Be the first to say something.</p>;
  }

  return (
    <ul className="space-y-4">
      {comments.map((comment) => (
        <li key={comment.id} className="flex gap-3">
          <Avatar name={comment.user.username} size="sm" />
          <div className="min-w-0 flex-1 rounded-xl border border-navy-50 bg-navy-50/40 px-3.5 py-2.5">
            <div className="flex items-baseline justify-between gap-2">
              <span className="text-sm font-medium text-navy-900">{comment.user.username}</span>
              <span className="text-xs text-navy-400">{formatDateTime(comment.createdAt)}</span>
            </div>
            <p className="mt-1 whitespace-pre-wrap text-sm text-navy-700">{comment.body}</p>
          </div>
        </li>
      ))}
    </ul>
  );
}
