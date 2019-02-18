###
# ===============LICENSE_START=======================================================
# Acumos
# ===================================================================================
# Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
# ===================================================================================
# This Acumos software file is distributed by AT&T and Tech Mahindra
# under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#  
#      http://www.apache.org/licenses/LICENSE-2.0
#  
# This file is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ===============LICENSE_END=========================================================
###

# This Spring-Boot server writes its logs below /maven/logs.
# Deployment is expected to mount a writeable volumne there.
# To support running the container as a non-privileged user,
# ensure that directory is world writeable in the image.

# Base image specified as POM property
FROM ${docker.base}

# The plugin puts files in maven
COPY maven/ /maven

# Create log directory with world-write permission
RUN mkdir /maven/logs && chown 777 /maven/logs

# Launch the service
CMD cd /maven; java -Xms128m -Xmx1024m -Djava.security.egd=file:/dev/./urandom -jar ${project.artifactId}-${project.version}.${project.packaging}
