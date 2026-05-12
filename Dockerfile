FROM nginx:1.27-alpine

COPY src/main/resources/static/ /usr/share/nginx/html/

EXPOSE 80
