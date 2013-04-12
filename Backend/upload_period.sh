#!/bin/sh

# Slow login solved by setting:
# AddressFamily inet
# in /etc/ssh_config

ARCFILE=$1.tgz
HOST=w_astromaximum-com_79c70237@astromaximum.com

scp deploy/$ARCFILE $HOST:arc/  &&  ssh $HOST tar xzfv arc/$ARCFILE -C http/data/
