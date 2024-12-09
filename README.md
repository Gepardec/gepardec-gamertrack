# gepardec-gamertrack
Lernprojekt für Juniors. Eine App für Tracking von Ergebnissen bei diversen sportlichen Auseinandersetzungen 


## Requirements

The following technologies are used by Gepardec-Gamertrack

1. `Java 23.x.x`
2. `WildFly 34.0.0.Final`
3. `Maven 3.4.x`
4. `H2-Database`
5. `Mockito`



## HTTPS-ENDPOINTS

Rest-Endpoints are available via
```http
 localhost:8080/gamertrack-war-1.0-SNAPSHOT/api/v1/
```
###

| Endpoint        | Description        |
|:----------------|:-------------------|
| `/users`        | CRUD - operations  |
| `/games`        | CRUD - operations  |
| `/gameoutcomes` | CRUD - operations  |
| `/score`        | CRU - operations   |

For more specific information for each endpoint visit: [OpenApi Spec](https://github.com/Gepardec/gepardec-gamertrack/blob/main/docs/openapi-spec.json)

