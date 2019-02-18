# Base image specified as POM property
FROM ${docker.base}

# Create unprivileged user
RUN groupadd acumos && useradd -r -g acumos acumos

# The plugin puts files in maven
COPY maven/ /maven
RUN chown -R acumos:acumos /maven

# Run as the unprivileged user
USER acumos

# Launch the service
CMD cd /maven; java -Xms128m -Xmx1024m -Djava.security.egd=file:/dev/./urandom -jar ${project.artifactId}-${project.version}.${project.packaging}
