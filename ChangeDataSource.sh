#!/bin/bash

/opt/jboss/wildfly/bin/standalone.sh &

echo "=> Waiting for WildFly to start"
until curl -s http://localhost:8080 > /dev/null; do
    echo "Waiting for WildFly..."
    sleep 5
done

echo "=> WildFly started. Now configuring datasource."

/opt/jboss/wildfly/bin/jboss-cli.sh --connect <<EOF
/subsystem=datasources/data-source=ExampleDS:write-attribute(name=connection-url, value="jdbc:h2:file:/opt/jboss/wildfly/gamertrackDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=\${wildfly.h2.compatibility.mode:REGULAR}")
exit
EOF

