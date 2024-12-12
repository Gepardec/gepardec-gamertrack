FROM quay.io/wildfly/wildfly:latest

ADD gamertrack-war/target/gamertrack-war-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/

