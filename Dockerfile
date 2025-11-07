# --- ETAPA 1: Construcción (Build Stage) ---
# Usamos una imagen de Node.js para construir el proyecto
FROM node:20-alpine AS build

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el package.json y package-lock.json
COPY package*.json ./

# Instalamos las dependencias
RUN npm install

# Copiamos el resto del código fuente del proyecto
COPY . .

# Ejecutamos el build de producción (como dice tu README)
RUN ng build --configuration production

# --- ETAPA 2: Publicación (Serve Stage) ---
# Usamos una imagen de Nginx muy ligera
FROM nginx:1.27-alpine

# IMPORTANTE: Reemplaza 'nombre-de-tu-proyecto'
# después de ejecutar 'ng build'. (Ej: /app/dist/mi-app)
COPY --from=build /app/dist/sakai-ng /usr/share/nginx/html

# Copiamos nuestro archivo de configuración personalizado de Nginx
# Este archivo lo crearemos en el siguiente paso.
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponemos el puerto 8080 (esto debe coincidir con tu 'deploy.yml')
EXPOSE 8080
