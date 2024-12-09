# gepardec-gamertrack

Lernprojekt für Juniors. Eine App für Tracking von Ergebnissen bei diversen sportlichen
Auseinandersetzungen

## Requirements

The following technologies are used by Gepardec-Gamertrack

1. `Java 23.x.x`
2. `WildFly 34.0.0.Final`
3. `Maven 3.4.x`
4. `H2-Database`
5. `Mockito`

## Build Project and deploy application

- *In order for all used relative paths to work  
  they should be executed from the projects root directory*
- *Use absolute path or relative path instead of $WILDFLY_HOME.*
    - *Alternatively set the environment variable with export WILDFLY_HOME=PATH/TO/WILDFLY/DIRECTORY
      for the current terminal session*

**Build**

```zsh 
  mvn clean install -am
```

**Start wildfly**

```zsh
  $WILDFLY_HOME/bin//wildfly-34.0.0.Final/bin/standalone.sh
```

**Deploy application to wildfly**

```zsh
  $WILDFLY_HOME/bin/jboss-cli.sh --connect --command="deploy --force ./gamertrack-war/target/gamertrack-war-1.0-SNAPSHOT.war"
```

**Undeploy and stop wildfly**

```zsh
  $WILDFLY_HOME/bin/jboss-cli.sh --connect --command="undeploy gamertrack-war-1.0-SNAPSHOT.war"
```

**Stop wildfly**

```zsh
  $WILDFLY_HOME/bin/jboss-cli.sh --connect --command="shutdown"
```

## ER-diagram

```mermaid
classDiagram
    namespace BaseShapes {
        class User {
            -String firstname
            -String lastname
            -List<Score> gameScores
        }

        class Game {
            +String name
            +String rules
        }
        class GameOutcome {
            +Game game
            +List<User> users
        }
        class Score {
            +Game game
            +int gamescore
        }
    }
    Score "0..n" --* "1" User
    User "n" *--|> "0..m" GameOutcome
    GameOutcome "0..n" --* "1" Game
    Game "1" *-- "0..n" Score
```

## HTTPS-ENDPOINTS

Rest-Endpoints are available via

```http
 localhost:8080/gamertrack-war-1.0-SNAPSHOT/api/v1/
```

###

| Endpoint        | Description       |
|:----------------|:------------------|
| `/users`        | CRUD - operations |
| `/games`        | CRUD - operations |
| `/gameoutcomes` | CRUD - operations |
| `/score`        | CRU - operations  |

For more specific information for each endpoint
visit: [OpenApi Spec](https://github.com/Gepardec/gepardec-gamertrack/blob/main/docs/openapi-spec.json)
