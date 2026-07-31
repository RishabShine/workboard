import clsx from "clsx";

export function Spinner({ size = "md", className }: { size?: "sm" | "md" | "lg"; className?: string }) {
  const dims = { sm: "h-4 w-4 border-2", md: "h-6 w-6 border-2", lg: "h-9 w-9 border-[3px]" }[size];
  return (
    <div
      className={clsx(
        "animate-spin rounded-full border-navy-200 border-t-navy-600",
        dims,
        className,
      )}
      role="status"
      aria-label="Loading"
    />
  );
}
