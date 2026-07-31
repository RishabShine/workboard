import clsx from "clsx";
import { forwardRef, type ButtonHTMLAttributes } from "react";
import { Spinner } from "./Spinner";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "ghost" | "danger";
  size?: "sm" | "md";
  isLoading?: boolean;
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ variant = "primary", size = "md", isLoading, className, children, disabled, ...props }, ref) => {
    const base =
      "inline-flex items-center justify-center gap-2 rounded-xl font-medium transition-colors focus-visible:outline-none disabled:opacity-50 disabled:pointer-events-none";
    const variants = {
      primary: "bg-navy-800 text-white hover:bg-navy-900 shadow-soft",
      secondary: "bg-white text-navy-800 border border-navy-100 hover:bg-navy-50",
      ghost: "bg-transparent text-navy-700 hover:bg-navy-50",
      danger: "bg-white text-red-600 border border-red-200 hover:bg-red-50",
    };
    const sizes = {
      sm: "px-3 py-1.5 text-sm",
      md: "px-4 py-2.5 text-sm",
    };

    return (
      <button
        ref={ref}
        disabled={disabled || isLoading}
        className={clsx(base, variants[variant], sizes[size], className)}
        {...props}
      >
        {isLoading && <Spinner size="sm" className={variant === "primary" ? "border-white/30 border-t-white" : ""} />}
        {children}
      </button>
    );
  },
);
Button.displayName = "Button";
