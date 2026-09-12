FROM node:22-alpine AS build
WORKDIR /repo
RUN corepack enable
COPY package.json pnpm-workspace.yaml ./
COPY runtimes/node-runner/package.json runtimes/node-runner/package.json
RUN pnpm install --filter @yagent/node-runner... --no-frozen-lockfile
COPY runtimes/node-runner runtimes/node-runner
RUN pnpm --filter @yagent/node-runner build

FROM node:22-alpine
WORKDIR /app
COPY --from=build /repo/runtimes/node-runner/dist ./dist
COPY --from=build /repo/runtimes/node-runner/node_modules ./node_modules
COPY --from=build /repo/runtimes/node-runner/package.json ./package.json
ENV PORT=8090
EXPOSE 8090
CMD ["node","dist/server.js"]
