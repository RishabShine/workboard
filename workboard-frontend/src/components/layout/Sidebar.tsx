import { NavLink } from "react-router-dom";
import { LayoutGrid, Mail, LogOut, Kanban } from "lucide-react";
import clsx from "clsx";
import { useAuth } from "@/hooks/useAuth";
import { Avatar } from "@/components/common/Avatar";

const navItems = [
  { to: "/projects", label: "Projects", icon: LayoutGrid },
  { to: "/invites", label: "Invites", icon: Mail },
];

export function Sidebar() {
  const { user, logout } = useAuth();

  return (
    <aside className="flex h-screen w-60 shrink-0 flex-col border-r border-navy-100 bg-white">
      <div className="flex items-center gap-2 px-5 py-5">
        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-navy-800 text-white">
          <Kanban className="h-4 w-4" />
        </div>
        <span className="text-[15px] font-semibold tracking-tight text-navy-900">Workboard</span>
      </div>

      <nav className="flex-1 space-y-1 px-3">
        {navItems.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              clsx(
                "flex items-center gap-2.5 rounded-xl px-3 py-2 text-sm font-medium transition-colors",
                isActive
                  ? "bg-navy-800 text-white"
                  : "text-navy-500 hover:bg-navy-50 hover:text-navy-800",
              )
            }
          >
            <Icon className="h-4 w-4" />
            {label}
          </NavLink>
        ))}
      </nav>

      <div className="border-t border-navy-50 p-3">
        <div className="flex items-center gap-2.5 rounded-xl px-2 py-2">
          <Avatar name={user?.username ?? "?"} size="sm" />
          <div className="min-w-0 flex-1">
            <p className="truncate text-sm font-medium text-navy-800">{user?.username}</p>
            <p className="truncate text-xs text-navy-400">{user?.email}</p>
          </div>
          <button
            onClick={logout}
            className="rounded-lg p-1.5 text-navy-400 hover:bg-navy-50 hover:text-navy-700"
            title="Log out"
          >
            <LogOut className="h-4 w-4" />
          </button>
        </div>
      </div>
    </aside>
  );
}
