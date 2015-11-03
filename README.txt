Copyright (C)  2015  Valkryst
    Permission is granted to copy, distribute and/or modify this document
    under the terms of the GNU Free Documentation License, Version 1.3
    or any later version published by the Free Software Foundation;
    with no Invariant Sections, no Front-Cover Texts, and no Back-Cover Texts.


Introduction:
	Schillsaver is a program that encodes files from their native formats to a video format.
	This can be thought of like a QR code where every frame of the resulting video is a part
	of the data of the file that was encoded.
	The option to decode these encoded files is also available.

	If you'd like to contribute to Schillsaver, then check in at https://github.com/Valkryst
	and see if I've uploaded the repo yet. It won't be uploaded until I'm happy with the base
	code.

	Schillsaver is somewhat simple to use, so if you screw something up, then post your OS,
	config file, and error log in whichever thread is currently up. I might not always see
	these messages, so the safest way to contact be is VIA schillsaver@valkryst.com.




Requirements:
	Any version of Java from 7 onwards.
		http://java.com/en/download/ or an equivalent Linux package.

	A recent version of FFMPEG.
		http://ffmpeg.org/download.html

	7-Zip or an equivalent program.
		http://www.7-zip.org/ or the p7zip package on Linux.

	A brain.



	If you want to use a different archival program than 7Zip, please read
	the "Configuration File Options" section as you'll need to change a few
	options.




Optional Requirements:
	YouTube-DL:
		https://rg3.github.io/youtube-dl/


	YouTube-Upload:
		https://github.com/tokland/youtube-upload


	If you're using either of the above optional programs, then you'll need
	to set their paths in the settings and enable them.



Instructions:
	* To open the program on Linux, you may need to open up the terminal and
	  do "java -jar Schillsaver.jar".

	* Read the "Configuration File Options" section on first launch of the program.
	  Currently, if you exit or cancel the "Settings Configuration" screen without
	  clicking Accept, the program will still run even though it shouldn't.

	  So, just set the path to ffmpeg and the compression program. The rest you
	  can leave as-is unless you have some idea of what you want to do.

	  Click the Accept button or else the settings won't save.


	* The "Settings Manager" will attempt to locate the executables of FFMPEG
	  and 7-Zip in their default install paths, so ensure that the correct paths
	  are specified when it pops up on the first run of the program.


	* You may only encode or decode at any one time. You cannot do both at the same time.


	* If the program is closed before en/decoding has been completed, there is a very high
	  chance that the archive used during the encoding process and the partly-encoded file
	  will be left where they are.

	  The program is currently unable to clean-up after itself in the event of a shutdown
	  during encoding or decoding.


  	* If the program is en/decoding it will overwrite any files that interfere with
  	  the file that is being written.

  	  So if you were encoding test.jpg, it'd be turned into test.jpg.7z, then the
  	  encoder would begin to write to test.jpg.mkv, but if test.jpg.mkv already
  	  exists, then the program will just say "Screw it, I'll write data anyway!"
  	  and the existing file will be overwritten.

  	  En/decoding will always do this, but if you alter the compression commands,
  	  then you can enable/disable 7zip or whichever archiver you use from overwriting
  	  and existing archives it encounters.

  	  I've attempted to get this working with 7zip, but my attempts have failed with
  	  the current way the program is written. The encoding process' behaviour is
  	  undefined if 7zip encounters an archive with the same name as the one it's
  	  trying to create.


	* If the program is encoding test.jpg it will archive it as test.7z then encode as
	  test.mkv with the default settings. If test.7z and test.mkv already exist when the
	  program is making use of them, then the behaviour of the program is undefined.

	  You could enounter some weird bugs, but it hasn't been tested.

	Encoding:
		* Select any number of files.
		* Click Encode and enter your settings.
		* You should now have an encoded mkv file either in the folder where each file
		  originated from or in the specified directory if you're working with an
		  all-in-one archive/encode.

    Decoding:
    	* Select any number of files.
    	* Click Decode.
    	* You should now have a decoded archive in each of the folders that each
    	  decoded file originated from.

    En/Decoding Example:
    	If I encode C:\Schill\save\test.jpg and C:\Schill\save\test.odt without using
    	the all-in-one archive/encode, then the resulting encoded files will be at
    	C:\Schill\save\test.jpg.mkv and C:\Schill\save\test.odt.mkv.

    	If I decode, then the same principle applies.



