[![Java CI with Maven](https://github.com/Valkryst/Schillsaver/actions/workflows/maven.yml/badge.svg)](https://github.com/Valkryst/Schillsaver/actions/workflows/maven.yml)
[![CodeQL](https://github.com/Valkryst/Schillsaver/actions/workflows/codeql.yml/badge.svg)](https://github.com/Valkryst/Schillsaver/actions/workflows/codeql.yml)

Schillsaver allows you to encode one or more files, from their native formats,
into a single video. This video consists of black/white blocks that represent
the bits of the input file(s).

The resulting video can be uploaded to a hosting site, such as YouTube, where it
can then be re-downloaded in the future and decoded back into the original file.
This allows for free file storage.

This program was written as a proof-of-concept, and I am not recommending that
you utilize YouTube for free file storage. However, what you do with this program
is up to you.

## Table of Contents

* [Requirements](https://github.com/Valkryst/Schillsaver#requirements)

## Requirements

* [FFMPEG](https://ffmpeg.org/download.html)
* [Java 21](https://github.com/Valkryst/Install_Java)
* [Schillsaver](https://github.com/Valkryst/Schillsaver/releases)

## Notes

* For a decode to work successfully, you *must* use the same block size setting
  and codec that you encoded with, the other settings don't matter.

  So, if you've encoded a video with block size 8 and libx264, then it has to be decoded with
  block size 8 and libx264. Any other size will result in a corrupt decode.

* To run the program on Linux, you may need launch the program using the `java -jar Schillsaver.jar` command.

* YouTube does not support 60 FPS for videos below 720p.

* When downloading your stored videos from YouTube, or whatever other service you
  use, ensure that you download the video at the same resolution that you encoded
  the video at.

  If you've encoded a 1080p video, then download it as 720p and try to decode the
  video, you'll get a corrupt decode.