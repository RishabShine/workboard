import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Plus, FolderKanban } from "lucide-react";
import { useQuery } from "@tanstack/react-query";
import { AppLayout } from "@/components/layout/AppLayout";
import { ProjectCard } from "@/components/projects/ProjectCard";
import { CreateProjectModal } from "@/components/projects/CreateProjectModal";
import { Button } from "@/components/common/Button";
import { EmptyState, ErrorState, PageSpinner } from "@/components/common/States";
import { getErrorMessage } from "@/api/client";
import { projectApi } from "@/api/projectApi";

export function ProjectsPage() {
  const navigate = useNavigate();
  const [showCreate, setShowCreate] = useState(false);

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ["projects"],
    queryFn: () => projectApi.listMyProjects(),
  });

  return (
    <AppLayout>
      <div className="mb-7 flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-navy-900">Projects</h1>
          <p className="mt-1 text-sm text-navy-400">Everything you're a member of.</p>
        </div>
        <Button onClick={() => setShowCreate(true)}>
          <Plus className="h-4 w-4" /> New project
        </Button>
      </div>

      {isLoading && <PageSpinner />}
      {error && <ErrorState message={getErrorMessage(error, "Couldn't load your projects.")} />}

      {data && data.projects.length === 0 && (
        <EmptyState
          icon={<FolderKanban className="h-5 w-5" />}
          title="No projects yet"
          description="Create your first project to start tracking tickets and inviting teammates."
          action={
            <Button size="sm" onClick={() => setShowCreate(true)}>
              <Plus className="h-4 w-4" /> New project
            </Button>
          }
        />
      )}

      {data && data.projects.length > 0 && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {data.projects.map((project) => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      )}

      {showCreate && (
        <CreateProjectModal
          onClose={() => setShowCreate(false)}
          onCreated={(projectId) => {
            setShowCreate(false);
            refetch();
            navigate(`/projects/${projectId}`);
          }}
        />
      )}
    </AppLayout>
  );
}
