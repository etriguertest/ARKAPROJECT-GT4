# Stage 1: Build the Angular application
FROM node:20 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve the application with NGINX and the custom config
FROM nginx:alpine AS deploy
# Copy the custom nginx.conf file into the container
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copy the built application from the builder stage
COPY --from=build /app/dist/sakai-ng/browser /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
