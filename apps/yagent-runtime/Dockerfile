FROM node:22-alpine AS build
WORKDIR /repo
RUN corepack enable
COPY package.json pnpm-workspace.yaml ./
COPY packages ./packages
COPY apps/yagent-runtime ./apps/yagent-runtime
RUN pnpm config set registry https://registry.npmmirror.com
RUN pnpm install --filter @yagent/runtime... --no-frozen-lockfile
RUN pnpm --filter @yagent/runtime... build

FROM node:22-alpine
WORKDIR /repo
COPY --from=build /repo/packages ./packages
COPY --from=build /repo/apps/yagent-runtime ./apps/yagent-runtime
COPY --from=build /repo/node_modules ./node_modules
WORKDIR /repo/apps/yagent-runtime
ENV PORT=3000
EXPOSE 3000
CMD ["node","dist/index.js"]
