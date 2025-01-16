FROM quay.io/wildfly/wildfly:latest

ADD gamertrack-war/target/gepardec-gamertrack.war /opt/jboss/wildfly/standalone/deployments/

