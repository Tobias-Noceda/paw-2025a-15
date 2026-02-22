FROM tomcat:9.0.107-jre21-temurin-noble

# Install socat to forward localhost:5432 to postgres container
RUN apt-get update && \
    apt-get install -y socat && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create entrypoint script
RUN echo '#!/bin/bash\n\
# Forward localhost:5432 to postgres:5432\n\
socat TCP-LISTEN:5432,fork,reuseaddr TCP:postgres:5432 &\n\
# Start Tomcat\n\
exec catalina.sh run' > /entrypoint.sh && \
    chmod +x /entrypoint.sh

# Deploy WAR
COPY webapp/target/webapp.war /usr/local/tomcat/webapps/paw-2025a-15.war

ENTRYPOINT ["/entrypoint.sh"]