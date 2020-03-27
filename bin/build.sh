#!/bin/sh

workspaceRoot=./

echo "preparinging..."
mkdir $workspaceRoot/bin/compiler-out/temp-classes/
cp -r $workspaceRoot/bin/compiler-out/basilisk/ $workspaceRoot/bin/compiler-out/temp-classes/basilisk/

rm $workspaceRoot/bin/compiler-out/Basilisk.jar
echo "removing old .jar version"

echo "compiling new .jar version..."
jar cvfm $workspaceRoot/bin/compiler-out/Basilisk.jar $workspaceRoot/bin/compiler-out/manifest.mf -C $workspaceRoot/bin/compiler-out/temp-classes .
echo "done compiling new .jar"

echo "formating build apps"
rm -rf ${workspaceRoot}/bin/application.linux-arm64/lib/websockets/
rm -rf ${workspaceRoot}/bin/application.linux-armv6hf/lib/websockets/
rm -rf ${workspaceRoot}/bin/application.linux32/lib/websockets/
rm -rf ${workspaceRoot}/bin/application.linux64/lib/websockets/
rm -rf ${workspaceRoot}/bin/application.windows32/lib/websockets/
rm -rf ${workspaceRoot}/bin/application.windows64/lib/websockets/
rm -rf ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/websockets/

rm -rf ${workspaceRoot}/bin/application.linux-arm64/data/
rm -rf ${workspaceRoot}/bin/application.linux-armv6hf/data/
rm -rf ${workspaceRoot}/bin/application.linux32/data/
rm -rf ${workspaceRoot}/bin/application.linux64/data/
rm -rf ${workspaceRoot}/bin/application.windows32/data/
rm -rf ${workspaceRoot}/bin/application.windows64/data/
rm -rf ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/data/
echo "done formating build apps, ready to copy new files"


cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux-arm64/lib/Basilisk.jar
cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux-armv6hf/lib/Basilisk.jar
echo "added new .jar to linux-arm builds"

cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux32/lib/Basilisk.jar
cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux64/lib/Basilisk.jar
echo "added new .jar to linux64 and linux32 builds"

cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.windows32/lib/Basilisk.jar
cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.windows64/lib/Basilisk.jar
echo "added new .jar to windows64 and windows32 builds"

cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/Basilisk.jar
echo "added new .jar to macosx build"



cp -r ${workspaceRoot}/bin/compiler-out/websockets/ ${workspaceRoot}/bin/application.linux-arm64/lib/websockets/
cp ${workspaceRoot}/bin/compiler-out/processing/core.jar ${workspaceRoot}/bin/application.linux-arm64/lib/processing/core.jar
echo "copied libs for linux-arm64 build"

cp -r ${workspaceRoot}/bin/compiler-out/websockets/ ${workspaceRoot}/bin/application.linux-armv6hf/lib/websockets/
cp ${workspaceRoot}/bin/compiler-out/processing/core.jar ${workspaceRoot}/bin/application.linux-armv6hf/lib/processing/core.jar
echo "copied libs for linux-armv6hf build"

cp -r ${workspaceRoot}/bin/compiler-out/websockets/ ${workspaceRoot}/bin/application.linux32/lib/websockets/
cp ${workspaceRoot}/bin/compiler-out/processing/core.jar ${workspaceRoot}/bin/application.linux32/lib/processing/core.jar
echo "copied libs for linux32 build"

cp -r ${workspaceRoot}/bin/compiler-out/websockets/ ${workspaceRoot}/bin/application.linux64/lib/websockets/
cp ${workspaceRoot}/bin/compiler-out/processing/core.jar ${workspaceRoot}/bin/application.linux64/lib/processing/core.jar
echo "copied libs for linux64 build"

cp -r ${workspaceRoot}/bin/compiler-out/websockets/ ${workspaceRoot}/bin/application.windows32/lib/websockets/
cp ${workspaceRoot}/bin/compiler-out/processing/core.jar ${workspaceRoot}/bin/application.windows32/lib/processing/core.jar
echo "copied libs for windows 32 build"

cp -r ${workspaceRoot}/bin/compiler-out/websockets/ ${workspaceRoot}/bin/application.windows64/lib/websockets/
cp -r ${workspaceRoot}/bin/compiler-out/processing/core.jar ${workspaceRoot}/bin/application.windows64/lib/processing/core.jar
echo "copied libs for windows 64 build"

cp -r ${workspaceRoot}/bin/compiler-out/websockets/ ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/websockets/
cp ${workspaceRoot}/bin/compiler-out/processing/core.jar ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/processing/core.jar
cp ${workspaceRoot}/bin/compiler-out/processing/apple.jar ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/processing/apple.jar
echo "copied libs for macosx build"



rm -rf ${workspaceRoot}/bin/application.linux-arm64/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux-arm64/source/
echo "updaing sources for linux arm64 build"

rm -rf ${workspaceRoot}/bin/application.linux-armv6hf/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux-armv6hf/source/
echo "updating sources for linux armv6hf build"

rm -rf ${workspaceRoot}/bin/application.linux32/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux32/source/
echo "updating sources for linux32 build"

rm -rf ${workspaceRoot}/bin/application.linux64/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux64/source/
echo "updating sources for linux64 build"

rm -rf ${workspaceRoot}/bin/application.windows32/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.windows32/source/
echo "updating sources for windows32 build"

rm -rf ${workspaceRoot}/bin/application.windows64/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.windows64/source/
echo "updating sources for windows64 build"

rm -rf ${workspaceRoot}/bin/application.macosx/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.macosx/source/
echo "updating sources for macosx build"



cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux-arm64/data/
echo "updating resources for linux arm64 build"
cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux-armv6hf/data/
echo "updating resources for linux armv6hf build"

cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux32/data/
echo "updating resources for linux32 build"
cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux64/data/
echo "updating resources for linux64 build"

cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.windows32/data/
echo "updating resources for windows32 build"
cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.windows64/data/
echo "updating resources for windows64 build"

cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/data/
echo "updating resources for macosx build"

echo "cleaning up..."
rm -rf $workspaceRoot/bin/compiler-out/temp-classes/basilisk/
rmdir $workspaceRoot/bin/compiler-out/temp-classes/