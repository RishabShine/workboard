import { useState, type FormEvent } from "react";
import { Plus, Trash2 } from "lucide-react";
import { Modal } from "@/components/common/Modal";
import { FormField, TextInput } from "@/components/common/FormField";
import { Button } from "@/components/common/Button";
import { ErrorState } from "@/components/common/States";
import { getErrorMessage } from "@/api/client";
import { projectApi } from "@/api/projectApi";
import type { CreateMilestoneRequest, CreateTagRequest } from "@/types/api";

const TAG_COLORS = ["#3d5a92", "#2c4576", "#5f7bb0", "#8ea3cc", "#0f1a30"];

export function CreateProjectModal({
  onClose,
  onCreated,
}: {
  onClose: () => void;
  onCreated: (projectId: number) => void;
}) {
  const [name, setName] = useState("");
  const [milestones, setMilestones] = useState<CreateMilestoneRequest[]>([]);
  const [tags, setTags] = useState<CreateTagRequest[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const addMilestone = () => setMilestones((m) => [...m, { name: "" }]);
  const addTag = () =>
    setTags((t) => [...t, { name: "", color: TAG_COLORS[t.length % TAG_COLORS.length] }]);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    if (!name.trim()) return;
    setIsSubmitting(true);
    setError(null);
    try {
      const created = await projectApi.createProject({
        name: name.trim(),
        milestones: milestones.filter((m) => m.name.trim()),
        tags: tags.filter((t) => t.name.trim()),
      });
      onCreated(created.id);
    } catch (err) {
      setError(getErrorMessage(err, "Couldn't create the project."));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <Modal title="New project" onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-5">
        {error && <ErrorState message={error} />}

        <FormField label="Project name">
          <TextInput
            autoFocus
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Website Redesign"
            required
          />
        </FormField>

        <div>
          <div className="mb-2 flex items-center justify-between">
            <span className="text-sm font-medium text-navy-700">Milestones (optional)</span>
            <button
              type="button"
              onClick={addMilestone}
              className="flex items-center gap-1 text-xs font-medium text-navy-600 hover:text-navy-900"
            >
              <Plus className="h-3.5 w-3.5" /> Add
            </button>
          </div>
          <div className="space-y-2">
            {milestones.map((m, i) => (
              <div key={i} className="flex gap-2">
                <TextInput
                  value={m.name}
                  onChange={(e) =>
                    setMilestones((prev) =>
                      prev.map((p, idx) => (idx === i ? { ...p, name: e.target.value } : p)),
                    )
                  }
                  placeholder="Milestone name"
                />
                <button
                  type="button"
                  onClick={() => setMilestones((prev) => prev.filter((_, idx) => idx !== i))}
                  className="rounded-lg p-2 text-navy-300 hover:bg-red-50 hover:text-red-500"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            ))}
          </div>
        </div>

        <div>
          <div className="mb-2 flex items-center justify-between">
            <span className="text-sm font-medium text-navy-700">Tags (optional)</span>
            <button
              type="button"
              onClick={addTag}
              className="flex items-center gap-1 text-xs font-medium text-navy-600 hover:text-navy-900"
            >
              <Plus className="h-3.5 w-3.5" /> Add
            </button>
          </div>
          <div className="space-y-2">
            {tags.map((t, i) => (
              <div key={i} className="flex items-center gap-2">
                <span
                  className="h-4 w-4 shrink-0 rounded-full border border-black/5"
                  style={{ backgroundColor: t.color ?? "#3d5a92" }}
                />
                <TextInput
                  value={t.name}
                  onChange={(e) =>
                    setTags((prev) =>
                      prev.map((p, idx) => (idx === i ? { ...p, name: e.target.value } : p)),
                    )
                  }
                  placeholder="Tag name"
                />
                <button
                  type="button"
                  onClick={() => setTags((prev) => prev.filter((_, idx) => idx !== i))}
                  className="rounded-lg p-2 text-navy-300 hover:bg-red-50 hover:text-red-500"
                >
                  <Trash2 className="h-4 w-4" />
                </button>
              </div>
            ))}
          </div>
        </div>

        <div className="flex justify-end gap-2 pt-2">
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" isLoading={isSubmitting}>
            Create project
          </Button>
        </div>
      </form>
    </Modal>
  );
}
