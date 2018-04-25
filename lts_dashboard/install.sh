#!/bin/bash

echo "## Gather requirements before running"

LTS_HOME=`pwd`
source $LTS_HOME/CONFIGURATIONS

cd $LTS_HOME
if [ ! -d "deploy-components" ]; then
  mkdir deploy-components
fi
cd deploy-components
if [ ! -d "frontend" ]; then
	mkdir frontend
fi
if [ ! -d "backend" ]; then
	mkdir backend
fi
if [ ! -d "certificates" ]; then
	mkdir certificates
fi

cd $LTS_HOME
echo "####### Creating config files #######"

# creating file for back end
echo "export GIT_TOKEN=$GIT_TOKEN" > BACKEND_CONFIGURATIONS
echo "export BACKEND_ACCESS_USER=$BACKEND_USER" >> BACKEND_CONFIGURATIONS
echo "export BACKEND_ACCESS_PASSWORD=$BACKEND_PASSWORD" >> BACKEND_CONFIGURATIONS
echo "export DSS_URL=$DSS_URL" >> BACKEND_CONFIGURATIONS
echo "export GIT_BASE_URL=$GIT_BASE_URL" >> BACKEND_CONFIGURATIONS
echo "export JSON_ACCEPT_FORMAT=$JSON_ACCEPT_FORMAT" >> BACKEND_CONFIGURATIONS
echo "export DSS_AUTH_TOKEN=$DSS_AUTH_TOKEN" >> BACKEND_CONFIGURATIONS
mv BACKEND_CONFIGURATIONS $LTS_HOME/deploy-components/backend
echo "## backend config.ini created"

echo "listenerConfigurations:" > netty-transports.yml
echo " -" >> netty-transports.yml
echo "  id: \"msf4j-https\"" >> netty-transports.yml
echo "  host: \"127.0.0.1\"" >> netty-transports.yml
echo "  port: $BACKEND_HOST_PORT" >> netty-transports.yml
echo "  scheme: https" >> netty-transports.yml
echo "  keyStoreFile: $BACKEND_KEYSOTRE_FILE_NAME" >> netty-transports.yml
echo "  keyStorePassword: $BACKEND_KEYSOTRE_PASSWORD" >> netty-transports.yml
echo "  certPass: $BACKEND_KEY_PASS" >> netty-transports.yml
mv netty-transports.yml $LTS_HOME/deploy-components/backend
echo "## netty-transports.yml created"

# creating config file for front end
echo "backend_url=https://$BACKEND_HOST:$BACKEND_HOST_PORT/lts" > config.properties
echo "backend_username=$BACKEND_USER" >> config.properties
echo "backend_password=$BACKEND_PASSWORD" >> config.properties
echo "sso_keystore_file_name=$SSO_KEYSTORE_FILE_NAME" >> config.properties
echo "sso_keystore_password=$SSO_KEYSTORE_PASSWORD" >> config.properties
echo "sso_certificate_alias=$SSO_CERTIFICATE_KEY" >> config.properties
echo "sso_redirect_url=$SSO_REDERECT_URL" >> config.properties
mv config.properties $LTS_HOME/app-deployer/ReactDeployer/src/main/resources
echo "## frontend config.properties created"

echo "REACT_APP_HOST_NAME=$HOST_URL" > .env
mv .env $LTS_HOME/lts-dashboard


if [ $CREATE_BACKEND_CERTIFICATE = true ]; then
	echo "###### Generating backend certificate ########"
	echo 
	echo "Use keystore password : $BACKEND_KEYSOTRE_PASSWORD (same used in configs)"
	keytool -genkeypair -keystore $BACKEND_KEYSOTRE_FILE_NAME \
	-noprompt \
	-dname "CN=OLEKSIYS-W3T, OU=Sun Java System Application Server, O=Sun Microsystems, L=Santa Clara, ST=California, C=US" \
	-storepass $BACKEND_KEYSOTRE_PASSWORD \
	-keypass $BACKEND_KEY_PASS \
	-keyalg RSA \
	-keysize 2048 \
	-alias lts-micorservice \
	-ext SAN=DNS:$BACKEND_HOST \
	-validity 9999

	
	keytool -export -alias lts-micorservice -keystore $BACKEND_KEYSOTRE_FILE_NAME -rfc -file lts-backend.cert
	mv $BACKEND_KEYSOTRE_FILE_NAME $LTS_HOME/deploy-components/backend
	mv lts-backend.cert $LTS_HOME/deploy-components/certificates
	echo "## copying keystore and certificate completed"
fi


if [ -f $SSO_KEYSTORE_FILE_PATH ]; then
	mv $SSO_KEYSTORE_FILE_PATH $LTS_HOME/app-deployer/ReactDeployer/src/main/resources
fi


if [ $BUILD_REACT = true ]; then
	# builing react app
	echo "###### Building React App ########"
	cd $LTS_HOME/lts-dashboard
	CI=false & npm run build
fi

if [ $BUILD_FRONT_END = true ]; then
	# building front end webapp
	echo "###### Building Front end webapp ########"
	cp -r $LTS_HOME/lts-dashboard/build $LTS_HOME/app-deployer/ReactDeployer/src/main/webapp
	cd $LTS_HOME/app-deployer/ReactDeployer
	mvn clean install -DskipTests
fi

if [ $BUILD_BACK_END = true ]; then
	# building backend
	echo "###### Building Backend ########"
	cd $LTS_HOME/lts-dashboard-back/LTS-Dashboard
	mvn clean install -DskipTests
fi

# making final deploy jars and files
echo "###### Copy jars ########"

cd $LTS_HOME
cp -r $LTS_HOME/app-deployer/ReactDeployer/target/lts  \
	$LTS_HOME/deploy-components/frontend
cp -r $LTS_HOME/app-deployer/ReactDeployer/target/lts.war  \
	$LTS_HOME/deploy-components/frontend
cp -r $LTS_HOME/lts-dashboard-back/LTS-Dashboard/target/lts.jar \
	$LTS_HOME/lts-dashboard-back/LTS-Dashboard/target/config.ini \
	$LTS_HOME/lts-dashboard-back/LTS-Dashboard/target/log4j.properties \
	$LTS_HOME/deploy-components/backend

echo "###### Copy completed ########"
echo 
echo "## Deployble frontend and backend are in deploy-components directory"





