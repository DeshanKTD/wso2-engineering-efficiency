#!/bin/bash

echo "## Gather requirements before running"

LTS_HOME=`pwd`

# builing react app
echo "###### Building React App ########"
cd $LTS_HOME/lts-dashboard
npm run build

# building front end webapp
echo "###### Building Front end webapp ########"
cp -r $LTS_HOME/lts-dashboard/build $LTS_HOME/app-deployer/ReactDeployer/src/main/webapp
cd $LTS_HOME/app-deployer/ReactDeployer
mvn clean install -DskipTests

# building backend
echo "###### Building Backend ########"
cd $LTS_HOME/lts-dashboard-back/LTS-Dashboard
mvn clean install -DskipTests

# making final deploy jars and files
echo "###### Copy jars ########"
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


cd $LTS_HOME
cp -r $LTS_HOME/app-deployer/ReactDeployer/target/lts $LTS_HOME/deploy-components/frontend
cp -r $LTS_HOME/lts-dashboard-back/LTS-Dashboard/target/lts.jar \
	$LTS_HOME/lts-dashboard-back/LTS-Dashboard/target/config.ini \
	$LTS_HOME/lts-dashboard-back/LTS-Dashboard/target/log4j.properties \
	$LTS_HOME/lts-dashboard-back/LTS-Dashboard/keystore.jks \
	$LTS_HOME/lts-dashboard-back/LTS-Dashboard/netty-transports.yml \
	$LTS_HOME/deploy-components/backend

echo "###### Copy completed ########"
echo 
echo "## Deployble frontend and backend are in deploy-components directory"