Tested File Options:
	* Assume that all settings are on a single line. The lines have been
	  adjusted to fit the formatting of this document.


	Version A (The Default):
		* Files encoded with these settings will work with YT.

		* Ratio - 1:2
			Example:
				After encoding a 1GB file, you can expect the resulting
				encoded file to be roughly 2GB.

		* Settings - Encoding:
			Check the config file generated on the first start of the program.

		* Settings - Decoding:
			Check the config file generated on the first start of the program.


	Version B:
		* Only supported by the program if you're using the fully-custom FFMPEG options.

		* Files encoded with these settings will not work with YT.

		* Ratio - 1:1.85
			Example:
				After encoding a 1GB file, you can expect the resulting
				encoded file to be roughly 1.85GB.

		* Settings - Encoding:
			ffmpeg -f rawvideo -pix_fmt yuv444p -s 160*90 -r 30 -i "file_padded.7z"
			-vf "scale=iw*8:ih*8" -sws_flags neighbor -sws_dither none -c:v libx264
			-x264-params aq-mode=0:qp=15:qblur=0:no-sao:no-deblock:no-open-gop:qstep=0:keyint=1
			-threads 8 file_padded.mp4

		* Settings - Decoding:
			ffmpeg -i "file_padded.mp4" -vf "scale=iw/8:-1" -pix_fmt yuv444p -sws_flags area
			-sws_dither none -f rawvideo -threads 8 "file_out.7zip"


	Version C:
		* Only supported by the program if you're using the fully-custom FFMPEG options.

		* Files encoded with these settings will not work with YT.

		* Ratio - 1:1.71
			Example:
				After encoding a 1GB file, you can expect the resulting
				encoded file to be roughly 1.85GB.

		* Settings - Encoding:
			ffmpeg -f rawvideo -pix_fmt yuv444p -s 160*90 -r 30 -i "file_padded.7z" -vf
			"scale=iw*8:ih*8" -sws_flags neighbor -sws_dither none -c:v libvpx-vp9 -qmin 1
			-qmax 1 -g 1 -qcomp 0 -threads 8 "file_padded.webm"


	Version D:
		* Only supported by the program if you're using the fully-custom FFMPEG options.

		* Files encoded with these settings will not work with YT.

		* Ratio - 1:1
			Example:
				After encoding a 1GB file, you can expect the resulting
				encoded file to be roughly 1GB.

		* Settings - Encoding:
			ffmpeg -f rawvideo -pix_fmt yuv444p -s 1280x720 -r 30 -i "file_padded.7z"
			-f rawvideo - | x264_64 --demux raw --profile high444 --output-csp i444
			--input-csp i444 --input-depth 8 --input-res 1280x720 --fps 30 --threads 8
			--qp 0 --tune psnr --psnr --output "file.7z.mp4"

		* Settings - Decoding:
			ffmpeg -i "file.7z.mp4" -f rawvideo -threads 8 "file_out.mp4"

		* Requirement:
			http://www.msystem.waw.pl/x265/



