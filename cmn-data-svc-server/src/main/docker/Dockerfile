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
