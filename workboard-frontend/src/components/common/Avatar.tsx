import clsx from "clsx";

const PALETTE = [
  "bg-navy-700",
  "bg-navy-500",
  "bg-indigo-500",
  "bg-sky-600",
  "bg-slate-600",
  "bg-blue-600",
];

function colorForName(name: string) {
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return PALETTE[Math.abs(hash) % PALETTE.length];
}

function initials(name: string) {
  const parts = name.trim().split(/\s+/);
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[1][0]).toUpperCase();
}

export function Avatar({
  name,
  size = "md",
  className,
}: {
  name: string;
  size?: "sm" | "md" | "lg";
  className?: string;
}) {
  const dims = { sm: "h-6 w-6 text-[10px]", md: "h-8 w-8 text-xs", lg: "h-11 w-11 text-sm" }[size];
  return (
    <div
      className={clsx(
        "flex shrink-0 items-center justify-center rounded-full font-semibold text-white",
        colorForName(name),
        dims,
        className,
      )}
      title={name}
    >
      {initials(name)}
    </div>
  );
}
