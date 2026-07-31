import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Kanban } from "lucide-react";
import { useAuth } from "@/hooks/useAuth";
import { FormField, TextInput } from "@/components/common/FormField";
import { Button } from "@/components/common/Button";
import { ErrorState } from "@/components/common/States";
import { getErrorMessage } from "@/api/client";

export function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setIsSubmitting(true);
    setError(null);
    try {
      await register({ username, email, password });
      // The backend currently returns a plain confirmation string rather
      // than a token, so we send the person to log in with their new
      // credentials instead of assuming an auto-login response shape.
      setSuccessMessage("Account created. Redirecting to sign in...");
      setTimeout(() => navigate("/login"), 900);
    } catch (err) {
      setError(getErrorMessage(err, "Couldn't create your account."));
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
          <h1 className="text-lg font-semibold text-navy-900">Create your account</h1>
          <p className="mt-1 text-sm text-navy-400">Start tracking work with Workboard</p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="space-y-4 rounded-2xl border border-navy-100 bg-white p-6 shadow-card"
        >
          {error && <ErrorState message={error} />}
          {successMessage && (
            <p className="rounded-xl bg-emerald-50 px-3.5 py-2.5 text-sm text-emerald-700">
              {successMessage}
            </p>
          )}

          <FormField label="Username">
            <TextInput
              autoFocus
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </FormField>

          <FormField label="Email">
            <TextInput
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </FormField>

          <FormField label="Password">
            <TextInput
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </FormField>

          <Button type="submit" className="w-full" isLoading={isSubmitting}>
            Create account
          </Button>
        </form>

        <p className="mt-5 text-center text-sm text-navy-400">
          Already have an account?{" "}
          <Link to="/login" className="font-medium text-navy-700 hover:text-navy-900">
            Sign in
          </Link>
        </p>
      </div>
    </div>
  );
}
