# otrcut

I started to write a wrapper for ffmpeg to cut recordings consumed at https://www.onlinetvrecorder.com/ with cutlists of http://cutlist.at/

I just checked in the first snaphot. 


Requirement: ffmpeg in the same folder as the jar or available via path variable.

usage: otrcut-<version>.jar --infile=<file> --outdir=<directory> [--exact=<y/n>]
 -e,--exact <arg>    Frame Exact Cutting
 -i,--infile <arg>   Input File Name
 -o,--outdir <arg>   Output Directory


Planned:

- download cutlist automatically
- decode OTRKEY file automatically (Windows, eventually Linux)
- GUI
