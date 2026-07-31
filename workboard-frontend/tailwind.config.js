/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        navy: {
          50: "#eef1f8",
          100: "#dce3f1",
          200: "#b6c4e0",
          300: "#8ea3cc",
          400: "#5f7bb0",
          500: "#3d5a92",
          600: "#2c4576",
          700: "#20335c",
          800: "#172542",
          900: "#0f1a30",
          950: "#0a1220",
        },
      },
      fontFamily: {
        sans: [
          "Inter",
          "-apple-system",
          "BlinkMacSystemFont",
          "Segoe UI",
          "sans-serif",
        ],
      },
      boxShadow: {
        soft: "0 1px 2px rgba(15, 26, 48, 0.06), 0 4px 12px rgba(15, 26, 48, 0.05)",
        card: "0 1px 3px rgba(15, 26, 48, 0.07), 0 8px 24px rgba(15, 26, 48, 0.06)",
      },
      borderRadius: {
        xl: "0.875rem",
        "2xl": "1.25rem",
      },
    },
  },
  plugins: [],
};
