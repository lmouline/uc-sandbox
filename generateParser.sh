#!/usr/bin/env bash

wget http://www.ssw.uni-linz.ac.at/Coco/Java/Coco.jar -O Coco.jar
#java -jar Coco.jar language/src/main/resources/grammar/Luc.atg -frames language/src/main/resources/frame -o language/src/main/java/ldas/uc/sandbox/language/parser/
mkdir -p language/target/generated-sources/cocor/ldas/uc/sandbox/language/parser/
java -jar Coco.jar language/src/main/resources/grammar/Luc.atg -frames language/src/main/resources/frame -o language/target/generated-sources/cocor/ldas/uc/sandbox/language/parser/