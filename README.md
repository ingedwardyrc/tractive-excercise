## Build & Run (Gradle)

```bash
./gradlew clean bootRun
```

# Tests

```bash
./gradlew clean test
```

## Integration tests
[integration](src/test/java/com/tractive/pettracker/integration)

## Unit tests
[unit](src/test/java/com/tractive/pettracker/unit)


### Create (POST)
```bash
curl -X POST -H "Content-Type: application/json" -d '{"petType":"CAT","trackerType":"SMALL","ownerId":123,"inZone":false,"lostTracker":false}' http://localhost:8080/api/pets
```

### Update (PUT)
```bash
curl -X PUT -H "Content-Type: application/json" -d '{"petType":"DOG","trackerType":"MEDIUM","ownerId":123,"inZone":true}' http://localhost:8080/api/pets/10
```

### Get by ID (GET)
```bash
curl http://localhost:8080/api/pets/10
```

### List (GET)
```bash
curl http://localhost:8080/api/pets
```

### Out-of-Zone Summary (GET)
```bash
curl http://localhost:8080/api/pets/out-of-zone-summary
```