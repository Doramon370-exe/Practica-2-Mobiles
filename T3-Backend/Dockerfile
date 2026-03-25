#dockerizando el proyecto de node.js para que pueda ser desplegado en cualquier entorno sin problemas de dependencias o configuraciones específicas del sistema operativo.
FROM node:18
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 5000
CMD ["node", "server.js"]