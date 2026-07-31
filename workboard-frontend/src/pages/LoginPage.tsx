import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { Kanban } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { FormField, TextInput } from "@/components/common/FormField";
import { Button } from "@/components/common/Button";
import { ErrorState } from "@/components/common/States";
import { getErrorMessage } from "@/api/client";

export function LoginPage() {
  const { login } = useAuth();
  const [usernameOrEmail, setUsernameOrEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    try {
      await login({ usernameOrEmail, password });
    } catch (err) {
      setError(getErrorMessage(err, "Invalid username/email or password."));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-[#F7F8FB] px-4">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex flex-col items-center">
          <div className="mb-3 flex h-11 w-11 items-center justify-center rounded-2xl bg-navy-800 text-white shadow-soft">
            <Kanban className="h-5 w-5" />
          </div>
          <h1 className="text-lg font-semibold text-navy-900">Welcome back</h1>
          <p className="mt-1 text-sm text-navy-400">Sign in to Workboard to continue</p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="space-y-4 rounded-2xl border border-navy-100 bg-white p-6 shadow-card"
        >
          {error && <ErrorState message={error} />}

          <FormField label="Username or email">
            <TextInput
              autoFocus
              value={usernameOrEmail}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
              placeholder="you@example.com"
              required
            />
          </FormField>

          <FormField label="Password">
            <TextInput
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              required
            />
          </FormField>

          <Button type="submit" className="w-full" isLoading={isSubmitting}>
            Sign in
          </Button>
        </form>

        <p className="mt-5 text-center text-sm text-navy-400">
          Don&apos;t have an account?{" "}
          <Link to="/register" className="font-medium text-navy-700 hover:text-navy-900">
            Create one
          </Link>
        </p>
      </div>
    </div>
  );
}
