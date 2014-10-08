# Streaming JSON Patch from Spring to a React UI

This is a simple football scores demo that uses Spring Sync to stream JSON Patch over STOMP to a most.js reactive stream attached to a React UI in the web client.

## Build the client

1. `cd src/main/resources/public`
1. `npm install`

## Run the app

1. From the repo root dir: `gradlew bootRun`
1. Once the app has booted up, open `http://localhost:8080` in a modern browser (one that supports WebSocket!)
