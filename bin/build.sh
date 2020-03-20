#!/bin/sh

workspaceRoot=./
rm $workspaceRoot/bin/compiler-out/Basilisk.jar
jar cvfm $workspaceRoot/bin/compiler-out/Basilisk.jar $workspaceRoot/bin/compiler-out/manifest.mf -C $workspaceRoot/bin/compiler-out/ .

cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux-arm64/lib/Basilisk.jar
cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux-armv6hf/lib/Basilisk.jar

cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux32/lib/Basilisk.jar
cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.linux64/lib/Basilisk.jar

cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.windows32/lib/Basilisk.jar
cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.windows64/lib/Basilisk.jar

cp ${workspaceRoot}/bin/compiler-out/Basilisk.jar ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/Basilisk.jar



cp ${workspaceRoot}/bin/compiler-out/websockets/Java-WebSocket-1.4.1-with-dependencies.jar ${workspaceRoot}/bin/application.linux-arm64/lib/Java-WebSocket-1.4.1-with-dependencies.jar
cp ${workspaceRoot}/bin/compiler-out/websockets/Java-WebSocket-1.4.1-with-dependencies.jar ${workspaceRoot}/bin/application.linux-armv6hf/lib/Java-WebSocket-1.4.1-with-dependencies.jar

cp ${workspaceRoot}/bin/compiler-out/websockets/Java-WebSocket-1.4.1-with-dependencies.jar ${workspaceRoot}/bin/application.linux32/lib/Java-WebSocket-1.4.1-with-dependencies.jar
cp ${workspaceRoot}/bin/compiler-out/websockets/Java-WebSocket-1.4.1-with-dependencies.jar ${workspaceRoot}/bin/application.linux64/lib/Java-WebSocket-1.4.1-with-dependencies.jar

cp ${workspaceRoot}/bin/compiler-out/websockets/Java-WebSocket-1.4.1-with-dependencies.jar ${workspaceRoot}/bin/application.windows32/lib/Java-WebSocket-1.4.1-with-dependencies.jar
cp ${workspaceRoot}/bin/compiler-out/websockets/Java-WebSocket-1.4.1-with-dependencies.jar ${workspaceRoot}/bin/application.windows64/lib/Java-WebSocket-1.4.1-with-dependencies.jar

cp ${workspaceRoot}/bin/compiler-out/websockets/Java-WebSocket-1.4.1-with-dependencies.jar ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/Java-WebSocket-1.4.1-with-dependencies.jar



rm -rf ${workspaceRoot}/bin/application.linux-arm64/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux-arm64/source/

rm -rf ${workspaceRoot}/bin/application.linux-armv6hf/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux-armv6hf/source/

rm -rf ${workspaceRoot}/bin/application.linux32/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux32/source/

rm -rf ${workspaceRoot}/bin/application.linux64/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.linux64/source/

rm -rf ${workspaceRoot}/bin/application.windows32/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.windows32/source/

rm -rf ${workspaceRoot}/bin/application.windows64/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.windows64/source/

rm -rf ${workspaceRoot}/bin/application.macosx/source/
cp -r ${workspaceRoot}/src/basilisk/ ${workspaceRoot}/bin/application.macosx/source/



cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux-arm64/data/
cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux-armv6hf/data/

cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux32/data/
cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.linux64/data/

cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.windows32/data/
cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.windows64/data/

cp -r ${workspaceRoot}/resources/ ${workspaceRoot}/bin/application.macosx/basilisk.app/Contents/Java/data/
