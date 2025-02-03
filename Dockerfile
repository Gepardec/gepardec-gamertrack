FROM quay.io/wildfly/wildfly:latest-jdk21

ADD gamertrack-war/target/gepardec-gamertrack.war /opt/jboss/wildfly/standalone/deployments/

COPY ChangeDataSource.sh /opt/jboss/wildfly/ChangeDataSource.sh


USER root
RUN chmod +x /opt/jboss/wildfly/ChangeDataSource.sh
RUN chmod -R 777 jboss/

USER jboss
RUN /opt/jboss/wildfly/ChangeDataSource.sh

ENTRYPOINT ["/bin/bash", "-c", "/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0"]