import { Link } from "react-router-dom";
import { ArrowUpRight } from "lucide-react";
import type { ProjectOverviewDto } from "@/types/api";

function formatDate(iso: string) {
  try {
    return new Date(iso).toLocaleDateString(undefined, {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  } catch {
    return iso;
  }
}

export function ProjectCard({ project }: { project: ProjectOverviewDto }) {
  return (
    <Link
      to={`/projects/${project.id}`}
      className="group flex flex-col justify-between rounded-2xl border border-navy-100 bg-white p-5 shadow-soft transition-all hover:-translate-y-0.5 hover:shadow-card"
    >
      <div>
        <div className="mb-3 flex items-start justify-between">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-navy-50 text-sm font-bold text-navy-700">
            {project.name.slice(0, 1).toUpperCase()}
          </div>
          <ArrowUpRight className="h-4 w-4 text-navy-300 transition-colors group-hover:text-navy-600" />
        </div>
        <h3 className="text-[15px] font-semibold text-navy-900">{project.name}</h3>
      </div>
      <p className="mt-4 text-xs text-navy-400">Created {formatDate(project.createdAt)}</p>
    </Link>
  );
}
