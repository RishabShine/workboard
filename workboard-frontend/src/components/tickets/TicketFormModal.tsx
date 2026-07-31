import { useState, type FormEvent } from "react";
import { Modal } from "@/components/common/Modal";
import { FormField, Select, TextArea, TextInput } from "@/components/common/FormField";
import { Button } from "@/components/common/Button";
import { ErrorState } from "@/components/common/States";
import { getErrorMessage } from "@/api/client";
import { ticketApi } from "@/api/ticketApi";
import {
  TICKET_STATUSES,
  TICKET_STATUS_LABELS,
  type MemberDto,
  type MilestoneDto,
  type TagDto,
  type TicketDetailDto,
  type TicketStatus,
} from "@/types/api";

interface TicketFormModalProps {
  projectId: number;
  members: MemberDto[];
  milestones: MilestoneDto[];
  tags: TagDto[];
  existingTicket?: TicketDetailDto;
  onClose: () => void;
  onSaved: (ticket: TicketDetailDto) => void;
}

export function TicketFormModal({
  projectId,
  members,
  milestones,
  tags,
  existingTicket,
  onClose,
  onSaved,
}: TicketFormModalProps) {
  const isEditing = !!existingTicket;

  const [title, setTitle] = useState(existingTicket?.title ?? "");
  const [body, setBody] = useState(existingTicket?.body ?? "");
  const [status, setStatus] = useState<TicketStatus>(existingTicket?.status ?? "BACKLOG");
  const [assignedToUserId, setAssignedToUserId] = useState<string>(
    existingTicket?.assignee ? String(existingTicket.assignee.id) : "",
  );
  const [milestoneId, setMilestoneId] = useState<string>(
    existingTicket?.milestone ? String(existingTicket.milestone.id) : "",
  );
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>(
    existingTicket?.tags.map((t) => t.id) ?? [],
  );
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  function toggleTag(id: number) {
    setSelectedTagIds((prev) => (prev.includes(id) ? prev.filter((t) => t !== id) : [...prev, id]));
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!title.trim()) return;
    setIsSubmitting(true);
    setError(null);
    try {
      const payload = {
        title: title.trim(),
        body,
        status,
        assignedToUserId: assignedToUserId ? Number(assignedToUserId) : null,
        milestoneId: milestoneId ? Number(milestoneId) : null,
        tagIds: selectedTagIds,
      };
      const saved = isEditing
        ? await ticketApi.updateTicket(existingTicket!.id, payload)
        : await ticketApi.createTicket(projectId, payload);
      onSaved(saved);
    } catch (err) {
      setError(getErrorMessage(err, "Couldn't save the ticket."));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Modal title={isEditing ? "Edit ticket" : "New ticket"} onClose={onClose} widthClassName="max-w-xl">
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && <ErrorState message={error} />}

        <FormField label="Title">
          <TextInput
            autoFocus
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="Short, descriptive title"
            required
          />
        </FormField>

        <FormField label="Description">
          <TextArea
            value={body}
            onChange={(e) => setBody(e.target.value)}
            placeholder="What needs to be done?"
          />
        </FormField>

        <div className="grid grid-cols-2 gap-4">
          <FormField label="Status">
            <Select value={status} onChange={(e) => setStatus(e.target.value as TicketStatus)}>
              {TICKET_STATUSES.map((s) => (
                <option key={s} value={s}>
                  {TICKET_STATUS_LABELS[s]}
                </option>
              ))}
            </Select>
          </FormField>

          <FormField label="Assignee">
            <Select value={assignedToUserId} onChange={(e) => setAssignedToUserId(e.target.value)}>
              <option value="">Unassigned</option>
              {members.map((m) => (
                <option key={m.user.id} value={m.user.id}>
                  {m.user.username}
                </option>
              ))}
            </Select>
          </FormField>
        </div>

        <FormField label="Milestone">
          <Select value={milestoneId} onChange={(e) => setMilestoneId(e.target.value)}>
            <option value="">No milestone</option>
            {milestones.map((m) => (
              <option key={m.id} value={m.id}>
                {m.name}
              </option>
            ))}
          </Select>
        </FormField>

        {tags.length > 0 && (
          <div>
            <span className="mb-1.5 block text-sm font-medium text-navy-700">Tags</span>
            <div className="flex flex-wrap gap-2">
              {tags.map((tag) => {
                const selected = selectedTagIds.includes(tag.id);
                return (
                  <button
                    type="button"
                    key={tag.id}
                    onClick={() => toggleTag(tag.id)}
                    className={`rounded-full border px-3 py-1 text-xs font-medium transition-colors ${
                      selected
                        ? "border-navy-800 bg-navy-800 text-white"
                        : "border-navy-100 bg-white text-navy-600 hover:bg-navy-50"
                    }`}
                  >
                    {tag.name}
                  </button>
                );
              })}
            </div>
          </div>
        )}

        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSubmitting}>
            {isEditing ? "Save changes" : "Create ticket"}
          </Button>
        </div>
      </form>
    </Modal>
  );
}
