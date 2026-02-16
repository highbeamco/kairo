import starlight from "@astrojs/starlight";
import type { AstroUserConfig } from "astro";
import { defineConfig } from "astro/config";

import moduleSidebar from "./src/generated-sidebar.json";

const config: AstroUserConfig = defineConfig({
  site: "https://kairo.airborne.software",
  integrations: [
    starlight({
      title: "Kairo",
      description:
        "Your one-stop toolkit for production-ready Kotlin backends.",
      social: [
        {
          icon: "github",
          label: "GitHub",
          href: "https://github.com/highbeamco/kairo",
        },
      ],
      sidebar: [
        {
          label: "Getting started",
          items: [
            { label: "Introduction", slug: "index" },
            { label: "Installation", slug: "getting-started" },
            { label: "Style guide", slug: "style-guide" },
            { label: "Changelog", slug: "changelog" },
          ],
        },
        ...moduleSidebar,
        {
          label: "API Reference",
          items: [
            {
              label: "API Docs (Dokka)",
              link: "/api/",
            },
          ],
        },
      ],
    }),
  ],
});

export default config;
