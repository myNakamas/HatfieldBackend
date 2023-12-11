#!/bin/bash

PRINTER_IP=$1
PRINTER_MODEL=$2
IMAGE_LOCATION=$3

export BROTHER_QL_PRINTER=$PRINTER_IP
export BROTHER_QL_MODEL=$PRINTER_MODEL
export PYTHONHOME=/home/user/.local/lib/python3.10/site-packages

/home/user/.local/bin/brother_ql -b network -p "$PRINTER_IP" -m "$PRINTER_MODEL" print -l 62 "$IMAGE_LOCATION"