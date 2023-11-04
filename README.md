# otrcut
I started to write a wrapper for ffmpeg to cut recordings consumed at https://www.onlinetvrecorder.com/ with cutlists of http://cutlist.at/.

I just checked in the first snaphot. 

<b>Requirement:</b> 

- ffmpeg in the same folder as the jar or available via path variable.
- If the option for downloading the cutlist is not given, the already existing cutlist has to be in the same directory as the input movie file.

<b>Usage:</b> otrcut-\<version\>.jar [options]

 -e,--exact          Frame Exact Cutting (optional)
 
 -g,--getonline      Get Cutlist Online (optional)
 
 -i,--infile <arg>   Input File Name
 
 -o,--outdir <arg>   Output Directory

<b>Planned:</b>

- decode OTRKEY file automatically (Windows, eventually Linux)
- GUI
