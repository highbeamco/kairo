interface Env {
  LIMBER_API_BASE_URL: string;
  SHOW_DEBUG_MESSAGES: boolean;
  ADDITIONAL_LIMBER_API_LATENCY_MS: number;
  AUTH0_DOMAIN: string;
  COPYRIGHT_HOLDER: string;
}

/* eslint-disable @typescript-eslint/no-non-null-assertion */
const env: Env = {
  LIMBER_API_BASE_URL: process.env.REACT_APP_LIMBER_API_BASE_URL!,
  SHOW_DEBUG_MESSAGES: Boolean(process.env.REACT_APP_SHOW_DEBUG_MESSAGES!),
  ADDITIONAL_LIMBER_API_LATENCY_MS: Number(process.env.REACT_APP_ADDITIONAL_LIMBER_API_LATENCY_MS!),
  AUTH0_DOMAIN: process.env.REACT_APP_AUTH0_DOMAIN!,
  COPYRIGHT_HOLDER: process.env.REACT_APP_COPYRIGHT_HOLDER!,
};

export default env;
