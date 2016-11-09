> Copyright (C)  2015-2016  Valkryst

> Permission is granted to copy, distribute and/or modify this document
under the terms of the GNU Free Documentation License, Version 1.3
or any later version published by the Free Software Foundation;
with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.

# Introduction:

Schillsaver is a program that encodes files from their native formats to a video 
format.This can be thought of like a QR code where every frame of the resulting 
video is a partof the data of the file that was encoded.
The option to decode these encoded files is also available.

If you'd like to contribute to Schillsaver, then you can find the GitHub repo [here](https://github.com/Valkryst/Schillsaver).

Schillsaver is somewhat simple to use, so if you screw something up, then email 
your OS, config file, and error log to me at *schillsaver@valkryst.com*.

	

# Requirements:

* Any version of Java from 8 onwards.
    * http://java.com/en/download/ or an equivalent Linux package.
    * See http://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jdk.html for
      more information on installing it for Linux.

    
* A recent version of [FFMPEG](http://ffmpeg.org/download.html).


* 7-Zip or an equivalent program.
    * http://www.7-zip.org/ or the p7zip package on Linux.



If you wish to use something other than 7Zip, please read the "Configuration File 
Options" section as you'll need to change a few options.



# Instructions:

* To open the program on Linux, you may need to open up the terminal and 
  run the command "java -jar Schillsaver.jar".


* If the program is closed before en/decoding has been completed, there is a very high 
  chance that the archive used during the encoding process and the partly-encoded file
  will be left where they are.

  The program is currently unable to clean-up after itself in the event of a shutdown
  during encoding or decoding.


* If the program is en/decoding it will overwrite any files that interfere with
  the file that is being written.

  So if you were encoding test.jpg, it'd be turned into test.jpg.7z, then the
  encoder would begin to write to test.jpg.mkv, but if test.jpg.mkv already
  exists, then the program will overwrite the existing test.jpg.mkv.

  En/decoding will always do this, but if you alter the compression commands,
  then you can enable/disable 7zip or whichever archiver you use from overwriting
  and existing archives it encounters. 

  I've attempted to get this working with 7zip, but my attempts have failed with
  the current way the program is written. The encoding process' behavior is
  undefined if 7zip encounters an archive with the same name as the one it's
  trying to create.


* If the program is encoding test.jpg it will archive it as test.7z then encode as 
  test.mkv with the default settings. If test.7z and test.mkv already exist when the
  program is making use of them, then the behavior of the program is undefined.

  You could encounter some weird bugs, but it hasn't been tested.



# Tested File Options:

* Assume that all settings are on a single line. The lines have been
  adjusted to fit the formatting of this document.


* **Version A (Default)**:
	* Files encoded with these settings will work with YT.

	* Ratio - 1:2
    	* After encoding a 1GB file, you can expect the resulting encoded file 
          to be roughly 2GB.

	* Settings - Encoding:
		* Check the config file generated on the first start of the program or 
		  the settings menu.

	* Settings - Decoding:
		* Check the config file generated on the first start of the program or
		  the settings menu.


* **Version B**:
	* Only supported by the program if you're using the fully-custom FFMPEG options.

	* Files encoded with these settings will not work with YT.

	* Ratio - 1:1.85
    	* After encoding a 1GB file, you can expect the resulting encoded file 
          to be roughly 1.85GB.

	* Settings - Encoding:
		* *ffmpeg -f rawvideo -pix_fmt yuv444p -s 160*90 -r 30 -i "file_padded.7z" 
		-vf "scale=iw*8:ih*8" -sws_flags neighbor -sws_dither none -c:v libx264 
		-x264-params aq-mode=0:qp=15:qblur=0:no-sao:no-deblock:no-open-gop:qstep=0:keyint=1 
		-threads 8 file_padded.mp4*

	* Settings - Decoding:
		* *ffmpeg -i "file_padded.mp4" -vf "scale=iw/8:-1" -pix_fmt yuv444p -sws_flags area 
		-sws_dither none -f rawvideo -threads 8 "file_out.7zip"*


* **Version C**:
	* Only supported by the program if you're using the fully-custom FFMPEG options.

	* Files encoded with these settings will not work with YT.

	* Ratio - 1:1.71
    	* After encoding a 1GB file, you can expect the resulting encoded file 
          to be roughly 1.85GB.

	* Settings - Encoding:
		* *ffmpeg -f rawvideo -pix_fmt yuv444p -s 160*90 -r 30 -i "file_padded.7z" -vf 
		"scale=iw*8:ih*8" -sws_flags neighbor -sws_dither none -c:v libvpx-vp9 -qmin 1 
		-qmax 1 -g 1 -qcomp 0 -threads 8 "file_padded.webm"*


* **Version D**:
	* Only supported by the program if you're using the fully-custom FFMPEG options.

	* Files encoded with these settings will not work with YT.

	* Ratio - 1:1
    	* After encoding a 1GB file, you can expect the resulting encoded file 
          to be roughly 1GB.

	* Settings - Encoding:
		* *ffmpeg -f rawvideo -pix_fmt yuv444p -s 1280x720 -r 30 -i "file_padded.7z" 
		-f rawvideo - | x264_64 --demux raw --profile high444 --output-csp i444 
		--input-csp i444 --input-depth 8 --input-res 1280x720 --fps 30 --threads 8 
		--qp 0 --tune psnr --psnr --output "file.7z.mp4"*

	* Settings - Decoding:
		* *ffmpeg -i "file.7z.mp4" -f rawvideo -threads 8 "file_out.mp4"*

	* Requirements:
		* http://www.msystem.waw.pl/x265/


# Configuration File Abbreviations:

* Enc - Encode/Encoding
* Dec - Decode/Decoding
* Vid - Video


# Configuration File Options:

* **FFMPEG Path**:
    * This is the absolute path to ffmpeg.

    * Ex - Windows:
    	* *"FFMPEG Path": "ffmpeg.exe"*
    	* In this example, ffmpeg.exe is in the same folder as the JAR file.

    * Ex - Windows:
	   * *"FFMPEG Path": "C:\Shill\saver\ffmpeg\bin\ffmpeg.exe"*


* **Compression Program Path**:
    * The absolute path to 7zip/7zip.exe or whichever compression program is
	  specified. If you use anything other than 7zip, then you'll need to alter
	  the decodeFormat and the compressionCommands options.

	* Ex - Windows:
		* *"Compression Program Path": "7z.exe"*

		* In this example, 7z.exe is in the same folder as the JAR file.

	* Ex - Windows:
		* "Compression Program Path": "C:\Shill\saver\7zip\7z.exe"*


* **Enc Format**:
	* This can be pretty-much any format supported by ffmpeg. 

	* If I wanted to encode as mkv, then I'd type in...
		* *"Enc Format": "mkv"*


* **Dec Format**:
	* Change this if you wish, the decoding format is the output format that
	  FFMPEG is told to use when decoding a video file. From my few tests, it
	  doesn't really matter.
	  
	  If you're decoding, for example, a *.7z archive and the Dec Format is jpg,
	  then the resulting file will be named *.jpg. You can simply change this
	  resulting extension manually and your original file should work correctly.

	* Ex:
		* *"Dec Format": "7z"*


* **Enc Vid Width**:
	* The width to make the video when encoding. You can leave this as-is
	  and everything will work fine.

	* Ex:
		* *"Enc Vid Width": 1280*


* **Enc Vid Height**:
	* The height to make the video when encoding. You can leave this as-is
	  and everything will work fine.

	* Ex:
		* *"Enc Vid Height" 720*


* **Enc Vid Framerate**:
	* The framerate to make the video when encoding. You can leave this as-is
	  and everything will work fine.

	* YouTube doesn't support 60fps unless the video is > 720p, so don't
	  bother with anything other than 30fps unless you change the
	  encodedVideoWidth & encodedVideoHeight to 1920 and 1080 or higher,
	  respectively.

	* Ex:
		* *"Enc Vid Framerate": 30*


* **Enc Vid Macro Block Dimensions**:
	* The dimensions of the black and white blocks in the encoded video.
	  Various values have been tested using libvpx as the encodingLibrary
	  and it has been found that 8 is the lowest value that libvpx supports
	  as far as current findings suggest.

	* A size of 4 has been tested with libx264, but the resulting filesize
	  was much larger than that of the file encoded with libvpx using a size
	  of 8.

	* You can leave this as-is.

	* Ex:
		* *"Enc Vid Macro Block Dimensions": 8*


* **Enc Library**:
	* The codec to encode/decode the video with. You can find all of the supported
	  libraries by running "ffmpeg.exe -codecs".

	* As far as we know, libvpx currently offers the best filesize and libx265
	  doesn't support enough of the features required for the encoding process
	  to work for this specific task.

	* You can leave this as-is.

	* Ex:
		* *"Enc Library": "libvpx"*


* **FFMPEG Log Level**:
	* The level of information that should be given by ffmpeg while ffmpeg is 
	  running. If you're having an issue with the encoding or decoding process,
	  then you should switch this over to debug.

	* You can leave this as-is.

	* Accepted Values (12/Jul/2016):
		* **quiet**:
			* Show nothing at all; be silent.

		* **panic**:
			* Only show fatal errors which could lead the process to crash, 
			  such as and assert failure. This is not currently used for 
			  anything. 

		* **fatal**:
			* Only show fatal errors. These are errors after which the process
			  absolutely cannot continue after.

		* **error**:
			* Show all errors, including ones which can be recovered from.

		* **warning**:
			* Show all warnings and errors. Any message related to possibly 
			  incorrect or unexpected events will be shown. 

		* **info**:
			* Show informative messages during processing. This is in addition 
			  to warnings and errors. This is the default value. 

		* **verbose**:
			* Same as "info", except more verbose.

		* **debug**:
			* Show everything, including debugging information.

		* **trace**:
			* No documentation on this value was provided by the ffmpeg 
			  documentation.

	* Ex:
		* *"FFMPEG Log Level": "error"*

	* Ex:
		* *"FFMPEG Log Level": "verbose"*


* **Use Custom FFMPEG Options**:
	* Whether or not to use the custom FFMPEG Enc options.

	* If this is enabled, then BOTH the custom encoding and decoding options 
      will be used and all other settings for FFMPEG will be completely ignored.

	* Ex:
		* *"Use Custom FFMPEG Enc Options": true*

	* Ex:
		* *"Use Custom FFMPEG Enc Options": false*



* **Custom FFMPEG Enc Options**:
	* The custom FFMPEG encoding options to use when encoding.

	* This is directly appended to the FFMPEG call, so if you set
	  this to "schiller" then the call will be something along
	  the lines of...

    	* "C:\Schill\saver\ffmpeg.exe" schiller

	* The program will include a space between the path of FFMPEG
	  and your options, insert the path to the input file, and
	  insert the path to the output file.

	* When writing your commands, simply write FILE_INPUT where
	  the input file's path should be placed. For the output file,
	  the same rule applies, but use FILE_OUTPUT.

	* Ex:
		* _"Custom FFMPEG Enc Options": "-f rawvideo -pix_fmt yuv444p -s 160\*90 -r 30 -i FILE_INPUT -vf "scale=iw\*8:ih\*8" -sws_flags neighbor -sws_dither none -c:v libx264 -threads 8 FILE_OUTPUT"_

		* Assume that the above is all on a single line. It has been
		  split up to fit the format of this document.


* **Custom FFMPEG Dec Options**:
	* The custom FFMPEG encoding options to use when decoding.

	* This is directly appended to the FFMPEG call, so if you set
	  this to "schiller" then the call will be something along
	  the lines of...

	   * "C:\Schill\saver\ffmpeg.exe" schiller

	* The program will include a space between the path of FFMPEG
	  and your options, insert the path to the input file, and
	  insert the path to the output file.

	* When writing your commands, simply write FILE_INPUT where
	  the input file's path should be placed. For the output file,
	  the same rule applies, but use FILE_OUTPUT.

	* Ex:
		* *"Custom FFMPEG Dec Options": "-i FILE_INPUT -vf "scale=iw/8:-1" -pix_fmt yuv444p -sws_flags area -sws_dither none -f rawvideo -threads 8 FILE_OUTPUT""*

		* Assume that the above is all on a single line. It has been
		split up to fit the format of this document.
		

* **Compression Commands**:
	* The base commands to use when compressing a file before encoding. If
	  you use anything other than 7zip, you'll need to change the Dec Format
	  to the format of whatever that other program uses.

	* A space, then the list of files will be appended after the end of the 
	  *Compression Commands string.

	* Ex:
		* *"*Compression Commands": "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on"*

	* Ex - After program does it's thing:
		* *"C:\Schill\saver\7z.exe" a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on filea.jpg fileb.png filec.wmv*

	* Tip:
		* To compress a file and have it encrypted with a password, edit the
		  command arguments in the first example to the following where 
		  PASSWORD is the password you want to use for encryption and 
		  "-r" means to recursively encrypt the files.

		* *"*Compression Commands": "a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on -p[PASSWORD] -r"*


* **Warn If Settings Possibly Incompatible With YouTube**:
    * Whether or not to check the settings for anything which may be incompatible
      with YouTube.

    * The check is not guaranteed to be entirely accurate, but more-so a basic
      idea if your settings are in an acceptable state.