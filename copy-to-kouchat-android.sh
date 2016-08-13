#! /bin/bash

MAIN_FROM_DIR=src/main/java/net/usikkert/kouchat
MAIN_TO_DIR=../kouchat-android/app/src/main/java/net/usikkert/kouchat

MAIN_RESOURCES_FROM_DIR=src/main/resources
MAIN_RESOURCES_TO_DIR=../kouchat-android/app/src/main/resources


TEST_FROM_DIR=src/test/java/net/usikkert/kouchat
TEST_TO_DIR=../kouchat-android/app/src/test/java/net/usikkert/kouchat

TEST_RESOURCES_FROM_DIR=src/test/resources
TEST_RESOURCES_TO_DIR=../kouchat-android/app/src/test/resources


#cp -R $MAIN_FROM_DIR/argument $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/autocomplete $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/event $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/jmx $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/message $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/misc $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/net $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/settings $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/ui $MAIN_TO_DIR/
cp -R $MAIN_FROM_DIR/util $MAIN_TO_DIR/

cp $MAIN_FROM_DIR/Constants.java $MAIN_TO_DIR/

cp $MAIN_RESOURCES_FROM_DIR/messages/core.properties $MAIN_RESOURCES_TO_DIR/messages/

rm $MAIN_TO_DIR/jmx/JMXAgent.java
rm $MAIN_TO_DIR/misc/SoundBeeper.java
rm $MAIN_TO_DIR/ui/UIFactory.java

rm -rf $MAIN_TO_DIR/ui/console
rm -rf $MAIN_TO_DIR/ui/swing


#cp -R $TEST_FROM_DIR/argument $TEST_TO_DIR/
#cp -R $TEST_FROM_DIR/functional $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/jmx $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/junit $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/message $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/misc $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/net $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/settings $TEST_TO_DIR/
#cp -R $TEST_FROM_DIR/testclient $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/ui $TEST_TO_DIR/
cp -R $TEST_FROM_DIR/util $TEST_TO_DIR/

cp $TEST_RESOURCES_FROM_DIR/* $TEST_RESOURCES_TO_DIR/

rm $TEST_TO_DIR/jmx/JMXAgentTest.java
rm $TEST_TO_DIR/misc/SoundBeeperTest.java
rm $TEST_TO_DIR/ui/UIFactoryTest.java

rm -rf $TEST_TO_DIR/ui/console
rm -rf $TEST_TO_DIR/ui/swing
