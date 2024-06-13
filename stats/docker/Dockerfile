# Use Red Hat Universal Base Image (UBI) as the base image
FROM registry.access.redhat.com/ubi8/ubi

# Set the working directory to /opt
WORKDIR /opt

# Copy the Java Zulu tar file to the image
COPY zulu-java.tar.gz .

# Unzip the Java tar file into the /opt directory and rename the directory to zulu
RUN tar -xzf zulu-java.tar.gz && \
    mv $(tar -tf zulu-java.tar.gz | head -1 | cut -d"/" -f1) zulu

# Set JAVA_HOME environment variable
ENV JAVA_HOME=/opt/zulu
ENV PATH=$JAVA_HOME/bin:$PATH

# Create a directory for the jar files
RUN mkdir /strat

# Copy the jar files to the /strat directory
COPY JSONServer.jar /strat/
COPY server.txt /strat/
COPY stats.jar /strat/

# Expose the necessary ports
EXPOSE 8010
EXPOSE 9090

# Run the jar files
WORKDIR /strat
CMD ["bash", "-c", "java -jar ./JSONServer.jar & java -jar ./stats.jar"]
