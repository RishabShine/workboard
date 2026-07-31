import { useState, type FormEvent } from "react";
import { Send } from "lucide-react";
import { TextArea } from "@/components/common/FormField";
import { Button } from "@/components/common/Button";
import { getErrorMessage } from "@/api/client";
import { commentApi } from "@/api/commentApi";
import type { CommentDto } from "@/types/api";

export function CommentForm({
  ticketId,
  onAdded,
}: {
  ticketId: number;
  onAdded: (comment: CommentDto) => void;
}) {
  const [body, setBody] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!body.trim()) return;
    setIsSubmitting(true);
    setError(null);
    try {
      const comment = await commentApi.addComment(ticketId, { body: body.trim(), replyTo: null });
      onAdded(comment);
      setBody("");
    } catch (err) {
      setError(getErrorMessage(err, "Couldn't post the comment."));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-2">
      <TextArea
        value={body}
        onChange={(e) => setBody(e.target.value)}
        placeholder="Add a comment..."
        rows={3}
      />
      {error && <p className="text-sm text-red-600">{error}</p>}
      <div className="flex justify-end">
        <Button type="submit" size="sm" isLoading={isSubmitting}>
          <Send className="h-3.5 w-3.5" /> Comment
        </Button>
      </div>
    </form>
  );
}
