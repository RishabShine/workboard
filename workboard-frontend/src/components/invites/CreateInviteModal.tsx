import { useState, type FormEvent } from "react";
import { Modal } from "@/components/common/Modal";
import { FormField, TextInput } from "@/components/common/FormField";
import { Button } from "@/components/common/Button";
import { ErrorState } from "@/components/common/States";
import { Avatar } from "@/components/common/Avatar";
import { getErrorMessage } from "@/api/client";
import { userApi } from "@/api/userApi";
import { membershipApi } from "@/api/membershipApi";
import type { UserSummaryDto } from "@/types/api";

export function CreateInviteModal({
  projectId,
  onClose,
  onInvited,
}: {
  projectId: number;
  onClose: () => void;
  onInvited: () => void;
}) {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<UserSummaryDto[]>([]);
  const [selected, setSelected] = useState<UserSummaryDto | null>(null);
  const [isSearching, setIsSearching] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSearch(e: FormEvent) {
    e.preventDefault();
    if (!query.trim()) return;
    setIsSearching(true);
    setError(null);
    setSelected(null);
    try {
      const users = await userApi.searchUsers(query.trim());
      setResults(users);
    } catch (err) {
      setError(getErrorMessage(err, "Couldn't search for users."));
    } finally {
      setIsSearching(false);
    }
  }

  async function handleInvite() {
    if (!selected) return;
    setIsSubmitting(true);
    setError(null);
    try {
      await membershipApi.createInvite(projectId, { recipientUserId: selected.id });
      onInvited();
    } catch (err) {
      setError(getErrorMessage(err, "Couldn't send the invite."));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Modal title="Invite a member" onClose={onClose}>
      <div className="space-y-4">
        {error && <ErrorState message={error} />}

        <form onSubmit={handleSearch} className="flex gap-2">
          <FormField label="Search by username or email">
            <TextInput
              autoFocus
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="e.g. jane"
            />
          </FormField>
        </form>
        <Button type="button" variant="secondary" size="sm" onClick={handleSearch} isLoading={isSearching}>
          Search
        </Button>

        {results.length > 0 && (
          <ul className="max-h-56 space-y-1 overflow-y-auto">
            {results.map((u) => (
              <li key={u.id}>
                <button
                  type="button"
                  onClick={() => setSelected(u)}
                  className={`flex w-full items-center gap-3 rounded-xl px-3 py-2 text-left transition-colors ${
                    selected?.id === u.id ? "bg-navy-50 ring-1 ring-navy-300" : "hover:bg-navy-50"
                  }`}
                >
                  <Avatar name={u.username} size="sm" />
                  <div>
                    <p className="text-sm font-medium text-navy-900">{u.username}</p>
                    <p className="text-xs text-navy-400">{u.email}</p>
                  </div>
                </button>
              </li>
            ))}
          </ul>
        )}

        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="button" onClick={handleInvite} disabled={!selected} isLoading={isSubmitting}>
            Send invite
          </Button>
        </div>
      </div>
    </Modal>
  );
}
