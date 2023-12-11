#!/bin/bash

PRINTER_IP=$1
PRINTER_MODEL=$2
IMAGE_LOCATION=$3

/home/user/.local/bin/brother_ql -b network -p ${PRINTER_IP} -m ${PRINTER_MODEL} print -l 62 ${IMAGE_LOCATION}