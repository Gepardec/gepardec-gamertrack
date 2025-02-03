FROM quay.io/wildfly/wildfly:latest-jdk21 AS build

COPY enable_h2.txt /opt/jboss/wildfly/enable_h2.txt
WORKDIR /opt/jboss/wildfly/bin

RUN ./jboss-cli.sh --commands="embed-server --std-out=echo --server-config=standalone.xml, /subsystem=datasources/data-source=ExampleDS:write-attribute(name=connection-url, value=\"jdbc:h2:file:/opt/jboss/wildfly/gamertrackDB;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=\${wildfly.h2.compatibility.mode:REGULAR}\")"

FROM quay.io/wildfly/wildfly:latest-jdk21
COPY --from=build /opt/jboss/wildfly/standalone/configuration/standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
ADD gamertrack-war/target/gepardec-gamertrack.war /opt/jboss/wildfly/standalone/deployments/


ENTRYPOINT ["/bin/bash", "-c", "/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0"]