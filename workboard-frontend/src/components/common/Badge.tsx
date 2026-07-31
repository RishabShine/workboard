import clsx from "clsx";
import type { ReactNode } from "react";

export function Badge({
  children,
  color = "neutral",
  className,
}: {
  children: ReactNode;
  color?: "neutral" | "navy" | "green" | "amber" | "red";
  className?: string;
}) {
  const colors = {
    neutral: "bg-navy-50 text-navy-700",
    navy: "bg-navy-800 text-white",
    green: "bg-emerald-50 text-emerald-700",
    amber: "bg-amber-50 text-amber-700",
    red: "bg-red-50 text-red-600",
  };
  return (
    <span
      className={clsx(
        "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium",
        colors[color],
        className,
      )}
    >
      {children}
    </span>
  );
}

export function TagPill({ name, color }: { name: string; color?: string | null }) {
  return (
    <span
      className="inline-flex items-center gap-1.5 rounded-full border border-navy-100 bg-white px-2.5 py-0.5 text-xs font-medium text-navy-700"
    >
      <span
        className="h-1.5 w-1.5 rounded-full"
        style={{ backgroundColor: color || "#3d5a92" }}
      />
      {name}
    </span>
  );
}
