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

# Ejecutamos el build de producción 
RUN npx ng build --configuration production


# AGREGAMOS ESTO PARA DEPURAR: Muestra lo que hay en dist/sakai-ng
# Verás una carpeta llamada 'browser' en la salida del build
RUN ls -la /app/dist/sakai-ng

# --- ETAPA 2: Publicación (Serve Stage) ---
# Usamos una imagen de Nginx muy ligera
FROM nginx:1.27-alpine

# CORREGIMOS LA RUTA: Añadimos '/browser' al final
COPY --from=build /app/dist/sakai-ng/browser /usr/share/nginx/html

# Copiamos nuestro archivo de configuración personalizado de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponemos el puerto 8080 (esto debe coincidir con tu 'deploy.yml')
EXPOSE 8080
