FROM node:22-alpine AS build
WORKDIR /repo
RUN corepack enable
ENV COREPACK_NPM_REGISTRY=https://registry.npmmirror.com
RUN corepack prepare pnpm@10.17.1 --activate

COPY package.json pnpm-workspace.yaml ./
COPY runtimes/node-runner runtimes/node-runner

RUN pnpm config set registry https://registry.npmmirror.com
RUN pnpm install --filter @yagent/node-runner... --no-frozen-lockfile
RUN pnpm --filter @yagent/node-runner build

FROM node:22-alpine
WORKDIR /repo
COPY --from=build /repo/node_modules ./node_modules
COPY --from=build /repo/runtimes/node-runner/node_modules ./runtimes/node-runner/node_modules
COPY --from=build /repo/runtimes/node-runner/dist ./runtimes/node-runner/dist
COPY --from=build /repo/runtimes/node-runner/package.json ./runtimes/node-runner/package.json
WORKDIR /repo/runtimes/node-runner
ENV PORT=8090
EXPOSE 8090
CMD ["node","dist/server.js"]