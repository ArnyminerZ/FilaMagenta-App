# Filà Magenta App

This repository contains all the source code of the software that runs all the applications for the Filà Magenta.

It's divided into multiple modules.

# Server ([`/server`](/server))

Here's the backend for the app.
It also contains a simple admin UI that can be accessed in [`/admin`](http://0.0.0.0:8080/admin).
You can see its source code in the main module's resources ([link](/server/src/main/resources/admin)).

## Running

The official and supported way of running the backend in production is through Docker.
If you don't have it installed, you can check their official guide [here](https://docs.docker.com/engine/install/).

> [!NOTE]
> Docker Compose is also required, check out the official [guide](https://docs.docker.com/compose/install/).

Once Docker is correctly installed on your device, you can run it with
```shell
# Replace with docker-compose if using the hold Docker Compose version
docker compose -f compose.yml -f compose.prod.yml up -d
```

> [!TIP]
> You can run the app in the foreground by removing the `-d` argument.
