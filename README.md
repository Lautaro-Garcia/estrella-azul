# Estrella Azul

> "¿Dónde estará la estrella azul, esa estrellita del alma? - Peteco Carabajal

Este repositorio es el código para la charla "El camino de la estrella" de la 10PinesConf 2026

La idea de la charla es mostrar [Datastar](https://data-star.dev/) y su filosofía de múltiple page applications.

## Que contiene este repo

- `slides/` contiene todas las slides que se muestran una vez el admin se loguea (pone la contraseña)
  - Las slides se ordenan por su nombre (parseado como un número) y si tienen en su nombre la palabra "estrella" cuentan como una slide con estrella
- `src/` tiene la applicación que es una app muy chiquitita y bastante estándar

## Tecnologías
Obvio que iba a hacer esto con un Lisp, así que Clojure fue mi go-to
- [http-kit](https://http-kit.github.io/) es el server
- Estoy usando la [SDK de Clojure](https://github.com/starfederation/datastar-clojure) para datastar
- [Hiccup](https://github.com/weavejester/hiccup) como template engine
- [Garden](https://github.com/noprompt/garden) para crear el CSS
- [Cheshire](https://github.com/dakrone/cheshire) para parsear y serializar json
- [Reitit](https://github.com/metosin/reitit) para definir las rutas

## Testing
Es una app muy chiquita para una charla, la hice toda con el REPL probando cosas

## Building
Para poder hostearla (porque el hosting no soporta nativamente Clojure pero sí cualquier cosa dockerizable) hice un Dockerfile y un docker-compose.yml así,
si quisiera, podría levantar la aplicación buildeada con
```bash
docker compose up
```
Por lo general levanto el web server desde el REPL, no me gasto en armar una imagen salvo para probar lo que voy a deployar

## ¿De dónde salió la idea para esta charla?
De [esta otra charla](https://youtu.be/W7Ki3aXgmZU) que dió el creador de Datastar
