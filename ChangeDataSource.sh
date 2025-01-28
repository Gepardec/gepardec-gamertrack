#!/bin/bash

/opt/jboss/wildfly/bin/standalone.sh &

# Warte, bis WildFly vollständig hochgefahren ist und erreichbar ist
echo "=> Waiting for WildFly to start"
until curl -s http://localhost:8080 > /dev/null; do
    echo "Waiting for WildFly..."
    sleep 5
done

echo "=> WildFly started. Now configuring datasource."

# Jetzt den CLI-Befehl ausführen, um die Datenquelle zu konfigurieren
/opt/jboss/wildfly/bin/jboss-cli.sh --connect <<EOF
/subsystem=datasources/data-source=ExampleDS:write-attribute(name=connection-url, value="jdbc:h2:file:/opt/jboss/wildfly/gamertrackDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=\${wildfly.h2.compatibility.mode:REGULAR}")
reload
shutdown
EOF



# Halte das Skript am Laufen, damit der Container nicht beendet wird