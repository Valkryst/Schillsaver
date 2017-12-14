#### License: 

Use this code however you wish. Modifications and improvements are welcome.

Please refer to the LICENSE file for additional information.

---

### Introduction:

Schillsaver is a program that encodes files from their native formats into a video
of black and white blocks, which represent the bits of the source file.

This is somewhat similar to a QA code, where every frame of the video is a small
portion of the source file's data.

The option to decode these files is also available.

### Requirements:

* Any version of Java from 8 onwards.
    * http://java.com/en/download/ or an equivalent Linux package.
    * See http://docs.oracle.com/javase/8/docs/technotes/guides/install/linux_jdk.html for
      more information on installing it for Linux.
        * There may be issues with using OpenJDK.

    
* A recent version of [FFMPEG](http://ffmpeg.org/download.html).

### Misc. Notes

* To open the program on Linux, you may need to open up the terminal and 
   run the command "java -jar Schillsaver.jar".

* Schillsaver does it's best to clean up any temporary files if any issues occur.

    * Please open an issue if you find a case where leftover files exist.

* When running an encode job, the files of the job are combined into a single zip
   file before encoding.
   
   * This choice was made in the effort to both simplify the program's logic and to
      slightly reduce the resulting file size of the encoded video.

* YouTube may not support 60 FPS videos when the video's resolution is less than
   1080p.