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
   run the command `java -jar Schillsaver.jar`.
   
* For a decode to work successfully, you *must* use the same block size setting 
  and codec that you encoded with, the other settings don't matter.
  
  So, if you've encoded a video with block size 8 and libx264, then it has to be decoded with
  block size 8 and libx264. Any other size will result in a corrupt decode.
  
* When downloading your stored videos from YouTube, or whatever other service you
  use, ensure that you download the video at the same resolution that you encoded
  the video at.
  
  If you've encoded a 1080p video, then download it as 720p and try to decode the
  video, you'll get a corrupt decode.

* Schillsaver does it's best to clean up any temporary files if any issues occur.

    * Please open an issue if you find a case where leftover files exist.

* When running an encode job, the files of the job are combined into a single zip
   file before encoding.
   
   * This choice was made in the effort to both simplify the program's logic and to
      slightly reduce the resulting file size of the encoded video.

* YouTube may not support 60 FPS videos when the video's resolution is less than
   1080p.