Configuration File Options:
	ffmpegPath:
		This is the absolute path to ffmpeg.

		Don't use quotation marks when entering in the path, the program will
		do that for you when it loads the path.

		Ex - Windows:
			ffmpegPath ffmpeg.exe

			In this example, ffmpeg.exe is in the same folder as the JAR file.

		Ex - Windows:
			ffmpegPath C:\Shill\saver\ffmpeg\bin\ffmpeg.exe


	compressionProgramPath:
		The absolute path to 7zip/7zip.exe or whichever compression program is
		specified. If you use anything other than 7zip, then you'll need to alter
		the decodeFormat and the compressionCommands options.

		Ex - Windows:
			compressionProgramPath 7z.exe

			In this example, 7z.exe is in the same folder as the JAR file.

		Ex - Windows:
			compressionProgramPath C:\Shill\saver\7zip\7z.exe


	encodeFormat:
		This can be pretty-much any format supported by ffmpeg.

		If I wanted to encode as mkv, then I'd type in...
			encodeFormat mkv


	decodeFormat:
		Leave this as-is. The program doesn't currently support changing
		the decode format as it 7z's the files when encoding. So, when
		decoding, you should end up with a packed 7z archive.

		Ex:
			decodeFormat 7z


	encodedVideoWidth:
		The width to make the video when encoding. You can leave this as-is
		and everything will work fine.

		Ex:
			encodedVideoWidth 1280


	encodedVideoHeight:
		The height to make the video when encoding. You can leave this as-is
		and everything will work fine.

		Ex:
			encodedVideoHeight 720


	encodedFramerate:
		The framerate to make the video when encoding. You can leave this as-is
		and everything will work fine.

		YouTube doesn't support 60fps unless the video is > 720p, so don't
		bother with anything other than 30fps unless you change the
		encodedVideoWidth & encodedVideoHeight to 1920 and 1080 or higher,
		respectivley.

		Ex:
			encodedFramerate 30


	macroBlockDimensions:
		The dimensions of the black and white blocks in the encoded video.
		Various values have been tested using libvpx as the encodingLibrary
		and it has been found that 8 is the lowest value that libvpx supports
		as far as current findings suggest.

		A size of 4 has been tested with libx264, but the resulting filesize
		was much larger than that of the file encoded with libvpx using a size
		of 8.

		You can leave this as-is.

		Ex:
			macroBlockDimensions 8


	encodingLibrary:
		The codec to encode/decode the video with. You can find all of the supported
		libraries by running "ffmpeg.exe -codecs".

		As far as we know, libvpx currently offers the best filesize and libx265
		doesn't support enough of the features required for the encoding process
		to work for this specific task.

		You can leave this as-is.

		Ex:
			encodingLibrary libvpx


	ffmpegLogLevel:
		The level of information that should be given by ffmpeg while ffmpeg is
		running. If you're having an issue with the encoding or decoding process,
		then you should switch this over to debug.

		You can leave this as-is.

		Accepted Values:
			As of 31/Oct/2015, these are the logging levels accepted by ffmpeg.

			quiet:
				Show nothing at all; be silent.

			panic:
				Only show fatal errors which could lead the process to crash,
				such as and assert failure. This is not currently used for
				anything.

			fatal:
				Only show fatal errors. These are errors after which the process
				absolutely cannot continue after.

			error:
				Show all errors, including ones which can be recovered from.

			warning:
				Show all warnings and errors. Any message related to possibly
				incorrect or unexpected events will be shown.

			info:
				Show informative messages during processing. This is in addition
				to warnings and errors. This is the default value.

			verbose:
				Same as "info", except more verbose.

			debug:
				Show everything, including debugging information.

			trace:
				No documentation on this value was provided by the ffmpeg
				documentation.


		Ex:
			ffmpegLogLevel error

		Ex:
			ffmpegLogLevel verbose


	useFullyCustomFfmpegOptions:
		Whether or not to use the fully-custom FFMPEG options.

		If this is enabled, then BOTH the encoding and decoding options will be
		used and all other settings for FFMPEG will be completely ignored.

		Example:
			useFullyCustomFfmpegOptions true

		Example:
			useFullyCustomFfmpegOptions false


	fullyCustomFfmpegEncodingOptions:
		The fully-custom FFMPEG encoding options to use when encoding.

		This is directly appended to the FFMPEG call, so if you set
		this to "schiller" then the call will be something along
		the lines of...

		"C:\Schill\saver\ffmpeg.exe" schiller

		The program will include a space between the path of FFMPEG
		and your options.

		Example:
			fullyCustomFfmpegEncodingOptions -f rawvideo -pix_fmt
			yuv444p -s 160*90 -r 30 -i "file_padded.7z" -vf "scale=iw*8:ih*8"
			-sws_flags neighbor -sws_dither none -c:v libx264
			-threads 8 file_padded.mp4

			Assume that the above is all on a single line. It has been
			split up to fit the format of this document.


	fullyCustomFfmpegDecodingOptions:
		The fully-custom FFMPEG encoding options to use when decoding.

		This is directly appended to the FFMPEG call, so if you set
		this to "schiller" then the call will be something along
		the lines of...

		"C:\Schill\saver\ffmpeg.exe" schiller

		The program will include a space between the path of FFMPEG
		and your options.

		Example:
			fullyCustomFfmpegEncodingOptions -i "file_padded.mp4" -vf
			"scale=iw/8:-1" -pix_fmt yuv444p -sws_flags area
			-sws_dither none -f rawvideo -threads 8 "file_out.7zip"

			Assume that the above is all on a single line. It has been
			split up to fit the format of this document.


	deleteOriginalFileWhenEncoding:
		Whether or not to delete the original file after encoding.

		So, for example, if I was encoding schill.txt into an mkv video, it
		would delete schill.txt after schill.mkv is created.

		Ex:
			deleteOriginalFileWhenEncoding false


	deleteOriginalFileWhenDecoding:
		Whether or not to delete the original file after decoding.

		So, for example, if I was decoding schill.mkv into an mkv video, it
		would delete schill.mkv after schill.txt is created.

		Ex:
			deleteOriginalFileWhenDecoding false


	showSplashScreen:
		Whether or not to show the splash screen on startup.

		This serves absolutley no purpose, so you can just disable it.

		Ex:
			showSplashScreen true


	splashScreenFilePath:
		This is the absolute path to splash screen image.

		Don't use quotation marks when entering in the path, the program will
		do that for you when it loads the path.

		The supported formats are jpeg, png, bmp, and wbmp.

		Ex - Windows:
			splashScreenFilePath Splash.png

			In this example, Splash.png is in the same folder as the JAR file.

		Ex - Windows:
			splashScreenFilePath C:\Schill\saver\Splash.png


	splashScreenDisplayTime:
		The amount of time, in milliseconds, to display the splash screen.

		Ex - 1 Second:
			splashScreenDisplayTime 1000

		Ex - 1.5 Seconds:
			splashScreenDisplayTime 1500

		Ex - 60 Seconds:
			splashScreenDisplayTime 60000


	compressionCommands:
		The base commands to use when compressing a file before encoding. If
		you use anything other than 7zip, you'll need to change the decodeFormat
		to the format of whatever that other program uses.

		A space, then the list of files will be appended after the end of the
		compressionCommands string.

		Ex:
			compressionCommands a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on

		Ex - After program does it's thing:
			"C:\Schill\saver\7z.exe" a -m0=lzma -mx=9 -mfb=64 -md=32m -ms=on
			filea.jpg fileb.png filec.wmv

			Assume that the two lines above are on a single line.


		Tip:
			To compress a file and have it encrypted with a password, edit the
			command arguments in the first example to the following where
			PASSWORD is the password you want to use for encryption and
			"-r" means to recursivley encrypt the files.

			compressionCommands a -m0=lzma -mx=9 -mfb=64
			-md=32m -ms=on -p[PASSWORD] -r

			Assume that the two lines above are on a single line.