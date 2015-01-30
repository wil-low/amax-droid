#!/bin/bash
#set -x
SRC_DIR=$1
DEST_DIR=$2
WIDTH=$3
WORKDIR=`pwd`
mkdir -p $WORKDIR/tmp/
cd $SRC_DIR

split_image() { # src, dest prefix
    perl $WORKDIR/strip_flow_root.pl < $1 | convert - -crop ${WIDTH}x${WIDTH} +repage $WORKDIR/tmp/$2%02d.png
}

split_image opaqplanet24.svg p
split_image opaqaspect24.svg a
split_image opaqzodiac24.svg z

cd $WORKDIR/tmp
optipng -o7 *.png
pngcrush -d $WORKDIR/tmp/tmp -rem alla *.png
cd $WORKDIR
mv $WORKDIR/tmp/tmp/* $DEST_DIR/
rm -r tmp/

